<!-- src/views/OwnerBoardGameMenuView.vue -->
<script setup>
import NavLandingSigned from "@/components/NavLandingSigned.vue";
import axios from "axios";
import { ref, computed, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authStore.js";

const auth = useAuthStore();

// ---- axios with auth header (fixes 403) ----
const axiosClient = axios.create({ baseURL: "http://localhost:8080" });
axiosClient.interceptors.request.use((config) => {
  const token = auth?.token || auth?.user?.token;
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// router / route
const route = useRoute();
const router = useRouter();

// ---------- lightweight in-app toast ----------
const toast = ref({
  show: false,
  variant: "success", // 'success' | 'error' | 'info'
  title: "",
  message: "",
});
function showToast({ variant = "success", title = "", message = "" }) {
  toast.value = { show: true, variant, title, message };
  clearTimeout(showToast._t);
  showToast._t = setTimeout(() => (toast.value.show = false), 4000);
}
function closeToast() {
  toast.value = { ...toast.value, show: false };
}

// If someone pushed us here with ?toast=...&variant=success
function readToastFromQuery() {
  const t = route.query.toast;
  if (typeof t === "string" && t.trim().length) {
    const v = route.query.variant;
    const variant = v === "error" ? "error" : v === "info" ? "info" : "success";
    showToast({
      variant,
      title: variant === "success" ? "Success" : variant === "error" ? "Error" : "Notice",
      message: t,
    });
    router.replace({ query: { ...route.query, toast: undefined, variant: undefined } });
  }
}
onMounted(readToastFromQuery);
watch(() => route.query.toast, readToastFromQuery);

// ---------- page data ----------
const searchQuery = ref("");
const boardGames = ref([]);

const filteredGames = computed(() =>
    boardGames.value.filter((g) =>
        (g.name || "").toLowerCase().includes(searchQuery.value.toLowerCase())
    )
);

async function fetchBoardGames() {
  try {
    const { data } = await axiosClient.get("/boardgames");
    boardGames.value = Array.isArray(data) ? data : [];
  } catch (error) {
    const status = error?.response?.status;
    const statusText = error?.response?.statusText;
    const serverMsg =
        error?.response?.data?.errors?.join("\n") ||
        error?.response?.data?.message ||
        "";
    const msg =
        serverMsg ||
        (status ? `HTTP ${status}${statusText ? " " + statusText : ""}` : "Network error");
    showToast({ variant: "error", title: "Load Failed", message: msg });
    boardGames.value = [];
  }
}

onMounted(fetchBoardGames);
</script>

<template>
  <div>
    <NavLandingSigned />

    <!-- toast -->
    <transition name="fade">
      <div v-if="toast.show" class="toast-wrap" role="status" aria-live="polite">
        <div
            class="toast-card"
            :class="{ success: toast.variant === 'success', error: toast.variant === 'error', info: toast.variant === 'info' }"
        >
          <div class="toast-head">
            <strong>{{ toast.title }}</strong>
            <button class="toast-x" @click="closeToast" aria-label="Close">✕</button>
          </div>
          <p class="toast-msg">{{ toast.message }}</p>
        </div>
      </div>
    </transition>

    <div class="container-fluid page">
      <div class="row">
        <div class="col-md-12">
          <div class="card p-4 shadow-sm">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h2 class="mb-0">Search and Browse Board Games</h2>
              <div class="d-flex gap-2">
                <!-- Use the route names that actually exist -->
                <router-link :to="{ name: 'addBoardGame' }">
                  <button class="btn btn-info">Add Board Game</button>
                </router-link>
                <router-link :to="{ name: 'updateBoardGame' }">
                  <button class="btn btn-outline">Update Board Game</button>
                </router-link>
              </div>
            </div>

            <div class="mb-3">
              <input
                  v-model="searchQuery"
                  type="text"
                  class="form-control"
                  placeholder="Search..."
                  aria-label="Search board games"
              />
            </div>

            <small>Click a board game name to view more details</small>
            <table class="table mt-2">
              <thead>
              <tr>
                <th>Game Name</th>
                <th>Min Players</th>
                <th>Max Players</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="game in filteredGames" :key="game.gameID || game.name">
                <td>
                  <router-link
                      :to="{ name: 'ownerBoardGameDetail', params: { gamename: game.name } }"
                  >
                    {{ game.name }}
                  </router-link>
                </td>
                <td>{{ game.minPlayers }}</td>
                <td>{{ game.maxPlayers }}</td>
              </tr>
              <tr v-if="filteredGames.length === 0">
                <td colspan="3" class="text-center text-muted py-4">
                  No board games match your search.
                </td>
              </tr>
              </tbody>
            </table>

          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page { margin-top: 96px; }

/* card/theme */
.card {
  border: 1px solid #293043;
  border-radius: 1rem;
  background-color: #0f1217;
  color: #e9edf5;
}

/* buttons */
.btn.btn-outline {
  background: transparent;
  border: 1px solid #2f384a;
  color: #dfe5f4;
}
.btn.btn-outline:hover { background: #182132; }

/* table */
.table { table-layout: fixed; width: 100%; }
.table th, .table td {
  vertical-align: middle;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding: 12px;
}
.table th:nth-child(1), .table td:nth-child(1) { width: 50%; }
.table th:nth-child(2), .table td:nth-child(2),
.table th:nth-child(3), .table td:nth-child(3) { width: 25%; }
.table td a { color: #8fb4ff; text-decoration: none; }
.table td a:hover { text-decoration: underline; }

/* toast */
.fade-enter-active, .fade-leave-active { transition: opacity .18s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

.toast-wrap {
  position: fixed;
  top: 86px; /* clears your fixed nav */
  left: 0; right: 0;
  display: flex; justify-content: center;
  z-index: 1100;
  padding: 0 16px;
}
.toast-card {
  width: min(740px, 100%);
  border-radius: 12px;
  padding: 12px 14px;
  border: 1px solid rgba(255,255,255,.08);
  background: #10151d;
  box-shadow: 0 12px 36px rgba(0,0,0,.35);
}
.toast-card.success { border-color: rgba(63,201,136,.35); box-shadow: 0 12px 36px rgba(63,201,136,.18); }
.toast-card.error   { border-color: rgba(255,105,97,.35); box-shadow: 0 12px 36px rgba(255,105,97,.18); }
.toast-card.info    { border-color: rgba(143,180,255,.35); box-shadow: 0 12px 36px rgba(143,180,255,.18); }

.toast-head { display:flex; align-items:center; justify-content:space-between; font-weight: 800; }
.toast-x { background: transparent; border: 0; color: #cfd7e6; cursor: pointer; font-size: 16px; }
.toast-x:hover { color: #fff; }
.toast-msg { margin: 6px 0 2px; color: #dbe3f7; }
</style>
