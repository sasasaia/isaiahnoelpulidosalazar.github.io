const API_URL = 'https://isaiahnoelpulidosalazar-github-io-pos.onrender.com/api';

let auth = JSON.parse(localStorage.getItem('linepos_auth') || 'null');

function showLoader() { document.getElementById('global-loader').classList.replace('opacity-0', 'opacity-1'); }
function hideLoader() { document.getElementById('global-loader').classList.replace('opacity-1', 'opacity-0'); }

document.getElementById('spinner-container').appendChild(new ECSpinner({size: "sm"}).element);

async function apiCall(endpoint, method = 'GET', body = null) {
    const headers = { 'Content-Type': 'application/json' };
    if (auth && auth.token) headers['Authorization'] = `Bearer ${auth.token}`;
    
    const res = await fetch(`${API_URL}${endpoint}`, { 
        method, 
        headers, 
        body: body ? JSON.stringify(body) : null 
    });
    
    const data = await res.json();
    if (!res.ok) throw new Error(data.error || 'API Error');
    return data;
}

function init() {
    if (!auth) {
        hideLoader();
        renderLogin();
    } else {
        setupWorkspace();
        hideLoader();
    }
}

function logout() {
    localStorage.removeItem('linepos_auth');
    auth = null;
    init();
}

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

function renderLogin() {
    toggleView('login');
    const root = document.getElementById('login-root');
    root.innerHTML = '';
    
    const hero = new ECHero({ title: "Line POS Platform", subtitle: "Enter your credentials to access your terminal.", eyebrow: "WELCOME BACK", background: "linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%)" });
    const box = new ECBasicCard();
    box.element.classList.add("boxShadow-0_10px_25px_rgba(0,0,0,0.1)", "minWidth-320px", "display-flex", "flexDirection-column", "gap-16px");
    
    const userIn = new ECTextbox({placeholder: "Username", label: "Account Username"});
    const passIn = new ECTextbox({placeholder: "Password", type: "password", label: "Password"});
    
    const btn = new ECButton("Secure Login", {variant: "filled"}).onClick(async () => {
        showLoader();
        try {
            const res = await apiCall('/login', 'POST', { username: userIn.getValue(), password: passIn.getValue() });
            auth = res;
            localStorage.setItem('linepos_auth', JSON.stringify(auth));
            new ECToast("Login Successful", {type: "success"}).show();
            init();
        } catch (e) {
            new ECToast(e.message, {type: "error"}).show();
            hideLoader();
        }
    });

    box.append(userIn).append(passIn).append(btn);
    hero.element.appendChild(box.element);
    root.appendChild(hero.element);
    window.ECStyleSheet.scan();
}

function setupWorkspace() {
    toggleView('app');
    
    document.getElementById('user-name-display').textContent = auth.username;
    document.getElementById('user-initials').textContent = auth.username.charAt(0).toUpperCase();
    document.getElementById('store-display').textContent = auth.role === 'SuperAdmin' ? 'Global Admin Scope' : `Store ID: ${auth.storeId}`;

    const nav = document.getElementById('sidebar-nav');
    nav.innerHTML = '';

    const addNavItem = (icon, label, action) => {
        const btn = document.createElement('button');
        btn.className = "width-100% display-flex alignItems-center gap-12px padding-10px_16px background-transparent border-none color-#94a3b8 cursor-pointer borderRadius-8px transition-0.2s hover:background-#1e293b hover:color-#ffffff textAlign-left fontSize-14px fontWeight-500";
        btn.innerHTML = `<span class="fontSize-18px">${icon}</span> ${label}`;
        btn.onclick = () => { document.getElementById('topbar-title').textContent = label; action(); };
        nav.appendChild(btn);
    };

    if (auth.role === 'SuperAdmin') {
        addNavItem('🏢', 'Store Instances', renderSuperAdmin);
        document.getElementById('topbar-title').textContent = 'Store Instances';
        renderSuperAdmin();
    } else if (auth.role === 'StoreAdmin') {
        addNavItem('💻', 'Point of Sale', renderPOS);
        addNavItem('📦', 'Inventory', renderInventory);
        addNavItem('👥', 'Manage Staff', renderStaff);
        document.getElementById('topbar-title').textContent = 'Point of Sale';
        renderPOS();
    } else {
        addNavItem('💻', 'Point of Sale', renderPOS);
        document.getElementById('topbar-title').textContent = 'Point of Sale';
        renderPOS();
    }
    
    window.ECStyleSheet.scan();
}

const rootArea = () => document.getElementById('app-root');

async function renderSuperAdmin() {
    showLoader();
    const root = rootArea();
    root.innerHTML = '';

    const header = document.createElement('div');
    header.className = "display-flex justifyContent-space-between alignItems-center marginBottom-24px";
    header.innerHTML = `<h2 class="margin-0 fontSize-20px">Store Instances</h2>`;
    
    const addBtn = new ECButton("Create Instance", {variant: "filled"}).onClick(() => modal.open());
    header.appendChild(addBtn.element);
    
    const tableContainer = document.createElement('div');
    root.appendChild(header);
    root.appendChild(tableContainer);

    const modal = new ECModal("Create New Store Instance");
    const nameIn = new ECTextbox({ label: "Store Name" });
    const adminUserIn = new ECTextbox({ label: "Admin Username" });
    const adminPassIn = new ECTextbox({ label: "Admin Password", type: "password" });
    
    const form = document.createElement('div');
    form.className = "display-flex flexDirection-column gap-12px";
    form.appendChild(nameIn.element); form.appendChild(adminUserIn.element); form.appendChild(adminPassIn.element);
    modal.setContent(form);
    
    modal.addFooterButton("Create", async () => {
        showLoader();
        try {
            await apiCall('/stores', 'POST', { name: nameIn.getValue(), adminUsername: adminUserIn.getValue(), adminPassword: adminPassIn.getValue() });
            new ECToast("Instance Created!", {type: "success"}).show();
            modal.close();
            nameIn.setValue(''); adminUserIn.setValue(''); adminPassIn.setValue('');
            loadStores();
        } catch (e) { 
            new ECToast(e.message, {type: "error"}).show(); 
            hideLoader(); 
        }
    }, "filled");
    document.body.appendChild(modal.element);

    async function loadStores() {
        try {
            const stores = await apiCall('/stores');
            const table = new ECDataTable({ columns:[{ key: 'Id', label: 'ID' }, { key: 'Name', label: 'Store Name' }], data: stores });
            tableContainer.innerHTML = '';
            tableContainer.appendChild(table.element);
            window.ECStyleSheet.scan();
        } catch (e) { 
            new ECToast("Failed to load instances", {type:"error"}).show(); 
        }
        hideLoader();
    }
    loadStores();
}

async function renderInventory() {
    showLoader();
    const root = rootArea();
    root.innerHTML = '';
    
    const header = document.createElement('div');
    header.className = "display-flex justifyContent-space-between alignItems-center marginBottom-24px";
    header.innerHTML = `<h2 class="margin-0 fontSize-20px">Inventory Management</h2>`;
    
    const addBtn = new ECButton("Add Item", {variant: "filled"}).onClick(() => modal.open());
    header.appendChild(addBtn.element);
    
    const tableContainer = document.createElement('div');
    root.appendChild(header);
    root.appendChild(tableContainer);

    const modal = new ECModal("Add Inventory Item");
    const nameIn = new ECTextbox({ label: "Item Name" });
    const skuIn = new ECTextbox({ label: "SKU" });
    const priceIn = new ECTextbox({ label: "Price", type: "number" });
    const stockIn = new ECTextbox({ label: "Stock Quantity", type: "number" });
    
    const form = document.createElement('div');
    form.className = "display-flex flexDirection-column gap-12px";
    form.append(nameIn.element, skuIn.element, priceIn.element, stockIn.element);
    modal.setContent(form);
    
    modal.addFooterButton("Save Item", async () => {
        showLoader();
        try {
            await apiCall('/inventory', 'POST', { 
                ItemName: nameIn.getValue(), SKU: skuIn.getValue(), 
                Price: parseFloat(priceIn.getValue()), Stock: parseInt(stockIn.getValue()) 
            });
            new ECToast("Item Saved!", {type: "success"}).show();
            modal.close();
            nameIn.setValue(''); skuIn.setValue(''); priceIn.setValue(''); stockIn.setValue('');
            loadInventory();
        } catch (e) { 
            new ECToast(e.message, {type: "error"}).show(); 
            hideLoader(); 
        }
    }, "filled");
    document.body.appendChild(modal.element);

    async function loadInventory() {
        try {
            const items = await apiCall('/inventory');
            const table = new ECDataTable({ 
                columns:[{ key: 'SKU' }, { key: 'ItemName' }, { key: 'Price', render: (v) => `$${v.toFixed(2)}` }, { key: 'Stock' }], 
                data: items 
            });
            tableContainer.innerHTML = '';
            tableContainer.appendChild(table.element);
            window.ECStyleSheet.scan();
        } catch (e) { 
            new ECToast("Failed to load inventory", {type:"error"}).show(); 
        }
        hideLoader();
    }
    loadInventory();
}

async function renderStaff() {
    showLoader();
    const root = rootArea();
    root.innerHTML = '';

    const header = document.createElement('div');
    header.className = "display-flex justifyContent-space-between alignItems-center marginBottom-24px";
    header.innerHTML = `<h2 class="margin-0 fontSize-20px">Store Staff</h2>`;
    
    const addBtn = new ECButton("Add User", {variant: "filled"}).onClick(() => modal.open());
    header.appendChild(addBtn.element);
    
    const tableContainer = document.createElement('div');
    root.appendChild(header);
    root.appendChild(tableContainer);

    const modal = new ECModal("Add Cashier/Admin");
    const userIn = new ECTextbox({ label: "Username" });
    const passIn = new ECTextbox({ label: "Password", type: "password" });
    const roleIn = new ECDropdown({ label: "Role" });
    roleIn.addOption("Cashier", "Cashier").addOption("StoreAdmin", "StoreAdmin");
    
    const form = document.createElement('div');
    form.className = "display-flex flexDirection-column gap-12px";
    form.append(userIn.element, passIn.element, roleIn.element);
    modal.setContent(form);
    
    modal.addFooterButton("Save User", async () => {
        showLoader();
        try {
            await apiCall('/users', 'POST', { username: userIn.getValue(), password: passIn.getValue(), role: roleIn.getValue() });
            new ECToast("User Created!", {type: "success"}).show();
            modal.close();
            userIn.setValue(''); passIn.setValue('');
            loadStaff();
        } catch (e) { 
            new ECToast(e.message, {type: "error"}).show(); 
            hideLoader(); 
        }
    }, "filled");
    document.body.appendChild(modal.element);

    async function loadStaff() {
        try {
            const users = await apiCall('/users');
            const table = new ECDataTable({ columns:[{ key: 'Id' }, { key: 'Username' }, { key: 'Role' }], data: users });
            tableContainer.innerHTML = '';
            tableContainer.appendChild(table.element);
            window.ECStyleSheet.scan();
        } catch (e) { 
            new ECToast("Failed to load staff", {type:"error"}).show(); 
        }
        hideLoader();
    }
    loadStaff();
}

function renderPOS() {
    showLoader();
    const root = rootArea();
    root.innerHTML = '';

    let cart = [];
    let inventoryData = [];

    const posWrap = document.createElement('div');
    posWrap.className = "display-grid gridTemplateColumns-3fr_1.5fr gap-24px alignItems-flex-start";
    
    const itemGrid = document.createElement('div');
    itemGrid.className = "display-grid gap-16px gridTemplateColumns-repeat(auto-fill,_minmax(200px,_1fr))";
    
    const cartPanel = document.createElement('div');
    cartPanel.className = "background-#ffffff border-1px_solid_#e2e8f0 borderRadius-12px padding-24px position-sticky top-0";
    
    const cartTitle = document.createElement('h3');
    cartTitle.className = "margin-0_0_16px fontSize-18px color-#1e293b";
    cartTitle.textContent = "Current Order";

    const cartList = document.createElement('div');
    cartList.className = "display-flex flexDirection-column gap-12px minHeight-200px maxHeight-400px overflowY-auto marginBottom-16px";
    
    const totalEl = document.createElement('h2');
    totalEl.className = "margin-0_0_16px borderTop-1px_solid_#e2e8f0 paddingTop-16px textAlign-right color-#3b82f6 fontSize-24px";
    totalEl.textContent = "Total: $0.00";
    
    const checkoutBtn = new ECButton("Process Checkout", { variant: "filled" }).onClick(async () => {
        if (cart.length === 0) return new ECToast("Cart is empty", {type: "warning"}).show();
        showLoader();
        try {
            checkoutBtn.disable();
            await apiCall('/sales', 'POST', { items: cart });
            new ECToast("Transaction Successful!", {type: "success"}).show();
            
            cart = [];
            renderCart();
            loadInventory();
        } catch (e) { 
            new ECToast(e.message, {type: "error"}).show(); 
        } finally {
            checkoutBtn.enable();
            hideLoader();
        }
    });
    checkoutBtn.element.classList.add('width-100%');

    cartPanel.append(cartTitle, cartList, totalEl, checkoutBtn.element);
    posWrap.append(itemGrid, cartPanel);
    root.appendChild(posWrap);

    function renderCart() {
        cartList.innerHTML = '';
        let total = 0;
        cart.forEach((item, index) => {
            const row = document.createElement('div');
            row.className = "display-flex justifyContent-space-between alignItems-center fontSize-14px borderBottom-1px_solid_#f1f5f9 paddingBottom-8px";
            
            const info = document.createElement('div');
            info.innerHTML = `<span class="fontWeight-600 color-#1e293b">${item.ItemName}</span><br><span class="color-#64748b">${item.Quantity} x $${item.Price.toFixed(2)}</span>`;
            
            const removeBtn = document.createElement('button');
            removeBtn.className = "background-none border-none color-#ef4444 cursor-pointer fontSize-18px hover:color-#dc2626";
            removeBtn.innerHTML = "&times;";
            removeBtn.onclick = () => {
                cart.splice(index, 1);
                renderCart();
            };

            row.append(info, removeBtn);
            cartList.appendChild(row);
            total += item.Price * item.Quantity;
        });
        totalEl.textContent = `Total: $${total.toFixed(2)}`;
    }

    async function loadInventory() {
        try {
            inventoryData = await apiCall('/inventory');
            itemGrid.innerHTML = '';
            inventoryData.forEach(item => {
                const card = new ECBasicCard();
                card.element.classList.add('cursor-pointer', 'transition-0.2s', 'hover:boxShadow-0_4px_12px_rgba(0,0,0,0.1)');
                
                const content = document.createElement('div');
                content.className = "textAlign-center";
                content.innerHTML = `
                    <div class="fontSize-14px fontWeight-600 color-#1e293b marginBottom-4px">${item.ItemName}</div>
                    <div class="fontSize-20px fontWeight-700 color-#3b82f6">$${item.Price.toFixed(2)}</div>
                    <div class="fontSize-12px color-#64748b marginTop-4px">Stock: ${item.Stock}</div>
                `;
                
                card.append(content);
                card.element.onclick = () => {
                    const existing = cart.find(i => i.Id === item.Id);
                    const currentQty = existing ? existing.Quantity : 0;
                    if (item.Stock <= currentQty) {
                        return new ECToast("Insufficient stock available", {type: "warning"}).show();
                    }
                    if (existing) {
                        existing.Quantity += 1;
                    } else {
                        cart.push({ ...item, Quantity: 1 });
                    }
                    renderCart();
                };
                itemGrid.appendChild(card.element);
            });
        } catch (e) { 
            new ECToast("Error loading products", {type: "error"}).show(); 
        }
        window.ECStyleSheet.scan();
        hideLoader();
    }
    loadInventory();
}

init();