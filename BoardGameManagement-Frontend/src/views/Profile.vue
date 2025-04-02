<script setup>
import { useAuthStore } from "@/stores/authStore.js";

const authStore = useAuthStore();
</script>

<template>
  <div>
    <h2>Welcome, {{ authStore.user.username }}</h2>
    <p>Email: {{ authStore.user.email }}</p>
    <p>Status: {{ authStore.user.isAuthenticated ? 'Logged in' : 'Logged out' }}</p>
  </div>
</template>
