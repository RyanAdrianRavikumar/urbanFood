import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";

const MenuItems = () => {
    const { restaurantId } = useParams();
    const userId = localStorage.getItem("userId");

    const [menuItems, setMenuItems] = useState([]);
    const [selectedItems, setSelectedItems] = useState({});
    const [orderPlaced, setOrderPlaced] = useState(false);
    const [discountCode, setDiscountCode] = useState(""); // Input for discount code
    const [discountId, setDiscountId] = useState(null); // Store discount ID
    const [discountValue, setDiscountValue] = useState(0); // Store discount percentage

    useEffect(() => {
        if (!restaurantId) {
            console.error("Restaurant ID is missing!");
            return;
        }

        fetch(`http://localhost:8082/restaurants/${restaurantId}/menu`)
            .then(response => response.json())
            .then(data => {
                console.log("Fetched menu data:", data);
                setMenuItems(data);
            })
            .catch(error => console.error("Error fetching menu:", error));
    }, [restaurantId]);

    const handleSelectItem = (itemId, qty, price) => {
        setSelectedItems(prevSelected => ({
            ...prevSelected,
            [itemId]: qty ? { qty, price } : undefined,
        }));
    };

    const handlePlaceOrder = () => {
        if (Object.keys(selectedItems).length === 0) {
            alert("Please select at least one item and specify quantities before placing the order.");
            return;
        }
        setOrderPlaced(true);
    };

    // Fetch discount ID and value using the discount code
    const fetchDiscountId = async () => {
        if (!discountCode) {
            alert("Please enter a discount code.");
            return;
        }

        try {
            const response = await fetch(`http://localhost:8085/discounts/by-code/${discountCode}`);
            if (!response.ok) {
                throw new Error("Invalid discount code.");
            }
            const discountData = await response.json();
            setDiscountId(discountData.id); // Store the discount ID
            setDiscountValue(discountData.discountValue); // Store the discount value
            alert(`Discount applied: ${discountData.discountValue}%`);
        } catch (error) {
            console.error("Error fetching discount:", error);
            alert("Invalid discount code. Please try again.");
        }
    };

    const handleConfirmOrder = () => {
        if (!userId || !restaurantId) {
            alert("User or Restaurant ID is missing.");
            return;
        }

        const orderData = {
            restaurantId,
            driverId: null,
            discountId: discountId, // Include discount ID if available
            menuItems: Object.keys(selectedItems).map(id => ({
                menuItemId: id,
                qty: selectedItems[id].qty,
                price: selectedItems[id].price,
            })),
        };

        console.log("Order Data Sent:", orderData);

        fetch(`http://localhost:8083/users/${userId}/orders`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(orderData),
        })
            .then(response => response.json())
            .then(data => {
                if (data.status === 400) {
                    alert("Error: Bad Request. Please check the order details.");
                } else {
                    alert("Order placed successfully!");
                    setSelectedItems({});
                    setOrderPlaced(false);
                }
            })
            .catch(error => {
                console.error("Error placing order:", error);
                alert("Something went wrong. Please try again.");
            });
    };

    const calculateTotalAmount = () => {
        const totalAmount = Object.keys(selectedItems).reduce((total, itemId) => {
            const item = selectedItems[itemId];
            return total + (item.qty * item.price || 0);
        }, 0);
        
        // Apply the discount value if available
        const discountAmount = totalAmount * (discountValue / 100); // Apply the discount percentage
        const totalWithDiscount = totalAmount - discountAmount;

        return { totalAmount: totalAmount.toFixed(2), totalWithDiscount: totalWithDiscount.toFixed(2) };
    };

    const { totalAmount, totalWithDiscount } = calculateTotalAmount();

    return (
        <div style={{ padding: "20px" }}>
            <h2>Menu</h2>
            {menuItems.length === 0 ? (
                <p>Loading menu...</p>
            ) : (
                <div style={{ display: "flex", flexWrap: "wrap", gap: "20px" }}>
                    {menuItems.map(item => (
                        <div
                            key={item.id}
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
                            {item.picture && (
                                <img
                                    src={`data:image/jpeg;base64,${item.picture}`}
                                    alt={item.name}
                                    style={{
                                        width: "100%",
                                        height: "150px",
                                        objectFit: "cover",
                                        borderRadius: "10px",
                                        marginBottom: "10px",
                                    }}
                                />
                            )}
                            <h3 style={{ margin: "5px 0", textAlign: "center" }}>{item.name}</h3>
                            <p style={{ margin: "3px 0", fontSize: "14px" }}>
                                <strong>Price:</strong> ${item.price}
                            </p>
                            <div style={{ display: "flex", alignItems: "center", margin: "10px 0" }}>
                                <label>
                                    <input
                                        type="checkbox"
                                        checked={selectedItems[item.id] !== undefined}
                                        onChange={(e) =>
                                            handleSelectItem(item.id, e.target.checked ? 1 : null, item.price)
                                        }
                                        style={{ marginRight: "5px" }}
                                    />
                                    Select
                                </label>
                                {selectedItems[item.id] !== undefined && (
                                    <input
                                        type="number"
                                        min="1"
                                        value={selectedItems[item.id]?.qty || 1}
                                        onChange={(e) =>
                                            handleSelectItem(item.id, parseInt(e.target.value, 10), item.price)
                                        }
                                        style={{
                                            width: "50px",
                                            marginLeft: "10px",
                                            textAlign: "center",
                                        }}
                                    />
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            )}

            <div style={{ marginTop: "20px", fontSize: "18px", fontWeight: "bold" }}>
                <p>Total Amount: ${totalAmount}</p>
                <p style={{ color: "green" }}>Total with {discountValue}% Discount: ${totalWithDiscount}</p>
            </div>

            <div style={{ marginTop: "20px" }}>
                <input
                    type="text"
                    placeholder="Enter discount code"
                    value={discountCode}
                    onChange={(e) => setDiscountCode(e.target.value)}
                    style={{ padding: "8px", marginRight: "10px" }}
                />
                <button onClick={fetchDiscountId} style={{ padding: "8px 12px", cursor: "pointer" }}>
                    Apply Discount
                </button>
            </div>

            <button
                onClick={handlePlaceOrder}
                disabled={orderPlaced || Object.keys(selectedItems).length === 0}
                style={{
                    marginTop: "20px",
                    padding: "10px 15px",
                    fontSize: "16px",
                    backgroundColor: "#007bff",
                    color: "white",
                    border: "none",
                    borderRadius: "5px",
                    cursor: Object.keys(selectedItems).length === 0 ? "not-allowed" : "pointer",
                }}
            >
                Place Order
            </button>

            {orderPlaced && (
                <div style={{ marginTop: "20px" }}>
                    <p>Confirm your order?</p>
                    <button onClick={handleConfirmOrder} style={{ padding: "8px 12px", backgroundColor: "green", color: "white", marginRight: "10px" }}>
                        Confirm
                    </button>
                    <button onClick={() => setOrderPlaced(false)} style={{ padding: "8px 12px", backgroundColor: "red", color: "white" }}>
                        Cancel
                    </button>
                </div>
            )}
        </div>
    );
};

export default MenuItems;
