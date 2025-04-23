import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const RestaurantList = () => {
  const [restaurants, setRestaurants] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    fetch("http://localhost:8081/restaurants")
      .then((response) => response.json())
      .then((data) => setRestaurants(data))
      .catch((error) => console.error("Error fetching restaurants:", error));
  }, []);

  const handleSelectRestaurant = (restaurantId) => {
    localStorage.setItem("selectedRestaurantId", restaurantId);
    navigate(`/menu/${restaurantId}`);
  };

  return (
    <div style={{ padding: "20px" }}>
      <h2>Select a Restaurant</h2>
      {restaurants.length === 0 ? (
        <p>Loading restaurants...</p>
      ) : (
        <div style={{ display: "flex", flexWrap: "wrap", gap: "20px" }}>
          {restaurants.map((restaurant) => (
            <div
              key={restaurant.id}
              onClick={() => handleSelectRestaurant(restaurant.id)}
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
              {restaurant.picture && (
                <img
                  src={`data:image/jpeg;base64,${restaurant.picture}`}
                  alt={restaurant.name}
                  style={{
                    width: "100%",
                    height: "150px",
                    objectFit: "cover",
                    borderRadius: "10px",
                    marginBottom: "10px",
                  }}
                />
              )}
              <h3 style={{ margin: "5px 0", textAlign: "center" }}>{restaurant.name}</h3>
              <p style={{ margin: "3px 0", fontSize: "14px" }}>
                <strong>Status:</strong> {restaurant.status.toUpperCase()}
              </p>
              <p style={{ margin: "3px 0", fontSize: "14px" }}>
                <strong>Rating:</strong> ⭐ {restaurant.rating ? restaurant.rating.toFixed(1) : "N/A"}
              </p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default RestaurantList;
