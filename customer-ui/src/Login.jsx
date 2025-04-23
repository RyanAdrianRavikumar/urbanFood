import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import styles from "./Login.module.css"; // Import CSS module

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post("http://localhost:8080/users/login", { email, password });
      if (response.data && response.data.userId) {
        localStorage.setItem("userId", response.data.userId);
        localStorage.setItem("token", response.data.token);
        navigate("/restaurants");
      } else {
        setErrorMessage("User ID not received. Please try again.");
      }
    } catch (error) {
      setErrorMessage("Invalid email or password.");
    }
  };

  return (
    <div className={styles["login-container"]}>
      {/* Left Side */}
      <div className={styles["login-left"]}>
        <h2>Welcome back!</h2>
        <p>You can sign in to access your existing account.</p>
      </div>

      {/* Right Side */}
      <div className={styles["login-right"]}>
        <h2>Sign In</h2>
        <form onSubmit={handleSubmit} className={styles["login-form"]}>
          <div className={styles["input-group"]}>
            <input
              type="email"
              placeholder="Username or email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className={styles["input-group"]}>
            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          {/* Remember Me & Forgot Password */}
          <div className={styles["options"]}>
            <label>
              <input type="checkbox" /> Remember me
            </label>
            <Link to="/forgot-password" className={styles["forgot-password"]}>
              Forgot password?
            </Link>
          </div>

          {errorMessage && <p className={styles["error-message"]}>{errorMessage}</p>}

          {/* Sign In Button */}
          <button type="submit" className={styles["login-button"]}>Sign In</button>
        </form>

        {/* Register Link */}
        <p className={styles["register-text"]}>
          New here? <Link to="/register" className={styles["register-link"]}>Create An Account</Link>
        </p>

        
      </div>
    </div>
  );
};

export default Login;
