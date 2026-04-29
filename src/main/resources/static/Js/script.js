// ============================================================================
// API CONFIGURATION - TASK 5: FETCH API
// ============================================================================
const API_BASE_URL = 'http://localhost:8080/api/v1';

// Global variables (will be populated from API)
let products = [];
let cart = [];

// ============================================================================
// TASK 5.1: FETCH PRODUCTS FROM BACKEND API
// ============================================================================

/**
 * Fetches all products from the Spring Boot backend API
 * Uses async/await pattern with proper error handling
 * 
 * @returns {Promise<Array>} List of products from the database
 */
async function fetchProducts() {
    try {
        console.log('🔄 Fetching products from API:', `${API_BASE_URL}/products`);
        
        const response = await fetch(`${API_BASE_URL}/products`);
        
        // TASK 5.2: Check if response is OK (status 200-299)
        if (!response.ok) {
            if (response.status === 404) {
                throw new Error('❌ API endpoint not found (404). Backend not running?');
            } else if (response.status === 500) {
                throw new Error('❌ Server error (500). Check backend logs.');
            } else {
                throw new Error(`❌ HTTP error! Status: ${response.status}`);
            }
        }
        
        const data = await response.json();
        console.log('✅ Products fetched successfully:', data.length, 'products found');
        
        // Convert API data to product format
        products = data.map(p => ({
            id: p.id,
            name: p.name,
            price: p.price,
            image: p.imageUrl || '../images/placeholder/no-image.jpg',
            category: p.category || 'Uncategorized',
            stockQuantity: p.stockQuantity
        }));
        
        return products;
        
    } catch (error) {
        console.error('❌ Error fetching products:', error.message);
        products = [];
        return [];
    }
}

// Helper function to find a product by its ID
function getProductById(id) {
    return products.find(product => product.id === parseInt(id));
}

// ============================================================================
// CART STATE MANAGEMENT
// ============================================================================
function saveCart() {
    localStorage.setItem('cart', JSON.stringify(cart));
}

function loadCart() {
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
        cart = JSON.parse(savedCart);
    }
}

// ============================================================================
// TASK 5.3: DYNAMIC PRODUCT RENDERING (UPDATED for API)
// ============================================================================
async function renderProducts() {
    const productContainer = document.querySelector('.product-list');
    if (!productContainer) return;
    
    // Show loading indicator
    productContainer.innerHTML = '<div class="loading" style="text-align: center; padding: 2rem;">🔄 Loading products from database...</div>';
    
    // Fetch products if not loaded yet
    if (products.length === 0) {
        await fetchProducts();
    }
    
    // Clear container
    productContainer.innerHTML = '';
    
    // Handle EMPTY STATE
    if (products.length === 0) {
        productContainer.innerHTML = `
            <div class="empty-state" style="text-align: center; padding: 3rem; grid-column: 1/-1;">
                <p>📦 No products available at the moment.</p>
                <p>Please make sure your Spring Boot backend is running on port 8080.</p>
            </div>
        `;
        return;
    }
    
    // Render each product
    products.forEach(product => {
        const article = document.createElement('article');
        article.classList.add('product-card');
        article.setAttribute('data-id', product.id);
        
        const img = document.createElement('img');
        img.src = product.image;
        img.alt = product.name;
        img.onerror = function() {
            this.src = '../images/placeholder/no-image.jpg';
        };
        
        const name = document.createElement('h2');
        name.textContent = product.name;
        
        const price = document.createElement('p');
        price.textContent = `Price: ₱${product.price}`;
        price.classList.add('price-text');
        
        const category = document.createElement('p');
        category.textContent = `Category: ${product.category}`;
        category.classList.add('category-text');
        
        const buttonGroup = document.createElement('div');
        buttonGroup.classList.add('button-group');
        
        const detailsLink = document.createElement('a');
        detailsLink.href = `detail.html?id=${product.id}`;
        detailsLink.textContent = 'View Details';
        detailsLink.classList.add('btn-link');
        
        const addBtn = document.createElement('button');
        addBtn.textContent = 'Add to Cart';
        addBtn.classList.add('btn', 'add-to-cart');
        addBtn.setAttribute('data-id', product.id);
        
        buttonGroup.appendChild(detailsLink);
        buttonGroup.appendChild(addBtn);
        
        article.appendChild(img);
        article.appendChild(name);
        article.appendChild(price);
        article.appendChild(category);
        article.appendChild(buttonGroup);
        
        productContainer.appendChild(article);
    });
}

// ============================================================================
// CART OPERATIONS
// ============================================================================
function addToCart(productId, quantity = 1) {
    const product = getProductById(productId);
    if (!product) return;
    
    const existingItem = cart.find(item => item.id === productId);
    
    if (existingItem) {
        existingItem.quantity += quantity;
    } else {
        cart.push({
            id: product.id,
            name: product.name,
            price: product.price,
            quantity: quantity,
            image: product.image
        });
    }
    
    saveCart();
    renderCart();
    animateAddToCart(productId);
    showToast(`${product.name} added to cart!`);
}

function removeFromCart(productId) {
    cart = cart.filter(item => item.id !== productId);
    saveCart();
    renderCart();
}

function updateCartQuantity(productId, newQuantity) {
    const item = cart.find(item => item.id === productId);
    if (item) {
        if (newQuantity <= 0) {
            removeFromCart(productId);
        } else {
            item.quantity = newQuantity;
            saveCart();
            renderCart();
        }
    }
}

function renderCart() {
    const cartItemsList = document.querySelector('.cart .cart-items-list');
    const subtotalElement = document.querySelector('.subtotal h2');
    const emptyCartMessage = document.querySelector('.cart .empty-cart-message');
    
    if (!cartItemsList) return;
    
    cartItemsList.innerHTML = '';
    
    if (cart.length === 0) {
        if (emptyCartMessage) emptyCartMessage.classList.add('show');
        if (subtotalElement) subtotalElement.textContent = 'Subtotal: ₱0';
        return;
    }
    
    if (emptyCartMessage) emptyCartMessage.classList.remove('show');
    
    const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    if (subtotalElement) subtotalElement.textContent = `Subtotal: ₱${total}`;
    
    cart.forEach(item => {
        const li = document.createElement('li');
        
        const imgDiv = document.createElement('div');
        imgDiv.classList.add('cart-img');
        const img = document.createElement('img');
        img.src = item.image;
        img.alt = item.name;
        img.onerror = function() {
            this.src = '../images/placeholder/no-image.jpg';
        };
        imgDiv.appendChild(img);
        
        const detailsDiv = document.createElement('div');
        detailsDiv.classList.add('product-dtls');
        const detailsUl = document.createElement('ul');
        
        const nameLi = document.createElement('li');
        const nameH3 = document.createElement('h3');
        nameH3.textContent = item.name;
        nameLi.appendChild(nameH3);
        
        const priceLi = document.createElement('li');
        const priceP = document.createElement('p');
        priceP.textContent = `Price: ₱${item.price}`;
        priceLi.appendChild(priceP);
        
        const qtyLi = document.createElement('li');
        const qtyInput = document.createElement('input');
        qtyInput.type = 'number';
        qtyInput.value = item.quantity;
        qtyInput.min = '1';
        qtyInput.addEventListener('change', (e) => {
            updateCartQuantity(item.id, parseInt(e.target.value));
        });
        qtyLi.appendChild(qtyInput);
        
        const removeLi = document.createElement('li');
        const removeBtn = document.createElement('button');
        removeBtn.textContent = 'Remove';
        removeBtn.classList.add('btn', 'remove-btn');
        removeBtn.addEventListener('click', () => {
            removeFromCart(item.id);
        });
        removeLi.appendChild(removeBtn);
        
        detailsUl.appendChild(nameLi);
        detailsUl.appendChild(priceLi);
        detailsUl.appendChild(qtyLi);
        detailsUl.appendChild(removeLi);
        detailsDiv.appendChild(detailsUl);
        
        li.appendChild(imgDiv);
        li.appendChild(detailsDiv);
        cartItemsList.appendChild(li);
    });
}

// ============================================================================
// PRODUCT DETAIL PAGE SETUP (UPDATED for API)
// ============================================================================
async function setupProductDetail() {
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');
    
    if (productId && document.querySelector('.product-details')) {
        if (products.length === 0) {
            await fetchProducts();
        }
        
        const product = getProductById(productId);
        
        if (product) {
            const productImg = document.getElementById('product-img');
            if (productImg) {
                productImg.src = product.image;
                productImg.alt = product.name;
            }
            
            const productName = document.getElementById('product-name');
            if (productName) productName.textContent = product.name;
            
            const productPrice = document.getElementById('product-price');
            if (productPrice) productPrice.textContent = `Price: ₱${product.price}`;
            
            const productCategory = document.getElementById('product-category');
            if (productCategory) productCategory.textContent = `Category: ${product.category}`;
            
            const stockCell = document.querySelector('table tr:nth-child(3) td');
            if (stockCell) stockCell.textContent = product.stockQuantity > 0 ? 'In Stock' : 'Out of Stock';
            
            const addToCartBtn = document.querySelector('.quantity .btn');
            const quantityInput = document.querySelector('#quantity-input');
            if (addToCartBtn && quantityInput) {
                const newBtn = addToCartBtn.cloneNode(true);
                addToCartBtn.parentNode.replaceChild(newBtn, addToCartBtn);
                newBtn.addEventListener('click', (e) => {
                    e.preventDefault();
                    const quantity = parseInt(quantityInput.value) || 1;
                    addToCart(product.id, quantity);
                });
            }
        }
    }
}

// ============================================================================
// LANDING PAGE PRODUCT GRIDS (UPDATED for API)
// ============================================================================
async function loadFeaturedProducts() {
    const featuredGrid = document.getElementById('featured-products-grid');
    if (!featuredGrid) return;
    
    if (products.length === 0) {
        await fetchProducts();
    }
    
    const featuredProducts = products.slice(0, 4);
    featuredGrid.innerHTML = '';
    
    if (featuredProducts.length === 0) {
        featuredGrid.innerHTML = '<p>No featured products available.</p>';
        return;
    }
    
    featuredProducts.forEach(product => {
        const productCard = createProductCard(product, 'featured');
        featuredGrid.appendChild(productCard);
    });
}

async function loadDiscountedProducts() {
    const discountedGrid = document.getElementById('discounted-products-grid');
    if (!discountedGrid) return;
    
    if (products.length === 0) {
        await fetchProducts();
    }
    
    const discountedProducts = products.slice(4, 8);
    discountedGrid.innerHTML = '';
    
    if (discountedProducts.length === 0) {
        discountedGrid.innerHTML = '<p>No discounted products available.</p>';
        return;
    }
    
    discountedProducts.forEach(product => {
        const discountedPrice = Math.round(product.price * 0.8);
        const productCard = createProductCard(product, 'discount', discountedPrice, 20);
        discountedGrid.appendChild(productCard);
    });
}

function createProductCard(product, type, discountedPrice = null, discountPercent = null) {
    const card = document.createElement('div');
    card.classList.add('product-card');
    
    card.addEventListener('click', () => {
        window.location.href = `detail.html?id=${product.id}`;
    });
    
    if (type === 'featured') {
        const badge = document.createElement('div');
        badge.classList.add('product-badge', 'featured-badge');
        badge.textContent = '⭐ Featured';
        card.appendChild(badge);
    } else if (type === 'discount' && discountPercent) {
        const badge = document.createElement('div');
        badge.classList.add('product-badge', 'discount-badge');
        badge.textContent = `-${discountPercent}% OFF`;
        card.appendChild(badge);
    }
    
    const img = document.createElement('img');
    img.src = product.image;
    img.alt = product.name;
    img.onerror = function() {
        this.src = '../images/placeholder/no-image.jpg';
    };
    card.appendChild(img);
    
    const infoDiv = document.createElement('div');
    infoDiv.classList.add('product-info');
    
    const title = document.createElement('h3');
    title.textContent = product.name;
    infoDiv.appendChild(title);
    
    const priceDiv = document.createElement('div');
    if (discountedPrice) {
        priceDiv.innerHTML = `
            <span class="discounted-price">₱${discountedPrice}</span>
            <span class="original-price">₱${product.price}</span>
        `;
    } else {
        priceDiv.innerHTML = `<span class="product-price">₱${product.price}</span>`;
    }
    infoDiv.appendChild(priceDiv);
    
    const category = document.createElement('div');
    category.classList.add('product-category');
    category.textContent = product.category;
    infoDiv.appendChild(category);
    
    const viewDetailsBtn = document.createElement('button');
    viewDetailsBtn.textContent = 'View Details';
    viewDetailsBtn.classList.add('view-details-btn');
    viewDetailsBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        window.location.href = `detail.html?id=${product.id}`;
    });
    infoDiv.appendChild(viewDetailsBtn);
    
    card.appendChild(infoDiv);
    return card;
}

// ============================================================================
// ANIMATIONS & UTILITIES
// ============================================================================
function animateAddToCart(productId) {
    const productCard = document.querySelector(`.product-card[data-id="${productId}"]`);
    if (productCard) {
        productCard.classList.add('fade-in');
        setTimeout(() => productCard.classList.remove('fade-in'), 500);
    }
}

function showToast(message) {
    let toast = document.querySelector('.toast-notification');
    if (toast) toast.remove();
    
    toast = document.createElement('div');
    toast.textContent = message;
    toast.classList.add('toast-notification');
    toast.style.cssText = `
        position: fixed; bottom: 20px; right: 20px;
        background-color: #28a745; color: white;
        padding: 12px 24px; border-radius: 8px;
        z-index: 1000; animation: slideIn 0.3s ease;
        box-shadow: 0 2px 10px rgba(0,0,0,0.2);
    `;
    document.body.appendChild(toast);
    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

function setupEventDelegation() {
    document.body.addEventListener('click', (e) => {
        if (e.target.classList && e.target.classList.contains('add-to-cart')) {
            const productId = e.target.getAttribute('data-id');
            if (productId) addToCart(parseInt(productId));
        }
    });
}

// ============================================================================
// FORM VALIDATION (keep your existing functions)
// ============================================================================
function setupFormValidation() {
    const checkoutForm = document.querySelector('.payment form');
    const placeOrderBtn = document.querySelector('.order-sum .btn');
    
    if (checkoutForm || placeOrderBtn) {
        const form = checkoutForm || document.querySelector('form');
        if (form && form.id !== 'signup-form') {
            form.addEventListener('submit', (e) => {
                e.preventDefault();
                validateCheckoutForm();
            });
        } else if (placeOrderBtn) {
            placeOrderBtn.addEventListener('click', (e) => {
                e.preventDefault();
                validateCheckoutForm();
            });
        }
    }
    
    const signupBtn = document.getElementById('sg-btn');
    if (signupBtn) {
        signupBtn.addEventListener('click', (e) => {
            e.preventDefault();
            validateSignupForm();
        });
    }
}

function loadCheckoutSummary() {
    const summarySubtotal = document.getElementById('summarySubtotal');
    if (!summarySubtotal) return;
    
    loadCart();
    
    const emptyCartMessage = document.querySelector('.empty-cart-message');
    const checkoutGrid = document.querySelector('.checkout-grid');
    
    if (cart.length === 0) {
        if (emptyCartMessage) emptyCartMessage.style.display = 'block';
        if (checkoutGrid) checkoutGrid.style.display = 'none';
        return;
    }
    
    if (emptyCartMessage) emptyCartMessage.style.display = 'none';
    if (checkoutGrid) checkoutGrid.style.display = 'grid';
    
    const subtotal = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    const shippingFee = 50;
    const tax = subtotal * 0.12;
    const total = subtotal + shippingFee + tax;
    
    summarySubtotal.textContent = `₱${subtotal.toFixed(2)}`;
    const taxElement = document.getElementById('summaryTax');
    const totalElement = document.getElementById('summaryTotal');
    
    if (taxElement) taxElement.textContent = `₱${tax.toFixed(2)}`;
    if (totalElement) totalElement.textContent = `₱${total.toFixed(2)}`;
}

function showFieldError(fieldId, message) {
    const field = document.getElementById(fieldId);
    if (field) {
        field.classList.add('error');
        const errorMsg = document.createElement('small');
        errorMsg.textContent = message;
        errorMsg.classList.add('error-message');
        errorMsg.style.color = '#dc3545';
        field.parentNode.appendChild(errorMsg);
    }
}

function validateCheckoutForm() {
    const fullName = document.getElementById('fullName')?.value.trim();
    const country = document.getElementById('country')?.value.trim();
    const province = document.getElementById('province')?.value.trim();
    const city = document.getElementById('city')?.value.trim();
    const street = document.getElementById('street')?.value.trim();
    const zip = document.getElementById('zip')?.value.trim();
    
    document.querySelectorAll('.error-message').forEach(msg => msg.remove());
    document.querySelectorAll('.form-group input').forEach(input => input.classList.remove('error'));
    
    let isValid = true;
    
    if (!fullName) { showFieldError('fullName', 'Full name is required'); isValid = false; }
    if (!country) { showFieldError('country', 'Country is required'); isValid = false; }
    if (!province) { showFieldError('province', 'Province is required'); isValid = false; }
    if (!city) { showFieldError('city', 'Municipality/City is required'); isValid = false; }
    if (!street) { showFieldError('street', 'Street/Barangay is required'); isValid = false; }
    if (!zip) { showFieldError('zip', 'Zip code is required'); isValid = false; }
    else if (zip && (zip.length < 4 || isNaN(zip))) { showFieldError('zip', 'Valid zip code required'); isValid = false; }
    
    const paymentMethods = document.querySelectorAll('input[name="paymentMethod"]');
    const paymentSelected = Array.from(paymentMethods).some(radio => radio.checked);
    if (!paymentSelected) {
        isValid = false;
        const paymentCard = document.querySelector('.payment-card');
        const errorMsg = document.createElement('small');
        errorMsg.textContent = 'Please select a payment method';
        errorMsg.classList.add('error-message');
        errorMsg.style.color = '#dc3545';
        paymentCard.appendChild(errorMsg);
    }
    
    if (isValid) {
        const subtotal = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
        const total = subtotal + 50 + (subtotal * 0.12);
        
        const order = {
            id: '#' + Math.floor(Math.random() * 1000000),
            total: total,
            date: new Date().toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' }),
            items: cart.map(item => `${item.name} x${item.quantity} - ₱${item.price * item.quantity}`),
            status: 'Processing',
            shippingAddress: { fullName, country, province, city, street, zip }
        };
        
        const orders = JSON.parse(localStorage.getItem('orders') || '[]');
        orders.unshift(order);
        localStorage.setItem('orders', JSON.stringify(orders));
        
        cart = [];
        saveCart();
        
        alert(`Order placed successfully!\n\nOrder ID: ${order.id}\nTotal: ₱${total.toFixed(2)}`);
        window.location.href = 'account.html';
    }
    return isValid;
}

function validateSignupForm() {
    const emailInput = document.querySelector('input[type="email"]');
    const passwordInput = document.querySelector('input[type="password"]');
    let isValid = true;
    
    document.querySelectorAll('.error-message').forEach(msg => msg.remove());
    
    if (!emailInput || !emailInput.value.trim()) {
        isValid = false;
        if (emailInput) {
            emailInput.classList.add('error');
            const errorMsg = document.createElement('small');
            errorMsg.textContent = 'Email is required';
            errorMsg.classList.add('error-message');
            emailInput.parentNode.appendChild(errorMsg);
        }
    } else if (!emailInput.value.includes('@')) {
        isValid = false;
        emailInput.classList.add('error');
        const errorMsg = document.createElement('small');
        errorMsg.textContent = 'Enter a valid email address';
        errorMsg.classList.add('error-message');
        emailInput.parentNode.appendChild(errorMsg);
    }
    
    if (!passwordInput || !passwordInput.value.trim()) {
        isValid = false;
        if (passwordInput) {
            passwordInput.classList.add('error');
            const errorMsg = document.createElement('small');
            errorMsg.textContent = 'Password is required';
            errorMsg.classList.add('error-message');
            passwordInput.parentNode.appendChild(errorMsg);
        }
    } else if (passwordInput.value.length < 6) {
        isValid = false;
        passwordInput.classList.add('error');
        const errorMsg = document.createElement('small');
        errorMsg.textContent = 'Password must be at least 6 characters';
        errorMsg.classList.add('error-message');
        passwordInput.parentNode.appendChild(errorMsg);
    }
    
    if (isValid) {
        alert('Account created successfully!');
    }
}

// ============================================================================
// USER ACCOUNT & ORDER HISTORY
// ============================================================================
const currentUser = {
    name: "Ariane",
    orderHistory: JSON.parse(localStorage.getItem('orders') || '[]')
};

function setupAccountPage() {
    const welcomeHeader = document.getElementById('welcome-message');
    if (welcomeHeader) {
        welcomeHeader.textContent = `Welcome back, ${currentUser.name}!`;
    }
    
    const totalOrdersElement = document.getElementById('total-orders');
    const savedItemsElement = document.getElementById('saved-items');
    
    if (totalOrdersElement) {
        totalOrdersElement.textContent = `${currentUser.orderHistory.length} Orders`;
    }
    if (savedItemsElement) {
        savedItemsElement.textContent = `0 Items`;
    }
    setupOrderHistory();
}

function setupOrderHistory() {
    const orderContainer = document.getElementById('order-history-container');
    if (!orderContainer) return;
    
    orderContainer.innerHTML = '';
    
    if (currentUser.orderHistory.length === 0) {
        orderContainer.innerHTML = '<p>No orders yet. Start shopping!</p>';
        return;
    }
    
    currentUser.orderHistory.forEach((order) => {
        const article = document.createElement('article');
        article.classList.add('order-card');
        const details = document.createElement('details');
        const summary = document.createElement('summary');
        summary.textContent = `${order.id} — ₱${order.total}`;
        
        const orderDetailsDiv = document.createElement('div');
        summary.addEventListener('click', (e) => {
            e.preventDefault();
            if (details.hasAttribute('open')) {
                details.removeAttribute('open');
                orderDetailsDiv.innerHTML = '';
            } else {
                details.setAttribute('open', '');
                orderDetailsDiv.innerHTML = `
                    <div class="order-details">
                        <p><strong>📅 Date:</strong> ${order.date}</p>
                        <p><strong>📦 Items:</strong></p>
                        <ul>${order.items.map(item => `<li>${item}</li>`).join('')}</ul>
                        <p><strong>💰 Total:</strong> ₱${order.total}</p>
                        <p><strong>📊 Status:</strong> ${order.status}</p>
                    </div>
                `;
            }
        });
        details.appendChild(summary);
        details.appendChild(orderDetailsDiv);
        article.appendChild(details);
        orderContainer.appendChild(article);
    });
}

function addStatusStyles() {
    const style = document.createElement('style');
    style.textContent = `
        .order-status { display: inline-block; padding: 4px 12px; border-radius: 20px; font-size: 0.85rem; font-weight: bold; }
        .status-delivered { background-color: #d4edda; color: #155724; }
        .status-pending { background-color: #fff3cd; color: #856404; }
        .order-details ul { margin-left: 20px; }
    `;
    document.head.appendChild(style);
}

function setupLogout() {
    const logoutLink = document.getElementById('logout');
    if (logoutLink) {
        logoutLink.addEventListener('click', (e) => {
            if (!confirm('Are you sure you want to logout?')) e.preventDefault();
        });
    }
}

function setupLandingPage() {
    const shopNowBtn = document.querySelector('.hero .btn');
    if (shopNowBtn) {
        shopNowBtn.addEventListener('click', () => window.location.href = 'products.html');
    }
}

function setupCheckoutPage() {
    const placeOrderBtn = document.getElementById('placeOrderBtn');
    if (placeOrderBtn) {
        placeOrderBtn.addEventListener('click', (e) => {
            e.preventDefault();
            validateCheckoutForm();
        });
    }
    loadCheckoutSummary();
}

function setupImageFallback() {
    const fallbackImage = '../images/placeholder/no-image.jpg';
    document.querySelectorAll('img').forEach(img => {
        if (!img.onerror) {
            img.onerror = function() { this.src = fallbackImage; };
        }
    });
}

// ============================================================================
// INITIALIZATION - DOMContentLoaded Event Listener
// ============================================================================
loadCart();

document.addEventListener('DOMContentLoaded', async () => {
    console.log('🚀 Page loaded, initializing...');
    
    // TASK 5.3: Call fetchProducts on page load
    await fetchProducts();
    
    renderProducts();
    renderCart();
    setupEventDelegation();
    setupFormValidation();
    setupAccountPage();
    setupProductDetail();
    setupLandingPage();
    setupLogout();
    addStatusStyles();
    setupCheckoutPage();
    setupImageFallback();
    loadFeaturedProducts();
    loadDiscountedProducts();
    
    console.log('✅ Initialization complete. Products loaded:', products.length);
});