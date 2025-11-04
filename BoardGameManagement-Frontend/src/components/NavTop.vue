<!-- src/components/NavTop.vue -->
<template>
  <nav class="nav">
    <div class="nav__inner">
      <router-link to="/" class="nav__brand">BoardMate</router-link>

      <div class="nav__links">
        <router-link to="/about" class="nav__link">About</router-link>
        <router-link to="/contact" class="nav__link">Contact</router-link>
        <router-link to="/signin" class="nav__btn">Sign in</router-link>
      </div>
    </div>
  </nav>
</template>

<style scoped>
.nav{
  position: sticky; top:0; z-index:1000;
  backdrop-filter: blur(10px);
  background: rgba(15,16,20,.55);
  border-bottom: 1px solid rgba(255,255,255,.06);
}
.nav__inner{
  max-width: 1200px; margin: 0 auto; padding: 12px 20px;
  display:flex; align-items:center; justify-content:space-between;
}
.nav__brand{
  font-weight: 900; letter-spacing:.2px; text-decoration:none;
  color: #ffffff; font-size: 20px;
}
.nav__links{display:flex; gap:12px; align-items:center;}
.nav__link{
  color:#e8ebf3; text-decoration:none; padding:8px 10px; border-radius:10px;
}
.nav__link:hover{ background: rgba(255,255,255,.08); }
.nav__btn{
  text-decoration:none; padding:8px 14px; border-radius:12px;
  background:#ffffff; color:#0b0d10; font-weight: 800; border:1px solid rgba(0,0,0,.1);
  box-shadow: 0 6px 16px rgba(0,0,0,.12);
}
.nav__btn:hover{ filter:brightness(1.03); }
</style>
