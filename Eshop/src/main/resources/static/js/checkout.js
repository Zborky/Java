// js/checkout.js
(function () {
  // Backend API endpoint for creating orders
  const ORDER_URL = "http://localhost:8080/api/orders";

  // Helper function to get DOM elements by ID
  const el = (id) => document.getElementById(id);

  // Currency formatter (US English format but with Euro currency)
  const fmt = new Intl.NumberFormat('en-US', { style: 'currency', currency: 'EUR' });

  // Cache UI elements for quick access
  const ui = {
    itemsWrap: el('summary-items'),   // Order summary container
    subtotal: el('summary-subtotal'), // Subtotal display
    shipping: el('summary-shipping'), // Shipping cost display
    total: el('summary-total'),       // Total price display
    shippingMethod: el('shippingMethod'), // Shipping method dropdown
    placeOrder: el('place-order'),    // "Place Order" button
    error: el('form-error'),          // Error message container
    form: el('order-form'),           // Checkout form element
    cartCount: el('cart-count'),      // Cart item count badge
  };

  /**
   * Reads cart items from the Cart object or LocalStorage.
   * Ensures a normalized format for item data.
   * @returns {Array} Array of cart items
   */
  function readCart() {
    // If Cart object exists (from cart.js), use it
    if (window.Cart && Array.isArray(Cart.items)) return Cart.items.map(i => ({
      id: i.id, name: i.name, price: i.price, image: i.image, qty: i.qty
    }));

    // Fallback to LocalStorage (supports two key names)
    try {
      const raw = JSON.parse(localStorage.getItem('mye_cart_v1'))
        || JSON.parse(localStorage.getItem('cart'))
        || [];
      // Normalize properties for consistency
      return raw.map(i => ({
        id: String(i.id),
        name: i.name,
        price: Number(i.price || i.pricePerUnit || 0),
        image: i.image || i.imagePath || '',
        qty: Number(i.qty || i.quantity || 1),
      }));
    } catch { return []; }
  }

  /**
   * Clears the cart both in-memory and in LocalStorage,
   * and updates the cart count UI.
   */
  function clearCart() {
    if (window.Cart && typeof Cart.clear === 'function') Cart.clear();
    else {
      localStorage.removeItem('mye_cart_v1');
      localStorage.removeItem('cart');
    }
    if (ui.cartCount) ui.cartCount.textContent = '0';
  }

  /**
   * Returns the shipping price based on the chosen method.
   * @param {string} method - Shipping method (PICKUP or COURIER)
   * @param {number} subtotal - Order subtotal
   * @returns {number} Shipping price
   */
  function shippingPrice(method, subtotal) {
    if (method === 'PICKUP') return 0;
    if (method === 'COURIER') return 4.90;
    return 0;
  }

  /**
   * Renders the order summary in the UI.
   * Displays all cart items, calculates totals, and updates the display.
   */
  function renderSummary() {
    const items = readCart();
    ui.itemsWrap.innerHTML = '';

    // If cart is empty, show message and disable ordering
    if (!items.length) {
      ui.itemsWrap.innerHTML = `<div class="muted">Your cart is empty.</div>`;
      ui.subtotal.textContent = fmt.format(0);
      ui.shipping.textContent = fmt.format(0);
      ui.total.textContent = fmt.format(0);
      ui.placeOrder.disabled = true;
      return;
    }

    // Calculate subtotal and render each item
    let subtotal = 0;
    items.forEach(i => {
      subtotal += i.price * i.qty;
      ui.itemsWrap.insertAdjacentHTML('beforeend', `
        <div class="summary-item">
          <img src="${i.image || 'https://via.placeholder.com/64'}" alt="${escapeHtml(i.name)}" style="width:64px;height:64px;object-fit:cover;border-radius:.5rem;">
          <div>
            <div><strong>${escapeHtml(i.name)}</strong></div>
            <div class="muted">Quantity: ${i.qty}</div>
            <div class="muted">${fmt.format(i.price)} / pcs</div>
          </div>
          <div><strong>${fmt.format(i.price * i.qty)}</strong></div>
        </div>
      `);
    });

    // Calculate shipping and total
    const ship = shippingPrice(ui.shippingMethod.value, subtotal);
    ui.subtotal.textContent = fmt.format(subtotal);
    ui.shipping.textContent = fmt.format(ship);
    ui.total.textContent = fmt.format(subtotal + ship);
    ui.placeOrder.disabled = false;

    // Update cart badge with total quantity
    const count = items.reduce((s, i) => s + i.qty, 0);
    if (ui.cartCount) ui.cartCount.textContent = count;
  }

  // Update order summary when shipping method changes
  ui.shippingMethod.addEventListener('change', renderSummary);

  /**
   * Handles checkout form submission.
   * Validates form fields, sends the order to the backend,
   * and redirects to the thank you page if successful.
   */
  ui.form.addEventListener('submit', async (e) => {
    e.preventDefault();
    ui.error.hidden = true;
    ui.error.textContent = '';

    const items = readCart();
    if (!items.length) {
      ui.error.hidden = false;
      ui.error.textContent = 'Your cart is empty.';
      return;
    }

    // Required fields validation
    const requiredIds = ['firstName', 'lastName', 'email', 'phone', 'city', 'zip', 'street', 'country', 'shippingMethod', 'paymentMethod', 'terms'];
    for (const id of requiredIds) {
      const node = el(id);
      if (!node) continue;
      if (node.type === 'checkbox' && !node.checked) {
        showError('Please confirm that you agree to the terms.');
        return;
      }
      if (node.type !== 'checkbox' && !String(node.value || '').trim()) {
        showError('Please fill in all required fields.');
        node.focus();
        return;
      }
    }

    // Calculate amounts
    const subtotal = items.reduce((s, i) => s + i.price * i.qty, 0);
    const ship = shippingPrice(ui.shippingMethod.value, subtotal);
    const total = subtotal + ship;

    // Build the request payload
    const payload = {
      customer: {
        firstName: el('firstName').value.trim(),
        lastName: el('lastName').value.trim(),
        email: el('email').value.trim(),
        phone: el('phone').value.trim(),
      },
      shippingAddress: {
        country: el('country').value,
        city: el('city').value.trim(),
        zip: el('zip').value.trim(),
        street: el('street').value.trim(),
      },
      shippingMethod: el('shippingMethod').value,
      paymentMethod: el('paymentMethod').value,
      note: (el('note').value || '').trim(),
      items: items.map(i => ({
        productId: i.id,
        name: i.name,
        unitPrice: i.price,
        quantity: i.qty
      })),
      amounts: {
        currency: 'EUR',
        subtotal: round2(subtotal),
        shipping: round2(ship),
        total: round2(total)
      }
    };

    // Prevent multiple submissions
    ui.placeOrder.disabled = true;
    ui.placeOrder.textContent = 'Sending...';

    try {
      // Send request to backend
      const res = await fetch(ORDER_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || `Order failed: ${res.status}`);
      }

      const data = await safeJson(res);

      // Clear cart and redirect to Thank You page
      clearCart();
      const orderNumber = data?.orderNumber || data?.id || '';
      const qs = orderNumber ? `?order=${encodeURIComponent(orderNumber)}` : '';
      window.location.href = `thankyou.html${qs}`;

    } catch (err) {
      console.error(err);
      showError('Failed to send the order. Please try again.');
      ui.placeOrder.disabled = false;
      ui.placeOrder.textContent = 'Place Order';
    }
  });

  /**
   * Shows an error message in the UI.
   * @param {string} msg - Message to display
   */
  function showError(msg) {
    ui.error.hidden = false;
    ui.error.textContent = msg;
  }

  /**
   * Rounds a number to two decimal places.
   * @param {number} n - Number to round
   * @returns {number}
   */
  function round2(n) { return Math.round((Number(n) || 0) * 100) / 100; }

  /**
   * Escapes HTML characters to prevent XSS.
   * @param {string} s - String to escape
   * @returns {string}
   */
  function escapeHtml(s = '') { return s.replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' }[c])); }

  /**
   * Safely parses JSON from a response.
   * @param {Response} res - Fetch API Response object
   * @returns {Object|null}
   */
  async function safeJson(res) { try { return await res.json(); } catch { return null; } }

  // Initial render of the order summary when page loads
  renderSummary();

  // Scroll to order form if checkout button is clicked from sidebar
  const sideCheckoutBtn = document.getElementById('checkout-btn');
  sideCheckoutBtn?.addEventListener('click', (e) => {
    e.preventDefault();
    document.getElementById('order-form')?.scrollIntoView({ behavior: 'smooth' });
  });
})();
