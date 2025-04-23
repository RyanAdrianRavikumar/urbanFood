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

  const editSectionRef = useRef(null);

  const fetchAllProducts = async () => {
    try {
      const response = await fetch("http://localhost:8080/products");
      if (response.ok) {
        const data = await response.json();
        setProducts(data);
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
    if (searchQuery.trim() === "") {
      fetchAllProducts();
      return;
    }
    setIsSearchActive(true);
    try {
      const response = await fetch(
        `http://localhost:8080/products/name/${searchQuery}`
      );
      if (response.ok) {
        const data = await response.json();
        setProducts([data]);
      } else {
        alert("No products found with the given name.");
      }
    } catch (error) {
      console.error("Error searching product:", error);
    }
  };

  const cancelSearch = () => {
    setSearchQuery("");
    setIsSearchActive(false);
    fetchAllProducts();
  };

  const handleEdit = (product) => {
    setEditingProduct(product);
    setFormData({
      productName: product.product_name,
      productCategory: product.product_category,
      price: product.price,
      availableStock: product.available_stock,
    });

    setTimeout(() => {
      if (editSectionRef.current) {
        editSectionRef.current.scrollIntoView({ behavior: "smooth" });
      }
    }, 100);
  };

  const handleDelete = async (productId) => {
    if (window.confirm("Are you sure you want to delete this product?")) {
      try {
        const response = await fetch(
          `http://localhost:8080/products/${productId}`,
          {
            method: "DELETE",
          }
        );

        if (response.ok) {
          alert("Product deleted successfully!");
          setProducts((prevProducts) =>
            prevProducts.filter((product) => product.product_id !== productId)
          );
        } else {
          alert("Failed to delete product.");
        }
      } catch (error) {
        console.error("Error deleting product:", error);
      }
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleUpdate = async () => {
    if (!editingProduct) return;

    try {
      const response = await fetch(
        `http://localhost:8080/products/${editingProduct.product_id}`,
        {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(formData),
        }
      );

      if (response.ok) {
        alert("Product updated successfully!");
        setProducts((prevProducts) =>
          prevProducts.map((p) =>
            p.product_id === editingProduct.product_id
              ? { ...p, ...formData }
              : p
          )
        );
        setEditingProduct(null);
      } else {
        alert("Failed to update product.");
      }
    } catch (error) {
      console.error("Error updating product:", error);
    }
  };

  const handleGenerateReport = async () => {
    try {
      const response = await fetch("http://localhost:8080/reports/sales");
      if (response.ok) {
        const reportBlob = await response.blob();
        const reportURL = window.URL.createObjectURL(reportBlob);
        const link = document.createElement("a");
        link.href = reportURL;
        link.download = "sales_report.pdf";
        link.click();
      } else {
        alert("Failed to generate sales report.");
      }
    } catch (error) {
      console.error("Error generating report:", error);
    }
  };

  return (
    <div className="container mt-4">
      <h2 className="mb-4 text-center">Product List</h2>

      {/* Search Bar */}
      <div className="input-group mb-4">
        <input
          type="text"
          className="form-control"
          placeholder="Search product by name"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
        <button className="btn btn-outline-primary" onClick={handleSearch}>
          Search
        </button>
        {isSearchActive && (
          <button
            className="btn btn-outline-secondary ms-2"
            onClick={cancelSearch}
          >
            Cancel Search
          </button>
        )}
      </div>

      {/* Report Button */}
      <div className="d-flex justify-content-end mb-3">
        <button className="btn btn-warning" onClick={handleGenerateReport}>
          Generate Sales Report
        </button>
      </div>

      {products.length === 0 ? (
        <p className="text-center">Loading products...</p>
      ) : (
        <div className="row">
          {products.map((product) => (
            <div key={product.product_id} className="col-md-4 mb-4">
              <div className="card h-100 shadow-sm">
                {product.product_image ? (
                  <img
                    src={`data:image/jpeg;base64,${product.product_image}`}
                    alt={product.product_name}
                    className="card-img-top"
                    style={{ height: "200px", objectFit: "cover" }}
                  />
                ) : (
                  <div
                    className="card-img-top bg-light d-flex align-items-center justify-content-center"
                    style={{ height: "200px" }}
                  >
                    No Image Available
                  </div>
                )}
                <div className="card-body text-center">
                  <h5 className="card-title">{product.product_name}</h5>
                  <p className="card-text">
                    <strong>Category:</strong> {product.product_category}
                  </p>
                  <p className="card-text">
                    <strong>Price:</strong> ${product.price}
                  </p>
                  <p className="card-text">
                    <strong>Stock:</strong> {product.available_stock}
                  </p>
                  <button
                    className="btn btn-primary btn-lg mt-2"
                    style={{ padding: "10px 20px" }}
                    onClick={() => handleEdit(product)}
                  >
                    Edit
                  </button>
                  <button
                    className="btn btn-danger btn-lg mt-2 ms-2"
                    style={{ padding: "10px 20px" }}
                    onClick={() => handleDelete(product.product_id)}
                  >
                    Delete
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {editingProduct && (
        <div className="card mt-4 p-4 shadow-sm" ref={editSectionRef}>
          <h3 className="mb-3">Edit Product</h3>
          <div className="mb-3">
            <label className="form-label">Product Name:</label>
            <input
              type="text"
              name="productName"
              value={formData.productName}
              onChange={handleChange}
              className="form-control"
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Category:</label>
            <input
              type="text"
              name="productCategory"
              value={formData.productCategory}
              onChange={handleChange}
              className="form-control"
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Price:</label>
            <input
              type="number"
              name="price"
              value={formData.price}
              onChange={handleChange}
              className="form-control"
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Stock:</label>
            <input
              type="number"
              name="availableStock"
              value={formData.availableStock}
              onChange={handleChange}
              className="form-control"
            />
          </div>
          <div className="d-flex justify-content-start">
            <button
              className="btn btn-success btn-lg"
              style={{ padding: "10px 20px", marginRight: "10px" }}
              onClick={handleUpdate}
            >
              Update
            </button>
            <button
              className="btn btn-secondary btn-lg"
              style={{ padding: "10px 20px" }}
              onClick={() => setEditingProduct(null)}
            >
              Cancel
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Product;
