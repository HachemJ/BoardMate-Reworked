<!-- src/App.vue -->
<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";
import NavTop from "@/components/NavTop.vue";

const route = useRoute();
// Hide nav on the landing page only:
const showNav = computed(() => route.name !== "presentation");
</script>

<template>
  <NavTop v-if="showNav" />
  <router-view />
</template>
