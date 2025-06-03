<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Header -->
    <header class="bg-white shadow">
      <div class="container mx-auto px-4 py-4 flex justify-between items-center">
        <h1 class="text-xl font-bold text-gray-900">Admin Dashboard</h1>
        <nav>
          <ul class="flex space-x-4">
            <li>
              <NuxtLink to="/" class="px-3 py-2 rounded-md text-sm font-medium" active-class="bg-primary-50 text-primary-500" exact>
                Home
              </NuxtLink>
            </li>
            <li>
              <NuxtLink to="/products" class="px-3 py-2 rounded-md text-sm font-medium" active-class="bg-primary-50 text-primary-500">
                Products
              </NuxtLink>
            </li>
            <li>
              <NuxtLink to="/orders" class="px-3 py-2 rounded-md text-sm font-medium" active-class="bg-primary-50 text-primary-500">
                Orders
              </NuxtLink>
            </li>
          </ul>
        </nav>
      </div>
    </header>

    <!-- Main content -->
    <main class="container mx-auto px-4 py-6">
      <slot />
    </main>

    <!-- Footer -->
    <footer class="bg-white shadow-inner mt-auto">
      <div class="container mx-auto px-4 py-4 text-center text-gray-500 text-sm">
        &copy; {{ new Date().getFullYear() }} Admin Dashboard
      </div>
    </footer>
  </div>
</template>
