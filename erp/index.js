const API_BASE = 'https://isaiahnoelpulidosalazar-github-io-erp.onrender.com/api';
let THEME = ECTheme.Blue;

let loaderModal = new ECModal("Loading...").setContent(new ECSpinner({size: "lg"}).element);

async function apiCall(endpoint, method = 'GET', body = null) {
    const token = localStorage.getItem('erp_token');
    const headers = { 'Content-Type': 'application/json' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const options = { method, headers };
    if (body) options.body = JSON.stringify(body);
    
    try {
        const res = await fetch(`${API_BASE}${endpoint}`, options);
        return await res.json();
    } catch (e) {
        new ECToast("Network Error", {type: "error"}).show();
        return { error: "Network Error" };
    }
}

async function initApp() {
    const setupRes = await apiCall('/check-setup');
    if (!setupRes.isSetup) return renderSetup();
    if (localStorage.getItem('erp_token')) return renderMainApp();
    renderLogin();
}

function clearRoot() {
    document.getElementById('app-root').innerHTML = '';
}

function renderSetup() {
    clearRoot();
    const hero = new ECHero({
        title: "Initialize Line ERP",
        subtitle: "Create the global super administrator account to begin.",
        eyebrow: "SYSTEM SETUP"
    });

    const box = new ECBasicCard();
    const user = new ECTextbox({placeholder: "Admin Username"});
    const pass = new ECTextbox({placeholder: "Password", type: "password"});
    const btn = new ECButton("Setup System").onClick(async () => {
        const res = await apiCall('/setup', 'POST', { username: user.getValue(), password: pass.getValue() });
        if (res.success) { new ECToast("Setup Complete!", {type: "success"}).show(); initApp(); }
    });

    box.append(user).append(pass).append(btn);
    hero.element.appendChild(box.element);
    document.getElementById('app-root').appendChild(hero.element);
}

function renderLogin() {
    clearRoot();
    const hero = new ECHero({ title: "Line ERP Login", eyebrow: "WELCOME BACK" });
    const box = new ECBasicCard();
    const user = new ECTextbox({placeholder: "Username"});
    const pass = new ECTextbox({placeholder: "Password", type: "password"});
    
    const btn = new ECButton("Login", {variant: "filled"}).onClick(async () => {
        const res = await apiCall('/login', 'POST', { username: user.getValue(), password: pass.getValue() });
        if (res.token) {
            localStorage.setItem('erp_token', res.token);
            localStorage.setItem('erp_role', res.role);
            new ECToast("Login Successful", {type: "success"}).show();
            initApp();
        } else {
            new ECToast(res.error || "Login Failed", {type: "error"}).show();
        }
    });

    box.append(user).append(pass).append(btn);
    hero.element.appendChild(box.element);
    document.getElementById('app-root').appendChild(hero.element);
}

let contentArea;
function renderMainApp() {
    clearRoot();
    const root = document.getElementById('app-root');
    
    const topbar = new ECTopbar("Line ERP Workspace");
    const logoutBtn = new ECButton("Logout", {variant: "outline"}).onClick(() => {
        localStorage.clear(); initApp();
    });
    
    const sidebarToggle = new ECButton("☰ Menu", {variant: "white"}).onClick(() => sidebar.open());
    topbar.addAction(logoutBtn);

    const sidebar = new ECSidebar("Modules");
    const navList = new ECList({ variant: "hoverable" });
    
    const modules =[
        { name: "📊 Dashboard", view: renderDashboard },
        { name: "💬 Communications", view: renderChat },
        { name: "📦 Inventory & Logistics", view: () => renderCRUD('inventory', 'Inventory Management',[{key:'ItemName', label:'Item Name'}, {key:'SKU'}, {key:'Quantity'}, {key:'Price'}]) },
        { name: "📈 Sales & Customers", view: () => renderCRUD('sales', 'Sales Orders',[{key:'Customer'}, {key:'Amount'}, {key:'Status', render: (v) => new ECBadge(v, v==='Paid'?'success':'warning').element }]) },
        { name: "👥 HR & Compliance", view: () => renderCRUD('employees', 'Employee Roster',[{key:'EmployeeName'}, {key:'Position'}, {key:'ComplianceStatus'}, {key:'Salary'}]) },
        { name: "📅 Schedule Tracker", view: () => renderCRUD('schedules', 'Shift Schedules',[{key:'EmployeeName'}, {key:'ShiftStart'}, {key:'ShiftEnd'}, {key:'Status'}]) },
        { name: "💰 Finance & Accounting", view: () => renderCRUD('finance', 'Transactions & Payroll',[{key:'Type'}, {key:'Description'}, {key:'Amount'}, {key:'TxDate'}]) }
    ];

    modules.forEach(m => navList.addItem(m.name, () => { loadView(m.view); sidebar.close(); }));
    sidebar.addContent(navList.element);

    contentArea = document.createElement('div');
    contentArea.className = "flexGrow-1 padding-80px_24px_24px_24px width-100% boxSizing-border-box overflowY-auto";
    
    root.appendChild(topbar.element);
    root.appendChild(sidebar.element);
    root.appendChild(contentArea);

    loadView(renderDashboard);
}

function loadView(viewFunc) {
    contentArea.innerHTML = '';
    viewFunc(contentArea);
    if(window.ECStyleSheet) window.ECStyleSheet.scan();
}

async function renderDashboard(container) {
    const res = await apiCall('/dashboard');
    const grid = new ECGrid({columns: 3});
    
    if (res.type === 'company') {
        grid.addItem(new ECBasicCard(`<h3 class="margin-0 color-var(--ec-text-muted,_#6c757d)">Total Sales</h3><p class="fontSize-32px fontWeight-bold margin-10px_0">$${res.sales.toFixed(2)}</p>`));
        grid.addItem(new ECBasicCard(`<h3 class="margin-0 color-var(--ec-text-muted,_#6c757d)">Inventory Items</h3><p class="fontSize-32px fontWeight-bold margin-10px_0">${res.inventoryCount}</p>`));
        grid.addItem(new ECBasicCard(`<h3 class="margin-0 color-var(--ec-text-muted,_#6c757d)">System Status</h3><p class="fontSize-18px fontWeight-bold margin-10px_0 color-#2e7d32">All Modules Online</p>`));
    } else {
        grid.addItem(new ECBasicCard(`<h3>SuperAdmin Hub</h3><p>Active Tenants: ${res.companies.length}</p>`));
    }
    
    container.appendChild(grid.element);
}

function renderChat(container) {
    const isGlobal = new ECToggle("Global Chat Mode", false);
    const chatBox = document.createElement('div');
    chatBox.className = "height-400px overflowY-auto border-1px_solid_var(--ec-border,_#dee2e6) padding-16px borderRadius-8px background-#fff marginBottom-16px display-flex flexDirection-column gap-12px";
    
    const input = new ECTextbox({placeholder: "Type a message..."});
    
    const loadChats = async () => {
        chatBox.innerHTML = '';
        const msgs = await apiCall(`/chat?global=${isGlobal.getValue()}`);
        msgs.forEach(m => {
            const bubble = document.createElement('div');
            bubble.className = "padding-8px_12px borderRadius-8px background-var(--ec-surface,_#f8f9fa) width-fit-content maxWidth-70%";
            bubble.innerHTML = `<span class="fontSize-12px fontWeight-bold color-var(--ec-accent,_#1a73e8)">${m.Sender}</span><br>${m.Message}`;
            chatBox.appendChild(bubble);
        });
        chatBox.scrollTop = chatBox.scrollHeight;
    };

    isGlobal.onChange(() => loadChats());

    input.onEnter(async (val) => {
        if(!val) return;
        await apiCall('/chat', 'POST', { message: val, global: isGlobal.getValue() });
        input.setValue('');
        loadChats();
    });

    container.appendChild(new ECBasicCard().append(isGlobal).append(new ECDivider()).append(chatBox).append(input).element);
    loadChats();
}

async function renderCRUD(moduleEndpoint, title, columns) {
    const wrapper = document.createElement('div');
    wrapper.className = "display-flex flexDirection-column gap-16px";
    
    const header = document.createElement('div');
    header.className = "display-flex justifyContent-space-between alignItems-center";
    header.innerHTML = `<h2 class="margin-0">${title}</h2>`;
    
    const addBtn = new ECButton("Add Record", {variant: "filled"}).onClick(() => showAddModal(moduleEndpoint, columns, refreshData));
    header.appendChild(addBtn.element);
    
    const table = new ECDataTable({ columns, pageSize: 10 });
    
    wrapper.appendChild(header);
    wrapper.appendChild(table.element);
    contentArea.appendChild(wrapper);

    const refreshData = async () => {
        const data = await apiCall(`/data/${moduleEndpoint}`);
        table.setData(data);
    };

    refreshData();
}

function showAddModal(module, columns, onSuccess) {
    const modal = new ECModal(`New ${module} Record`);
    const formFields = {};
    
    columns.forEach(col => {
        const input = new ECTextbox({label: col.label || col.key, placeholder: `Enter ${col.key}`});
        formFields[col.key] = input;
        modal.addContent(input.element);
        modal.addContent(document.createElement('br'));
    });

    modal.addFooterButton("Save", async () => {
        const payload = {};
        columns.forEach(col => payload[col.key] = formFields[col.key].getValue());
        
        const res = await apiCall(`/data/${module}`, 'POST', payload);
        if (res.success) {
            new ECToast("Record added successfully!", {type: "success"}).show();
            modal.close();
            onSuccess();
        } else {
            new ECToast("Failed to add record", {type: "error"}).show();
        }
    }, "filled");

    document.body.appendChild(modal.element);
    modal.open();
}

initApp();