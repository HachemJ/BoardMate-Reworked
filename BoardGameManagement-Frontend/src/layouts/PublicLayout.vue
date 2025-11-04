<script setup>
import GuestNav from "@/components/nav/GuestNav.vue";
</script>

<template>
  <div class="public-shell">
    <!-- show landing nav unless route asks to hide it -->
    <NavLanding v-if="!$route.meta?.hidePublicNav" />
    <router-view />
  </div>
</template>

