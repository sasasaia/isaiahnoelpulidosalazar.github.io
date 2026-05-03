const express = require('express');
const cors = require('cors');
const sql = require('mssql');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
require('dotenv').config();

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

const isValidPassword = (pw) => {
    return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(pw);
};

const getContextCompanyId = (req) => {
    if (req.user.role === 'SuperAdmin') {
        const headerId = req.headers['x-company-id'];
        return headerId && headerId !== 'null' ? parseInt(headerId, 10) : null;
    }
    return req.user.companyId;
};

const requireAuth = async (req, res, next) => {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) return res.status(401).json({ error: "Missing or invalid token format" });
    
    const token = authHeader.split(' ')[1];
    try { 
        req.user = jwt.verify(token, process.env.JWT_SECRET); 
        
        const pool = await poolPromise;
        await pool.request()
            .input('u', sql.NVarChar, req.user.username)
            .query("UPDATE ErpUsers SET LastActive = GETDATE() WHERE Username=@u");
        
        next(); 
    } catch (err) { res.status(401).json({ error: "Session expired or invalid token" }); }
};

app.post('/api/login', async (req, res) => {
    const { username, password } = req.body;
    if (!username || !password) return res.status(400).json({ error: "Username and password are required" });

    try {
        const pool = await poolPromise;
        const r = await pool.request()
            .input('u', sql.NVarChar, username)
            .query("SELECT * FROM ErpUsers WHERE Username=@u");
            
        if (r.recordset.length === 0 || !(await bcrypt.compare(password, r.recordset[0].PasswordHash))) {
            return res.status(401).json({ error: "Invalid credentials" });
        }
        
        const user = r.recordset[0];
        if (!user.IsActive) return res.status(403).json({ error: "Account disabled." });

        const token = jwt.sign(
            { id: user.Id, username: user.Username, role: user.Role, companyId: user.CompanyId }, 
            process.env.JWT_SECRET, 
            { expiresIn: '24h' }
        );
        res.json({ token, role: user.Role, username: user.Username, companyId: user.CompanyId });
    } catch (err) { res.status(500).json({ error: "Authentication error" }); }
});

app.get('/api/dashboard', requireAuth, async (req, res) => {
    const compId = getContextCompanyId(req);
    try {
        const pool = await poolPromise;

        if (req.user.role === 'SuperAdmin' && !compId) {
            const c = await pool.request().query("SELECT COUNT(*) as c FROM ErpCompanies");
            const u = await pool.request().query("SELECT COUNT(*) as total FROM ErpUsers");
            const online = await pool.request().query("SELECT COUNT(*) as c FROM ErpUsers WHERE LastActive > DATEADD(minute, -15, GETDATE())");
            return res.json({ type: 'super', companies: c.recordset[0].c, totalUsers: u.recordset[0].total, online: online.recordset[0].c });
        } 
        
        const reqObj = pool.request().input('c', sql.Int, compId);
        
        const sales = await reqObj.query("SELECT SUM(Amount) as Total FROM ErpSales WHERE CompanyId=@c");
        const orders = await reqObj.query("SELECT COUNT(*) as Total FROM ErpSales WHERE CompanyId=@c");
        const emp = await reqObj.query("SELECT COUNT(*) as Total FROM ErpEmployees WHERE CompanyId=@c AND ComplianceStatus='Cleared'");
        const online = await reqObj.query("SELECT COUNT(*) as Total FROM ErpUsers WHERE CompanyId=@c AND LastActive > DATEADD(minute, -15, GETDATE())");
        
        const notifs = await pool.request().input('u', sql.NVarChar, req.user.username).query("SELECT COUNT(*) as Total FROM ErpNotifications WHERE Username=@u AND IsRead=0");
        const chats = await pool.request().input('u', sql.NVarChar, req.user.username).query("SELECT COUNT(*) as Total FROM ErpChat WHERE Receiver=@u AND Timestamp > DATEADD(day, -1, GETDATE())");

        res.json({ 
            type: 'company', 
            sales: sales.recordset[0].Total || 0, 
            orders: orders.recordset[0].Total || 0,
            activeEmployees: emp.recordset[0].Total || 0,
            online: online.recordset[0].Total || 0,
            notifications: notifs.recordset[0].Total || 0,
            newChats: chats.recordset[0].Total || 0
        });
    } catch (err) { res.status(500).json({ error: "Dashboard compilation failed" }); }
});

app.get('/api/companies', requireAuth, async (req, res) => {
    if (req.user.role !== 'SuperAdmin') return res.status(403).json({ error: "Unauthorized" });
    try {
        const pool = await poolPromise;
        const r = await pool.request().query("SELECT Id, Name, Type FROM ErpCompanies");
        res.json(r.recordset);
    } catch (err) { res.status(500).json({ error: "Database error" }); }
});

app.post('/api/companies', requireAuth, async (req, res) => {
    if (req.user.role !== 'SuperAdmin') return res.status(403).json({ error: "Unauthorized" });
    const { name, type, adminUsername, adminPassword } = req.body;
    
    if (!name || !type || !adminUsername || !adminPassword) return res.status(400).json({ error: "Missing fields" });
    if (!isValidPassword(adminPassword)) return res.status(400).json({ error: "Password must be 8+ chars and contain upper, lower, number, and special character." });

    try {
        const pool = await poolPromise;
        
        const userCheck = await pool.request().input('u', sql.NVarChar, adminUsername).query("SELECT Id FROM ErpUsers WHERE Username=@u");
        if (userCheck.recordset.length > 0) return res.status(400).json({ error: "Username is already taken" });

        const compRes = await pool.request()
            .input('n', sql.NVarChar, name)
            .input('t', sql.NVarChar, type)
            .query("INSERT INTO ErpCompanies (Name, Type) OUTPUT INSERTED.Id VALUES (@n, @t)");
            
        const newCompId = compRes.recordset[0].Id;
        const hash = await bcrypt.hash(adminPassword, 10);
        
        await pool.request()
            .input('u', sql.NVarChar, adminUsername)
            .input('h', sql.NVarChar, hash)
            .input('c', sql.Int, newCompId)
            .query("INSERT INTO ErpUsers (Username, PasswordHash, Role, CompanyId) VALUES (@u, @h, 'CompanyAdmin', @c)");
            
        res.json({ success: true });
    } catch (err) { res.status(500).json({ error: "Failed to create tenant" }); }
});

app.post('/api/users', requireAuth, async (req, res) => {
    const compId = getContextCompanyId(req);
    if (!compId) return res.status(400).json({ error: "No active company context" });
    
    const { username, password, role } = req.body;
    if (!username || !password || !role) return res.status(400).json({ error: "Missing required fields" });
    if (!isValidPassword(password)) return res.status(400).json({ error: "Password does not meet complexity requirements." });

    const allowedRoles =['Employee', 'Manager'];
    if (req.user.role === 'SuperAdmin') allowedRoles.push('CompanyAdmin');
    if (!allowedRoles.includes(role)) return res.status(403).json({ error: "Unauthorized role assignment" });

    try {
        const pool = await poolPromise;
        const hash = await bcrypt.hash(password, 10);
        await pool.request()
            .input('u', sql.NVarChar, username)
            .input('h', sql.NVarChar, hash)
            .input('r', sql.NVarChar, role)
            .input('c', sql.Int, compId)
            .query("INSERT INTO ErpUsers (Username, PasswordHash, Role, CompanyId) VALUES (@u, @h, @r, @c)");
        res.json({ success: true });
    } catch (err) { res.status(500).json({ error: "Failed to create user (may already exist)" }); }
});

const TABLE_SCHEMAS = {
    inventory: { table: 'ErpInventory', fields:['ItemName', 'SKU', 'Quantity', 'Price'] },
    sales: { table: 'ErpSales', fields:['Customer', 'Amount', 'Status'] },
    employees: { table: 'ErpEmployees', fields:['EmployeeName', 'Position', 'ComplianceStatus', 'Salary'] },
    schedules: { table: 'ErpSchedules', fields: ['EmployeeName', 'ShiftStart', 'ShiftEnd', 'Status'] },
    finance: { table: 'ErpTransactions', fields: ['Type', 'Description', 'Amount'] }
};

app.get('/api/data/:module', requireAuth, async (req, res) => {
    const schema = TABLE_SCHEMAS[req.params.module];
    const compId = getContextCompanyId(req);

    if (!schema) return res.status(400).json({ error: "Invalid module" });

    try {
        const pool = await poolPromise;
        if (compId) {
            const r = await pool.request().input('c', sql.Int, compId).query(`SELECT * FROM ${schema.table} WHERE CompanyId=@c ORDER BY Id DESC`);
            res.json(r.recordset);
        } else if (req.user.role === 'SuperAdmin') {
            const r = await pool.request().query(`SELECT * FROM ${schema.table} ORDER BY Id DESC`);
            res.json(r.recordset);
        } else {
            res.status(400).json({ error: "Invalid context" });
        }
    } catch (err) { res.status(500).json({ error: "Database error fetching module data" }); }
});

app.post('/api/data/:module', requireAuth, async (req, res) => {
    const schema = TABLE_SCHEMAS[req.params.module];
    const compId = getContextCompanyId(req);
    
    if (!schema) return res.status(400).json({ error: "Invalid module" });
    if (!compId) return res.status(400).json({ error: "Please select a specific company to add records." });
    
    const fieldsToInsert = [];
    const valuesToInsert =[];

    schema.fields.forEach(field => {
        if (req.body[field] !== undefined) {
            fieldsToInsert.push(field);
            valuesToInsert.push(req.body[field]);
        }
    });

    if (fieldsToInsert.length === 0) return res.status(400).json({ error: "No valid fields provided" });

    const cols =['CompanyId', ...fieldsToInsert].join(', ');
    const params =['@c', ...fieldsToInsert.map((_, i) => `@p${i}`)].join(', ');

    try {
        const pool = await poolPromise;
        const reqObj = pool.request().input('c', sql.Int, compId);
        
        valuesToInsert.forEach((v, i) => reqObj.input(`p${i}`, v)); 
        await reqObj.query(`INSERT INTO ${schema.table} (${cols}) VALUES (${params})`);
        
        res.json({ success: true });
    } catch (err) { res.status(500).json({ error: "Failed to insert record" }); }
});

app.get('/api/chat/users', requireAuth, async (req, res) => {
    const compId = getContextCompanyId(req);
    try {
        const pool = await poolPromise;
        let q = "SELECT Username, LastActive FROM ErpUsers WHERE Username != @u";
        const reqObj = pool.request().input('u', sql.NVarChar, req.user.username);
        
        if (compId) {
            q += " AND CompanyId=@c";
            reqObj.input('c', sql.Int, compId);
        }
        
        const r = await reqObj.query(q);
        res.json(r.recordset);
    } catch (e) { res.status(500).json({ error: "Failed to fetch users" }); }
});

app.get('/api/chat', requireAuth, async (req, res) => {
    const { mode, user } = req.query; 
    const compId = getContextCompanyId(req);
    
    try {
        const pool = await poolPromise;
        const reqObj = pool.request();
        let q = "";

        if (mode === 'global') {
            q = "SELECT TOP 50 Sender, Message, Timestamp FROM ErpChat WHERE IsGlobal=1 ORDER BY Timestamp ASC";
        } else if (mode === 'company') {
            reqObj.input('cId', sql.Int, compId || 0);
            q = "SELECT TOP 50 Sender, Message, Timestamp FROM ErpChat WHERE IsGlobal=0 AND Receiver IS NULL AND CompanyId=@cId ORDER BY Timestamp ASC";
        } else if (mode === 'private') {
            if (!user) return res.status(400).json({error: "Target user required"});
            reqObj.input('me', sql.NVarChar, req.user.username).input('them', sql.NVarChar, user);
            q = "SELECT TOP 50 Sender, Message, Timestamp FROM ErpChat WHERE (Sender=@me AND Receiver=@them) OR (Sender=@them AND Receiver=@me) ORDER BY Timestamp ASC";
        } else {
            return res.status(400).json({ error: "Invalid chat mode" });
        }
        
        const r = await reqObj.query(q);
        res.json(r.recordset);
    } catch (err) { res.status(500).json({ error: "Failed to fetch chat history" }); }
});

app.post('/api/chat', requireAuth, async (req, res) => {
    const { mode, message, receiver } = req.body;
    if (!message || !mode) return res.status(400).json({error: "Missing fields"});

    const compId = getContextCompanyId(req);
    const isGlobal = mode === 'global' ? 1 : 0;
    const rec = mode === 'private' ? receiver : null;
    const cId = mode === 'global' ? null : compId;

    try {
        const pool = await poolPromise;
        await pool.request()
            .input('c', sql.Int, cId)
            .input('s', sql.NVarChar, req.user.username)
            .input('m', sql.NVarChar, message)
            .input('g', sql.Bit, isGlobal)
            .input('r', sql.NVarChar, rec)
            .query("INSERT INTO ErpChat (CompanyId, Sender, Message, IsGlobal, Receiver) VALUES (@c, @s, @m, @g, @r)");
        res.json({ success: true });
    } catch (e) { res.status(500).json({ error: "Failed to send message" }); }
});

app.listen(process.env.ERP_PORT || 3001, () => console.log(`Secure Line ERP Server on ${process.env.ERP_PORT || 3001}`));