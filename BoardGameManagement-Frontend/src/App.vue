<!-- src/App.vue -->
<script setup>
import { useRoute } from "vue-router";
import NavTop from "@/components/NavTop.vue";

const route = useRoute();
</script>

<template>
  <!-- Show NavTop on every page EXCEPT the landing and sign-in -->
  <NavTop v-if="route.name !== 'presentation' && route.name !== 'signin'" />

  <!-- All pages render here -->
  <router-view />
</template>

