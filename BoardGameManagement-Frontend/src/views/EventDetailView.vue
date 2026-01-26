<!-- src/views/EventDetailView.vue -->
<template>
  <div>
    <NavLandingSigned />

    <main class="page">
      <section class="card detail" v-if="loaded && !loadError">
  <EventCard :event="eventCardData">
    <template #footer>
      <div class="actions">
        <button class="btn primary" :disabled="disableRegister" @click="registerForEvent">Register</button>
        <button class="btn danger" :disabled="disableCancel" @click="cancelRegistration">Cancel Registration</button>
        <button class="btn ghost" @click="goBack">Go Back</button>
      </div>
    </template>
  </EventCard>
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
import EventCard from "@/components/EventCard.vue";
import axios from "axios";
import { useRoute, useRouter } from "vue-router";
import { ref, computed, onMounted } from "vue";
import { useAuthStore } from "@/stores/authStore";

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();
const api = axios.create({ baseURL: import.meta.env.VITE_BACKEND_URL || "http://localhost:8080" });

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
const eventCardData = computed(() => ({
  name: details.value.name,
  boardGameName: details.value.boardGameName,
  date: details.value.eventDate,
  startTime: details.value.startTime,
  endTime: details.value.endTime,
  location: details.value.location,
  description: details.value.description,
  spotsCurrent: Number(count.value ?? 0),
  spotsMax: Number(details.value.maxSpot ?? 0),
  status: state.value,
}));

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
  try {
    const { data } = await api.get(`/registrations/events/${id}`);
    count.value = Array.isArray(data) ? data.length : 0;
  } catch {
    count.value = 0;
  }
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
function goBack() {
  const back = router.options.history.state.back;
  if (typeof back === "string" && back.startsWith("/pages/event")) {
    router.back();
    return;
  }
  router.push({ name: "event" });
}
</script>

<style scoped>
.page { margin-top: 96px; padding: 24px; }
.card { border: 1px solid #293043; border-radius: 16px; background: #0f1217; color: #e9edf5; padding: 20px; }
.detail { max-width: 1080px; margin: 0 auto; }

.actions { display: flex; gap: 10px; }
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

