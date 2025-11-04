<template>
  <header class="nav">
    <div class="wrap">
      <router-link to="/" class="brand">
        <span class="logo">🎲</span>
        <span class="name">BoardMate</span>
      </router-link>

      <nav class="tabs">
        <router-link to="/about" class="tab" active-class="is-active">About</router-link>
        <router-link to="/contact" class="tab" active-class="is-active">Contact</router-link>
      </nav>

      <!-- keep right side minimal for public -->
      <div class="spacer"></div>
    </div>
  </header>
</template>

<style scoped>
:root{
  --ink:#0f1320;
  --ink-muted:#475066;
  --bg: linear-gradient(90deg,#eef3f6,#e9f2ea,#e8f0fb);
}

.nav{ position:sticky; top:0; z-index:50; background:var(--bg); }
.wrap{ max-width:1120px; margin:0 auto; padding:12px 20px; display:flex; align-items:center; gap:20px; }
.brand{ display:flex; align-items:center; gap:10px; text-decoration:none; }
.logo{ font-size:22px }
.name{ font-weight:800; letter-spacing:.2px; color:var(--ink) }

.tabs{ display:flex; gap:26px; margin-left:auto; }
.tab{ color:var(--ink-muted); text-decoration:none; font-weight:600; padding:6px 2px; border-bottom:2px solid transparent }
.tab:hover{ color:var(--ink) }
.tab.is-active{ color:var(--ink); border-color:#6ea8ff }
.spacer{ width:1px; }
</style>
