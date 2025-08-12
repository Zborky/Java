// js/checkout.js
(function () {
  // API endpoint for order creation
  const ORDER_URL = "http://localhost:8080/api/orders";

  // Shortcut for document.getElementById
  const el = (id) => document.getElementById(id);

  // Currency formatter for Slovak Euro format
  const fmt = new Intl.NumberFormat('sk-SK', { style: 'currency', currency: 'EUR' });

  // Cached UI elements
  const ui = {
    itemsWrap: el('summary-items'), // Container for order items summary
    subtotal: el('summary-subtotal'),
    shipping: el('summary-shipping'),
    total: el('summary-total'),
    shippingMethod: el('shippingMethod'),
    placeOrder: el('place-order'),
    error: el('form-error'),
    form: el('order-form'),
    cartCount: el('cart-count'),
  };

  /**
   * Read cart items from Cart object or LocalStorage
   * @returns {Array} Array of cart items
   */
  function readCart() {
    if (window.Cart && Array.isArray(Cart.items)) return Cart.items.map(i => ({
      id: i.id, name: i.name, price: i.price, image: i.image, qty: i.qty
    }));
    // Fallback to LocalStorage
    try {
      const raw = JSON.parse(localStorage.getItem('mye_cart_v1'))
        || JSON.parse(localStorage.getItem('cart')) // old key support
        || [];
      // Normalize fields
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
   * Clear cart from memory and UI
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
   * Calculate shipping price based on method and subtotal
   */
  function shippingPrice(method, subtotal) {
    if (method === 'PICKUP') return 0;
    if (method === 'COURIER') return 4.90;
    // Optionally free shipping above a certain subtotal could be added here
    return 0;
  }

  /**
   * Render the order summary section
   */
  function renderSummary() {
    const items = readCart();
    ui.itemsWrap.innerHTML = '';

    // If no items in cart
    if (!items.length) {
      ui.itemsWrap.innerHTML = `<div class="muted">Košík je prázdny.</div>`;
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
            <div class="muted">Množstvo: ${i.qty}</div>
            <div class="muted">${fmt.format(i.price)} / ks</div>
          </div>
          <div><strong>${fmt.format(i.price * i.qty)}</strong></div>
        </div>
      `);
    });

    // Shipping & total
    const ship = shippingPrice(ui.shippingMethod.value, subtotal);
    ui.subtotal.textContent = fmt.format(subtotal);
    ui.shipping.textContent = fmt.format(ship);
    ui.total.textContent = fmt.format(subtotal + ship);
    ui.placeOrder.disabled = false;

    // Update cart badge count
    const count = items.reduce((s, i) => s + i.qty, 0);
    if (ui.cartCount) ui.cartCount.textContent = count;
  }

  // Update summary when shipping method changes
  ui.shippingMethod.addEventListener('change', renderSummary);

  /**
   * Handle order submission
   */
  ui.form.addEventListener('submit', async (e) => {
    e.preventDefault();
    ui.error.hidden = true;
    ui.error.textContent = '';

    const items = readCart();
    if (!items.length) {
      ui.error.hidden = false;
      ui.error.textContent = 'Košík je prázdny.';
      return;
    }

    // Basic validation for required fields
    const requiredIds = ['firstName', 'lastName', 'email', 'phone', 'city', 'zip', 'street', 'country', 'shippingMethod', 'paymentMethod', 'terms'];
    for (const id of requiredIds) {
      const node = el(id);
      if (!node) continue;
      if (node.type === 'checkbox' && !node.checked) {
        showError('Prosím, potvrď súhlas s podmienkami.');
        return;
      }
      if (node.type !== 'checkbox' && !String(node.value || '').trim()) {
        showError('Prosím, vyplň všetky povinné polia.');
        node.focus();
        return;
      }
    }

    // Build request payload
    const subtotal = items.reduce((s, i) => s + i.price * i.qty, 0);
    const ship = shippingPrice(ui.shippingMethod.value, subtotal);
    const total = subtotal + ship;

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

    // Disable button during submission
    ui.placeOrder.disabled = true;
    ui.placeOrder.textContent = 'Odosielam...';

    try {
      // Send order to backend
      const res = await fetch(ORDER_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || `Order failed: ${res.status}`);
      }

      // Parse response
      const data = await safeJson(res);

      // Clear cart and redirect to thank you page
      clearCart();
      const orderNumber = data?.orderNumber || data?.id || '';
      const qs = orderNumber ? `?order=${encodeURIComponent(orderNumber)}` : '';
      window.location.href = `thankyou.html${qs}`;

    } catch (err) {
      console.error(err);
      showError('Objednávku sa nepodarilo odoslať. Skús to znova, prosím.');
      ui.placeOrder.disabled = false;
      ui.placeOrder.textContent = 'Odoslať objednávku';
    }
  });

  /**
   * Display error message
   */
  function showError(msg) {
    ui.error.hidden = false;
    ui.error.textContent = msg;
  }

  /**
   * Round number to 2 decimal places
   */
  function round2(n) { return Math.round((Number(n) || 0) * 100) / 100; }

  /**
   * Escape HTML to prevent XSS
   */
  function escapeHtml(s = '') { return s.replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' }[c])); }

  /**
   * Safely parse JSON response
   */
  async function safeJson(res) { try { return await res.json(); } catch { return null; } }

  // Initialize summary on page load
  renderSummary();

  // Scroll to form if checkout button is clicked in sidebar
  const sideCheckoutBtn = document.getElementById('checkout-btn');
  sideCheckoutBtn?.addEventListener('click', (e) => {
    e.preventDefault();
    document.getElementById('order-form')?.scrollIntoView({ behavior: 'smooth' });
  });
})();
