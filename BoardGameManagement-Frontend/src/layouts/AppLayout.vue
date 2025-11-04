<script setup>
import AppNav from "@/components/nav/AppNav.vue";

function handleLogout(){
  // do your real logout work, then:
  localStorage.removeItem("authToken");
  // send them back to marketing site:
  window.location.href = "/";
}
</script>

<template>
  <AppNav @logout="handleLogout" />
  <main><router-view /></main>
</template>
