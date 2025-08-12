// js/cart.js
(function () {
  // LocalStorage key for cart persistence
  const STORAGE_KEY = 'mye_cart_v1';

  // Cached references to DOM elements used in the cart UI
  const els = {
    drawer: document.getElementById('cart-drawer'),
    overlay: document.getElementById('cart-overlay'),
    items: document.getElementById('cart-items'),
    total: document.getElementById('cart-total'),
    count: document.getElementById('cart-count'),
    openBtn: document.getElementById('cart-button'),
    closeBtn: document.getElementById('cart-close'),
    checkoutBtn: document.getElementById('checkout-btn'),
  };

  // Currency formatter for Slovak Euro format
  const fmt = new Intl.NumberFormat('sk-SK', { style: 'currency', currency: 'EUR' });

  // Cart items array (loaded from LocalStorage)
  let items = load();

  /**
   * Load cart items from LocalStorage
   * @returns {Array} The stored cart items
   */
  function load() {
    try { return JSON.parse(localStorage.getItem(STORAGE_KEY)) || []; }
    catch { return []; } // Return empty array on parse error
  }

  /**
   * Save current cart state to LocalStorage
   */
  function save() { localStorage.setItem(STORAGE_KEY, JSON.stringify(items)); }

  /**
   * Find index of an item in the cart by its ID
   * @param {string} id - Product ID
   * @returns {number} Index of item in array or -1 if not found
   */
  function findIndex(id) { return items.findIndex(i => i.id === id); }

  /**
   * Add a product to the cart
   * @param {Object} product - Product details
   * @param {number} qty - Quantity to add (default 1)
   */
  function add(product, qty = 1) {
    if (!product || !product.id) return;
    const i = findIndex(product.id);
    if (i > -1) items[i].qty += qty; // Increment if already exists
    else items.push({
      id: String(product.id),
      name: product.name ?? 'Produkt',
      price: Number(product.price || 0),
      image: product.image || product.imagePath || '',
      qty: Number(qty) || 1
    });
    render();
  }

  /**
   * Remove an item from the cart by ID
   */
  function remove(id) {
    items = items.filter(i => i.id !== String(id));
    render();
  }

  /**
   * Set a specific quantity for an item
   */
  function setQty(id, qty) {
    qty = Math.max(1, Number(qty) || 1); // Minimum qty is 1
    const i = findIndex(String(id));
    if (i > -1) { items[i].qty = qty; render(); }
  }

  /**
   * Decrease quantity by one, remove if reaches zero
   */
  function dec(id) {
    const i = findIndex(String(id));
    if (i > -1) {
      items[i].qty -= 1;
      if (items[i].qty <= 0) items.splice(i, 1); // Remove item if qty 0
      render();
    }
  }

  /**
   * Clear all items from cart
   */
  function clear() { items = []; render(); }

  /**
   * Calculate total price of all items
   */
  function total() { return items.reduce((s, i) => s + i.price * i.qty, 0); }

  /**
   * Calculate total number of items in the cart
   */
  function count() { return items.reduce((s, i) => s + i.qty, 0); }

  /**
   * Render the cart UI based on current state
   */
  function render() {
    // If no items in cart
    if (!items.length) {
      els.items.innerHTML = `
        <div style="padding:1rem; color:#666;">
          Tvoj košík je prázdny. Pridaj si niečo pekné 🙂
        </div>`;
      els.checkoutBtn.disabled = true;
    } else {
      // Generate HTML for each cart item
      els.items.innerHTML = items.map(i => `
        <div class="cart-item" data-id="${escapeHtml(i.id)}">
          <img src="${i.image || 'https://via.placeholder.com/64'}" alt="${escapeHtml(i.name)}">
          <div>
            <div class="item-title">${escapeHtml(i.name)}</div>
            <div class="item-price">${fmt.format(i.price)}</div>
            <div class="qty-row">
              <button class="qty-btn" data-cart-action="dec">−</button>
              <span>${i.qty}</span>
              <button class="qty-btn" data-cart-action="inc">+</button>
              <button class="remove-btn" data-cart-action="remove">Odstrániť</button>
            </div>
          </div>
          <div style="align-self:center; font-weight:600;">
            ${fmt.format(i.price * i.qty)}
          </div>
        </div>
      `).join('');
      els.checkoutBtn.disabled = false;
    }

    // Update totals
    els.total.textContent = fmt.format(total());
    els.count.textContent = count();

    // Save current state
    save();
  }

  /**
   * Open the cart drawer UI
   */
  function open() {
    els.drawer.classList.add('open');
    els.overlay.hidden = false;
    els.drawer.setAttribute('aria-hidden', 'false');
  }

  /**
   * Close the cart drawer UI
   */
  function close() {
    els.drawer.classList.remove('open');
    els.overlay.hidden = true;
    els.drawer.setAttribute('aria-hidden', 'true');
  }

  // --- Event bindings ---
  els.openBtn?.addEventListener('click', open);
  els.closeBtn?.addEventListener('click', close);
  els.overlay?.addEventListener('click', close);

  // Delegate click events inside the cart item list
  els.items.addEventListener('click', (e) => {
    const row = e.target.closest('.cart-item');
    if (!row) return;
    const id = row.dataset.id;

    if (e.target.matches('[data-cart-action="inc"]')) setQty(id, (getQty(id) + 1));
    if (e.target.matches('[data-cart-action="dec"]')) dec(id);
    if (e.target.matches('[data-cart-action="remove"]')) remove(id);
  });

  // Checkout button click
  els.checkoutBtn?.addEventListener('click', () => {
    // TODO: Redirect to checkout page in the future
    window.location.href = 'checkout.html';
  });

  /**
   * Get quantity of a specific item
   */
  function getQty(id) {
    const i = findIndex(String(id));
    return i > -1 ? items[i].qty : 0;
  }

  /**
   * Escape HTML special characters to prevent XSS
   */
  function escapeHtml(s = '') {
    return s.replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' }[c]));
  }

  // --- Public API for Cart ---
  window.Cart = {
    add, remove, setQty, clear, open, close,
    get items() { return [...items]; }, // Return a copy of items
    total
  };

  // Initial render after page reload (restore state)
  render();
})();
