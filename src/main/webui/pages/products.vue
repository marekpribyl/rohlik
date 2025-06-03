<template>
  <div>
    <UContainer>
      <div class="flex justify-between items-center mb-4">
        <h1 class="text-2xl font-bold">Products Management</h1>
        <UButton color="primary" @click="openCreateModal">
          Add New Product
        </UButton>
      </div>

      <!-- Products Table -->
      <UTable :columns="columns" :rows="products" :loading="loading">
        <template #actions-data="{ row }">
          <div class="flex space-x-2">
            <UButton color="blue" variant="soft" size="xs" @click="openEditModal(row)">
              Edit
            </UButton>
            <UButton color="amber" variant="soft" size="xs" @click="openStockModal(row)">
              Update Stock
            </UButton>
            <UButton color="red" variant="soft" size="xs" @click="confirmDelete(row)">
              Delete
            </UButton>
          </div>
        </template>
        <template #stock-data="{ row }">
          <div>
            <div>Total: {{ row.stockInfo.quantity }}</div>
            <div>Available: {{ row.stockInfo.available }}</div>
          </div>
        </template>
        <template #price-data="{ row }">
          ${{ row.price.toFixed(2) }}
        </template>
      </UTable>

      <!-- Create/Edit Product Modal -->
      <UModal v-model="showProductModal" :title="isEditing ? 'Edit Product' : 'Create New Product'">
        <form @submit.prevent="saveProduct">
          <div class="space-y-4">
            <UFormGroup label="SKU" name="sku" :error="errors.sku">
              <UInput v-model="productForm.sku" :disabled="isEditing" placeholder="Enter SKU" />
            </UFormGroup>
            <UFormGroup label="Name" name="name" :error="errors.name">
              <UInput v-model="productForm.name" placeholder="Enter product name" />
            </UFormGroup>
            <UFormGroup label="Price" name="price" :error="errors.price">
              <UInput v-model="productForm.price" type="number" step="0.01" min="0.01" placeholder="Enter price" />
            </UFormGroup>
            <UFormGroup v-if="!isEditing" label="Initial Stock" name="quantity" :error="errors.quantity">
              <UInput v-model="productForm.quantity" type="number" min="0" placeholder="Enter initial stock" />
            </UFormGroup>
          </div>
          <div class="flex justify-end space-x-2 mt-4">
            <UButton color="gray" variant="soft" @click="showProductModal = false">
              Cancel
            </UButton>
            <UButton type="submit" color="primary">
              {{ isEditing ? 'Update' : 'Create' }}
            </UButton>
          </div>
        </form>
      </UModal>

      <!-- Update Stock Modal -->
      <UModal v-model="showStockModal" title="Update Stock">
        <form @submit.prevent="updateStock">
          <div class="space-y-4">
            <p>Current Stock: {{ selectedProduct?.stockInfo?.quantity || 0 }}</p>
            <p>Available: {{ selectedProduct?.stockInfo?.available || 0 }}</p>
            <UFormGroup label="Quantity Change" name="quantityChange" :error="errors.quantityChange">
              <UInput 
                v-model="stockForm.quantityChange" 
                type="number" 
                placeholder="Enter quantity change (positive to add, negative to remove)" 
              />
            </UFormGroup>
          </div>
          <div class="flex justify-end space-x-2 mt-4">
            <UButton color="gray" variant="soft" @click="showStockModal = false">
              Cancel
            </UButton>
            <UButton type="submit" color="primary">
              Update Stock
            </UButton>
          </div>
        </form>
      </UModal>
    </UContainer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { productsApi } from '~/services/api'

definePageMeta({
  title: 'Products Management'
})

// Table columns
const columns = [
  { key: 'sku', label: 'SKU' },
  { key: 'name', label: 'Name' },
  { key: 'stock', label: 'Stock' },
  { key: 'price', label: 'Price' },
  { key: 'actions', label: 'Actions' }
]

// State
const products = ref([])
const loading = ref(true)
const showProductModal = ref(false)
const showStockModal = ref(false)
const isEditing = ref(false)
const selectedProduct = ref(null)
const errors = reactive({})

// Form state
const productForm = reactive({
  sku: '',
  name: '',
  price: '',
  quantity: 0
})

const stockForm = reactive({
  quantityChange: 0
})

// Fetch products
async function fetchProducts() {
  loading.value = true
  try {
    const data = await productsApi.getAllProducts(0, 100)
    products.value = data.products || []
  } catch (error) {
    console.error('Error fetching products:', error)
    UNotification({
      title: 'Error',
      description: 'Failed to load products',
      color: 'red'
    })
  } finally {
    loading.value = false
  }
}

// Open create modal
function openCreateModal() {
  isEditing.value = false
  resetForm()
  showProductModal.value = true
}

// Open edit modal
function openEditModal(product) {
  isEditing.value = true
  selectedProduct.value = product
  productForm.sku = product.sku
  productForm.name = product.name
  productForm.price = product.price
  showProductModal.value = true
}

// Open stock update modal
function openStockModal(product) {
  selectedProduct.value = product
  stockForm.quantityChange = 0
  showStockModal.value = true
}

// Reset form
function resetForm() {
  productForm.sku = ''
  productForm.name = ''
  productForm.price = ''
  productForm.quantity = 0
  errors.sku = ''
  errors.name = ''
  errors.price = ''
  errors.quantity = ''
}

// Validate form
function validateProductForm() {
  errors.sku = ''
  errors.name = ''
  errors.price = ''
  errors.quantity = ''

  let isValid = true

  if (!productForm.sku && !isEditing.value) {
    errors.sku = 'SKU is required'
    isValid = false
  }

  if (!productForm.name) {
    errors.name = 'Name is required'
    isValid = false
  }

  if (!productForm.price || parseFloat(productForm.price) <= 0) {
    errors.price = 'Price must be greater than 0'
    isValid = false
  }

  if (!isEditing.value && (productForm.quantity === '' || parseInt(productForm.quantity) < 0)) {
    errors.quantity = 'Quantity must be 0 or greater'
    isValid = false
  }

  return isValid
}

// Save product (create or update)
async function saveProduct() {
  if (!validateProductForm()) return

  loading.value = true
  try {
    if (isEditing.value) {
      // Update existing product
      const updateData = {
        name: productForm.name,
        price: parseFloat(productForm.price)
      }

      await productsApi.updateProduct(productForm.sku, updateData)

      UNotification({
        title: 'Success',
        description: 'Product updated successfully',
        color: 'green'
      })
    } else {
      // Create new product
      const newProduct = {
        sku: productForm.sku,
        name: productForm.name,
        price: parseFloat(productForm.price),
        quantity: parseInt(productForm.quantity)
      }

      await productsApi.createProduct(newProduct)

      UNotification({
        title: 'Success',
        description: 'Product created successfully',
        color: 'green'
      })
    }

    showProductModal.value = false
    await fetchProducts()
  } catch (error) {
    console.error('Error saving product:', error)
    UNotification({
      title: 'Error',
      description: isEditing.value ? 'Failed to update product' : 'Failed to create product',
      color: 'red'
    })
  } finally {
    loading.value = false
  }
}

// Update stock
async function updateStock() {
  if (!selectedProduct.value) return

  if (stockForm.quantityChange === '' || isNaN(parseInt(stockForm.quantityChange))) {
    errors.quantityChange = 'Please enter a valid number'
    return
  }

  loading.value = true
  try {
    await productsApi.updateStock(selectedProduct.value.sku, parseInt(stockForm.quantityChange))

    UNotification({
      title: 'Success',
      description: 'Stock updated successfully',
      color: 'green'
    })

    showStockModal.value = false
    await fetchProducts()
  } catch (error) {
    console.error('Error updating stock:', error)
    UNotification({
      title: 'Error',
      description: 'Failed to update stock',
      color: 'red'
    })
  } finally {
    loading.value = false
  }
}

// Confirm delete
function confirmDelete(product) {
  UModal.confirm({
    title: 'Delete Product',
    content: `Are you sure you want to delete ${product.name} (${product.sku})?`,
    confirmLabel: 'Delete',
    cancelLabel: 'Cancel',
    confirmButtonProps: {
      color: 'red'
    }
  }).then(async () => {
    try {
      await productsApi.deleteProduct(product.sku)

      UNotification({
        title: 'Success',
        description: 'Product deleted successfully',
        color: 'green'
      })

      await fetchProducts()
    } catch (error) {
      console.error('Error deleting product:', error)
      UNotification({
        title: 'Error',
        description: 'Failed to delete product',
        color: 'red'
      })
    }
  }).catch(() => {
    // User canceled
  })
}

// Load products on mount
onMounted(() => {
  fetchProducts()
})
</script>
