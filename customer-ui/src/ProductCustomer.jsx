import React, { useEffect, useState, useRef, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";

const ProductCustomer = () => {
  const [products, setProducts] = useState([]);
  const [selectedProducts, setSelectedProducts] = useState([]);
  const [orderId, setOrderId] = useState(null);
  const [showPaymentForm, setShowPaymentForm] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState("");
  const [paymentAmount, setPaymentAmount] = useState(0);

  const cartRef = useRef(null);
  const navigate = useNavigate();

  const fetchAllProducts = useCallback(async () => {
    try {
      const response = await fetch("http://localhost:8080/products");
      if (response.ok) {
        const data = await response.json();
        setProducts(data);

        data.forEach((product) => {
          if (product.available_stock < 10 && product.supplier_id) {
            notifyLowStock(product.supplier_id);
          }
        });
      } else {
        alert("Failed to load products. Please try again later.");
      }
    } catch (error) {
      alert("Network error. Please check your connection.");
    }
  }, []);

  useEffect(() => {
    fetchAllProducts();
  }, [fetchAllProducts]);

  const notifyLowStock = async (supplierId) => {
    try {
      const response = await fetch(
        `http://localhost:8086/Suppliers/${supplierId}/LowStockEmail`,
        { method: "POST" }
      );
      if (response.ok) {
        console.log(`Low stock notification sent to supplier ${supplierId}`);
      } else {
        const msg = await response.text();
        console.warn("Failed to send low stock email:", msg);
      }
    } catch (error) {
      console.error("Error sending low stock notification:", error);
    }
  };

  const handleQuantityChange = (productId, newQuantity) => {
    setSelectedProducts((prev) =>
      prev.map((item) =>
        item.productId === productId ? { ...item, quantity: newQuantity } : item
      )
    );
  };

  const handleAddToCart = (productId) => {
    const existing = selectedProducts.find((item) => item.productId === productId);
    if (existing) return;

    setSelectedProducts((prev) => {
      const updated = [...prev, { productId, quantity: 1 }];
      if (cartRef.current) {
        cartRef.current.scrollIntoView({ behavior: "smooth" });
      }
      return updated;
    });
  };

  const handlePlaceOrder = async () => {
    const customerId = localStorage.getItem("customerId");
    if (!customerId) {
      alert("Login required to place order.");
      return;
    }

    const productIds = selectedProducts.map((item) => item.productId);
    const quantities = selectedProducts.map((item) => item.quantity);

    try {
      const response = await fetch(`http://localhost:8080/customer/${customerId}/order`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ productIds, quantities }),
      });

      if (response.ok) {
        const responseText = await response.text();
        const newOrderId = parseInt(responseText.match(/\d+/)?.[0]);
        setOrderId(newOrderId);

        const total = selectedProducts.reduce((sum, item) => {
          const prod = products.find((p) => p.product_id === item.productId);
          return sum + item.quantity * (prod?.price || 0);
        }, 0);

        setPaymentAmount(total.toFixed(2));
        setShowPaymentForm(true);
        setSelectedProducts([]);
      } else {
        const errorText = await response.text();
        alert("Failed to place order: " + errorText);
      }
    } catch (error) {
      alert("Order failed due to network error.");
    }
  };

  const handlePayment = async () => {
    const customerId = localStorage.getItem("customerId");
    if (!paymentMethod || !paymentAmount) {
      alert("Please select payment method.");
      return;
    }

    const productNames = selectedProducts.map((item) => {
      const product = products.find((p) => p.product_id === item.productId);
      return product ? product.product_name : "Unknown";
    });

    try {
      const response = await fetch(
        `http://localhost:8080/customers/${customerId}/orders/${orderId}/payments`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            paymentMethod,
            amount: parseFloat(paymentAmount),
            productNames,
          }),
        }
      );

      const result = await response.text();
      if (response.ok) {
        alert("Payment successful!");
        setShowPaymentForm(false);
      } else {
        alert("Payment failed: " + result);
      }
    } catch (err) {
      console.error(err);
      alert("Payment failed due to network error.");
    }
  };

  const getProductName = (productId) => {
    const product = products.find((product) => product.product_id === productId);
    return product ? product.product_name : "Unknown";
  };

  const handleWriteReview = (productId) => {
    const customerId = localStorage.getItem("customerId");
    if (!customerId) {
      alert("Please log in to write a review.");
      return;
    }

    const product = products.find((p) => p.product_id === productId);
    const productName = product ? product.product_name : "Unknown";
    const encodedProductName = encodeURIComponent(productName);
    const reviewUrl = `http://192.168.8.196:3000/reviews?customerId=${customerId}&productId=${productId}&productName=${encodedProductName}`;
    window.open(reviewUrl, "_blank");

  };

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2 className="mb-0">Products</h2>
        <div>
          <button
            className="btn btn-info btn-lg me-2"
            onClick={() => navigate("/Delivery")}
            aria-label="View my orders"
          >
            View My Orders
          </button>
          <a
            href={`http://192.168.8.196:3000/feedback?customerId=${localStorage.getItem("customerId")}`}
            target="_blank"
            rel="noopener noreferrer"
            className="btn btn-warning btn-lg"
            aria-label="Leave feedback or review"
          >
            Leave Feedback / Review
          </a>
        </div>
      </div>

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
                <h3 className="card-title h5">{product.product_name}</h3>
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
                  onClick={() => handleAddToCart(product.product_id)}
                  aria-label={`Add ${product.product_name} to cart`}
                >
                  Add to Cart
                </button>
                <button
                  className="btn btn-outline-secondary btn-sm mt-3"
                  onClick={() => handleWriteReview(product.product_id)}
                  aria-label={`Write review for ${product.product_name}`}
                >
                  Write a Review
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {selectedProducts.length > 0 ? (
        <div ref={cartRef} className="card mt-4 p-4 shadow-sm" aria-live="polite">
          <h3 className="mb-3">Cart</h3>
          <ul className="list-unstyled">
            {selectedProducts.map((item) => {
              const productName = getProductName(item.productId);
              const subtotal =
                item.quantity *
                products.find((p) => p.product_id === item.productId)?.price;

              return (
                <li key={item.productId} className="mb-3 d-flex align-items-center">
                  <span className="me-3">{productName}</span>
                  <div className="btn-group me-3" role="group">
                    <button
                      className="btn btn-secondary btn-sm"
                      onClick={() => handleQuantityChange(item.productId, item.quantity - 1)}
                      disabled={item.quantity <= 1}
                      aria-label={`Decrease quantity of ${productName}`}
                    >
                      -
                    </button>
                    <span className="px-3">{item.quantity}</span>
                    <button
                      className="btn btn-secondary btn-sm"
                      onClick={() => handleQuantityChange(item.productId, item.quantity + 1)}
                      aria-label={`Increase quantity of ${productName}`}
                    >
                      +
                    </button>
                  </div>
                  <span>Subtotal: ${subtotal.toFixed(2)}</span>
                </li>
              );
            })}
          </ul>
          <button
            className="btn btn-success btn-lg mt-2"
            onClick={handlePlaceOrder}
            aria-label="Place order"
          >
            Place Order
          </button>
        </div>
      ) : (
        <div ref={cartRef} className="mt-4 text-center">
          <h3>Your cart is empty</h3>
        </div>
      )}

      {showPaymentForm && (
        <div className="card mt-4 p-4 shadow-sm" role="dialog" aria-modal="true">
          <h3 className="mb-3">Make Payment</h3>
          <div className="mb-3">
            <label htmlFor="method" className="form-label">Payment Method</label>
            <select
              id="method"
              className="form-select"
              value={paymentMethod}
              onChange={(e) => setPaymentMethod(e.target.value)}
              aria-required="true"
            >
              <option value="">-- Select Payment Method --</option>
              <option value="Credit Card">Credit Card</option>
              <option value="Debit Card">Debit Card</option>
              <option value="Cash on Delivery">Cash on Delivery</option>
              <option value="PayPal">PayPal</option>
            </select>
          </div>
          <div className="mb-3">
            <label htmlFor="amount" className="form-label">Amount to Pay</label>
            <input
              id="amount"
              type="number"
              className="form-control"
              value={paymentAmount}
              readOnly
            />
          </div>
          <button className="btn btn-primary" onClick={handlePayment}>
            Confirm Payment
          </button>
        </div>
      )}
    </div>
  );
};

export default ProductCustomer;
