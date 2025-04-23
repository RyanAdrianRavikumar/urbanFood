import React, { useEffect, useState, useRef } from "react";
import "bootstrap/dist/css/bootstrap.min.css";

const Product = () => {
  const [products, setProducts] = useState([]);
  const [editingProduct, setEditingProduct] = useState(null);
  const [formData, setFormData] = useState({
    productName: "",
    productCategory: "",
    price: "",
    availableStock: "",
  });
  const [searchQuery, setSearchQuery] = useState("");
  const [isSearchActive, setIsSearchActive] = useState(false);
  const [showOutOfStock, setShowOutOfStock] = useState(false);
  const [newProduct, setNewProduct] = useState({
    supplierId: "",
    name: "",
    category: "",
    price: "",
    stock: "",
    image: null,
  });
  const [notifiedSuppliers, setNotifiedSuppliers] = useState(new Set());
  const [notificationStatus, setNotificationStatus] = useState({});

  const editSectionRef = useRef(null);

  const checkStockLevels = async (products) => {
    const newNotifiedSuppliers = new Set(notifiedSuppliers);
    const newNotificationStatus = { ...notificationStatus };
    let needsUpdate = false;

    for (const product of products) {
      if (
        product.available_stock < 10 &&
        product.supplier_id &&
        !notifiedSuppliers.has(product.supplier_id)
      ) {
        try {
          const response = await fetch(
            `http://localhost:8086/Suppliers/${product.supplier_id}/LowStockEmail`,
            { method: "POST" }
          );

          if (response.ok) {
            newNotifiedSuppliers.add(product.supplier_id);
            newNotificationStatus[product.supplier_id] = {
              status: "success",
              message: "Supplier notified",
            };
            needsUpdate = true;
          } else {
            newNotificationStatus[product.supplier_id] = {
              status: "failed",
              message: "Notification failed",
            };
          }
        } catch (error) {
          console.error(`Failed to notify supplier ${product.supplier_id}:`, error);
          newNotificationStatus[product.supplier_id] = {
            status: "error",
            message: "Error sending notification",
          };
        }
      }
    }

    if (needsUpdate) {
      setNotifiedSuppliers(newNotifiedSuppliers);
      setNotificationStatus(newNotificationStatus);
    }
  };

  const fetchAllProducts = async () => {
    try {
      const response = await fetch("http://localhost:8080/products");
      if (response.ok) {
        const data = await response.json();
        setProducts(data);
        checkStockLevels(data);
      } else {
        console.error("Failed to fetch products.");
      }
    } catch (error) {
      console.error("Error fetching products:", error);
    }
  };

  useEffect(() => {
    fetchAllProducts();
  }, []);

  const handleSearch = async () => {
    if (!searchQuery.trim()) return fetchAllProducts();
    setIsSearchActive(true);
    try {
      const res = await fetch(`http://localhost:8080/products/name/${searchQuery}`);
      if (res.ok) {
        const prod = await res.json();
        setProducts([prod]);
      } else {
        alert("No products found.");
      }
    } catch (err) {
      console.error(err);
    }
  };

  const cancelSearch = () => {
    setSearchQuery("");
    setIsSearchActive(false);
    fetchAllProducts();
  };

  const handleOutOfStock = () => {
    setShowOutOfStock(!showOutOfStock);
  };

  const handleEdit = (product) => {
    setEditingProduct(product);
    setFormData({
      productName: product.product_name,
      productCategory: product.product_category,
      price: product.price,
      availableStock: product.available_stock,
    });
    setTimeout(
      () => editSectionRef.current?.scrollIntoView({ behavior: "smooth" }),
      100
    );
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this product?")) return;
    try {
      const res = await fetch(`http://localhost:8080/products/${id}`, {
        method: "DELETE",
      });
      if (res.ok) setProducts((ps) => ps.filter((p) => p.product_id !== id));
      else alert("Delete failed");
    } catch (err) {
      console.error(err);
    }
  };

  const handleChange = (e) =>
    setFormData((f) => ({ ...f, [e.target.name]: e.target.value }));

  const handleUpdate = async () => {
    if (!editingProduct) return;
    try {
      const res = await fetch(
        `http://localhost:8080/products/${editingProduct.product_id}`,
        {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(formData),
        }
      );
      if (res.ok) {
        const updatedProduct = { ...editingProduct, ...formData };
        setProducts((ps) =>
          ps.map((p) =>
            p.product_id === editingProduct.product_id ? updatedProduct : p
          )
        );
        
        // Check stock after update
        if (formData.availableStock < 10 && updatedProduct.supplier_id) {
          checkStockLevels([updatedProduct]);
        }
        
        setEditingProduct(null);
      } else alert("Update failed");
    } catch (err) {
      console.error(err);
    }
  };

  const handleNewProductChange = (e) => {
    const { name, value, files } = e.target;
    setNewProduct((np) => ({ ...np, [name]: files ? files[0] : value }));
  };

  const handleAddProduct = async () => {
    const form = new FormData();
    form.append("supplierId", newProduct.supplierId);
    form.append("name", newProduct.name);
    form.append("category", newProduct.category);
    form.append("price", newProduct.price);
    form.append("stock", newProduct.stock);
    form.append("image", newProduct.image);
    try {
      const res = await fetch("http://localhost:8080/products/add", {
        method: "POST",
        body: form,
      });
      if (res.ok) {
        alert("Product added successfully!");
        fetchAllProducts();
        setNewProduct({
          supplierId: "",
          name: "",
          category: "",
          price: "",
          stock: "",
          image: null,
        });
      } else alert("Add failed");
    } catch (err) {
      console.error(err);
    }
  };

  const handleGenerateReport = async () => {
    try {
      const res = await fetch("http://localhost:8080/reports/sales");
      if (res.ok) {
        const blob = await res.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = "sales_report.pdf";
        a.click();
      } else alert("Report failed");
    } catch (err) {
      console.error(err);
    }
  };

  const filteredProducts = showOutOfStock
    ? products.filter((p) => p.available_stock <= 10)
    : products;

  return (
    <div className="container mt-4">
      <h2 className="mb-4 text-center">Product Management</h2>

      <div className="input-group mb-4">
        <input
          className="form-control"
          placeholder="Search by name"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
        <button className="btn btn-primary" onClick={handleSearch}>
          Search
        </button>
        {isSearchActive && (
          <button className="btn btn-secondary ms-2" onClick={cancelSearch}>
            Clear
          </button>
        )}
        <button
          className={`btn ms-2 ${
            showOutOfStock ? "btn-danger" : "btn-outline-danger"
          }`}
          onClick={handleOutOfStock}
        >
          {showOutOfStock ? "Show All Products" : "Show Low Stock"}
        </button>
      </div>

      <div className="card mb-4 p-3">
        <h5>Add New Product</h5>
        <div className="row g-2">
          <div className="col">
            <input
              name="supplierId"
              placeholder="Supplier ID"
              value={newProduct.supplierId}
              onChange={handleNewProductChange}
              className="form-control"
            />
          </div>
          <div className="col">
            <input
              name="name"
              placeholder="Name"
              value={newProduct.name}
              onChange={handleNewProductChange}
              className="form-control"
            />
          </div>
          <div className="col">
            <input
              name="category"
              placeholder="Category"
              value={newProduct.category}
              onChange={handleNewProductChange}
              className="form-control"
            />
          </div>
          <div className="col">
            <input
              type="number"
              name="price"
              placeholder="Price"
              value={newProduct.price}
              onChange={handleNewProductChange}
              className="form-control"
            />
          </div>
          <div className="col">
            <input
              type="number"
              name="stock"
              placeholder="Stock"
              value={newProduct.stock}
              onChange={handleNewProductChange}
              className="form-control"
            />
          </div>
          <div className="col">
            <input
              type="file"
              name="image"
              accept="image/*"
              onChange={handleNewProductChange}
              className="form-control"
            />
          </div>
          <div className="col-auto">
            <button className="btn btn-success" onClick={handleAddProduct}>
              Add
            </button>
          </div>
        </div>
      </div>

      <div className="d-flex justify-content-end mb-3">
        <button className="btn btn-warning" onClick={handleGenerateReport}>
          Sales Report
        </button>
      </div>

      <div className="row">
        {filteredProducts.map((p) => (
          <div key={p.product_id} className="col-md-4 mb-4">
            <div className="card h-100">
              {p.product_image ? (
                <img
                  src={`data:image/jpeg;base64,${p.product_image}`}
                  className="card-img-top"
                  style={{ height: 200, objectFit: "cover" }}
                  alt="Product"
                />
              ) : (
                <div
                  style={{ height: 200 }}
                  className="bg-light d-flex align-items-center justify-content-center"
                >
                  No Image
                </div>
              )}
              <div className="card-body text-center">
                <h5>{p.product_name}</h5>
                <p>
                  <strong>Cat:</strong> {p.product_category}
                </p>
                <p>
                  <strong>Price:</strong> ${p.price}
                </p>
                <p className={p.available_stock === 0 ? "text-danger" : ""}>
                  <strong>Stock:</strong> {p.available_stock}
                  {p.available_stock < 10 && notificationStatus[p.supplier_id]?.status === "success" && (
                    <span className="badge bg-warning text-dark ms-2">
                      Supplier Notified
                    </span>
                  )}
                </p>
                <button
                  className="btn btn-primary me-2"
                  onClick={() => handleEdit(p)}
                >
                  Edit
                </button>
                <button
                  className="btn btn-danger"
                  onClick={() => handleDelete(p.product_id)}
                >
                  Delete
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {editingProduct && (
        <div className="card p-4 mb-4" ref={editSectionRef}>
          <h5>Edit Product</h5>
          <div className="mb-2">
            <input
              name="productName"
              value={formData.productName}
              onChange={handleChange}
              className="form-control"
            />
          </div>
          <div className="mb-2">
            <input
              name="productCategory"
              value={formData.productCategory}
              onChange={handleChange}
              className="form-control"
            />
          </div>
          <div className="mb-2">
            <input
              name="price"
              type="number"
              value={formData.price}
              onChange={handleChange}
              className="form-control"
            />
          </div>
          <div className="mb-2">
            <input
              name="availableStock"
              type="number"
              value={formData.availableStock}
              onChange={handleChange}
              className="form-control"
            />
          </div>
          <button className="btn btn-success me-2" onClick={handleUpdate}>
            Save
          </button>
          <button
            className="btn btn-secondary"
            onClick={() => setEditingProduct(null)}
          >
            Cancel
          </button>
        </div>
      )}
    </div>
  );
};

export default Product;