<!-- src/views/BorrowRequestMenuView.vue -->
<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from "vue";
import NavLandingSigned from "@/components/NavLandingSigned.vue";
import axios from "axios";
import { useAuthStore } from "@/stores/authStore.js";

const auth = useAuthStore();
const api = axios.create({ baseURL: "http://localhost:8080" });

/* Tabs */
const TABS = ["My Borrow Requests", "Incoming Requests"];
const tab = ref(TABS[0]);

/* Identity */
const playerId = Number(auth.user?.id);
const ownerId = Number(auth.user?.id);
const isOwner = !!auth.user?.isAOwner;

/* Data */
const playerReqs = ref([]);   // GET /players/{playerId}/borrowrequests
const ownerReqs  = ref([]);   // GET /borrowrequests?ownerId={ownerId}

const loading = reactive({ player: false, owner: false });
const error = reactive({ player: "", owner: "" });

/* Toasts + inline banner */
const toasts = ref([]);
function toast(kind, msg, ms = 2600) {
  const id = Math.random().toString(36).slice(2);
  toasts.value.push({ id, kind, msg });
  setTimeout(() => { toasts.value = toasts.value.filter(t => t.id !== id); }, ms);
}
const banner = reactive({ visible: false, kind: "info", msg: "" });
let bannerTimer;
function showBanner(kind, msg, ms = 2500) {
  banner.visible = true; banner.kind = kind; banner.msg = msg;
  clearTimeout(bannerTimer);
  bannerTimer = setTimeout(() => (banner.visible = false), ms);
}

/* Helpers */
function todayISO() {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,"0")}-${String(d.getDate()).padStart(2,"0")}`;
}
const NOW_ISO = computed(() => todayISO());
function betweenInclusive(x, a, b){ return x >= a && x <= b; }
function fmtDate(d){ return d || "—"; }
function statusBadge(s) {
  switch (s) {
    case "Pending":    return "badge warn";
    case "Accepted":   return "badge success";
    case "InProgress": return "badge info";
    case "Denied":     return "badge danger";
    case "Done":       return "badge neutral";
    default:           return "badge";
  }
}

/* Player guards */
function canPlayerConfirm(r){
  return r.requestStatus === "Accepted" && betweenInclusive(NOW_ISO.value, r.startOfLoan, r.endOfLoan);
}
function canPlayerCancel(r){
  return (r.requestStatus === "Accepted" || r.requestStatus === "InProgress") && (NOW_ISO.value >= r.startOfLoan);
}
function playerRowMuted(r){ return r.requestStatus === "Done" || r.requestStatus === "Denied"; }

/* Owner guards */
function canOwnerAct(r){ return r.requestStatus === "Pending"; }
function canOwnerConfirmReturned(r){ return r.requestStatus === "InProgress" && NOW_ISO.value === r.endOfLoan; }

/* Fetchers */
async function fetchPlayer(){
  loading.player = true; error.player = "";
  try {
    const { data } = await api.get(`/players/${playerId}/borrowrequests`);
    playerReqs.value = Array.isArray(data) ? data : [];
  } catch (e) {
    error.player = e?.response?.data?.message || "Failed to load your requests.";
  } finally { loading.player = false; }
}
async function fetchOwner(){
  if (!isOwner) return;
  loading.owner = true; error.owner = "";
  try {
    const { data } = await api.get(`/borrowrequests`, { params: { ownerId } });
    ownerReqs.value = Array.isArray(data) ? data : [];
  } catch (e) {
    const errs = Array.isArray(e?.response?.data?.errors) ? e.response.data.errors.join(", ") : "";
    error.owner = errs || e?.response?.data?.message || "Failed to load incoming requests.";
  } finally { loading.owner = false; }
}

/* Poll owner inbox */
let poll = null;
onMounted(async () => {
  await fetchPlayer();
  if (isOwner) {
    await fetchOwner();
    poll = setInterval(fetchOwner, 10000);
  }
});
onUnmounted(() => {
  if (poll) clearInterval(poll);
  clearTimeout(bannerTimer);
});

/* Owner actions */
async function ownerAccept(id, borrowerName){
  try {
    await api.put(`/borrowrequests/${id}`, null, { params: { action: "accept" } });
    showBanner("success", `Accepted request from ${borrowerName}.`);
    toast("success", "Request accepted");
    await fetchOwner();
  } catch (e) {
    const errs = Array.isArray(e?.response?.data?.errors) ? e.response.data.errors.join("\n") : "";
    toast("error", errs || "Failed to accept");
  }
}
async function ownerDeny(id, borrowerName){
  try {
    await api.put(`/borrowrequests/${id}`, null, { params: { action: "decline" } });
    showBanner("info", `Denied request from ${borrowerName}.`);
    toast("info", "Request denied");
    await fetchOwner();
  } catch (e) {
    const errs = Array.isArray(e?.response?.data?.errors) ? e.response.data.errors.join("\n") : "";
    toast("error", errs || "Failed to deny");
  }
}
async function ownerConfirmReturned(id, gameName){
  try {
    await api.put(`/borrowrequests/${id}/boardGameCopy`, null, { params: { confirmOrCancel: "cancel" } });
    showBanner("success", `Confirmed ${gameName} returned.`);
    toast("success", "Return confirmed");
    await fetchOwner();
  } catch (e) {
    const errs = Array.isArray(e?.response?.data?.errors) ? e.response.data.errors.join("\n") : "";
    toast("error", errs || "Could not confirm return");
  }
}

/* Player actions */
async function playerConfirm(id, gameName){
  try {
    await api.put(`/borrowrequests/${id}/boardGameCopy`, null, { params: { confirmOrCancel: "confirm" } });
    showBanner("success", `Borrow started: confirmed you received ${gameName}.`);
    toast("success", "Confirmed received");
    await fetchPlayer(); if (isOwner) await fetchOwner();
  } catch (e) {
    const errs = Array.isArray(e?.response?.data?.errors) ? e.response.data.errors.join("\n") : "";
    toast("error", errs || "Could not confirm");
  }
}
async function playerCancel(id, gameName){
  try {
    await api.put(`/borrowrequests/${id}/boardGameCopy`, null, { params: { confirmOrCancel: "cancel" } });
    showBanner("info", `Cancelled/returned: ${gameName}.`);
    toast("info", "Cancelled borrowing");
    await fetchPlayer(); if (isOwner) await fetchOwner();
  } catch (e) {
    const errs = Array.isArray(e?.response?.data?.errors) ? e.response.data.errors.join("\n") : "";
    toast("error", errs || "Could not cancel");
  }
}
</script>

<template>
  <div>
    <NavLandingSigned />

    <main class="page">
      <header class="page-header">
        <h1>Borrow Requests</h1>
        <p class="muted">Track your outgoing requests and (if you’re an owner) manage incoming ones.</p>
      </header>

      <div class="tabs">
        <button class="tab" :class="{ active: tab === TABS[0] }" @click="tab = TABS[0]">{{ TABS[0] }}</button>
        <button v-if="isOwner" class="tab" :class="{ active: tab === TABS[1] }" @click="tab = TABS[1]">{{ TABS[1] }}</button>
      </div>

      <div v-if="banner.visible" class="inline-banner" :class="banner.kind">{{ banner.msg }}</div>

      <!-- PLAYER VIEW -->
      <section v-if="tab === TABS[0]" class="card">
        <header class="section-head">
          <h3>My Borrow Requests</h3>
          <small v-if="loading.player" class="hint">Loading…</small>
        </header>

        <div v-if="error.player" class="inline-banner error">{{ error.player }}</div>

        <div class="table-wrap">
          <table class="table">
            <thead>
            <tr>
              <th>Game</th>
              <th class="num">Start</th>
              <th class="num">End</th>
              <th>Status</th>
              <th class="ta-right">Actions</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="r in playerReqs" :key="r.requestId" :class="{ muted: playerRowMuted(r) }">
              <td>{{ r.specificGameName }}</td>
              <td class="num">{{ fmtDate(r.startOfLoan) }}</td>
              <td class="num">{{ fmtDate(r.endOfLoan) }}</td>
              <td><span :class="statusBadge(r.requestStatus)">{{ r.requestStatus }}</span></td>
              <td class="ta-right">
                <button class="btn primary" :disabled="!canPlayerConfirm(r)" @click="playerConfirm(r.requestId, r.specificGameName)">Confirm</button>
                <button class="btn ghost"   :disabled="!canPlayerCancel(r)"  @click="playerCancel(r.requestId, r.specificGameName)">Cancel</button>
              </td>
            </tr>
            <tr v-if="!loading.player && playerReqs.length === 0">
              <td colspan="5" class="empty">You have no borrow requests yet.</td>
            </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- OWNER VIEW -->
      <section v-else class="card">
        <header class="section-head">
          <h3>Incoming Requests</h3>
          <small v-if="loading.owner" class="hint">Auto-refreshing…</small>
        </header>

        <div v-if="error.owner" class="inline-banner error">{{ error.owner }}</div>

        <div class="table-wrap">
          <table class="table">
            <thead>
            <tr>
              <th>Game</th>
              <th>Borrower</th>
              <th class="num">Start</th>
              <th class="num">End</th>
              <th>Status</th>
              <th class="ta-right">Actions</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="r in ownerReqs" :key="r.requestId" :class="{ muted: r.requestStatus === 'Done' || r.requestStatus === 'Denied' }">
              <td>{{ r.specificGameName }}</td>
              <td>{{ r.borrowerName }}</td>
              <td class="num">{{ fmtDate(r.startOfLoan) }}</td>
              <td class="num">{{ fmtDate(r.endOfLoan) }}</td>
              <td><span :class="statusBadge(r.requestStatus)">{{ r.requestStatus }}</span></td>
              <td class="ta-right">
                <template v-if="canOwnerAct(r)">
                  <button class="btn primary" @click="ownerAccept(r.requestId, r.borrowerName)">Accept</button>
                  <button class="btn danger"  @click="ownerDeny(r.requestId, r.borrowerName)">Deny</button>
                </template>
                <template v-else>
                  <button
                      class="btn ghost"
                      :disabled="!canOwnerConfirmReturned(r)"
                      @click="ownerConfirmReturned(r.requestId, r.specificGameName)"
                      title="Confirm the copy was returned"
                  >
                    Confirm returned
                  </button>
                </template>
              </td>
            </tr>
            <tr v-if="!loading.owner && ownerReqs.length === 0">
              <td colspan="6" class="empty">No incoming requests right now.</td>
            </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- toasts -->
      <div class="toasts">
        <div v-for="t in toasts" :key="t.id" class="toast" :class="t.kind">{{ t.msg }}</div>
      </div>
    </main>
  </div>
</template>

<style scoped>
/* layout */
.page { margin-top: 96px; padding: 24px; }
.page-header h1 { margin: 0; font-size: 32px; font-weight: 800; color: #e8ecf7; }
.page-header .muted { color: #b9c2d6; margin-top: 4px; }

/* tabs */
.tabs { display: flex; gap: 8px; margin: 12px 0; }
.tab { padding: 10px 16px; border-radius: 10px; border: 1px solid #2f384a; background: transparent; color: #dfe5f4; font-weight: 700; }
.tab.active { background: #ffffff; color: #0f1217; border-color: #ffffff; box-shadow: 0 2px 6px rgba(255,255,255,0.15); }

/* cards */
.card { border: 1px solid #293043; border-radius: 16px; background: #0f1217; color: #e9edf5; padding: 20px; }

/* inline banner */
.inline-banner { margin-bottom: 10px; padding: 10px 12px; border-radius: 10px; border: 1px solid #2b3343; background: #0f1217; font-weight: 700; }
.inline-banner.success { border-color: #3e7350; background: #112218; color: #b7ffd1; }
.inline-banner.error   { border-color: #8a2a2a; background: #1a1010; color: #ffd6d6; }
.inline-banner.info    { border-color: #3b5b9e; background: #0f1625; color: #d7e5ff; }

/* section head */
.section-head { display: flex; align-items: baseline; justify-content: space-between; margin-bottom: 8px; }
.hint { color: #aab6cf; }

/* buttons */
.btn { padding: 8px 14px; border-radius: 10px; font-weight: 700; border: 1px solid transparent; cursor: pointer; margin-left: 6px; }
.btn.primary { background: #ffffff; color: #0f1217; border-color: #ffffff; }
.btn.primary:disabled { opacity: .55; cursor: not-allowed; }
.btn.danger { background: #d44d4d; color: #fff; border-color: #d44d4d; }
.btn.ghost { background: transparent; color: #dfe5f4; border: 1px solid #2f384a; }
.btn:disabled { opacity: .6; cursor: not-allowed; }

/* table */
.table-wrap { border: 1px solid #1f2533; border-radius: 12px; overflow: hidden; }
.table { width: 100%; border-collapse: collapse; }
.table th, .table td {
  padding: 12px; text-align: left; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
  color: #dfe5f4; vertical-align: middle; border-bottom: 1px solid #1f2634;
}
.table thead { background: #161b24; }
.table tbody tr.muted { color: #828ba0; }
.num { text-align: right; font-variant-numeric: tabular-nums; }
.ta-right { text-align: right; }
.empty { text-align: center; color: #9aa3b7; padding: 14px; }

/* status badges */
.badge { padding: 4px 10px; border-radius: 999px; font-weight: 800; border: 1px solid transparent; }
.badge.warn    { color: #cde6ff; border-color: #6a5b19; background: #2a2410; }
.badge.success { color: #c8ffd5; border-color: #2c5b38; background: #0f2015; }
.badge.info    { color: #d7e5ff; border-color: #3b5b9e; background: #0f1625; }
.badge.danger  { color: #ffd6d6; border-color: #5b2c2c; background: #1f1111; }
.badge.neutral { color: #e8ecf7; border-color: #2f384a; background: #141922; }

/* toasts */
.toasts { position: fixed; right: 14px; bottom: 14px; display: flex; flex-direction: column; gap: 8px; z-index: 1000; }
.toast { padding: 10px 14px; border-radius: 10px; border: 1px solid #2b3343; background: #0f1217; color: #e9edf5; font-weight: 700;
  box-shadow: 0 4px 12px rgba(0,0,0,0.35); }
.toast.success { border-color: #3e7350; background: #112218; color: #b7ffd1; }
.toast.error   { border-color: #8a2a2a; background: #1a1010; color: #ffd6d6; }
.toast.info    { border-color: #3b5b9e; background: #0f1625; color: #d7e5ff; }
</style>
