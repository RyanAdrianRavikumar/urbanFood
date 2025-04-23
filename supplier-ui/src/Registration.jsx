import { useState } from "react";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";
import styles from "./Registration.module.css"; // Import CSS module

const Registration = () => {
  const [fullname, setFullname] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [phone, setPhone] = useState("");
  const [address, setAddress] = useState("");

  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    console.log("Attempting registration with:", { fullname, email, password, phone, address });

    try {
      const response = await axios.post("http://localhost:8080/users/register", {
        fullname,
        email,
        password,
        phone,
        address,
      });

      console.log("Registration Successful:", response.data);
      alert("Registration Successful! Please login.");
      navigate("/"); // Redirect to login page
    } catch (error) {
      console.error("Registration Failed:", error.response ? error.response.data : error.message);
      alert("Registration failed. Please try again.");
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.registerBox}>
        <h2 className={styles.title}>Register</h2>
        <form className={styles.registerForm} onSubmit={handleRegister}>
          <div className={styles.inputGroup}>
            <label>Full Name:</label>
            <input
              className={styles.input}
              type="text"
              value={fullname}
              onChange={(e) => setFullname(e.target.value)}
              required
            />
          </div>
          <div className={styles.inputGroup}>
            <label>Email:</label>
            <input
              className={styles.input}
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className={styles.inputGroup}>
            <label>Password:</label>
            <input
              className={styles.input}
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          <div className={styles.inputGroup}>
            <label>Phone:</label>
            <input
              className={styles.input}
              type="tel"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              required
            />
          </div>
          <div className={styles.inputGroup}>
            <label>Address:</label>
            <input
              className={styles.input}
              type="text"
              value={address}
              onChange={(e) => setAddress(e.target.value)}
              required
            />
          </div>
          <button className={styles.registerButton} type="submit">
            Register
          </button>
        </form>
        <p className={styles.footerText}>
          Already have an account? <Link to="/">Login</Link>
        </p>
      </div>
    </div>
  );
};

export default Registration;