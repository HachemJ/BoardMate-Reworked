<template>
  <ErrorBoundary>
    <router-view />
    <div class="idle-note">
      If the app has been idle, the first request may take a few seconds to wake up.
    </div>
  </ErrorBoundary>
</template>

<script setup>
import ErrorBoundary from "@/components/ErrorBoundary.vue";
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
</style>
