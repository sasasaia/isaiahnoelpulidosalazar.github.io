const API_BASE = 'https://isaiahnoelpulidosalazar-github-io-erp.onrender.com/api';

function showLoader() { document.getElementById('global-loader').classList.replace('opacity-0', 'opacity-1'); }
function hideLoader() { document.getElementById('global-loader').classList.replace('opacity-1', 'opacity-0'); }

document.getElementById('spinner-container').appendChild(new ECSpinner({size: "sm"}).element);

async function apiCall(endpoint, method = 'GET', body = null) {
    const token = localStorage.getItem('erp_token');
    const activeCompanyId = localStorage.getItem('erp_active_company');
    
    const headers = { 'Content-Type': 'application/json' };
    if (token) headers['Authorization'] = `Bearer ${token}`;
    if (activeCompanyId) headers['x-company-id'] = activeCompanyId;

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
    showLoader();
    const setupRes = await apiCall('/check-setup');
    if (!setupRes.isSetup) { hideLoader(); return renderSetup(); }
    if (localStorage.getItem('erp_token')) { await setupWorkspace(); hideLoader(); } 
    else { hideLoader(); renderLogin(); }
}

function logout() { localStorage.clear(); location.reload(); }

function toggleView(view) {
    document.getElementById('login-root').classList.remove('display-flex', 'display-none');
    document.getElementById('app-layout').classList.remove('display-flex', 'display-none');
    
    if (view === 'login') {
        document.getElementById('login-root').classList.add('display-flex');
        document.getElementById('app-layout').classList.add('display-none');
    } else {
        document.getElementById('login-root').classList.add('display-none');
        document.getElementById('app-layout').classList.add('display-flex');
    }
}

function renderSetup() {
    toggleView('login');
    const root = document.getElementById('login-root');
    root.innerHTML = '';
    const hero = new ECHero({ title: "Initialize System", subtitle: "Set up the SuperAdmin profile.", eyebrow: "LINE ERP SETUP" });
    const box = new ECBasicCard();
    const user = new ECTextbox({placeholder: "Admin Username"});
    const pass = new ECTextbox({placeholder: "Password", type: "password"});
    const btn = new ECButton("Initialize").onClick(async () => {
        const res = await apiCall('/setup', 'POST', { username: user.getValue(), password: pass.getValue() });
        if (res.success) { new ECToast("Setup Complete!", {type: "success"}).show(); initApp(); }
    });
    box.append(user).append(pass).append(btn);
    hero.element.appendChild(box.element);
    root.appendChild(hero.element);
    window.ECStyleSheet.scan();
}

function renderLogin() {
    toggleView('login');
    const root = document.getElementById('login-root');
    root.innerHTML = '';
    const hero = new ECHero({ title: "Line ERP Platform", subtitle: "Enter your credentials to access your workspace.", eyebrow: "WELCOME BACK", background: "linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%)" });
    const box = new ECBasicCard();
    box.element.classList.add("boxShadow-0_10px_25px_rgba(0,0,0,0.1)", "minWidth-320px", "display-flex", "flexDirection-column", "gap-16px");
    
    const user = new ECTextbox({placeholder: "Username", label: "Account Username"});
    const pass = new ECTextbox({placeholder: "Password", type: "password", label: "Password"});
    const btn = new ECButton("Secure Login", {variant: "filled"}).onClick(async () => {
        showLoader();
        const res = await apiCall('/login', 'POST', { username: user.getValue(), password: pass.getValue() });
        if (res.token) {
            localStorage.setItem('erp_token', res.token);
            localStorage.setItem('erp_role', res.role);
            localStorage.setItem('erp_username', res.username);
            new ECToast("Login Successful", {type: "success"}).show();
            initApp();
        } else {
            hideLoader();
            new ECToast(res.error || "Login Failed", {type: "error"}).show();
        }
    });

    box.append(user).append(pass).append(btn);
    hero.element.appendChild(box.element);
    root.appendChild(hero.element);
    window.ECStyleSheet.scan();
}

async function setupWorkspace() {
    toggleView('app');
    document.getElementById('user-name-display').textContent = localStorage.getItem('erp_username');
    document.getElementById('user-initials').textContent = localStorage.getItem('erp_username').charAt(0).toUpperCase();
    
    const role = localStorage.getItem('erp_role');
    const nav = document.getElementById('sidebar-nav');
    nav.innerHTML = '';

    const addNavItem = (icon, label, action) => {
        const btn = document.createElement('button');
        btn.className = "width-100% display-flex alignItems-center gap-12px padding-10px_16px background-transparent border-none color-#94a3b8 cursor-pointer borderRadius-8px transition-0.2s hover:background-#1e293b hover:color-#ffffff textAlign-left fontSize-14px fontWeight-500";
        btn.innerHTML = `<span class="fontSize-18px">${icon}</span> ${label}`;
        btn.onclick = () => { document.getElementById('topbar-title').textContent = label; action(); };
        nav.appendChild(btn);
    };

    addNavItem('📊', 'Dashboard', renderDashboard);
    
    if (role === 'SuperAdmin') {
        addNavItem('🏢', 'Tenants & Companies', renderCompaniesView);
        await setupContextSwitcher();
    } else if (role === 'CompanyAdmin') {
        addNavItem('👥', 'Manage Employees', () => showAddUserModal());
    }

    addNavItem('💬', 'Communications', renderChat);
    addNavItem('📦', 'Inventory & Logistics', () => renderCRUD('inventory', 'Inventory',[
        {key:'ItemName', label:'Item Name', type:'text'}, 
        {key:'SKU', label:'SKU', type:'text'}, 
        {key:'Quantity', label:'Quantity', type:'text'}, 
        {key:'Price', label:'Unit Price', type:'text'}
    ]));
    addNavItem('📈', 'Sales Orders', () => renderCRUD('sales', 'Sales Orders',[
        {key:'Customer', label:'Customer Name', type:'text'}, 
        {key:'Amount', label:'Amount', type:'text'}, 
        {key:'Status', label:'Status', type:'dropdown', options:[{label:'Pending', value:'Pending'}, {label:'Paid', value:'Paid'}, {label:'Shipped', value:'Shipped'}]}
    ]));
    addNavItem('👨‍💼', 'HR Roster', () => renderCRUD('employees', 'Employee Database',[
        {key:'EmployeeName', label:'Name', type:'text'}, 
        {key:'Position', label:'Position', type:'text'}, 
        {key:'ComplianceStatus', label:'Compliance', type:'dropdown', options:[{label:'Cleared', value:'Cleared'}, {label:'Pending', value:'Pending'}]}, 
        {key:'Salary', label:'Salary', type:'text'}
    ]));
    addNavItem('📅', 'Schedule Tracker', renderCalendar);
    addNavItem('💰', 'Transactions', () => renderCRUD('finance', 'Financial Ledger',[
        {key:'Type', label:'Type', type:'dropdown', options:[{label:'Income', value:'Income'}, {label:'Expense', value:'Expense'}, {label:'Payroll', value:'Payroll'}]}, 
        {key:'Description', label:'Description', type:'text'}, 
        {key:'Amount', label:'Amount', type:'text'}
    ]));

    renderDashboard();
    window.ECStyleSheet.scan();
}

async function setupContextSwitcher() {
    const container = document.getElementById('context-switcher-container');
    container.innerHTML = '';
    
    const comps = await apiCall('/companies');
    const dd = new ECDropdown();
    dd.element.classList.add("minWidth-200px");
    dd.addOption("", "Global Scope (All Contexts)");
    comps.forEach(c => dd.addOption(c.Id, c.Name));
    
    const active = localStorage.getItem('erp_active_company');
    if (active) dd.setValue(active);

    dd.onChange((val) => {
        if (val) localStorage.setItem('erp_active_company', val);
        else localStorage.removeItem('erp_active_company');
        renderDashboard();
    });

    container.appendChild(dd.element);
}

const rootArea = () => document.getElementById('app-root');

async function renderDashboard() {
    showLoader();
    const root = rootArea();
    root.innerHTML = '';
    
    const res = await apiCall('/dashboard');
    const grid = new ECGrid({columns: 3, gap: "20px"});
    
    const createStatCard = (title, val, color, icon) => {
        return new ECBasicCard(`
            <div class="display-flex alignItems-center gap-16px">
                <div class="width-48px height-48px borderRadius-12px display-flex alignItems-center justifyContent-center fontSize-24px background-var(--ec-surface,_#f8f9fa)">${icon}</div>
                <div class="display-flex flexDirection-column">
                    <span class="fontSize-13px fontWeight-600 color-#64748b textTransform-uppercase">${title}</span>
                    <span class="fontSize-28px fontWeight-bold color-${color}">${val}</span>
                </div>
            </div>
        `);
    };

    if (res.type === 'company') {
        grid.addItem(createStatCard("Total Sales", `$${res.sales.toFixed(2)}`, "#10b981", "💰"));
        grid.addItem(createStatCard("Total Orders", res.orders, "#3b82f6", "📦"));
        grid.addItem(createStatCard("Currently Online", res.online, "#14b8a6", "🟢"));
        grid.addItem(createStatCard("Active Employees", res.activeEmployees, "#8b5cf6", "👨‍💼"));
        grid.addItem(createStatCard("New Notifications", res.notifications, "#f59e0b", "🔔"));
        grid.addItem(createStatCard("New Chats", res.newChats, "#ec4899", "💬"));
        root.appendChild(grid.element);
    } else {
        grid.addItem(createStatCard("Registered Tenants", res.companies, "#3b82f6", "🏢"));
        grid.addItem(createStatCard("Global Users", res.totalUsers, "#8b5cf6", "🌍"));
        grid.addItem(createStatCard("Currently Online", res.online, "#10b981", "🟢"));
        root.appendChild(grid.element);
    }

    window.ECStyleSheet.scan();
    hideLoader();
}

async function renderCompaniesView() {
    showLoader();
    const root = rootArea();
    root.innerHTML = '';
    
    const header = document.createElement('div');
    header.className = "display-flex justifyContent-space-between alignItems-center marginBottom-24px";
    header.innerHTML = `<h2 class="margin-0 fontSize-20px">Tenant Management</h2>`;
    
    const addBtn = new ECButton("Register New Company", {variant: "filled"}).onClick(() => showAddCompanyModal());
    header.appendChild(addBtn.element);
    root.appendChild(header);

    const comps = await apiCall('/companies');
    const table = new ECDataTable({ columns:[{key:'Id'}, {key:'Name'}, {key:'Type'}], data: comps });

    root.appendChild(table.element);
    window.ECStyleSheet.scan();
    hideLoader();
}

// ---------------- COMMUNICATIONS / CHAT ----------------

async function renderChat() {
    showLoader();
    const root = rootArea();
    root.innerHTML = '';
    
    let activeMode = 'company';
    let activeUser = null;

    // Outer layout container
    const chatContainer = document.createElement('div');
    chatContainer.className = "display-flex gap-20px height-calc(100vh_-_120px) width-100%";

    // Sidebar
    const sidebar = document.createElement('div');
    sidebar.className = "width-260px background-#fff border-1px_solid_#e2e8f0 borderRadius-12px padding-16px display-flex flexDirection-column gap-16px overflowY-auto";
    
    const renderSidebarBtn = (label, icon, mode, user = null, isOnline = false) => {
        const btn = document.createElement('button');
        const isActive = activeMode === mode && activeUser === user;
        btn.className = `width-100% display-flex alignItems-center gap-10px padding-10px_12px border-none borderRadius-8px cursor-pointer transition-0.2s textAlign-left fontSize-14px fontWeight-500 ${isActive ? 'background-#eff6ff color-#2563eb' : 'background-transparent color-#475569 hover:background-#f8fafc'}`;
        
        btn.innerHTML = `<span class="fontSize-18px">${icon}</span> <span class="flex-1">${label}</span> ${isOnline ? '<span class="width-8px height-8px borderRadius-50% background-#10b981"></span>' : ''}`;
        
        btn.onclick = () => {
            activeMode = mode; activeUser = user;
            renderChatUI(); // Re-render everything to show selected state
        };
        sidebar.appendChild(btn);
    };

    const renderChatUI = async () => {
        sidebar.innerHTML = '<h3 class="fontSize-12px fontWeight-bold color-#94a3b8 textTransform-uppercase margin-0">Channels</h3>';
        
        renderSidebarBtn('Global Network', '🌍', 'global');
        renderSidebarBtn('Company Chat', '🏢', 'company');
        
        sidebar.insertAdjacentHTML('beforeend', '<h3 class="fontSize-12px fontWeight-bold color-#94a3b8 textTransform-uppercase marginTop-16px marginBottom-0">Direct Messages</h3>');
        
        const users = await apiCall('/chat/users');
        users.forEach(u => {
            const isOnline = (new Date() - new Date(u.LastActive)) < 15 * 60 * 1000; // Online if active within 15 mins
            renderSidebarBtn(u.Username, '👤', 'private', u.Username, isOnline);
        });

        loadMessages();
    };

    // Chat Area
    const chatArea = document.createElement('div');
    chatArea.className = "flex-1 background-#fff border-1px_solid_#e2e8f0 borderRadius-12px display-flex flexDirection-column position-relative overflow-hidden";

    const chatHeader = document.createElement('div');
    chatHeader.className = "padding-16px borderBottom-1px_solid_#e2e8f0 background-#f8fafc fontWeight-bold color-#1e293b";
    
    const messageList = document.createElement('div');
    messageList.className = "flex-1 padding-20px overflowY-auto display-flex flexDirection-column gap-16px";

    const inputWrap = document.createElement('div');
    inputWrap.className = "padding-16px borderTop-1px_solid_#e2e8f0 background-#fff display-flex gap-12px";
    
    const input = new ECTextbox({placeholder: "Type a message..."});
    input.element.classList.add('flex-1');
    const sendBtn = new ECButton("Send", {variant:"filled"}).onClick(sendMessage);

    inputWrap.appendChild(input.element);
    inputWrap.appendChild(sendBtn.element);
    
    chatArea.appendChild(chatHeader);
    chatArea.appendChild(messageList);
    chatArea.appendChild(inputWrap);

    async function loadMessages() {
        chatHeader.innerHTML = activeMode === 'private' ? `Private Chat with ${activeUser}` : 
                               activeMode === 'global' ? 'Global Network Chat' : 'Company General Chat';
        
        messageList.innerHTML = '<div class="color-#94a3b8 fontSize-13px textAlign-center">Loading history...</div>';
        const msgs = await apiCall(`/chat?mode=${activeMode}${activeUser ? `&user=${activeUser}` : ''}`);
        
        messageList.innerHTML = '';
        if(msgs.length === 0) messageList.innerHTML = '<div class="color-#94a3b8 fontSize-13px textAlign-center marginTop-20px">No messages yet. Start the conversation!</div>';

        msgs.forEach(m => {
            const isMe = m.Sender === localStorage.getItem('erp_username');
            const wrap = document.createElement('div');
            wrap.className = `display-flex ${isMe ? 'justifyContent-flex-end' : 'justifyContent-flex-start'}`;
            
            const bubble = document.createElement('div');
            bubble.className = `padding-10px_14px borderRadius-12px maxWidth-70% fontSize-14px ${isMe ? 'background-#3b82f6 color-#ffffff' : 'background-#f1f5f9 color-#1e293b'}`;
            bubble.innerHTML = `<div class="fontSize-11px fontWeight-bold marginBottom-4px ${isMe ? 'color-#bfdbfe' : 'color-#64748b'}">${m.Sender}</div>${m.Message}`;
            
            wrap.appendChild(bubble);
            messageList.appendChild(wrap);
        });
        messageList.scrollTop = messageList.scrollHeight;
    }

    async function sendMessage() {
        const val = input.getValue();
        if(!val) return;
        await apiCall('/chat', 'POST', { mode: activeMode, message: val, receiver: activeUser });
        input.setValue('');
        loadMessages();
    }

    input.onEnter(sendMessage);

    chatContainer.appendChild(sidebar);
    chatContainer.appendChild(chatArea);
    root.appendChild(chatContainer);
    
    await renderChatUI();
    window.ECStyleSheet.scan();
    hideLoader();
}

// ---------------- CALENDAR / SCHEDULES ----------------

async function renderCalendar() {
    showLoader();
    const root = rootArea();
    root.innerHTML = '';
    
    const header = document.createElement('div');
    header.className = "display-flex justifyContent-space-between alignItems-center marginBottom-24px";
    header.innerHTML = `<h2 class="margin-0 fontSize-20px">Monthly Schedule Tracker</h2>`;
    
    const addBtn = new ECButton("Add Shift", {variant: "filled"}).onClick(() => {
        showAddModal('schedules',[
            {key:'EmployeeName', label:'Employee', type:'text'},
            {key:'ShiftStart', label:'Start Time (YYYY-MM-DD)', type:'date'},
            {key:'ShiftEnd', label:'End Time (YYYY-MM-DD)', type:'date'},
            {key:'Status', label:'Status', type:'dropdown', options:[{label:'Scheduled',value:'Scheduled'}, {label:'Completed',value:'Completed'}]}
        ], renderCalendar);
    });
    header.appendChild(addBtn.element);
    root.appendChild(header);

    const schedules = await apiCall('/data/schedules');
    if (schedules.error) { new ECToast(schedules.error, {type:"error"}).show(); hideLoader(); return; }
    
    const now = new Date();
    const year = now.getFullYear();
    const month = now.getMonth();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const firstDay = new Date(year, month, 1).getDay();

    const calCard = new ECBasicCard();
    calCard.element.classList.add("padding-0", "overflow-hidden");
    
    const calGrid = document.createElement('div');
    calGrid.className = "display-grid gridTemplateColumns-repeat(7,_1fr) borderTop-1px_solid_#e2e8f0 borderLeft-1px_solid_#e2e8f0";

    const days =['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
    days.forEach(d => {
        const el = document.createElement('div');
        el.className = "padding-12px background-#f8fafc borderBottom-1px_solid_#e2e8f0 borderRight-1px_solid_#e2e8f0 textAlign-center fontWeight-600 fontSize-14px color-#475569";
        el.textContent = d;
        calGrid.appendChild(el);
    });

    for(let i=0; i<firstDay; i++) {
        const el = document.createElement('div');
        el.className = "background-#f1f5f9 borderBottom-1px_solid_#e2e8f0 borderRight-1px_solid_#e2e8f0 minHeight-100px";
        calGrid.appendChild(el);
    }

    for(let d=1; d<=daysInMonth; d++) {
        const cell = document.createElement('div');
        cell.className = "padding-8px borderBottom-1px_solid_#e2e8f0 borderRight-1px_solid_#e2e8f0 minHeight-100px background-#ffffff";
        cell.innerHTML = `<div class="fontSize-14px fontWeight-500 color-#94a3b8 marginBottom-8px">${d}</div>`;
        
        const dayShifts = schedules.filter(s => {
            const shiftDate = new Date(s.ShiftStart);
            return shiftDate.getDate() === d && shiftDate.getMonth() === month && shiftDate.getFullYear() === year;
        });

        dayShifts.forEach(shift => {
            const badge = new ECBadge(shift.EmployeeName, shift.Status === 'Completed' ? 'success' : 'primary');
            badge.element.classList.add("marginBottom-4px", "display-block", "width-fit-content");
            cell.appendChild(badge.element);
        });

        calGrid.appendChild(cell);
    }

    calCard.element.appendChild(calGrid);
    root.appendChild(calCard.element);
    
    window.ECStyleSheet.scan();
    hideLoader();
}

async function renderCRUD(moduleEndpoint, title, columns) {
    showLoader();
    const root = rootArea();
    root.innerHTML = '';

    const header = document.createElement('div');
    header.className = "display-flex justifyContent-space-between alignItems-center marginBottom-24px";
    header.innerHTML = `<h2 class="margin-0 fontSize-20px">${title}</h2>`;
    
    const addBtn = new ECButton("Add Record", {variant: "filled"}).onClick(() => showAddModal(moduleEndpoint, columns, () => renderCRUD(moduleEndpoint, title, columns)));
    header.appendChild(addBtn.element);
    root.appendChild(header);
    
    const renderCols = columns.map(c => {
        if(c.key === 'Status' || c.key === 'ComplianceStatus') {
            return { ...c, render: (v) => new ECBadge(v, (v==='Paid' || v==='Cleared' || v==='Shipped') ? 'success' : 'warning').element };
        }
        return c;
    });

    const table = new ECDataTable({ columns: renderCols, pageSize: 15 });
    root.appendChild(table.element);

    const data = await apiCall(`/data/${moduleEndpoint}`);
    if (data.error) { 
        new ECToast(data.error, {type:"error"}).show(); 
        hideLoader(); 
        return; 
    }
    
    table.setData(data);
    window.ECStyleSheet.scan();
    hideLoader();
}

function showAddModal(module, columns, onSuccess) {
    const modal = new ECModal(`New Entry`);
    const formFields = {};
    
    columns.forEach(col => {
        if (col.type === 'dropdown') {
            const dd = new ECDropdown({label: col.label});
            col.options.forEach(o => dd.addOption(o.value, o.label));
            formFields[col.key] = dd;
            modal.addContent(dd.element);
        } else if (col.type === 'date') {
            const dp = new ECDatePicker({label: col.label});
            formFields[col.key] = dp;
            modal.addContent(dp.element);
        } else {
            const tb = new ECTextbox({label: col.label, placeholder: `Enter ${col.label}`});
            formFields[col.key] = tb;
            modal.addContent(tb.element);
        }
        modal.addContent(document.createElement('br'));
    });

    modal.addFooterButton("Save Record", async () => {
        showLoader();
        const payload = {};
        columns.forEach(col => payload[col.key] = formFields[col.key].getValue());
        
        const res = await apiCall(`/data/${module}`, 'POST', payload);
        if (res.success) {
            new ECToast("Record added successfully!", {type: "success"}).show();
            modal.close();
            onSuccess();
        } else {
            new ECToast(res.error || "Failed to add record", {type: "error"}).show();
            hideLoader();
        }
    }, "filled");

    document.body.appendChild(modal.element);
    modal.open();
    window.ECStyleSheet.scan();
}

function showAddCompanyModal() {
    const modal = new ECModal("Register New Tenant");
    const name = new ECTextbox({label: "Company Name"});
    const type = new ECDropdown({label: "Industry Type"});
    type.addOption("Manufacturing", "Manufacturing").addOption("Retail", "Retail").addOption("Tech", "Tech");
    const user = new ECTextbox({label: "Admin Username"});
    const pass = new ECTextbox({label: "Temporary Password", type: "password"});

    modal.addContent(name.element).addContent(document.createElement('br'))
         .addContent(type.element).addContent(document.createElement('br'))
         .addContent(user.element).addContent(document.createElement('br'))
         .addContent(pass.element);

    modal.addFooterButton("Register", async () => {
        const res = await apiCall('/companies', 'POST', { name: name.getValue(), type: type.getValue(), adminUsername: user.getValue(), adminPassword: pass.getValue() });
        if(res.success) {
            new ECToast("Tenant created.", {type:"success"}).show();
            modal.close();
            renderCompaniesView();
            setupContextSwitcher();
        }
    });

    document.body.appendChild(modal.element);
    modal.open();
    window.ECStyleSheet.scan();
}

function showAddUserModal() {
    const modal = new ECModal("Add Employee Account");
    const user = new ECTextbox({label: "Username"});
    const pass = new ECTextbox({label: "Password", type: "password"});
    const role = new ECDropdown({label: "Role"});
    role.addOption("Employee", "Standard Employee").addOption("Manager", "Manager");

    modal.addContent(user.element).addContent(document.createElement('br'))
         .addContent(pass.element).addContent(document.createElement('br'))
         .addContent(role.element);

    modal.addFooterButton("Create User", async () => {
        const res = await apiCall('/users', 'POST', { username: user.getValue(), password: pass.getValue(), role: role.getValue() });
        if(res.success) {
            new ECToast("User created.", {type:"success"}).show();
            modal.close();
        }
    });

    document.body.appendChild(modal.element);
    modal.open();
    window.ECStyleSheet.scan();
}

initApp();