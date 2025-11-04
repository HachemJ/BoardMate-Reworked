<script setup>
const emit = defineEmits(["logout"]);
</script>

<template>
  <header class="nav">
    <div class="wrap">
      <router-link to="/app" class="brand">
        <span class="logo">🎲</span>
        <span class="name">BoardMate</span>
      </router-link>

      <nav class="tabs">
        <router-link to="/app/browse" class="tab" active-class="is-active">Browse</router-link>
        <router-link to="/app/library" class="tab" active-class="is-active">My Library</router-link>
        <router-link to="/app/events" class="tab" active-class="is-active">Events</router-link>
        <router-link to="/app/requests" class="tab" active-class="is-active">Requests</router-link>
      </nav>

      <div class="actions">
        <router-link to="/app/account" class="ghost">Account</router-link>
        <button class="ghost" @click="emit('logout')">Log out</button>
      </div>
    </div>
  </header>
</template>

<style scoped>
:root{
  --ink:#e7eaf3;
  --ink-muted:#a5abc2;
  --line:#1f2333;
  --bg: linear-gradient(90deg,#0f1117,#121521 40%,#0f1117);
}

.nav{ position:sticky; top:0; z-index:60; background:var(--bg); }
.wrap{ max-width:1120px; margin:0 auto; padding:12px 20px; display:flex; align-items:center; gap:20px; }
.brand{ display:flex; align-items:center; gap:10px; text-decoration:none; }
.logo{ font-size:22px }
.name{ color:var(--ink); font-weight:800; letter-spacing:.2px }

.tabs{ display:flex; gap:26px; margin-left:20px; }
.tab{ color:var(--ink-muted); text-decoration:none; font-weight:600; padding:6px 2px; border-bottom:2px solid transparent }
.tab:hover{ color:var(--ink) }
.tab.is-active{ color:var(--ink); border-color:#6ea8ff }

.actions{ margin-left:auto; display:flex; gap:10px }
.ghost{ color:var(--ink); background:transparent; border:1px solid #2a3046; padding:8px 12px; border-radius:10px; text-decoration:none; cursor:pointer }
.ghost:hover{ background: rgba(255,255,255,.06) }
</style>
