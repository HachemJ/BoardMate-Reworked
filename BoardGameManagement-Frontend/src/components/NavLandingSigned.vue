<template>
  <header class="landing-nav">
    <div class="nav-inner">
      <!-- Brand -->
      <router-link to="/" class="brand">
        <img class="brand-icon" src="/src/assets/img/favicon.png" alt="BoardMate" />
        <span class="brand-name">BoardMate</span>
      </router-link>

      <!-- Primary links -->
      <nav class="nav-links">
        <router-link to="/about" class="nav-link">About</router-link>
        <router-link to="/contact" class="nav-link">Contact</router-link>
        <router-link to="/profile" class="nav-link">Profile</router-link>
        <router-link to="/pages/playerboardgame" class="nav-link">Player Games</router-link>
        <router-link to="/pages/event" class="nav-link">Events</router-link>
        <router-link to="/pages/borrowrequests" class="nav-link">Borrow</router-link>
      </nav>

      <!-- User menu -->
      <div class="user-area" ref="userArea">
        <button
            class="user-chip"
            @click="menuOpen = !menuOpen"
            @keydown.down.prevent="menuOpen = true; focusFirst()"
            aria-haspopup="menu"
            :aria-expanded="menuOpen ? 'true' : 'false'"
        >
          <img class="chip-avatar" :src="avatarUrl" alt="" />
          <span class="chip-name">{{ shortName }}</span>
          <svg class="chev" viewBox="0 0 20 20" aria-hidden="true"><path d="M5 7l5 6 5-6" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        </button>

        <div v-if="menuOpen" class="menu" role="menu">
          <router-link class="menu-item" role="menuitem" to="/profile" @click="close">Profile</router-link>
          <button class="menu-item danger" role="menuitem" @click="signOut">Sign out</button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authStore";

const router = useRouter();
const auth = useAuthStore();

const menuOpen = ref(false);
const userArea = ref(null);

const shortName = computed(() => {
  const n = auth.user?.username || auth.user?.name || auth.user?.email || "User";
  return n.length > 14 ? n.slice(0, 12) + "…" : n;
});
const avatarUrl = computed(() => "/default_avatar.jpg");

function close() {
  menuOpen.value = false;
}
function onDocClick(e) {
  if (!userArea.value) return;
  if (!userArea.value.contains(e.target)) close();
}
function onEsc(e) {
  if (e.key === "Escape") close();
}
function focusFirst() {
  nextTick(() => {
    const el = userArea.value?.querySelector(".menu .menu-item");
    el?.focus?.();
  });
}
async function signOut() {
  try {
    auth.logout?.(); // your store’s logout (adjust if named differently)
  } catch (_) {}
  close();
  router.push("/signin");
}

onMounted(() => {
  document.addEventListener("click", onDocClick, true);
  document.addEventListener("keydown", onEsc, true);
});
onUnmounted(() => {
  document.removeEventListener("click", onDocClick, true);
  document.removeEventListener("keydown", onEsc, true);
});
</script>

<style scoped>
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
  display: flex; align-items: center; justify-content: space-between;
  background: rgba(18,20,24,0.72);
  border: 1px solid rgba(255,255,255,0.08);
  backdrop-filter: blur(10px);
  border-radius: 14px;
  padding: 10px 14px;
  box-shadow: 0 6px 18px rgba(0,0,0,.22);
}

/* brand */
.brand { display: flex; align-items: center; gap:10px; text-decoration:none; }
.brand-icon{ width:22px; height:22px; border-radius:6px}
.brand-name{ font-weight: 800; letter-spacing:.2px; color:#ECEFF5; }

/* links */
.nav-links{ display:flex; align-items:center; gap:10px; }
.nav-link{
  text-decoration:none; color:#D9DEE8; font-weight:700;
  padding:8px 10px; border-radius:10px; transition: background .2s, color .2s, transform .2s;
}
.nav-link:hover{ background: rgba(255,255,255,0.08); color:#fff; transform: translateY(-1px); }
.router-link-active{ background: rgba(255,255,255,0.12); color:#fff; }

/* user chip + menu */
.user-area { position: relative; }
.user-chip{
  display:flex; align-items:center; gap:8px;
  padding:6px 10px; border-radius:999px; border:1px solid rgba(255,255,255,.08);
  background: rgba(255,255,255,.06); color:#E6EBF5; font-weight:800;
  cursor:pointer; transition: filter .2s, transform .2s;
}
.user-chip:hover{ filter:brightness(1.03); transform: translateY(-1px); }
.chip-avatar{ width:22px; height:22px; border-radius:999px; object-fit:cover; background:#12161e; }
.chip-name{ max-width: 140px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
.chev{ width:16px; height:16px; opacity:.8; }

.menu{
  position:absolute; right:0; top:calc(100% + 8px);
  min-width: 180px;
  background:#0f1217; color:#e9edf5; border:1px solid #1f2533; border-radius:12px;
  box-shadow: 0 12px 32px rgba(0,0,0,.4);
  padding:6px; display:flex; flex-direction:column;
}
.menu-item{
  display:block; text-align:left; width:100%;
  background:transparent; border:none; color:#e9edf5; text-decoration:none;
  padding:10px 12px; border-radius:10px; font-weight:700; cursor:pointer;
}
.menu-item:hover{ background:#171c25; }
.menu-item.danger{ color:#ffd6db; }
.menu-item.danger:hover{ background:#25181b; }
</style>
