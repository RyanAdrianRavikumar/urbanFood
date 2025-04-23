import React, { useState } from "react";
import { Routes, Route } from "react-router-dom";

import CustomerLogin from "./CustomerLogin";
import ProductCustomer from "./ProductCustomer";
import Delivery from "./Delivery"; // Assuming this component exists

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  const handleLoginSuccess = () => {
    setIsLoggedIn(true);
  };

  return (
    <Routes>
      <Route
        path="/"
        element={
          isLoggedIn ? (
            <ProductCustomer />
          ) : (
            <CustomerLogin onLoginSuccess={handleLoginSuccess} />
          )
        }
      />
      <Route path="/delivery" element={<Delivery />} />
    </Routes>
  );
}

export default App;
