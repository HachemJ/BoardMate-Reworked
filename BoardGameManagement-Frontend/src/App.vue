<template>
  <ErrorBoundary>
    <router-view />

    <div v-if="showDemoBanner" class="demo-banner">
      <div class="demo-text">
        Demo mode: explore the UI freely. Actions that change data require signing in.
      </div>
      <button class="demo-dismiss" type="button" @click="dismissDemoBanner">Got it</button>
    </div>

    <div v-if="demoNotice" class="demo-toast">
      <span class="demo-toast__text">{{ demoNotice }}</span>
      <button class="demo-toast__action" type="button" @click="goSignin">Sign in</button>
      <button class="demo-toast__close" type="button" @click="demoNotice = ''" aria-label="Dismiss">x</button>
    </div>

    <div class="idle-note">
      If the app has been idle, the first request may take a few seconds to wake up.
    </div>
  </ErrorBoundary>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authStore";
import ErrorBoundary from "@/components/ErrorBoundary.vue";

const auth = useAuthStore();
const router = useRouter();
const demoNotice = ref("");

const showDemoBanner = computed(
  () => auth.user?.isGuest && !localStorage.getItem("demoBannerDismissed")
);

function dismissDemoBanner() {
  localStorage.setItem("demoBannerDismissed", "1");
}

function goSignin() {
  demoNotice.value = "";
  router.push("/signin");
}

function onDemoNotice(e) {
  demoNotice.value = e?.detail?.message || "Demo mode: sign in to perform this action.";
}

onMounted(() => {
  window.addEventListener("demo-notice", onDemoNotice);
});
onBeforeUnmount(() => {
  window.removeEventListener("demo-notice", onDemoNotice);
});
</script>

<style>
html, body, #app { height: 100%; background:#0b0e13; }
.idle-note {
  position: fixed;
  left: 50%;
  bottom: 12px;
  transform: translateX(-50%);
  z-index: 999;
  padding: 6px 10px;
  font-size: 12px;
  color: #9aa3b2;
  background: rgba(11, 14, 19, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 999px;
  backdrop-filter: blur(6px);
  pointer-events: none;
}
@media (max-width: 600px) {
  .idle-note {
    font-size: 11px;
    padding: 6px 8px;
    max-width: 92%;
    text-align: center;
  }
}

.demo-banner {
  position: fixed;
  left: 50%;
  top: 70px;
  transform: translateX(-50%);
  z-index: 2000;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  max-width: min(820px, 92vw);
  background: rgba(18, 22, 30, 0.92);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  color: #d7deea;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
  pointer-events: auto;
}
.demo-text { font-size: 12px; font-weight: 700; }
.demo-dismiss {
  margin-left: auto;
  padding: 6px 10px;
  font-size: 12px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: #1c222d;
  color: #e7ecf5;
  cursor: pointer;
  pointer-events: auto;
}

.demo-toast {
  position: fixed;
  right: 16px;
  bottom: 56px;
  z-index: 999;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: rgba(18, 22, 30, 0.92);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  color: #d7deea;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
}
.demo-toast__text { font-size: 12px; font-weight: 700; }
.demo-toast__action {
  padding: 6px 10px;
  font-size: 12px;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: #1c222d;
  color: #e7ecf5;
}
.demo-toast__close {
  border: none;
  background: transparent;
  color: #9aa3b2;
  font-weight: 800;
}
</style>
