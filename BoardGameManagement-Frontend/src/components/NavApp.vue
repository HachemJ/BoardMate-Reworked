<!-- src/components/NavApp.vue -->
<template>
  <header class="landing-nav">
    <div class="nav-inner">
      <!-- Brand -->
      <router-link to="/" class="brand">
        <img class="brand-icon" src="/src/assets/img/favicon.png" alt="BoardMate" />
        <span class="brand-name">BoardMate</span>
      </router-link>

      <!-- Left group: public pages -->
      <nav class="nav-links">
        <router-link to="/about" class="nav-link">About</router-link>
        <router-link to="/contact" class="nav-link">Contact</router-link>
      </nav>

      <!-- Right group: app navigation -->
      <nav class="nav-links">
        <router-link to="/profile" class="nav-link">Profile</router-link>

        <!-- owner vs player menus -->
        <router-link
            v-if="auth.user?.isAOwner"
            to="/pages/ownerboardgame"
            class="nav-link"
        >Owner Games</router-link>
        <router-link
            v-else
            to="/pages/playerboardgame"
            class="nav-link"
        >Player Games</router-link>

        <router-link to="/pages/event" class="nav-link">Events</router-link>
        <router-link to="/pages/borrowrequests" class="nav-link">Borrow</router-link>

        <!-- user chip + sign out -->
        <div class="user-chip" v-if="auth.user?.username">
          <span class="avatar">{{ initials }}</span>
          <span class="uname">{{ auth.user.username }}</span>
        </div>
        <button class="btn btn-ghost" @click="signOut">Sign out</button>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authStore";

const router = useRouter();
const auth = useAuthStore();

const initials = computed(() => {
  const n = auth.user?.username || "";
  const parts = n.trim().split(/\s+/);
  const i = (parts[0]?.[0] || "") + (parts[1]?.[0] || "");
  return i.toUpperCase() || "U";
});

function signOut() {
  auth.logout?.(); // your store already had login(); usually logout() clears user
  router.push("/signin");
}
</script>

<style scoped>
/* same visual vibe as NavLanding */
.landing-nav{
  position: fixed;
  top: 14px;
  left: 0; right: 0;
  z-index: 50;
  display: flex;
  justify-content: center;
  pointer-events: none;
}
.nav-inner{
  pointer-events: all;
  width: min(1180px, calc(100% - 32px));
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 14px;

  background: rgba(18,20,24,0.72);
  border: 1px solid rgba(255,255,255,0.08);
  backdrop-filter: blur(10px);
  border-radius: 14px;
  padding: 10px 14px;
  box-shadow: 0 6px 18px rgba(0,0,0,.22);
}

/* brand */
.brand {
  display:flex; align-items:center; gap:10px; text-decoration:none;
  background: transparent !important;
}
.brand-icon{ width:22px; height:22px; border-radius:6px }
.brand-name{ font-weight: 800; letter-spacing:.2px; color:#ECEFF5; }

/* link groups */
.nav-links{ display:flex; align-items:center; gap:12px; justify-content:flex-end; }
.nav-link{
  text-decoration:none; color:#D9DEE8; font-weight: 700;
  padding: 8px 10px; border-radius: 10px;
  transition: background .2s ease, color .2s ease, transform .2s ease;
}
.nav-link:hover{ background: rgba(255,255,255,0.08); color:#fff; transform: translateY(-1px); }
.router-link-active{ background: rgba(255,255,255,0.12); color:#fff; }

/* user chip + signout */
.user-chip{
  display:flex; align-items:center; gap:8px;
  padding:6px 8px; border-radius:12px; background: rgba(255,255,255,.06);
  border:1px solid rgba(255,255,255,.08); color:#fff; margin-left:4px;
}
.avatar{
  width:22px; height:22px; border-radius:50%;
  display:inline-flex; align-items:center; justify-content:center;
  font-size:11px; font-weight:900; background:#11151c; border:1px solid #2b3447;
}
.uname{ font-weight:800; font-size:12px; }

/* buttons */
.btn{
  padding: 8px 12px; border-radius: 10px; font-weight: 800;
  transition: transform .18s, filter .18s, background .18s, color .18s, border-color .18s;
  border:1px solid rgba(255,255,255,.1);
}
.btn-ghost{
  background:#171b22; color:#e9edf5; border-color:#2a3242;
}
.btn-ghost:hover{ transform: translateY(-2px); filter: brightness(1.03); }
@media (max-width: 900px){
  .nav-inner{ grid-template-columns: 1fr; row-gap:10px; }
  .nav-links{ flex-wrap:wrap; justify-content:flex-start; }
}
</style>
