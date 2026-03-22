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

// Generate product cards dynamically and display them
function renderProducts(filteredProducts) {
  const grid = document.getElementById("productGrid");
  const emptyMsg = document.getElementById("emptyMsg");
  const pagination = document.getElementById("paginationControls");
  const countBadge = document.getElementById("productCountBadge");

  grid.innerHTML = "";

  // Update product count display
  countBadge.textContent =
    filteredProducts.length +
    " item" +
    (filteredProducts.length !== 1 ? "s" : "");

  // Show empty state if no products match filters
  if (filteredProducts.length === 0) {
    grid.style.display = "none";
    emptyMsg.style.display = "flex";
    pagination.style.display = "none";
    return;
  }

  emptyMsg.style.display = "none";
  grid.style.display = "grid";

  // Pagination setup - calculate which products to show on current page
  const totalPages = Math.ceil(filteredProducts.length / ITEMS_PER_PAGE);
  const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
  const endIndex = startIndex + ITEMS_PER_PAGE;
  const pageProducts = filteredProducts.slice(startIndex, endIndex);

  // Create a card for each product on this page
  pageProducts.forEach((product, index) => {
    const card = document.createElement("div");
    card.classList.add("product-card");
    card.style.animationDelay = index * 0.06 + "s";

    // Determine stock status text and styling
    let stockText = "In Stock: " + product.stock;
    let stockClass = "stock";

    if (product.stock === 0) {
      stockText = "Out of Stock";
      stockClass = "stock low";
    } else if (product.stock < 5) {
      stockText = "Low Stock: " + product.stock;
      stockClass = "stock low";
    }

    // Build product card HTML with all information
    card.innerHTML = `
      <h3>${product.name}</h3>
      <span class="category-badge ${getBadgeClass(product.category)}">${product.category}</span>
      <hr class="card-divider" />
      <p class="price">₹${product.price.toLocaleString("en-IN")}</p>
      <p class="${stockClass}">${stockText}</p>
      <div style="display: flex; gap: 8px;">
        <button class="btn-edit" data-id="${product.id}">Edit</button>
        <button class="btn-delete" data-id="${product.id}">Remove</button>
      </div>
    `;

    grid.appendChild(card);
  });

  // Attach event listeners to new buttons
  attachDeleteEvents();
  attachEditEvents();

  // Show pagination controls
  renderPagination(totalPages, filteredProducts);
}

// Create pagination buttons (Previous, page numbers, Next)
function renderPagination(totalPages, filteredProducts) {
  const pagination = document.getElementById("paginationControls");
  pagination.innerHTML = "";

  if (totalPages <= 1) {
    pagination.style.display = "none";
    return;
  }

  pagination.style.display = "flex";

  // Previous button
  const prevBtn = document.createElement("button");
  prevBtn.textContent = "← Prev";
  prevBtn.disabled = currentPage === 1;
  prevBtn.addEventListener("click", () => {
    if (currentPage > 1) {
      currentPage--;
      renderProducts(filteredProducts);
    }
  });
  pagination.appendChild(prevBtn);

  // Page number buttons
  for (let i = 1; i <= totalPages; i++) {
    const btn = document.createElement("button");
    btn.textContent = i;
    if (i === currentPage) btn.classList.add("active");
    btn.addEventListener("click", () => {
      currentPage = i;
      renderProducts(filteredProducts);
    });
    pagination.appendChild(btn);
  }

  // Next button
  const nextBtn = document.createElement("button");
  nextBtn.textContent = "Next →";
  nextBtn.disabled = currentPage === totalPages;
  nextBtn.addEventListener("click", () => {
    if (currentPage < totalPages) {
      currentPage++;
      renderProducts(filteredProducts);
    }
  });
  pagination.appendChild(nextBtn);
}

// Handle delete button clicks - show confirmation modal first
let pendingDeleteId = null;

function attachDeleteEvents() {
  const deleteButtons = document.querySelectorAll(".btn-delete");

  deleteButtons.forEach((btn) => {
    btn.addEventListener("click", (e) => {
      const idToDelete = parseInt(e.target.getAttribute("data-id"));
      showDeleteConfirm(idToDelete);
    });
  });
}

function showDeleteConfirm(productId) {
  pendingDeleteId = productId;
  document.getElementById("deleteConfirmModal").style.display = "flex";
}

function closeDeleteConfirm() {
  pendingDeleteId = null;
  document.getElementById("deleteConfirmModal").style.display = "none";
}

// Execute delete and refresh everything
function confirmDelete() {
  if (pendingDeleteId === null) return;

  allProducts = allProducts.filter((p) => p.id !== pendingDeleteId);

  saveToLocalStorage(allProducts);
  updateAnalytics(allProducts);
  updateCategoryCounts();
  applyFiltersAndRender();

  closeDeleteConfirm();
}

// Initialize delete modal event listeners
function initializeDeleteModal() {
  const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");
  if (confirmDeleteBtn) {
    confirmDeleteBtn.addEventListener("click", confirmDelete);
  }
}

// Handle edit button clicks
function attachEditEvents() {
  const editButtons = document.querySelectorAll(".btn-edit");

  editButtons.forEach((btn) => {
    btn.addEventListener("click", (e) => {
      const idToEdit = parseInt(e.target.getAttribute("data-id"));
      openEditModal(idToEdit);
    });
  });
}

// Apply all active filters and sort, then render products
function applyFiltersAndRender() {
  currentPage = 1;

  const query = document
    .getElementById("searchInput")
    .value.trim()
    .toLowerCase();
  const category = document.getElementById("categoryFilter").value;
  const lowStockOnly = document.getElementById("lowStockFilter").checked;
  const sortOption = document.getElementById("sortSelect").value;

  let result = [...allProducts];

  // Search filter - case insensitive name matching
  if (query !== "") {
    result = result.filter((p) => p.name.toLowerCase().includes(query));
  }

  // Category filter
  if (category !== "all") {
    result = result.filter((p) => p.category === category);
  }

  // Low stock filter - items with less than 5 in stock
  if (lowStockOnly) {
    result = result.filter((p) => p.stock < 5);
  }

  // Apply sorting
  if (sortOption === "price-low") result.sort((a, b) => a.price - b.price);
  if (sortOption === "price-high") result.sort((a, b) => b.price - a.price);
  if (sortOption === "name-az")
    result.sort((a, b) => a.name.localeCompare(b.name));
  if (sortOption === "name-za")
    result.sort((a, b) => b.name.localeCompare(a.name));

  renderProducts(result);
}

// Check form inputs and show error messages
function validateForm() {
  let isValid = true;

  const name = document.getElementById("productName").value.trim();
  const price = parseFloat(document.getElementById("productPrice").value);
  const stock = parseInt(document.getElementById("productStock").value);
  const category = document.getElementById("productCategory").value;

  document.getElementById("nameError").textContent = "";
  document.getElementById("priceError").textContent = "";
  document.getElementById("stockError").textContent = "";
  document.getElementById("categoryError").textContent = "";

  if (name === "") {
    document.getElementById("nameError").textContent =
      "Product name is required.";
    isValid = false;
  }

  if (isNaN(price) || price <= 0) {
    document.getElementById("priceError").textContent =
      "Enter a price greater than 0.";
    isValid = false;
  }

  if (isNaN(stock) || stock < 0) {
    document.getElementById("stockError").textContent =
      "Stock cannot be negative.";
    isValid = false;
  }

  if (category === "") {
    document.getElementById("categoryError").textContent =
      "Please select a category.";
    isValid = false;
  }

  return isValid;
}

// Handle adding a new product through the form
function setupFormListener() {
  const form = document.getElementById("addProductForm");

  form.addEventListener("submit", (e) => {
    e.preventDefault();

    if (!validateForm()) return;

    const newProduct = {
      id: Date.now(),
      name: document.getElementById("productName").value.trim(),
      price: parseFloat(document.getElementById("productPrice").value),
      stock: parseInt(document.getElementById("productStock").value),
      category: document.getElementById("productCategory").value,
    };

    allProducts.push(newProduct);
    saveToLocalStorage(allProducts);

    updateAnalytics(allProducts);
    updateCategoryCounts();
    applyFiltersAndRender();

    form.reset();
  });
}

// Setup listeners for search, filter, and sort controls
function setupControlListeners() {
  document
    .getElementById("searchInput")
    .addEventListener("input", applyFiltersAndRender);
  document
    .getElementById("categoryFilter")
    .addEventListener("change", applyFiltersAndRender);
  document
    .getElementById("lowStockFilter")
    .addEventListener("change", applyFiltersAndRender);
  document
    .getElementById("sortSelect")
    .addEventListener("change", applyFiltersAndRender);
}

// Count and display products in each category
function updateCategoryCounts() {
  const categories = ["electronics", "clothing", "books", "accessories"];

  categories.forEach((cat) => {
    const count = allProducts.filter((p) => p.category === cat).length;
    const countElement = document.getElementById(cat + "Count");
    if (countElement) countElement.textContent = count;
  });
}

// Handle edit modal functionality
let currentEditProductId = null;

function openEditModal(productId) {
  const product = allProducts.find((p) => p.id === productId);
  if (!product) return;

  currentEditProductId = productId;

  document.getElementById("editProductName").value = product.name;
  document.getElementById("editProductPrice").value = product.price;
  document.getElementById("editProductStock").value = product.stock;
  document.getElementById("editProductCategory").value = product.category;

  document.getElementById("editNameError").textContent = "";
  document.getElementById("editPriceError").textContent = "";
  document.getElementById("editStockError").textContent = "";
  document.getElementById("editCategoryError").textContent = "";

  document.getElementById("editModal").style.display = "flex";
}

function closeEditModal() {
  document.getElementById("editModal").style.display = "none";
  document.getElementById("editProductForm").reset();
  currentEditProductId = null;

  document.getElementById("editNameError").textContent = "";
  document.getElementById("editPriceError").textContent = "";
  document.getElementById("editStockError").textContent = "";
  document.getElementById("editCategoryError").textContent = "";
}

// Validate edit form
function validateEditForm() {
  let isValid = true;

  const name = document.getElementById("editProductName").value.trim();
  const price = parseFloat(document.getElementById("editProductPrice").value);
  const stock = parseInt(document.getElementById("editProductStock").value);
  const category = document.getElementById("editProductCategory").value;

  document.getElementById("editNameError").textContent = "";
  document.getElementById("editPriceError").textContent = "";
  document.getElementById("editStockError").textContent = "";
  document.getElementById("editCategoryError").textContent = "";

  if (name === "") {
    document.getElementById("editNameError").textContent =
      "Product name is required.";
    isValid = false;
  }

  if (isNaN(price) || price <= 0) {
    document.getElementById("editPriceError").textContent =
      "Enter a price greater than 0.";
    isValid = false;
  }

  if (isNaN(stock) || stock < 0) {
    document.getElementById("editStockError").textContent =
      "Stock cannot be negative.";
    isValid = false;
  }

  if (category === "") {
    document.getElementById("editCategoryError").textContent =
      "Please select a category.";
    isValid = false;
  }

  return isValid;
}

// Handle edit form submission
function setupEditFormListener() {
  const form = document.getElementById("editProductForm");

  form.addEventListener("submit", (e) => {
    e.preventDefault();

    if (!validateEditForm()) return;

    const product = allProducts.find((p) => p.id === currentEditProductId);
    if (!product) return;

    product.name = document.getElementById("editProductName").value.trim();
    product.price = parseFloat(
      document.getElementById("editProductPrice").value,
    );
    product.stock = parseInt(document.getElementById("editProductStock").value);
    product.category = document.getElementById("editProductCategory").value;

    saveToLocalStorage(allProducts);
    updateAnalytics(allProducts);
    updateCategoryCounts();
    applyFiltersAndRender();

    closeEditModal();
  });
}
