document.addEventListener('DOMContentLoaded', () => {
    loadProducts();
});


async function loadProducts(){
    //Url adrres to our API endpoint for products
    const url = "http://localhost:8080/api/products";
    
    // Find the container in the HTML where we will insert the products
    const container = document.getElementById("product-list");

    try{
        const response = await fetch(url);

        // Check if the response was successful (HTTP status 200-299)
        if(!response.ok){
            throw new Error(`Error fetching data: ${response.statusText}`);
        }

        // Convert the response from JSON format to a JavaScript array of objects
        const products = await response.json();

        // If we didn't get any products, display a message
        if(products.length ===  0){
            container.innerHTML = '<p>No products found.</p>';
            return;
        }
        // Clear the container in case there was something there (e.g., a "Loading..." message)
        const innerHTML = '';

        // Loop through each product in the array and create an HTML element (a card) for it
        products.forEach(product => {
            // Create a new div element for the product card
            const productCard = document.createElement('div');
            
            // Add a CSS class to it for styling
            productCard.classList.add('product-card');

            // Fill the card's inner HTML with data from the product using template literals (the ` `` ` backticks)
            productCard.innerHTML = `
                ${product.imagePath ? `<img src="/uploads/${product.imagePath}" alt="${product.name || 'Produkt'}" class="product-image" />` : ''}
                 
                <div class="info">
                    <h2>${product.name}</h2>
                    <p>${product.description}</p>
                    <div class="price">$${product.price.toFixed(2)}</div>
                </div>
            `;

            // Add the finished card to the main container on the page
            container.appendChild(productCard);
        });

    } catch (error) {
        // If any error occurs (e.g., the backend is not running), display an error message
        console.error('Failed to load products:', error);
        container.innerHTML = '<p class="error">Sorry, we could not load the products. Please try again later.</p>';
    }

    }

