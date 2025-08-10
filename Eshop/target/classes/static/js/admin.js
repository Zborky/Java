const form = document.getElementById("productForm");
const tableBody = document.querySelector("#productTable tbody");
const messageBox = document.getElementById("responseMessage");
const formTitle = document.getElementById("formTitle");

let editMode = false;

form.addEventListener("submit", async function (e) {
  e.preventDefault();

  const formData = new FormData(form);
  const id = formData.get("id");
  const method = editMode ? "PUT" : "POST";

  let url = "http://localhost:8080/api/products";
  if (editMode) {
    url += `/${id}`;  // správne template literal s backtickmi a ${}
  }

  try {
    const response = await fetch(url, {
      method: method,
      body: formData,
    });

    const result = await response.text();

    if (response.ok) {
      messageBox.style.color = "green";
      messageBox.textContent = editMode ? "Produkt bol upravený!" : "Produkt bol pridaný!";
      form.reset();
      form.elements["id"].value = "";  // reset id hidden inputu
      formTitle.textContent = "Pridať nový produkt";
      editMode = false;
      loadProducts();
    } else {
      messageBox.style.color = "red";
      messageBox.textContent = `Chyba: ${result}`;  // pridaj spätné apostrofy
    }
  } catch (error) {
    messageBox.style.color = "red";
    messageBox.textContent = "Chyba pri odosielaní požiadavky.";
    console.error(error);
  }
});

async function loadProducts() {
  try {
    const response = await fetch("http://localhost:8080/api/products");
    const products = await response.json();

    tableBody.innerHTML = "";

    products.forEach(product => {
      const row = document.createElement("tr");

      row.innerHTML = `
        <td>${product.id}</td>
        <td>${product.name}</td>
        <td>${product.description}</td>
        <td>${product.price}</td>
        <td>${product.category}</td>
        <td>${product.imageUrl ? `<img src="${product.imageUrl}" width="60">` : "—"}</td>
        <td>
          <button onclick="editProduct(${product.id})">Upraviť</button>
          <button onclick="deleteProduct(${product.id})">Zmazať</button>
        </td>
      `;

      tableBody.appendChild(row);
    });

  } catch (error) {
    console.error("Chyba pri načítaní produktov", error);
  }
}

window.editProduct = async function (id) {
  try {
    const response = await fetch(`http://localhost:8080/api/products/${id}`);
    const product = await response.json();

    form.elements["id"].value = product.id;
    form.elements["name"].value = product.name;
    form.elements["description"].value = product.description;
    form.elements["price"].value = product.price;
    form.elements["category"].value = product.category;

    formTitle.textContent = "Upraviť produkt";
    editMode = true;
    window.scrollTo(0, 0);
  } catch (error) {
    console.error("Nepodarilo sa načítať produkt na úpravu", error);
  }
};

window.deleteProduct = async function (id) {
  if (!confirm("Naozaj chceš zmazať tento produkt?")) return;

  try {
    const response = await fetch(`http://localhost:8080/api/products/${id}`, {
      method: "DELETE",
    });

    if (response.ok) {
      messageBox.style.color = "green";
      messageBox.textContent = "Produkt bol zmazaný!";
      loadProducts();
    } else {
      messageBox.style.color = "red";
      messageBox.textContent = "Chyba pri mazaní produktu.";
    }
  } catch (error) {
    console.error("Chyba pri mazaní produktu", error);
  }
};

// Načítaj produkty pri načítaní stránky
loadProducts();
