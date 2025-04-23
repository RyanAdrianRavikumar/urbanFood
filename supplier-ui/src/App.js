import React, { useState } from "react";
import CustomerLogin from "./CustomerLogin"; // Import CustomerLogin component
import ProductCustomer from "./Product"; // Import ProductCustomer component
import Product from "./Product";

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false); // State to track login status

  // Function to handle successful login
  const handleLoginSuccess = () => {
    setIsLoggedIn(true); // Set the login status to true
  };

  return (
    <div>
      {isLoggedIn ? (
        <ProductCustomer /> // Render ProductCustomer if logged in
      ) : (
        <CustomerLogin onLoginSuccess={handleLoginSuccess} /> // Render CustomerLogin if not logged in
      )}
    </div>
  );
}

export default App;
