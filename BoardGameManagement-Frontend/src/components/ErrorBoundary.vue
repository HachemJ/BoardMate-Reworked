<script setup>
import { ref, onErrorCaptured } from "vue";
import { useRouter } from "vue-router";

const hasError = ref(false);
const errorMessage = ref("");
const router = useRouter();

onErrorCaptured((err, instance, info) => {
  console.error("Unhandled UI error:", err, info);
  hasError.value = true;
  errorMessage.value = err?.message || "Unexpected error";
  return false;
});

function goBack() {
  hasError.value = false;
  if (window.history.length > 1) {
    router.back();
    return;
  }
  router.push({ name: "profile" });
}

function goDashboard() {
  hasError.value = false;
  router.push({ name: "profile" });
}
</script>

<template>
  <div v-if="hasError" class="error-wrap">
    <div class="error-card">
      <h1>Something went wrong</h1>
      <p>We hit an unexpected error while loading this page.</p>
      <p class="detail">{{ errorMessage }}</p>
      <div class="actions">
        <button class="btn ghost" @click="goBack">Go Back</button>
        <button class="btn primary" @click="goDashboard">Go to Dashboard</button>
      </div>
    </div>
  </div>
  <slot v-else />
</template>

<style scoped>
.error-wrap {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
  background: #0b0e13;
  color: #e9edf5;
}
.error-card {
  max-width: 560px;
  width: 100%;
  padding: 20px;
  border-radius: 16px;
  border: 1px solid #1f2533;
  background: #0f1217;
  box-shadow: 0 12px 30px rgba(0,0,0,0.35);
}
.error-card h1 {
  margin: 0 0 8px;
  font-size: 22px;
}
.detail {
  color: #a8b2c4;
  font-size: 13px;
  margin-top: 8px;
  word-break: break-word;
}
.actions {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}
.btn {
  padding: 10px 14px;
  border-radius: 10px;
  border: 1px solid transparent;
  cursor: pointer;
  font-weight: 700;
}
.btn.primary {
  background: #ffffff;
  color: #0f1217;
  border-color: #ffffff;
}
.btn.ghost {
  background: transparent;
  border: 1px solid #2f384a;
  color: #dfe5f4;
}
.btn.ghost:hover { background: #182132; }
</style>
