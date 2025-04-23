import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const Product = () => {
  const [products, setProducts] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    // Fetch all products from the API
    fetch("http://localhost:8080/products")
      .then((response) => response.json())
      .then((data) => setProducts(data)) // Store products data in state
      .catch((error) => console.error("Error fetching products:", error));
  }, []);

  const handleSelectProduct = (productId) => {
    localStorage.setItem("selectedProductId", productId);
    navigate(`/product/${productId}`); // Navigate to the product detail page (you can modify this as per your routing)
  };

  return (
    <div style={{ padding: "20px" }}>
      <h2>Product List</h2>
      {products.length === 0 ? (
        <p>Loading products...</p>
      ) : (
        <div style={{ display: "flex", flexWrap: "wrap", gap: "20px" }}>
          {products.map((product) => (
            <div
              key={product.product_id}
              onClick={() => handleSelectProduct(product.product_id)}
              style={{
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                width: "250px",
                padding: "15px",
                border: "1px solid #ddd",
                borderRadius: "10px",
                boxShadow: "2px 2px 10px rgba(0, 0, 0, 0.1)",
                backgroundColor: "#fff",
                cursor: "pointer",
                transition: "transform 0.2s ease-in-out",
              }}
              onMouseEnter={(e) => (e.currentTarget.style.transform = "scale(1.03)")}
              onMouseLeave={(e) => (e.currentTarget.style.transform = "scale(1)")}
            >
              {/* Display product image */}
              {product.product_image ? (
                <img
                  src={`data:image/jpeg;base64,${product.product_image}`}
                  alt={product.product_name}
                  style={{
                    width: "100%",
                    height: "150px",
                    objectFit: "cover",
                    borderRadius: "10px",
                    marginBottom: "10px",
                  }}
                />
              ) : (
                <div style={{ width: "100%", height: "150px", backgroundColor: "#f0f0f0", borderRadius: "10px", marginBottom: "10px" }}>
                  No Image Available
                </div>
              )}
              <h3 style={{ margin: "5px 0", textAlign: "center" }}>{product.product_name}</h3>
              <p style={{ margin: "3px 0", fontSize: "14px" }}>
                <strong>Category:</strong> {product.product_category}
              </p>
              <p style={{ margin: "3px 0", fontSize: "14px" }}>
                <strong>Price:</strong> ${product.price}
              </p>
              <p style={{ margin: "3px 0", fontSize: "14px" }}>
                <strong>Stock:</strong> {product.available_stock} available
              </p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Product;
