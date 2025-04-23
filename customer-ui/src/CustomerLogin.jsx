import React, { useState } from "react";
import { useNavigate } from "react-router-dom"; 

const CustomerLogin = ({ onLoginSuccess }) => {
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });
  const [loginMessage, setLoginMessage] = useState("");

  const navigate = useNavigate(); 

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleLogin = async () => {
    try {
      const response = await fetch("http://localhost:8080/customer/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });

      const data = await response.json();

      if (response.ok) {
        setLoginMessage(data.message);
        localStorage.setItem("customerId", data.customerId);
        onLoginSuccess(data.customerId);
      } else {
        setLoginMessage("Login failed. " + data.message);
      }
    } catch (error) {
      console.error("Error during login:", error);
      setLoginMessage("Login failed due to a network issue.");
    }
  };

  return (
    <div className="container mt-4">
      <h2 className="text-center mb-4">Customer Login</h2>
      <div className="card p-4 shadow-sm">
        <div className="mb-3">
          <label className="form-label">Email:</label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            className="form-control"
            placeholder="Enter your email"
          />
        </div>
        <div className="mb-3">
          <label className="form-label">Password:</label>
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            className="form-control"
            placeholder="Enter your password"
          />
        </div>

        <div className="d-flex justify-content-between">
          <button
            className="btn btn-primary btn-lg"
            style={{ padding: "10px 20px" }}
            onClick={handleLogin}
          >
            Login
          </button>

          
          
        </div>

        {loginMessage && (
          <div className="mt-3 alert alert-info">{loginMessage}</div>
        )}
      </div>
    </div>
  );
};

export default CustomerLogin;
