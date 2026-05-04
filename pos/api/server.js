require('dotenv').config();
const express = require('express');
const cors = require('cors');
const sql = require('mssql');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

if (!process.env.JWT_SECRET) {
    console.error("FATAL ERROR: JWT_SECRET environment variable is missing.");
    process.exit(1);
}

const app = express();
app.use(cors());
app.use(express.json());

const dbConfig = {
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME,
    server: process.env.DB_SERVER,
    pool: { max: 10, min: 0, idleTimeoutMillis: 30000 },
    options: { encrypt: true, trustServerCertificate: true }
};

const poolPromise = new sql.ConnectionPool(dbConfig)
    .connect()
    .then(pool => {
        console.log('Connected to MSSQL Database Pool');
        return pool;
    })
    .catch(err => {
        console.error('Database Connection Failed:', err);
        process.exit(1);
    });

const requireAuth = (req, res, next) => {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) return res.status(401).json({ error: "Missing or invalid token format" });
    
    try { 
        req.user = jwt.verify(authHeader.split(' ')[1], process.env.JWT_SECRET); 
        next(); 
    } catch (err) { res.status(401).json({ error: "Session expired or invalid token" }); }
};

const getStoreId = (req) => req.user.storeId;

app.post('/api/login', async (req, res) => {
    const { username, password } = req.body;
    if (!username || !password) return res.status(400).json({ error: "Username and password are required" });

    try {
        const pool = await poolPromise;
        const r = await pool.request()
            .input('u', sql.NVarChar, username)
            .query("SELECT * FROM PosUsers WHERE Username=@u");
            
        if (r.recordset.length === 0 || !bcrypt.compareSync(password, r.recordset[0].PasswordHash)) {
            return res.status(401).json({ error: "Invalid credentials" });
        }
        
        const user = r.recordset[0];
        const token = jwt.sign(
            { id: user.Id, username: user.Username, role: user.Role, storeId: user.StoreId }, 
            process.env.JWT_SECRET, 
            { expiresIn: '24h' }
        );
        res.json({ token, role: user.Role, username: user.Username, storeId: user.StoreId });
    } catch (err) { res.status(500).json({ error: "Authentication error" }); }
});

app.get('/api/stores', requireAuth, async (req, res) => {
    if (req.user.role !== 'SuperAdmin') return res.status(403).json({ error: "Unauthorized" });
    try {
        const pool = await poolPromise;
        const r = await pool.request().query("SELECT * FROM PosStores");
        res.json(r.recordset);
    } catch (err) { res.status(500).json({ error: "Database error" }); }
});

app.post('/api/stores', requireAuth, async (req, res) => {
    if (req.user.role !== 'SuperAdmin') return res.status(403).json({ error: "Unauthorized" });
    const { name, adminUsername, adminPassword } = req.body;
    
    if (!name || !adminUsername || !adminPassword) return res.status(400).json({ error: "Missing fields" });

    try {
        const pool = await poolPromise;
        const userCheck = await pool.request().input('u', sql.NVarChar, adminUsername).query("SELECT Id FROM PosUsers WHERE Username=@u");
        if (userCheck.recordset.length > 0) return res.status(400).json({ error: "Username is already taken" });

        const storeRes = await pool.request()
            .input('n', sql.NVarChar, name)
            .query("INSERT INTO PosStores (Name) OUTPUT INSERTED.Id VALUES (@n)");
            
        const newStoreId = storeRes.recordset[0].Id;
        const hash = bcrypt.hashSync(adminPassword, 10);
        
        await pool.request()
            .input('u', sql.NVarChar, adminUsername)
            .input('h', sql.NVarChar, hash)
            .input('s', sql.Int, newStoreId)
            .query("INSERT INTO PosUsers (Username, PasswordHash, Role, StoreId) VALUES (@u, @h, 'StoreAdmin', @s)");
            
        res.json({ success: true });
    } catch (err) { res.status(500).json({ error: "Failed to create store instance" }); }
});

app.get('/api/users', requireAuth, async (req, res) => {
    const storeId = getStoreId(req);
    if (!storeId) return res.status(403).json({ error: "No store context" });
    try {
        const pool = await poolPromise;
        const r = await pool.request().input('s', sql.Int, storeId).query("SELECT Id, Username, Role FROM PosUsers WHERE StoreId=@s");
        res.json(r.recordset);
    } catch (err) { res.status(500).json({ error: "Database error" }); }
});

app.post('/api/users', requireAuth, async (req, res) => {
    if (req.user.role !== 'StoreAdmin') return res.status(403).json({ error: "Unauthorized" });
    const { username, password, role } = req.body;
    
    if (!username || !password || !role) return res.status(400).json({ error: "Missing required fields" });

    try {
        const pool = await poolPromise;
        const hash = bcrypt.hashSync(password, 10);
        await pool.request()
            .input('u', sql.NVarChar, username)
            .input('h', sql.NVarChar, hash)
            .input('r', sql.NVarChar, role)
            .input('s', sql.Int, req.user.storeId)
            .query("INSERT INTO PosUsers (Username, PasswordHash, Role, StoreId) VALUES (@u, @h, @r, @s)");
        res.json({ success: true });
    } catch (err) { res.status(500).json({ error: "Username might be taken or database error" }); }
});

app.get('/api/inventory', requireAuth, async (req, res) => {
    const storeId = getStoreId(req);
    try {
        const pool = await poolPromise;
        const r = await pool.request().input('s', sql.Int, storeId).query("SELECT * FROM PosInventory WHERE StoreId=@s");
        res.json(r.recordset);
    } catch (err) { res.status(500).json({ error: "Database error" }); }
});

app.post('/api/inventory', requireAuth, async (req, res) => {
    if (req.user.role !== 'StoreAdmin') return res.status(403).json({ error: "Unauthorized" });
    const { ItemName, SKU, Price, Stock } = req.body;
    try {
        const pool = await poolPromise;
        await pool.request()
            .input('s', sql.Int, req.user.storeId)
            .input('i', sql.NVarChar, ItemName)
            .input('sku', sql.NVarChar, SKU)
            .input('p', sql.Float, Price)
            .input('st', sql.Int, Stock)
            .query("INSERT INTO PosInventory (StoreId, ItemName, SKU, Price, Stock) VALUES (@s, @i, @sku, @p, @st)");
        res.json({ success: true });
    } catch (err) { res.status(500).json({ error: "Database error" }); }
});

app.post('/api/sales', requireAuth, async (req, res) => {
    const storeId = getStoreId(req);
    const { items } = req.body; 

    if (!items || items.length === 0) return res.status(400).json({ error: "Empty cart" });

    try {
        const pool = await poolPromise;
        
        const transaction = new sql.Transaction(pool);
        await transaction.begin();

        try {
            let total = items.reduce((sum, item) => sum + (item.Price * item.Quantity), 0);
            
            const salesReq = new sql.Request(transaction);
            salesReq.input('s', sql.Int, storeId);
            salesReq.input('c', sql.NVarChar, req.user.username);
            salesReq.input('t', sql.Float, total);
            await salesReq.query("INSERT INTO PosSales (StoreId, Cashier, TotalAmount, Timestamp) VALUES (@s, @c, @t, GETDATE())");
            
            for (let item of items) {
                const invReq = new sql.Request(transaction);
                invReq.input('qty', sql.Int, item.Quantity);
                invReq.input('id', sql.Int, item.Id);
                invReq.input('sid', sql.Int, storeId);
                await invReq.query("UPDATE PosInventory SET Stock = Stock - @qty WHERE Id = @id AND StoreId = @sid");
            }
            
            await transaction.commit();
            res.json({ success: true });
        } catch (err) {
            await transaction.rollback();
            throw err;
        }
    } catch (e) {
        res.status(500).json({ error: "Checkout failed" });
    }
});

app.listen(process.env.PORT || 3001, () => console.log(`Line POS API running on port ${process.env.PORT || 3001}`));