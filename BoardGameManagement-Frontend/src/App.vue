<template>
  <ErrorBoundary>
    <router-view />
  </ErrorBoundary>
</template>

<script setup>
import ErrorBoundary from "@/components/ErrorBoundary.vue";
</script>

<style>
html, body, #app { height: 100%; background:#0b0e13; }
</style>
