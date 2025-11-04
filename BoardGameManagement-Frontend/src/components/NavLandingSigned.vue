<!-- src/components/NavLandingSigned.vue -->
<template>
  <header class="landing-nav">
    <div class="nav-inner">
      <!-- Brand -->
      <router-link to="/" class="brand">
        <img class="brand-icon" src="/src/assets/img/favicon.png" alt="BoardMate" />
        <span class="brand-name">BoardMate</span>
      </router-link>

      <!-- Center links -->
      <nav class="links center">
        <router-link
            v-for="l in centerLinks"
            :key="l.to"
            :to="l.to"
            class="link"
            :class="{ active: isActive(l.activeMatch) }"
        >
          {{ l.label }}
        </router-link>
      </nav>

      <!-- Right links (About, Contact) -->
      <nav class="links right">
        <router-link
            v-for="l in rightLinks"
            :key="l.to"
            :to="l.to"
            class="link"
            :class="{ active: isActive(l.activeMatch) }"
        >
          {{ l.label }}
        </router-link>
      </nav>

      <!-- User menu -->
      <div class="user">
        <button class="user-btn" @click="open = !open">
          <span class="avatar-dot">👤</span>
          <span class="user-name">{{ displayName }}</span>
          <svg class="chev" width="14" height="14" viewBox="0 0 24 24"><path fill="currentColor" d="M7 10l5 5l5-5z"/></svg>
        </button>

        <div v-if="open" class="menu" @click.self="open=false">
          <router-link class="menu-item" to="/profile" @click="open=false">Profile details</router-link>
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

const displayName = computed(() => auth.user?.username || auth.user?.name || 'User')

const onKey = (e) => { if (e.key === 'Escape') open.value = false }
onMounted(() => window.addEventListener('keydown', onKey))
onBeforeUnmount(() => window.removeEventListener('keydown', onKey))

// Center of the bar
const centerLinks = computed(() => [
  { label: 'Board Games', to: '/boardgames',          activeMatch: ['/boardgames'] },
  { label: 'Events',      to: '/pages/event',         activeMatch: ['/pages/event'] },
  { label: 'Borrow',      to: '/pages/borrowrequests',activeMatch: ['/pages/borrowrequests'] },
])

// Rightmost of the bar (as requested)
const rightLinks = computed(() => [
  { label: 'About',   to: '/about',   activeMatch: ['/about','/pages/landing-pages/about-us'] },
  { label: 'Contact', to: '/contact', activeMatch: ['/contact','/pages/landing-pages/contact-us'] },
])

function isActive(matches) {
  const arr = Array.isArray(matches) ? matches : [matches]
  return arr.some(p => route.path === p || route.path.startsWith(p + '/'))
}

function signOut() {
  auth.logout?.()
  router.push('/signin')
}
</script>

<style scoped>
/* Shell */
.landing-nav{
  position: fixed; top: 14px; left: 0; right: 0; z-index: 1000;
  display:flex; justify-content:center;
}
.nav-inner{
  width:min(1180px, calc(100% - 32px));
  display:grid; align-items:center;
  grid-template-columns: auto 1fr auto auto; /* brand | center | right | user */
  gap: 12px;
  background: rgba(18,20,24,0.78);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  padding: 10px 14px;
  box-shadow: 0 6px 18px rgba(0,0,0,.22);
}

/* Brand */
.brand{display:flex; align-items:center; gap:10px; text-decoration:none;}
.brand-icon{ width:22px; height:22px; border-radius:6px;}
.brand-name{ color:#ECEFF5; font-weight:800; letter-spacing:.2px; }

/* Links */
.links{display:flex; align-items:center; gap:12px;}
.center{ justify-content:center; }
.right{ justify-content:flex-end; }
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

/* User menu */
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
