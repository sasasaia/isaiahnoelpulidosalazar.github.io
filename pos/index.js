const API_URL = 'https://isaiahnoelpulidosalazar-github-io-pos.onrender.com/api';

let auth = JSON.parse(localStorage.getItem('linepos_auth') || 'null');
const appContainer = document.getElementById('app');

async function apiCall(endpoint, method = 'GET', body = null) {
    const headers = { 'Content-Type': 'application/json' };
    if (auth && auth.token) headers['Authorization'] = `Bearer ${auth.token}`;
    const res = await fetch(`${API_URL}${endpoint}`, { method, headers, body: body ? JSON.stringify(body) : null });
    const data = await res.json();
    if (!res.ok) throw new Error(data.error || 'API Error');
    return data;
}

function init() {
    if (!auth) renderLogin();
    else renderMainApp();
}

function logout() {
    localStorage.removeItem('linepos_auth');
    auth = null;
    init();
}

function renderLogin() {
    appContainer.innerHTML = '';
    const card = new ECBasicCard();
    card.element.classList.add('margin-auto', 'marginTop-100px', 'maxWidth-400px');
    
    const title = document.createElement('h2');
    title.textContent = "Line POS - Login";
    title.className = "textAlign-center marginBottom-24px color-var(--ec-text)";
    
    const userIn = new ECTextbox({ label: "Username" });
    const passIn = new ECTextbox({ label: "Password", type: "password" });
    passIn.element.classList.add("marginBottom-20px");

    const btn = new ECButton("Login", { variant: "filled" }).onClick(async () => {
        try {
            const res = await apiCall('/login', 'POST', { username: userIn.getValue(), password: passIn.getValue() });
            auth = res;
            localStorage.setItem('linepos_auth', JSON.stringify(auth));
            new ECToast("Login successful", { type: "success" }).show();
            init();
        } catch (e) {
            new ECToast(e.message, { type: "error" }).show();
        }
    });
    btn.element.classList.add('width-100%');
    
    card.append(title).append(userIn.element).append(passIn.element).append(btn.element);
    appContainer.appendChild(card.element);
}

function renderMainApp() {
    appContainer.innerHTML = '';
    const topbar = new ECTopbar(`Line POS - ${auth.role === 'SuperAdmin' ? 'Global Admin' : 'Store ID: ' + auth.storeId}`);
    
    const userBadge = new ECBadge(`@${auth.username}`, "info");
    const logoutBtn = new ECButton("Logout", { variant: "outline" }).onClick(logout);
    
    topbar.addAction(userBadge.element);
    topbar.addAction(logoutBtn.element);
    appContainer.appendChild(topbar.element);

    const content = document.createElement('div');
    content.className = "padding-20px marginTop-56px";
    appContainer.appendChild(content);

    if (auth.role === 'SuperAdmin') renderSuperAdmin(content);
    else if (auth.role === 'StoreAdmin') renderTabs({ "Point of Sale": renderPOS, "Inventory": renderInventory, "Staff": renderStaff }, content);
    else renderPOS(content); 
}

function renderTabs(tabsMap, container) {
    const tabHeader = document.createElement('div');
    tabHeader.className = "display-flex gap-8px borderBottom-1px_solid_#dee2e6 marginBottom-20px";
    const tabBody = document.createElement('div');

    let activeBtn = null;
    Object.keys(tabsMap).forEach((tabName, idx) => {
        const btn = document.createElement('button');
        btn.className = "padding-12px_24px background-none border-none borderBottom-3px_solid_transparent cursor-pointer fontSize-15px fontWeight-600 transition-all_0.2s color-var(--ec-text-muted)";
        btn.textContent = tabName;
        
        btn.onclick = () => {
            if (activeBtn) {
                activeBtn.classList.replace("borderBottom-3px_solid_var(--ec-accent,_#1a73e8)", "borderBottom-3px_solid_transparent");
                activeBtn.classList.replace("color-var(--ec-text)", "color-var(--ec-text-muted)");
            }
            btn.classList.replace("borderBottom-3px_solid_transparent", "borderBottom-3px_solid_var(--ec-accent,_#1a73e8)");
            btn.classList.replace("color-var(--ec-text-muted)", "color-var(--ec-text)");
            activeBtn = btn;
            tabBody.innerHTML = '';
            tabsMap[tabName](tabBody);
        };
        tabHeader.appendChild(btn);
        if (idx === 0) btn.click();
    });

    container.appendChild(tabHeader);
    container.appendChild(tabBody);
}

async function renderSuperAdmin(container) {
    const header = document.createElement('div');
    header.className = "display-flex justifyContent-space-between marginBottom-16px";
    header.innerHTML = `<h2 class="margin-0">Store Instances</h2>`;
    
    const addBtn = new ECButton("+ Create Instance").onClick(() => modal.open());
    header.appendChild(addBtn.element);
    
    const tableContainer = document.createElement('div');
    container.appendChild(header);
    container.appendChild(tableContainer);

    const modal = new ECModal("Create New Store Instance");
    const nameIn = new ECTextbox({ label: "Store Name" });
    const adminUserIn = new ECTextbox({ label: "Admin Username" });
    const adminPassIn = new ECTextbox({ label: "Admin Password", type: "password" });
    
    const form = document.createElement('div');
    form.className = "display-flex flexDirection-column gap-12px";
    form.appendChild(nameIn.element); form.appendChild(adminUserIn.element); form.appendChild(adminPassIn.element);
    modal.setContent(form);
    
    modal.addFooterButton("Cancel", () => modal.close(), "outline");
    modal.addFooterButton("Create", async () => {
        try {
            await apiCall('/stores', 'POST', { name: nameIn.getValue(), adminUsername: adminUserIn.getValue(), adminPassword: adminPassIn.getValue() });
            new ECToast("Instance Created!", {type: "success"}).show();
            modal.close();
            nameIn.setValue(''); adminUserIn.setValue(''); adminPassIn.setValue('');
            loadStores();
        } catch (e) { new ECToast(e.message, {type: "error"}).show(); }
    });
    document.body.appendChild(modal.element);

    async function loadStores() {
        try {
            const stores = await apiCall('/stores');
            const table = new ECDataTable({ columns:[{ key: 'Id', label: 'ID' }, { key: 'Name', label: 'Store Name' }], data: stores });
            tableContainer.innerHTML = '';
            tableContainer.appendChild(table.element);
        } catch (e) { new ECToast("Failed to load instances", {type:"error"}).show(); }
    }
    loadStores();
}

async function renderInventory(container) {
    const header = document.createElement('div');
    header.className = "display-flex justifyContent-space-between marginBottom-16px";
    header.innerHTML = `<h2 class="margin-0">Inventory Management</h2>`;
    
    const addBtn = new ECButton("+ Add Item").onClick(() => modal.open());
    header.appendChild(addBtn.element);
    
    const tableContainer = document.createElement('div');
    container.appendChild(header);
    container.appendChild(tableContainer);

    const modal = new ECModal("Add Inventory Item");
    const nameIn = new ECTextbox({ label: "Item Name" });
    const skuIn = new ECTextbox({ label: "SKU" });
    const priceIn = new ECTextbox({ label: "Price", type: "number" });
    const stockIn = new ECTextbox({ label: "Stock Quantity", type: "number" });
    
    const form = document.createElement('div');
    form.className = "display-flex flexDirection-column gap-12px";
    form.append(nameIn.element, skuIn.element, priceIn.element, stockIn.element);
    modal.setContent(form);
    
    modal.addFooterButton("Cancel", () => modal.close(), "outline");
    modal.addFooterButton("Save Item", async () => {
        try {
            await apiCall('/inventory', 'POST', { 
                ItemName: nameIn.getValue(), SKU: skuIn.getValue(), 
                Price: parseFloat(priceIn.getValue()), Stock: parseInt(stockIn.getValue()) 
            });
            new ECToast("Item Saved!", {type: "success"}).show();
            modal.close();
            nameIn.setValue(''); skuIn.setValue(''); priceIn.setValue(''); stockIn.setValue('');
            loadInventory();
        } catch (e) { new ECToast(e.message, {type: "error"}).show(); }
    });
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
        } catch (e) { new ECToast("Failed to load inventory", {type:"error"}).show(); }
    }
    loadInventory();
}

async function renderStaff(container) {
    const header = document.createElement('div');
    header.className = "display-flex justifyContent-space-between marginBottom-16px";
    header.innerHTML = `<h2 class="margin-0">Store Staff</h2>`;
    
    const addBtn = new ECButton("+ Add User").onClick(() => modal.open());
    header.appendChild(addBtn.element);
    
    const tableContainer = document.createElement('div');
    container.appendChild(header);
    container.appendChild(tableContainer);

    const modal = new ECModal("Add Cashier/Admin");
    const userIn = new ECTextbox({ label: "Username" });
    const passIn = new ECTextbox({ label: "Password", type: "password" });
    const roleIn = new ECDropdown({ label: "Role", items:[{label:"Cashier", value:"Cashier"}, {label:"StoreAdmin", value:"StoreAdmin"}] });
    
    const form = document.createElement('div');
    form.className = "display-flex flexDirection-column gap-12px";
    form.append(userIn.element, passIn.element, roleIn.element);
    modal.setContent(form);
    
    modal.addFooterButton("Cancel", () => modal.close(), "outline");
    modal.addFooterButton("Save User", async () => {
        try {
            await apiCall('/users', 'POST', { username: userIn.getValue(), password: passIn.getValue(), role: roleIn.getValue() });
            new ECToast("User Created!", {type: "success"}).show();
            modal.close();
            userIn.setValue(''); passIn.setValue('');
            loadStaff();
        } catch (e) { new ECToast(e.message, {type: "error"}).show(); }
    });
    document.body.appendChild(modal.element);

    async function loadStaff() {
        try {
            const users = await apiCall('/users');
            const table = new ECDataTable({ columns:[{ key: 'Id' }, { key: 'Username' }, { key: 'Role' }], data: users });
            tableContainer.innerHTML = '';
            tableContainer.appendChild(table.element);
        } catch (e) { new ECToast("Failed to load staff", {type:"error"}).show(); }
    }
    loadStaff();
}

function renderPOS(container) {
    let cart = [];
    let inventoryData = [];

    const posWrap = document.createElement('div');
    posWrap.className = "display-grid gridTemplateColumns-3fr_1.5fr gap-20px alignItems-flex-start";
    
    const itemGrid = document.createElement('div');
    itemGrid.className = "display-grid gap-16px gridTemplateColumns-repeat(auto-fill,_minmax(200px,_1fr))";
    
    const cartPanel = document.createElement('div');
    cartPanel.className = "background-var(--ec-bg) border-1px_solid_var(--ec-border) borderRadius-12px padding-20px position-sticky top-76px";
    
    const cartTitle = document.createElement('h3');
    cartTitle.className = "margin-0_0_16px";
    cartTitle.textContent = "Current Order";

    const cartList = document.createElement('div');
    cartList.className = "display-flex flexDirection-column gap-8px minHeight-200px maxHeight-400px overflowY-auto marginBottom-16px";
    
    const totalEl = document.createElement('h2');
    totalEl.className = "margin-0_0_16px borderTop-1px_solid_#dee2e6 paddingTop-16px textAlign-right color-var(--ec-accent)";
    totalEl.textContent = "Total: $0.00";
    
    const checkoutBtn = new ECButton("Process Checkout", { variant: "filled" }).onClick(async () => {
        if (cart.length === 0) return new ECToast("Cart is empty", {type: "warning"}).show();
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
        }
    });
    checkoutBtn.element.classList.add('width-100%');

    cartPanel.append(cartTitle, cartList, totalEl, checkoutBtn.element);
    posWrap.append(itemGrid, cartPanel);
    container.appendChild(posWrap);

    function addToCart(product) {
        const existing = cart.find(i => i.Id === product.Id);
        const currentQty = existing ? existing.Quantity : 0;

        if (product.Stock <= currentQty) {
            return new ECToast("Insufficient stock available", {type: "warning"}).show();
        }

        if (existing) {
            existing.Quantity += 1;
        } else {
            cart.push({ ...product, Quantity: 1 });
        }
        renderCart();
    }

    function renderCart() {
        cartList.innerHTML = '';
        let total = 0;
        cart.forEach((item, index) => {
            const row = document.createElement('div');
            row.className = "display-flex justifyContent-space-between alignItems-center fontSize-14px borderBottom-1px_solid_#f1f1f1 paddingBottom-8px";
            
            const info = document.createElement('div');
            info.innerHTML = `<strong>${item.ItemName}</strong><br><small>${item.Quantity} x $${item.Price.toFixed(2)}</small>`;
            
            const removeBtn = document.createElement('button');
            removeBtn.className = "background-none border-none color-red cursor-pointer fontSize-18px";
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
                card.element.classList.add('cursor-pointer', 'ecbounce-2');
                
                const content = document.createElement('div');
                content.className = "textAlign-center";
                content.innerHTML = `
                    <div class="fontSize-14px fontWeight-600">${item.ItemName}</div>
                    <div class="fontSize-18px fontWeight-700 color-var(--ec-accent)">$${item.Price.toFixed(2)}</div>
                    <div class="fontSize-12px color-var(--ec-text-muted)">Stock: ${item.Stock}</div>
                `;
                
                card.append(content);
                card.element.onclick = () => addToCart(item);
                itemGrid.appendChild(card.element);
            });
        } catch (e) { 
            new ECToast("Error loading products", {type: "error"}).show(); 
        }
    }
    loadInventory();
}

init();