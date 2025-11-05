<!-- src/views/EventDetailView.vue -->
<template>
  <div>
    <NavLandingSigned />

    <main class="page">
      <section class="card detail" v-if="loaded && !loadError">
        <header class="detail-head">
          <div class="title">
            <h1>{{ details.name }}</h1>
            <p class="muted">Board Game: <span class="accent">{{ details.boardGameName || '—' }}</span></p>
          </div>

          <div class="state-badges">
            <span v-if="state" class="badge" :class="state.toLowerCase()">{{ state }}</span>
            <span v-if="isFull" class="badge full">Full</span>
          </div>
        </header>

        <div class="grid">
          <div class="col">
            <div class="kv"><span class="k">Date</span><span class="v">{{ details.eventDate || '—' }}</span></div>
            <div class="kv"><span class="k">Start</span><span class="v">{{ prettyTime(details.startTime) || '—' }}</span></div>
            <div class="kv"><span class="k">End</span><span class="v">{{ prettyTime(details.endTime) || '—' }}</span></div>
            <div class="kv"><span class="k">Location</span><span class="v">{{ details.location || '—' }}</span></div>
            <div class="kv">
              <span class="k">Spots</span>
              <span class="v">
                <template v-if="details.maxSpot">
                  {{ Number(count ?? 0) }}/{{ details.maxSpot }}
                </template>
                <template v-else>—</template>
              </span>
            </div>
          </div>

          <div class="col">
            <div class="kv"><span class="k">Owner</span><span class="v">{{ details.ownerName || '—' }}</span></div>
            <div class="kv desc">
              <span class="k">Description</span>
              <span class="v block">{{ details.description || '—' }}</span>
            </div>
          </div>
        </div>

        <div class="actions">
          <button class="btn primary" :disabled="disableRegister" @click="registerForEvent">Register</button>
          <button class="btn danger" :disabled="disableCancel" @click="cancelRegistration">Cancel Registration</button>
          <button class="btn ghost" @click="goBack">Go Back</button>
        </div>
      </section>

      <section v-else-if="loadError" class="card empty">
        Could not load this event.
        <div class="mt-10"><button class="btn ghost" @click="goBack">Back to Events</button></div>
      </section>

      <div class="toasts">
        <div v-for="t in toasts" :key="t.id" class="toast" :class="t.kind">{{ t.msg }}</div>
      </div>
    </main>
  </div>
</template>

<script setup>
import NavLandingSigned from "@/components/NavLandingSigned.vue";
import axios from "axios";
import { useRoute, useRouter } from "vue-router";
import { ref, computed, onMounted } from "vue";
import { useAuthStore } from "@/stores/authStore";

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();
const api = axios.create({ baseURL: "http://localhost:8080" });

const loaded = ref(false);
const loadError = ref(false);
const details = ref({
  eventID: null,
  name: "",
  description: "",
  maxSpot: "",
  eventDate: "",
  startTime: "",
  endTime: "",
  location: "",
  ownerName: "",
  boardGameName: "",
});
const regStatus = ref("Not Registered");
const count = ref(null);

function prettyTime(t) {
  if (!t) return "";
  const hhmm = t.length >= 5 ? t.slice(0, 5) : t;
  const [h, m] = hhmm.split(":").map(Number);
  const d = new Date();
  d.setHours(h || 0, m || 0, 0, 0);
  return d.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
}
function toLocalDateTime(dateStr, timeStr) {
  if (!dateStr || !timeStr) return null;
  const [y, mo, d] = dateStr.split("-").map(Number);
  const [hh, mm] = timeStr.slice(0, 5).split(":").map(Number);
  const dt = new Date(y, (mo || 1) - 1, d || 1, hh || 0, mm || 0, 0);
  return isNaN(dt.getTime()) ? null : dt;
}

const startDT = computed(() => toLocalDateTime(details.value.eventDate, details.value.startTime));
const endDT   = computed(() => toLocalDateTime(details.value.eventDate, details.value.endTime));
const state = computed(() => {
  const now = Date.now();
  if (!startDT.value || !endDT.value) return null;
  if (now < startDT.value.getTime()) return "Upcoming";
  if (now >= startDT.value.getTime() && now < endDT.value.getTime()) return "Ongoing";
  return "Finished";
});
const isFull = computed(() => {
  const max = Number(details.value.maxSpot ?? 0);
  return max > 0 && (Number(count.value ?? 0) >= max);
});

const disableRegister = computed(() =>
    !details.value.eventID || regStatus.value === "Registered" || state.value !== "Upcoming" || isFull.value
);
const disableCancel = computed(() => !details.value.eventID || regStatus.value !== "Registered");

const toasts = ref([]);
function toast(kind, msg, ms = 2500) {
  const id = Math.random().toString(36).slice(2);
  toasts.value.push({ id, kind, msg });
  setTimeout(() => (toasts.value = toasts.value.filter(t => t.id !== id)), ms);
}

async function fetchEventById(id) {
  const { data } = await api.get(`/events/${id}`);
  return data;
}
async function fetchRegStatus(id) {
  try {
    const { data } = await api.get(`/registrations/${auth.user.id}/${id}`);
    regStatus.value = data === null ? "Not Registered" : "Registered";
  } catch (e) {
    if (e?.response?.status === 404) regStatus.value = "Not Registered";
    else regStatus.value = "Error";
  }
}
async function fetchCount(id) {
  const tryPaths = [
    `/registrations/count/${id}`,
    `/registrations/event/${id}/count`,
    `/registrations/event/${id}`,
  ];
  for (const p of tryPaths) {
    try {
      const { data } = await api.get(p);
      count.value = Array.isArray(data) ? data.length : Number(data ?? 0);
      return;
    } catch { /* try next */ }
  }
  count.value = 0;
}

onMounted(async () => {
  try {
    const id = Number(route.params.id);
    if (!id) throw new Error("Bad id");
    const ev = await fetchEventById(id);
    details.value = ev || {};
    await Promise.all([fetchRegStatus(id), fetchCount(id)]);
    loaded.value = true;
  } catch {
    loadError.value = true;
  }
});

async function registerForEvent() {
  if (!details.value.eventID || disableRegister.value) return;
  try {
    await api.post("/registrations", { playerID: Number(auth.user.id), eventID: Number(details.value.eventID) });
    toast("success", "Registration successful");
    await Promise.all([fetchRegStatus(details.value.eventID), fetchCount(details.value.eventID)]);
  } catch (e) {
    toast("error", e?.response?.data?.message || "Registration failed");
  }
}
async function cancelRegistration() {
  if (!details.value.eventID || disableCancel.value) return;
  try {
    await api.delete(`/registrations/${auth.user.id}/${details.value.eventID}`);
    toast("success", "Registration cancelled");
    await Promise.all([fetchRegStatus(details.value.eventID), fetchCount(details.value.eventID)]);
  } catch (e) {
    toast("error", e?.response?.data?.message || "Cancel failed");
  }
}
function goBack() { router.push({ name: "event" }); }
</script>

<style scoped>
.page { margin-top: 96px; padding: 24px; }
.card { border: 1px solid #293043; border-radius: 16px; background: #0f1217; color: #e9edf5; padding: 20px; }
.detail { max-width: 1080px; margin: 0 auto; }

.detail-head { display: flex; align-items: flex-start; justify-content: space-between; gap: 16px; margin-bottom: 10px; }
.detail-head h1 { margin: 0; font-size: 32px; font-weight: 800; color: #e8ecf7; }
.muted { opacity: .8; margin: 6px 0 0; }
.accent { color: #9fc1ff; }

.state-badges { display: flex; gap: 8px; }
.badge { padding: 6px 12px; border-radius: 999px; font-weight: 800; border: 1px solid transparent; }
.badge.upcoming { color: #cde6ff; border-color: #2e4a74; background: #0d1726; }
.badge.ongoing  { color: #c8ffd5; border-color: #2c5b38; background: #0f2015; }
.badge.finished { color: #ffd6d6; border-color: #5b2c2c; background: #1f1111; }
.badge.full     { color: #ffe2b8; border-color: #6a4a23; background: #24180b; }

.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-top: 8px; }
.col { display: flex; flex-direction: column; gap: 10px; }
.kv { display: flex; justify-content: space-between; gap: 16px; padding: 10px 12px; border: 1px solid #1f2533; border-radius: 12px; background: #10151c; }
.k { opacity: .8; }
.v { font-weight: 700; color: #e6ecff; }
.v.block { white-space: normal; line-height: 1.5; }
.desc .k { align-self: flex-start; }

.actions { display: flex; gap: 10px; margin-top: 16px; }
.btn { padding: 10px 16px; border-radius: 10px; font-weight: 600; transition: all .2s ease; border: 1px solid transparent; }
.btn.primary { background: #ffffff; color: #0f1217; border-color: #ffffff; }
.btn.primary:disabled { opacity: .6; cursor: not-allowed; }
.btn.danger { background: #d44d4d; border-color: #d44d4d; color: #fff; }
.btn.danger:disabled { opacity: .6; cursor: not-allowed; }
.btn.ghost { background: transparent; border: 1px solid #2f384a; color: #dfe5f4; }
.btn.ghost:hover { background: #182132; }

.toasts { position: fixed; right: 14px; bottom: 14px; display: flex; flex-direction: column; gap: 8px; z-index: 1000; }
.toast { padding: 10px 14px; border-radius: 10px; border: 1px solid #2b3343; background: #0f1217; color: #e9edf5; font-weight: 600;
  box-shadow: 0 4px 12px rgba(0,0,0,0.35); }
.toast.success { border-color: #3e7350; background: #112218; color: #b7ffd1; }
.toast.error   { border-color: #8a2a2a; background: #1a1010; color: #ffd6d6; }
.toast.info    { border-color: #3b5b9e; background: #0f1625; color: #d7e5ff; }
</style>
