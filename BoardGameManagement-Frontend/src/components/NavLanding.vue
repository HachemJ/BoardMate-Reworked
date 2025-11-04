<template>
  <header class="landing-nav">
    <div class="nav-inner">
      <!-- Brand (uses your tab/favicon image) -->
      <router-link to="/" class="brand">
        <!-- If your icon is elsewhere, adjust the src path -->
        <img class="brand-icon" src="/src/assets/img/favicon.png" alt="BoardMate" />
        <span class="brand-name">BoardMate</span>
      </router-link>

      <nav class="nav-links">
        <router-link to="/pages/landing-pages/about-us" class="nav-link">About</router-link>
        <router-link to="/pages/landing-pages/contact-us" class="nav-link">Contact</router-link>
      </nav>
    </div>
  </header>
</template>

<script setup>
// no props needed
</script>

<style scoped>
.landing-nav{
  position: fixed;
  top: 14px;
  left: 0; right: 0;
  z-index: 50;
  display: flex;
  justify-content: center;
  pointer-events: none; /* container ignores clicks so the pill gets them only */
}
.nav-inner{
  pointer-events: all;
  width: min(1080px, calc(100% - 32px));
  display: flex; align-items: center; justify-content: space-between;
  background: rgba(18,20,24,0.72);
  border: 1px solid rgba(255,255,255,0.08);
  backdrop-filter: blur(10px);
  border-radius: 14px;
  padding: 10px 14px;
  box-shadow: 0 6px 18px rgba(0,0,0,.22);
}

/* brand */
.brand {
  display: flex;
  align-items: center;   /* centers logo + text vertically */
  gap: 10px;             /* small space between them */
  text-decoration: none;
  background: transparent !important;
}

.brand:focus,
.brand:active,
.brand:hover {
  background: transparent !important; /* keep transparent even on hover */
}

.brand-icon{ width:22px; height:22px; border-radius:6px}
.brand-name{
  font-weight: 800; letter-spacing:.2px;
  color: #ECEFF5;
}

/* links */
.nav-links{ display:flex; align-items:center; gap:18px; }
.nav-link{
  text-decoration:none;
  color:#D9DEE8;
  font-weight: 700;
  padding: 8px 10px;
  border-radius: 10px;
  transition: background .2s ease, color .2s ease, transform .2s ease;
}
.nav-link:hover{
  background: rgba(255,255,255,0.08);
  color: #FFFFFF;
  transform: translateY(-1px);
}
/* active state (optional) */
.router-link-active{
  background: rgba(255,255,255,0.12);
  color: #FFFFFF;
}
</style>
