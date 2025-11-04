<!-- src/components/NavLandingSigned.vue -->
<template>
  <header class="landing-nav">
    <div class="nav-inner">
      <!-- brand -->
      <router-link to="/" class="brand">
        <img class="brand-icon" src="/src/assets/img/favicon.png" alt="BoardMate" />
        <span class="brand-name">BoardMate</span>
      </router-link>

      <!-- centered links -->
      <nav class="links">
        <router-link
            v-for="l in links"
            :key="l.to"
            :to="l.to"
            class="link"
            :class="{ active: isActive(l.to) }"
            aria-current="page"
        >
          {{ l.label }}
        </router-link>
      </nav>

      <!-- user menu -->
      <div class="user">
        <button class="user-btn" @click="open = !open">
          <span class="avatar-dot">👤</span>
          <span class="user-name">{{ auth.user?.username || 'User' }}</span>
          <svg class="chev" width="14" height="14" viewBox="0 0 24 24"><path fill="currentColor" d="M7 10l5 5l5-5z"/></svg>
        </button>
        <div v-if="open" class="menu" @click.outside="open = false">
          <router-link class="menu-item" to="/profile">Profile</router-link>
          <router-link class="menu-item" to="/pages/landing-pages/about-us">About</router-link>
          <router-link class="menu-item" to="/pages/landing-pages/contact-us">Contact</router-link>
          <button class="menu-item danger" @click="signOut">Sign out</button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const open = ref(false)

// Close the menu on ESC or route change
const onKey = (e) => { if (e.key === 'Escape') open.value = false }
onMounted(() => window.addEventListener('keydown', onKey))
onBeforeUnmount(() => window.removeEventListener('keydown', onKey))

// Center nav links (adjust / add more anytime)
const links = computed(() => [
  { label: 'About',       to: '/about' },
  { label: 'Contact',     to: '/contact' },
  { label: 'Profile',     to: '/profile' },
  { label: 'Player Games',to: '/pages/playerboardgame' },
  { label: 'Events',      to: '/pages/event' },
  { label: 'Borrow',      to: '/pages/borrowrequests' },
])

/**
 * Mark a link as active when current path starts with it.
 * Works for nested pages (e.g., /contact/anything).
 */
function isActive(path) {
  return route.path === path || route.path.startsWith(path + '/')
}

function signOut() {
  auth.logout?.()
  router.push('/signin')
}
</script>

<style scoped>
/* shell */
.landing-nav{
  position: fixed;
  top: 14px; left: 0; right: 0;
  z-index: 1000;
  display:flex; justify-content:center;
}
.nav-inner{
  width:min(1180px, calc(100% - 32px));
  display:flex; align-items:center; justify-content:space-between;
  background: rgba(18,20,24,0.78);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  padding: 10px 14px;
  box-shadow: 0 6px 18px rgba(0,0,0,.22);
}

/* brand */
.brand{display:flex; align-items:center; gap:10px; text-decoration:none;}
.brand-icon{ width:22px; height:22px; border-radius:6px;}
.brand-name{ color:#ECEFF5; font-weight:800; letter-spacing:.2px; }

/* links */
.links{display:flex; align-items:center; gap:12px;}
.link{
  color:#D9DEE8; text-decoration:none;
  padding:8px 12px; border-radius:12px; font-weight:800;
  transition: background .2s ease, color .2s ease, transform .2s ease;
}
.link:hover{ background: rgba(255,255,255,0.08); color:#fff; transform: translateY(-1px); }
.link.active{
  background: rgba(255,255,255,0.14);
  color:#fff;
  box-shadow: inset 0 0 0 1px rgba(255,255,255,0.12);
}

/* user menu */
.user{ position: relative; }
.user-btn{
  display:flex; align-items:center; gap:8px;
  background:#171a20; color:#e9edf5;
  border:1px solid rgba(255,255,255,.08);
  padding:8px 10px; border-radius:999px; font-weight:800;
}
.user-btn:hover{ filter: brightness(1.05); }
.avatar-dot{ width:18px; height:18px; }
.user-name{ max-width:120px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
.chev{ opacity:.8; }

.menu{
  position:absolute; right:0; top:44px; min-width:180px;
  background:#0f1217; border:1px solid #1f2533; border-radius:14px;
  box-shadow: 0 10px 30px rgba(0,0,0,.35); padding:6px;
}
.menu-item{
  display:block; width:100%; text-align:left;
  color:#e9edf5; text-decoration:none; padding:10px 12px; border-radius:10px;
}
.menu-item:hover{ background:#151a22; }
.menu-item.danger{ color:#ffd3d7; }
.menu-item.danger:hover{ background:#24171a; }
</style>
