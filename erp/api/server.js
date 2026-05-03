const express = require('express');
const cors = require('cors');
const sql = require('mssql');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
require('dotenv').config();

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

async function initErpDB() {
    try {
        const pool = await sql.connect(dbConfig);
        await pool.query(`
            IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='ErpCompanies' and xtype='U')
            CREATE TABLE ErpCompanies (Id INT IDENTITY(1,1) PRIMARY KEY, Name NVARCHAR(100), Type NVARCHAR(50));

            IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='ErpUsers' and xtype='U')
            CREATE TABLE ErpUsers (Id INT IDENTITY(1,1) PRIMARY KEY, Username NVARCHAR(50) UNIQUE, PasswordHash NVARCHAR(255), Role NVARCHAR(20), CompanyId INT NULL FOREIGN KEY REFERENCES ErpCompanies(Id), IsActive BIT DEFAULT 1);

            IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='ErpInventory' and xtype='U')
            CREATE TABLE ErpInventory (Id INT IDENTITY(1,1) PRIMARY KEY, CompanyId INT, ItemName NVARCHAR(100), SKU NVARCHAR(50), Quantity INT, Price DECIMAL(18,2));

            IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='ErpSales' and xtype='U')
            CREATE TABLE ErpSales (Id INT IDENTITY(1,1) PRIMARY KEY, CompanyId INT, Customer NVARCHAR(100), Amount DECIMAL(18,2), Status NVARCHAR(50), OrderDate DATETIME DEFAULT GETDATE());

            IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='ErpEmployees' and xtype='U')
            CREATE TABLE ErpEmployees (Id INT IDENTITY(1,1) PRIMARY KEY, CompanyId INT, EmployeeName NVARCHAR(100), Position NVARCHAR(100), ComplianceStatus NVARCHAR(50), Salary DECIMAL(18,2));

            IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='ErpSchedules' and xtype='U')
            CREATE TABLE ErpSchedules (Id INT IDENTITY(1,1) PRIMARY KEY, CompanyId INT, EmployeeName NVARCHAR(100), ShiftStart DATETIME, ShiftEnd DATETIME, Status NVARCHAR(50));

            IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='ErpTransactions' and xtype='U')
            CREATE TABLE ErpTransactions (Id INT IDENTITY(1,1) PRIMARY KEY, CompanyId INT, Type NVARCHAR(50), Description NVARCHAR(200), Amount DECIMAL(18,2), TxDate DATETIME DEFAULT GETDATE());

            IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='ErpChat' and xtype='U')
            CREATE TABLE ErpChat (Id INT IDENTITY(1,1) PRIMARY KEY, CompanyId INT NULL, Sender NVARCHAR(50), Message NVARCHAR(MAX), IsGlobal BIT, Timestamp DATETIME DEFAULT GETDATE());
        `);
        console.log("Line ERP Advanced Tables Initialized.");
    } catch (err) { console.error("DB Error:", err.message); }
}
initErpDB();

const requireAuth = (req, res, next) => {
    const token = req.headers.authorization?.split(' ')[1];
    if (!token) return res.status(401).json({ error: "Missing token" });
    try { req.user = jwt.verify(token, process.env.JWT_SECRET || 'secret'); next(); } 
    catch (err) { res.status(401).json({ error: "Session expired" }); }
};

const getContextCompanyId = (req) => {
    if (req.user.role === 'SuperAdmin') {
        const headerId = req.headers['x-company-id'];
        return headerId && headerId !== 'null' ? parseInt(headerId) : null;
    }
    return req.user.companyId;
};

app.get('/api/check-setup', async (req, res) => {
    const pool = await sql.connect(dbConfig);
    const r = await pool.request().query("SELECT COUNT(*) as c FROM ErpUsers");
    res.json({ isSetup: r.recordset[0].c > 0 });
});

app.post('/api/setup', async (req, res) => {
    const pool = await sql.connect(dbConfig);
    const hash = await bcrypt.hash(req.body.password, 10);
    await pool.request().input('u', req.body.username).input('h', hash)
        .query("INSERT INTO ErpUsers (Username, PasswordHash, Role) VALUES (@u, @h, 'SuperAdmin')");
    res.json({ success: true });
});

app.post('/api/login', async (req, res) => {
    const pool = await sql.connect(dbConfig);
    const r = await pool.request().input('u', req.body.username).query("SELECT * FROM ErpUsers WHERE Username=@u");
    if (r.recordset.length === 0 || !(await bcrypt.compare(req.body.password, r.recordset[0].PasswordHash))) 
        return res.status(401).json({ error: "Invalid credentials" });
    
    const user = r.recordset[0];
    const token = jwt.sign({ id: user.Id, username: user.Username, role: user.Role, companyId: user.CompanyId }, process.env.JWT_SECRET || 'secret', { expiresIn: '24h' });
    res.json({ token, role: user.Role, username: user.Username, companyId: user.CompanyId });
});

app.get('/api/dashboard', requireAuth, async (req, res) => {
    const pool = await sql.connect(dbConfig);
    const compId = getContextCompanyId(req);

    if (req.user.role === 'SuperAdmin' && !compId) {
        const c = await pool.request().query("SELECT * FROM ErpCompanies");
        const u = await pool.request().query("SELECT COUNT(*) as total FROM ErpUsers");
        res.json({ type: 'super', companies: c.recordset, totalUsers: u.recordset[0].total });
    } else {
        const sales = await pool.request().input('c', compId).query("SELECT SUM(Amount) as Total FROM ErpSales WHERE CompanyId=@c");
        const inv = await pool.request().input('c', compId).query("SELECT COUNT(*) as Total FROM ErpInventory WHERE CompanyId=@c");
        const emp = await pool.request().input('c', compId).query("SELECT COUNT(*) as Total FROM ErpEmployees WHERE CompanyId=@c");
        const tx = await pool.request().input('c', compId).query("SELECT COUNT(*) as Total FROM ErpTransactions WHERE CompanyId=@c");
        
        res.json({ 
            type: 'company', 
            sales: sales.recordset[0].Total || 0, 
            inventoryCount: inv.recordset[0].Total || 0,
            employeeCount: emp.recordset[0].Total || 0,
            transactionCount: tx.recordset[0].Total || 0
        });
    }
});

app.get('/api/companies', requireAuth, async (req, res) => {
    const pool = await sql.connect(dbConfig);
    const r = await pool.request().query("SELECT * FROM ErpCompanies");
    res.json(r.recordset);
});

app.post('/api/companies', requireAuth, async (req, res) => {
    if (req.user.role !== 'SuperAdmin') return res.status(403).json({ error: "Unauthorized" });
    const { name, type, adminUsername, adminPassword } = req.body;
    try {
        const pool = await sql.connect(dbConfig);
        const compRes = await pool.request().input('n', name).input('t', type)
            .query("INSERT INTO ErpCompanies (Name, Type) OUTPUT INSERTED.Id VALUES (@n, @t)");
        const newCompId = compRes.recordset[0].Id;
        const hash = await bcrypt.hash(adminPassword, 10);
        await pool.request().input('u', adminUsername).input('h', hash).input('c', newCompId)
            .query("INSERT INTO ErpUsers (Username, PasswordHash, Role, CompanyId) VALUES (@u, @h, 'CompanyAdmin', @c)");
        res.json({ success: true });
    } catch (err) { res.status(500).json({ error: err.message }); }
});

app.post('/api/users', requireAuth, async (req, res) => {
    const compId = getContextCompanyId(req);
    if (!compId) return res.status(400).json({ error: "No active company context" });
    const { username, password, role } = req.body;
    try {
        const pool = await sql.connect(dbConfig);
        const hash = await bcrypt.hash(password, 10);
        await pool.request().input('u', username).input('h', hash).input('r', role).input('c', compId)
            .query("INSERT INTO ErpUsers (Username, PasswordHash, Role, CompanyId) VALUES (@u, @h, @r, @c)");
        res.json({ success: true });
    } catch (err) { res.status(500).json({ error: err.message }); }
});

const TABLES = {
    inventory: 'ErpInventory',
    sales: 'ErpSales',
    employees: 'ErpEmployees',
    schedules: 'ErpSchedules',
    finance: 'ErpTransactions'
};

app.get('/api/data/:module', requireAuth, async (req, res) => {
    const table = TABLES[req.params.module];
    const compId = getContextCompanyId(req);

    if (!table) return res.status(400).json({ error: "Invalid module" });

    try {
        const pool = await sql.connect(dbConfig);
        if (compId) {
            const r = await pool.request().input('c', compId).query(`SELECT * FROM ${table} WHERE CompanyId=@c ORDER BY Id DESC`);
            res.json(r.recordset);
        } else if (req.user.role === 'SuperAdmin') {
            const r = await pool.request().query(`SELECT * FROM ${table} ORDER BY Id DESC`);
            res.json(r.recordset);
        } else {
            res.status(400).json({ error: "Invalid context" });
        }
    } catch (err) { res.status(500).json({ error: err.message }); }
});

app.post('/api/data/:module', requireAuth, async (req, res) => {
    const table = TABLES[req.params.module];
    const compId = getContextCompanyId(req);
    
    if (!table) return res.status(400).json({ error: "Invalid module" });
    if (!compId) return res.status(400).json({ error: "Please select a specific company to add records." });
    
    const fields = Object.keys(req.body);
    const values = Object.values(req.body);
    
    const cols =['CompanyId', ...fields].join(', ');
    const params =['@c', ...fields.map((_, i) => `@p${i}`)].join(', ');

    try {
        const pool = await sql.connect(dbConfig);
        const reqObj = pool.request().input('c', sql.Int, compId);
        values.forEach((v, i) => reqObj.input(`p${i}`, v));

        await reqObj.query(`INSERT INTO ${table} (${cols}) VALUES (${params})`);
        res.json({ success: true });
    } catch (err) { res.status(500).json({ error: err.message }); }
});

app.get('/api/chat', requireAuth, async (req, res) => {
    const isGlobal = req.query.global === 'true';
    const compId = getContextCompanyId(req);
    const pool = await sql.connect(dbConfig);
    let q = "SELECT TOP 50 Sender, Message, Timestamp FROM ErpChat WHERE IsGlobal=1 ORDER BY Timestamp ASC";
    if (!isGlobal) {
        if (compId) q = `SELECT TOP 50 Sender, Message, Timestamp FROM ErpChat WHERE IsGlobal=0 AND CompanyId=${compId} ORDER BY Timestamp ASC`;
        else if (req.user.role === 'SuperAdmin') q = `SELECT TOP 50 Sender, Message, Timestamp FROM ErpChat WHERE IsGlobal=0 ORDER BY Timestamp ASC`;
    }
    const r = await pool.request().query(q);
    res.json(r.recordset);
});

app.post('/api/chat', requireAuth, async (req, res) => {
    const isGlobal = req.body.global === true;
    const compId = getContextCompanyId(req);
    const pool = await sql.connect(dbConfig);
    await pool.request().input('c', isGlobal ? null : compId).input('s', req.user.username).input('m', req.body.message).input('g', isGlobal ? 1 : 0)
        .query("INSERT INTO ErpChat (CompanyId, Sender, Message, IsGlobal) VALUES (@c, @s, @m, @g)");
    res.json({ success: true });
});

app.listen(process.env.ERP_PORT || 3001, () => console.log(`Line ERP Server on ${process.env.ERP_PORT || 3001}`));