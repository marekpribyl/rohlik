<template>
  <div>
    <UContainer>
      <h1 class="text-2xl font-bold mb-4">Admin Dashboard</h1>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <UCard>
          <template #header>
            <div class="flex items-center justify-between">
              <h2 class="text-xl font-semibold">Products Management</h2>
              <UButton to="/products" color="primary" variant="soft">
                Manage Products
              </UButton>
            </div>
          </template>
          <p>View, create, update, and delete products. Manage product inventory.</p>
        </UCard>
        <UCard>
          <template #header>
            <div class="flex items-center justify-between">
              <h2 class="text-xl font-semibold">Orders Management</h2>
              <UButton to="/orders" color="primary" variant="soft">
                Manage Orders
              </UButton>
            </div>
          </template>
          <p>View orders and update their status.</p>
        </UCard>
      </div>
    </UContainer>
  </div>
</template>

<script setup>
definePageMeta({
  title: 'Admin Dashboard'
})
</script>
