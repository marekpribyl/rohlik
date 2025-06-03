<template>
  <div>
    <UContainer>
      <div class="flex justify-between items-center mb-4">
        <h1 class="text-2xl font-bold">Orders Management</h1>
      </div>

      <!-- Orders Table -->
      <UTable :columns="columns" :rows="orders" :loading="loading">
        <template #status-data="{ row }">
          <UBadge :color="getStatusColor(row.status)">
            {{ row.status }}
          </UBadge>
        </template>
        <template #createdAt-data="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
        <template #expiresAt-data="{ row }">
          {{ formatDate(row.expiresAt) }}
        </template>
        <template #updatedAt-data="{ row }">
          {{ row.updatedAt ? formatDate(row.updatedAt) : 'N/A' }}
        </template>
        <template #totalPrice-data="{ row }">
          ${{ row.totalPrice.toFixed(2) }}
        </template>
        <template #actions-data="{ row }">
          <div class="flex space-x-2">
            <UButton color="blue" variant="soft" size="xs" @click="viewOrderDetails(row)">
              View Details
            </UButton>
            <UButton 
              v-if="row.status === 'CREATED'" 
              color="green" 
              variant="soft" 
              size="xs" 
              @click="updateOrderStatus(row, 'PAID')"
            >
              Mark as Paid
            </UButton>
            <UButton 
              v-if="row.status === 'CREATED'" 
              color="red" 
              variant="soft" 
              size="xs" 
              @click="updateOrderStatus(row, 'CANCELED')"
            >
              Cancel
            </UButton>
            <UButton 
              v-if="row.status === 'CREATED'" 
              color="amber" 
              variant="soft" 
              size="xs" 
              @click="updateOrderStatus(row, 'EXPIRED')"
            >
              Mark as Expired
            </UButton>
          </div>
        </template>
      </UTable>

      <!-- Order Details Modal -->
      <UModal v-model="showOrderDetailsModal" :title="`Order Details: ${selectedOrder?.orderNumber || ''}`">
        <div v-if="selectedOrder" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <p class="font-semibold">Status:</p>
              <UBadge :color="getStatusColor(selectedOrder.status)">
                {{ selectedOrder.status }}
              </UBadge>
            </div>
            <div>
              <p class="font-semibold">Total Price:</p>
              <p>${{ selectedOrder.totalPrice.toFixed(2) }}</p>
            </div>
            <div>
              <p class="font-semibold">Created At:</p>
              <p>{{ formatDate(selectedOrder.createdAt) }}</p>
            </div>
            <div>
              <p class="font-semibold">Expires At:</p>
              <p>{{ formatDate(selectedOrder.expiresAt) }}</p>
            </div>
            <div v-if="selectedOrder.updatedAt">
              <p class="font-semibold">Updated At:</p>
              <p>{{ formatDate(selectedOrder.updatedAt) }}</p>
            </div>
          </div>

          <div class="mt-4">
            <h3 class="text-lg font-semibold mb-2">Order Items</h3>
            <UTable :columns="itemColumns" :rows="selectedOrder.items">
              <template #pricePerUnit-data="{ row }">
                ${{ row.pricePerUnit.toFixed(2) }}
              </template>
              <template #totalPrice-data="{ row }">
                ${{ row.totalPrice.toFixed(2) }}
              </template>
            </UTable>
          </div>
        </div>
      </UModal>
    </UContainer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ordersApi } from '~/services/api'

definePageMeta({
  title: 'Orders Management'
})

// Table columns
const columns = [
  { key: 'orderNumber', label: 'Order #' },
  { key: 'status', label: 'Status' },
  { key: 'createdAt', label: 'Created At' },
  { key: 'expiresAt', label: 'Expires At' },
  { key: 'updatedAt', label: 'Updated At' },
  { key: 'totalPrice', label: 'Total Price' },
  { key: 'actions', label: 'Actions' }
]

const itemColumns = [
  { key: 'sku', label: 'SKU' },
  { key: 'quantity', label: 'Quantity' },
  { key: 'pricePerUnit', label: 'Price Per Unit' },
  { key: 'totalPrice', label: 'Total Price' }
]

// State
const orders = ref([])
const loading = ref(true)
const showOrderDetailsModal = ref(false)
const selectedOrder = ref(null)

// Format date
function formatDate(dateString) {
  if (!dateString) return 'N/A'
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date)
}

// Get status color
function getStatusColor(status) {
  switch (status) {
    case 'CREATED':
      return 'blue'
    case 'PAID':
      return 'green'
    case 'CANCELED':
      return 'red'
    case 'EXPIRED':
      return 'amber'
    default:
      return 'gray'
  }
}

// Fetch orders
async function fetchOrders() {
  // Note: This API endpoint doesn't exist in the provided API
  // In a real application, you would need to implement an endpoint to get all orders
  // For now, we'll use a mock implementation
  loading.value = true
  try {
    // Mock implementation - in a real app, you would fetch from an API
    // const response = await fetch('/api/orders/v1')
    // if (!response.ok) throw new Error('Failed to fetch orders')
    // const data = await response.json()
    // orders.value = data || []

    // Mock data for demonstration
    orders.value = [
      {
        orderNumber: 'ORD-001',
        status: 'CREATED',
        createdAt: new Date().toISOString(),
        expiresAt: new Date(Date.now() + 30 * 60000).toISOString(),
        updatedAt: null,
        totalPrice: 125.50,
        items: [
          {
            sku: 'SKU-123',
            quantity: 2,
            pricePerUnit: 25.00,
            totalPrice: 50.00
          },
          {
            sku: 'SKU-456',
            quantity: 3,
            pricePerUnit: 25.50,
            totalPrice: 75.50
          }
        ]
      },
      {
        orderNumber: 'ORD-002',
        status: 'PAID',
        createdAt: new Date(Date.now() - 60 * 60000).toISOString(),
        expiresAt: new Date(Date.now() - 30 * 60000).toISOString(),
        updatedAt: new Date(Date.now() - 45 * 60000).toISOString(),
        totalPrice: 75.25,
        items: [
          {
            sku: 'SKU-789',
            quantity: 1,
            pricePerUnit: 75.25,
            totalPrice: 75.25
          }
        ]
      }
    ]
  } catch (error) {
    console.error('Error fetching orders:', error)
    UNotification({
      title: 'Error',
      description: 'Failed to load orders',
      color: 'red'
    })
  } finally {
    loading.value = false
  }
}

// View order details
function viewOrderDetails(order) {
  selectedOrder.value = order
  showOrderDetailsModal.value = true
}

// Update order status
async function updateOrderStatus(order, newStatus) {
  loading.value = true
  try {
    await ordersApi.updateOrderStatus(order.orderNumber, newStatus)

    UNotification({
      title: 'Success',
      description: `Order status updated to ${newStatus}`,
      color: 'green'
    })

    // In a real application, you would fetch the updated order
    // For now, we'll update the local state
    const index = orders.value.findIndex(o => o.orderNumber === order.orderNumber)
    if (index !== -1) {
      orders.value[index].status = newStatus
      orders.value[index].updatedAt = new Date().toISOString()
    }
  } catch (error) {
    console.error('Error updating order status:', error)
    UNotification({
      title: 'Error',
      description: `Failed to update order status to ${newStatus}`,
      color: 'red'
    })
  } finally {
    loading.value = false
  }
}

// Load orders on mount
onMounted(() => {
  fetchOrders()
})
</script>
