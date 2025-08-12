// Wait for the DOM to be fully loaded before running the product loading function
document.addEventListener('DOMContentLoaded', () => {
    loadProducts();
});

/**
 * Load products from the backend API and render them in the HTML container.
 */
async function loadProducts() {
    // URL address of our API endpoint for products
    const url = "http://localhost:8080/api/products";
    
    // Find the container in the HTML where the products will be displayed
    const container = document.getElementById("product-list");

    try {
        // Fetch data from the backend
        const response = await fetch(url);

        // Check if the response was successful (HTTP status 200–299)
        if (!response.ok) {
            throw new Error(`Error fetching data: ${response.statusText}`);
        }

        // Convert the response JSON into a JavaScript array of product objects
        const products = await response.json();

        // If no products were returned, show a message
        if (products.length === 0) {
            container.innerHTML = '<p>No products found.</p>';
            return;
        }

        // Clear the container in case there was any placeholder content
        container.innerHTML = '';

        // Loop through each product and create an HTML card for it
        products.forEach(product => {
            // Create a new div element for the product card
            const productCard = document.createElement('div');
            productCard.classList.add('product-card'); // Add a CSS class for styling

            // Construct the image URL (if product has an image path)
            const imgUrl = product.imagePath ? `/uploads/${product.imagePath}` : '';

            // Fill the card with HTML content
            productCard.innerHTML = `
                ${imgUrl ? `<img src="${imgUrl}" alt="${product.name || 'Produkt'}" class="product-image" />` : ''}
                <div class="info">
                    <h2>${product.name}</h2>
                    <p>${product.description || ''}</p>
                    <div class="price">$${Number(product.price ?? 0).toFixed(2)}</div>
                </div>

                <!-- Add to Cart button with product data attributes -->
                <button 
                    class="add-to-cart"
                    data-id="${product.id}"
                    data-name="${product.name || ''}"
                    data-price="${Number(product.price ?? 0)}"
                    data-image="${imgUrl}">
                    Add to Cart
                </button>
            `;

            // Append the product card to the main container
            container.appendChild(productCard);
        });

    } catch (error) {
        // Display an error message if the fetch fails
        console.error('Failed to load products:', error);
        container.innerHTML = '<p class="error">Sorry, we could not load the products. Please try again later.</p>';
    }
}

/**
 * Delegated click listener for the "Add to Cart" button.
 * Listens for clicks anywhere in the document but reacts only if the click
 * was on an element with the `.add-to-cart` class.
 */
document.addEventListener('click', (e) => {
    const btn = e.target.closest('.add-to-cart');
    if (!btn) return; // Ignore clicks not on an "Add to Cart" button

    // Construct product object from button's data attributes
    const product = {
        id: btn.dataset.id,
        name: btn.dataset.name,
        price: Number(btn.dataset.price || 0),
        image: btn.dataset.image || ''
    };

    // If Cart API from cart.js is available, use it
    if (window.Cart && typeof Cart.add === 'function') {
        Cart.add(product, 1);
        if (typeof Cart.open === 'function') Cart.open(); // Open the cart drawer
    } else {
        // Otherwise, fallback to LocalStorage handling
        addToCartLocal(product);
        updateCartCountLocal();
    }
});

/* ------- Fallback functions (used only if Cart API is not loaded) ------- */

/**
 * Add a product to the cart stored in LocalStorage.
 * @param {Object} product - The product to add
 */
function addToCartLocal(product) {
    let cart = [];
    const storedCart = localStorage.getItem('cart');

    // Try to read the existing cart from LocalStorage
    try {
        cart = storedCart ? JSON.parse(storedCart) : [];
    } catch (e) {
        console.error('Error parsing cart data:', e);
        cart = [];
    }

    // Check if product already exists in cart
    const idx = cart.findIndex(item => String(item.id) === String(product.id));
    if (idx >= 0) {
        // If it exists, increase quantity
        cart[idx].quantity = Number(cart[idx].quantity || 1) + 1;
    } else {
        // Otherwise, add new product with quantity 1
        cart.push({ ...product, quantity: 1 });
    }

    // Save updated cart back to LocalStorage
    localStorage.setItem('cart', JSON.stringify(cart));
}

/**
 * Update the cart count badge in the UI based on LocalStorage cart data.
 */
function updateCartCountLocal() {
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    const cartCount = cart.reduce((total, item) => total + (Number(item.quantity) || 0), 0);

    // Find cart count element in DOM and update its text
    const el = document.getElementById('cart-count');
    if (el) el.textContent = cartCount;
}
