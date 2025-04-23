import React, { useEffect, useState, useRef } from "react";
import "bootstrap/dist/css/bootstrap.min.css";

const ProductCustomer = () => {
  const [products, setProducts] = useState([]);
  const [selectedProducts, setSelectedProducts] = useState([]);
  const [orderId, setOrderId] = useState(null);
  const [showPaymentForm, setShowPaymentForm] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState("");
  const [paymentAmount, setPaymentAmount] = useState(0);

  const cartRef = useRef(null); // Reference for the cart section

  useEffect(() => {
    fetchAllProducts();
  }, []);

  const fetchAllProducts = async () => {
    try {
      const response = await fetch("http://localhost:8080/products");
      if (response.ok) {
        const data = await response.json();
        setProducts(data);
      } else {
        alert("Failed to load products. Please try again later.");
      }
    } catch (error) {
      alert("Network error. Please check your connection.");
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

    try {
      const response = await fetch(
        `http://localhost:8080/customers/${customerId}/orders/${orderId}/payments`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            paymentMethod,
            amount: parseFloat(paymentAmount),
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

  return (
    <div className="container mt-4">
      <h2 className="mb-4 text-center">Products</h2>
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
                  onClick={() => handleAddToCart(product.product_id)}
                >
                  Add to Cart
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {selectedProducts.length > 0 ? (
        <div ref={cartRef} className="card mt-4 p-4 shadow-sm">
          <h3 className="mb-3">Cart</h3>
          <ul>
            {selectedProducts.map((item) => {
              const productName = getProductName(item.productId);
              const subtotal =
                item.quantity *
                products.find((p) => p.product_id === item.productId)?.price;

              return (
                <li key={item.productId} className="mb-3">
                  {productName} - 
                  <button
                    className="btn btn-secondary btn-sm me-2"
                    onClick={() => handleQuantityChange(item.productId, item.quantity - 1)}
                    disabled={item.quantity <= 1}
                  >
                    -
                  </button>
                  Quantity: {item.quantity}
                  <button
                    className="btn btn-secondary btn-sm ms-2"
                    onClick={() => handleQuantityChange(item.productId, item.quantity + 1)}
                  >
                    +
                  </button>
                  - Subtotal: ${subtotal.toFixed(2)}
                </li>
              );
            })}
          </ul>
          <button className="btn btn-success btn-lg mt-2" onClick={handlePlaceOrder}>
            Place Order
          </button>
        </div>
      ) : (
        <div ref={cartRef} className="mt-4 text-center">
          <h3>Your cart is empty</h3>
        </div>
      )}

      {showPaymentForm && (
        <div className="card mt-4 p-4 shadow-sm">
          <h3 className="mb-3">Make Payment</h3>
          <div className="mb-3">
            <label htmlFor="method" className="form-label">Payment Method</label>
            <select
              id="method"
              className="form-select"
              value={paymentMethod}
              onChange={(e) => setPaymentMethod(e.target.value)}
            >
              <option value="">-- Select Payment Method --</option>
              <option value="Credit Card">Credit Card</option>
              <option value="Debit Card">Debit Card</option>
              <option value="Cash on Delivery">Cash on Delivery</option>
              <option value="PayPal">PayPal</option>
            </select>
          </div>
          <div className="mb-3">
            <label className="form-label">Amount to Pay</label>
            <input
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
