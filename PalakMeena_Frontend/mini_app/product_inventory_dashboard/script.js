// Product Inventory Dashboard - main.js

// Sample data with 10 products across 4 categories
const defaultProducts = [
  { id: 1, name: "Laptop", price: 55000, stock: 5, category: "electronics" },
  {
    id: 2,
    name: "Wireless Mouse",
    price: 799,
    stock: 20,
    category: "electronics",
  },
  {
    id: 3,
    name: "Bluetooth Speaker",
    price: 2499,
    stock: 0,
    category: "electronics",
  },
  { id: 4, name: "Men's T-Shirt", price: 399, stock: 50, category: "clothing" },
  { id: 5, name: "Denim Jacket", price: 1899, stock: 3, category: "clothing" },
  { id: 6, name: "The Alchemist", price: 299, stock: 15, category: "books" },
  { id: 7, name: "Atomic Habits", price: 399, stock: 8, category: "books" },
  {
    id: 8,
    name: "Leather Wallet",
    price: 699,
    stock: 0,
    category: "accessories",
  },
  { id: 9, name: "Sunglasses", price: 1299, stock: 4, category: "accessories" },
  {
    id: 10,
    name: "USB-C Hub",
    price: 1499,
    stock: 12,
    category: "electronics",
  },
];

// Keep track of all products, current page, and items per page
let allProducts = [];
let currentPage = 1;
const ITEMS_PER_PAGE = 6;

// Simulate API call delay with Promise - mimics real server response time
function fetchProducts(products) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(products);
    }, 1500);
  });
}

// Save products to browser storage for persistence
function saveToLocalStorage(products) {
  localStorage.setItem("inventoryProducts", JSON.stringify(products));
}

// Load products from storage or return null if empty
function loadFromLocalStorage() {
  const data = localStorage.getItem("inventoryProducts");
  return data ? JSON.parse(data) : null;
}

// Update the statistics cards at the top
function updateAnalytics(products) {
  document.getElementById("totalProducts").textContent = products.length;

  // Calculate total value (price × stock) for all products
  const totalValue = products.reduce((sum, p) => sum + p.price * p.stock, 0);
  document.getElementById("totalValue").textContent =
    "₹" + totalValue.toLocaleString("en-IN");

  // Count products with no stock
  const outOfStockCount = products.filter((p) => p.stock === 0).length;
  document.getElementById("outOfStock").textContent = outOfStockCount;
}

// Get the CSS class for product badge based on category
function getBadgeClass(category) {
  const classes = {
    electronics: "badge-electronics",
    clothing: "badge-clothing",
    books: "badge-books",
    accessories: "badge-accessories",
  };
  return classes[category] || "badge-electronics";
}
