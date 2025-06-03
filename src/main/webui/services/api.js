// API Service for interacting with the backend

// Base URLs for API endpoints
const PRODUCTS_API_URL = '/api/products/v1';
const ORDERS_API_URL = '/api/orders/v1';

// Products API
export const productsApi = {
  // Get all products with pagination
  async getAllProducts(offset = 0, count = 100) {
    try {
      const response = await fetch(`${PRODUCTS_API_URL}?offset=${offset}&count=${count}`);
      if (!response.ok) throw new Error('Failed to fetch products');
      return await response.json();
    } catch (error) {
      console.error('Error fetching products:', error);
      throw error;
    }
  },

  // Get product by SKU
  async getProductBySku(sku) {
    try {
      const response = await fetch(`${PRODUCTS_API_URL}/${sku}`);
      if (!response.ok) {
        if (response.status === 404) {
          throw new Error('Product not found');
        }
        throw new Error('Failed to fetch product');
      }
      return await response.json();
    } catch (error) {
      console.error(`Error fetching product ${sku}:`, error);
      throw error;
    }
  },

  // Create a new product
  async createProduct(product) {
    try {
      const response = await fetch(PRODUCTS_API_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(product)
      });
      if (!response.ok) throw new Error('Failed to create product');
      return await response.json();
    } catch (error) {
      console.error('Error creating product:', error);
      throw error;
    }
  },

  // Update a product
  async updateProduct(sku, product) {
    try {
      const response = await fetch(`${PRODUCTS_API_URL}/${sku}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(product)
      });
      if (!response.ok) {
        if (response.status === 404) {
          throw new Error('Product not found');
        }
        throw new Error('Failed to update product');
      }
      return await response.json();
    } catch (error) {
      console.error(`Error updating product ${sku}:`, error);
      throw error;
    }
  },

  // Delete a product
  async deleteProduct(sku) {
    try {
      const response = await fetch(`${PRODUCTS_API_URL}/${sku}`, {
        method: 'DELETE'
      });
      if (!response.ok) {
        if (response.status === 404) {
          throw new Error('Product not found');
        }
        if (response.status === 409) {
          throw new Error('Cannot delete product with active orders');
        }
        throw new Error('Failed to delete product');
      }
      return true;
    } catch (error) {
      console.error(`Error deleting product ${sku}:`, error);
      throw error;
    }
  },

  // Update product stock
  async updateStock(sku, quantityChange) {
    try {
      const response = await fetch(`${PRODUCTS_API_URL}/${sku}/inventory`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(quantityChange)
      });
      if (!response.ok) {
        if (response.status === 400) {
          throw new Error('Insufficient stock');
        }
        throw new Error('Failed to update stock');
      }
      return await response.json();
    } catch (error) {
      console.error(`Error updating stock for product ${sku}:`, error);
      throw error;
    }
  }
};

// Orders API
export const ordersApi = {
  // Get order by order number
  async getOrderByOrderNumber(orderNumber) {
    try {
      const response = await fetch(`${ORDERS_API_URL}/${orderNumber}`);
      if (!response.ok) {
        if (response.status === 404) {
          throw new Error('Order not found');
        }
        throw new Error('Failed to fetch order');
      }
      return await response.json();
    } catch (error) {
      console.error(`Error fetching order ${orderNumber}:`, error);
      throw error;
    }
  },

  // Create a new order
  async createOrder(order) {
    try {
      const response = await fetch(ORDERS_API_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(order)
      });
      if (!response.ok) {
        if (response.status === 400) {
          throw new Error('Invalid input or insufficient stock');
        }
        throw new Error('Failed to create order');
      }
      return await response.json();
    } catch (error) {
      console.error('Error creating order:', error);
      throw error;
    }
  },

  // Update order status
  async updateOrderStatus(orderNumber, status) {
    try {
      const response = await fetch(`${ORDERS_API_URL}/${orderNumber}/status/${status}`, {
        method: 'PUT'
      });
      if (!response.ok) {
        if (response.status === 404) {
          throw new Error('Order not found');
        }
        if (response.status === 409) {
          throw new Error('Transition to the provided state is not allowed');
        }
        throw new Error('Failed to update order status');
      }
      return true;
    } catch (error) {
      console.error(`Error updating status for order ${orderNumber}:`, error);
      throw error;
    }
  }
};
