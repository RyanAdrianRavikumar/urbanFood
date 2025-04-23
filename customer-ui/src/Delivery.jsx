import React, { useEffect, useState } from "react";

const Delivery = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchOrders();
  }, []);

  // Function to group orders by order_id
  const groupOrdersById = (data) => {
    if (!Array.isArray(data)) return [];
    
    const grouped = {};
    
    data.forEach(item => {
      if (!item) return;
      
      if (!grouped[item.order_id]) {
        grouped[item.order_id] = {
          order_id: item.order_id,
          order_date: item.order_date,
          status: item.status || 'Unknown',
          total_amount: item.total_amount || 0,
          items: []
        };
      }
      grouped[item.order_id].items.push({
        product_name: item.product_name || 'Unknown Product',
        product_category: item.product_category || 'Unknown Category',
        quantity: item.quantity || 0,
        price: item.price || 0,
        subtotal: item.subtotal || 0
      });
    });
    
    return Object.values(grouped);
  };

  const fetchOrders = async () => {
    const customerId = localStorage.getItem("customerId");
    if (!customerId) {
      alert("Login required.");
      setLoading(false);
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/customers/${customerId}/orders`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      const groupedOrders = groupOrdersById(data || []);
      setOrders(groupedOrders);
    } catch (error) {
      console.error("Failed to fetch orders:", error);
      setOrders([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mt-4">
      <h2 className="mb-4 text-center">Your Orders</h2>

      {loading ? (
        <div className="text-center">Loading...</div>
      ) : (
        <div className="list-group">
          {orders && orders.length > 0 ? (
            orders.map((order) => (
              <div key={order.order_id} className="list-group-item mb-3">
                <div className="d-flex justify-content-between align-items-start">
                  <div>
                    <h5>Order #{order.order_id}</h5>
                    <p className="mb-1">
                      <strong>Date:</strong> {order.order_date ? new Date(order.order_date).toLocaleString() : 'Unknown date'}
                    </p>
                    <p className="mb-1">
                      <strong>Status:</strong> 
                      <span className={`badge ${order.status === 'Paid' ? 'bg-success' : 'bg-warning'} ms-2`}>
                        {order.status}
                      </span>
                    </p>
                  </div>
                  <div className="text-end">
                    <h5>Total: ${order.total_amount?.toFixed(2) || '0.00'}</h5>
                  </div>
                </div>
                
                <hr />
                
                <h6>Items:</h6>
                <ul className="list-group">
                  {order.items && order.items.length > 0 ? (
                    order.items.map((item, index) => (
                      <li key={`${order.order_id}-${index}`} className="list-group-item">
                        <div className="d-flex justify-content-between">
                          <div>
                            {item.product_name} ({item.product_category})
                          </div>
                          <div>
                            {item.quantity} x ${item.price?.toFixed(2) || '0.00'} = ${item.subtotal?.toFixed(2) || '0.00'}
                          </div>
                        </div>
                      </li>
                    ))
                  ) : (
                    <li className="list-group-item">No items in this order</li>
                  )}
                </ul>
              </div>
            ))
          ) : (
            <div className="text-center">No orders found.</div>
          )}
        </div>
      )}
    </div>
  );
};

export default Delivery;