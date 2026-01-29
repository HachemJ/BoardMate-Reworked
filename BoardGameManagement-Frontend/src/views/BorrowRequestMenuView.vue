<script setup>
import { ref, reactive, onMounted, watchEffect, onUnmounted, computed } from "vue";
import NavLandingSigned from "@/components/NavLandingSigned.vue";
import axios from "axios";
import { useAuthStore } from "@/stores/authStore.js";
import { useRoute, useRouter } from "vue-router";
import { showDemoNotice } from "@/utils/demoNotice";

const authStore = useAuthStore();
const axiosClient = axios.create({ baseURL: import.meta.env.VITE_BACKEND_URL || "http://localhost:8080" });
const route = useRoute();
const router = useRouter();

const isOwner = computed(() => !!authStore.user?.isAOwner);
const isGuest = computed(() => !!authStore.user?.isGuest);
const ownerId = computed(() => authStore.user?.id ?? null);
const playerId = computed(() => authStore.user?.id ?? null);

const tabs = [
  { key: "browse", label: "Browse" },
  { key: "mine", label: "My Requests" },
  { key: "manage", label: "Manage", ownerOnly: true },
];
const visibleTabs = computed(() => {
  if (isGuest.value) return tabs.filter((t) => t.key === "browse");
  return tabs.filter((t) => !t.ownerOnly || isOwner.value);
});

const activeTab = computed(() => {
  const raw = String(route.query.tab || "").toLowerCase();
  const found = visibleTabs.value.find((t) => t.key === raw);
  return found ? found.key : (visibleTabs.value[0]?.key || "browse");
});

watchEffect(() => {
  if (route.name !== "borrowRequestMenu") return;
  const current = String(route.query.tab || "").toLowerCase();
  if (current !== activeTab.value) {
    router.replace({ name: "borrowRequestMenu", query: { ...route.query, tab: activeTab.value } });
  }
});

const copies = ref([]);
const ownerCopies = ref([]);
const ownerRequests = ref([]);
const playerRequests = ref([]);
const selectedCopy = ref(null);
const copyQuery = ref("");

const loading = reactive({
  copies: false,
  ownerCopies: false,
  ownerRequests: false,
  playerRequests: false,
});

const borrowForm = reactive({
  startDate: "",
  startTime: "",
  endDate: "",
  endTime: "",
});
const borrowTouched = ref(false);
const borrowSubmitting = ref(false);
const borrowLength = ref("24h");
const borrowNotice = reactive({ type: "", message: "" });

const startDateMin = computed(() => todayString());
const startTimeMin = computed(() => {
  if (!borrowForm.startDate) return "";
  if (borrowForm.startDate !== todayString()) return "";
  return timeString(roundToNext15Minutes(new Date()));
});
const endDateMin = computed(() => borrowForm.startDate || todayString());
const endTimeMin = computed(() => {
  if (!borrowForm.endDate || !borrowForm.startDate) return "";
  if (borrowForm.endDate !== borrowForm.startDate) return "";
  return borrowForm.startTime || "";
});

const LENGTH_OPTIONS = [
  { key: "12h", label: "12h", hours: 12 },
  { key: "24h", label: "24h", hours: 24 },
  { key: "48h", label: "48h", hours: 48 },
  { key: "custom", label: "Custom", hours: 0 },
];

const filteredCopies = computed(() => {
  const q = copyQuery.value.trim().toLowerCase();
  if (!q) return copies.value;
  return copies.value.filter((c) => {
    const hay = [c.boardGameName, c.playerName, c.specification].filter(Boolean).join(" ").toLowerCase();
    return hay.includes(q);
  });
});

const borrowError = computed(() => {
  if (!borrowTouched.value) return "";
  if (!borrowForm.startDate || !borrowForm.startTime) return "Start date and time are required.";
  if (!borrowForm.endDate || !borrowForm.endTime) return "End date and time are required.";
  if (borrowForm.endDate < borrowForm.startDate) return "End date must be after start date.";
  if (borrowForm.endDate === borrowForm.startDate && borrowForm.endTime <= borrowForm.startTime) {
    return "End time must be after start time.";
  }
  if (isStartInPast()) return "Start time must be in the future.";
  if (!selectedCopy.value) return "Select a copy to request.";
  return "";
});

const isBorrowValid = computed(() => !borrowError.value && !!selectedCopy.value);

const borrowSummary = computed(() => {
  if (!borrowForm.startDate || !borrowForm.startTime || !borrowForm.endDate || !borrowForm.endTime) return "";
  const start = new Date(`${borrowForm.startDate}T${borrowForm.startTime}:00`);
  const end = new Date(`${borrowForm.endDate}T${borrowForm.endTime}:00`);
  const diffMs = Math.max(0, end - start);
  const diffHours = Math.max(1, Math.round(diffMs / (1000 * 60 * 60)));
  const label = diffHours < 24 ? `${diffHours} hour${diffHours === 1 ? "" : "s"}` : `${dayDiff(borrowForm.startDate, borrowForm.endDate) + 1} day${dayDiff(borrowForm.startDate, borrowForm.endDate) + 1 === 1 ? "" : "s"}`;
  return `Borrowing from ${prettyDateTime(borrowForm.startDate, borrowForm.startTime)} -> ${prettyDateTime(borrowForm.endDate, borrowForm.endTime)} (${label})`;
});

function setNotice(type, message, timeout = 2600) {
  borrowNotice.type = type;
  borrowNotice.message = message;
  if (timeout) {
    setTimeout(() => {
      borrowNotice.type = "";
      borrowNotice.message = "";
    }, timeout);
  }
}

function todayString() {
  const today = new Date();
  const y = today.getFullYear();
  const m = String(today.getMonth() + 1).padStart(2, "0");
  const d = String(today.getDate()).padStart(2, "0");
  return `${y}-${m}-${d}`;
}

function timeString(date) {
  const h = String(date.getHours()).padStart(2, "0");
  const m = String(date.getMinutes()).padStart(2, "0");
  return `${h}:${m}`;
}

function roundToNext15Minutes(date) {
  const rounded = new Date(date);
  const minutes = rounded.getMinutes();
  const delta = (15 - (minutes % 15)) % 15;
  rounded.setMinutes(minutes + delta, 0, 0);
  return rounded;
}

function addDays(dateStr, days) {
  if (!dateStr) return "";
  const [y, m, d] = dateStr.split("-").map(Number);
  const dt = new Date(y, (m || 1) - 1, d || 1);
  dt.setDate(dt.getDate() + days);
  const yy = dt.getFullYear();
  const mm = String(dt.getMonth() + 1).padStart(2, "0");
  const dd = String(dt.getDate()).padStart(2, "0");
  return `${yy}-${mm}-${dd}`;
}

function dayDiff(startStr, endStr) {
  const [sy, sm, sd] = startStr.split("-").map(Number);
  const [ey, em, ed] = endStr.split("-").map(Number);
  const start = new Date(sy, (sm || 1) - 1, sd || 1);
  const end = new Date(ey, (em || 1) - 1, ed || 1);
  const diff = (end - start) / (1000 * 60 * 60 * 24);
  return Math.max(0, Math.floor(diff));
}

function prettyDate(dateStr) {
  if (!dateStr) return "";
  const [y, m, d] = dateStr.split("-").map(Number);
  const dt = new Date(y, (m || 1) - 1, d || 1);
  return dt.toLocaleDateString(undefined, { month: "short", day: "numeric" });
}

function prettyDateTime(dateStr, timeStr) {
  if (!dateStr || !timeStr) return "";
  const [y, m, d] = dateStr.split("-").map(Number);
  const [hh, mm] = timeStr.split(":").map(Number);
  const dt = new Date(y, (m || 1) - 1, d || 1, hh || 0, mm || 0);
  return dt.toLocaleString(undefined, { month: "short", day: "numeric", hour: "2-digit", minute: "2-digit" });
}

function formatDisplayDateTime(value) {
  if (!value) return "";
  const dt = new Date(value);
  if (Number.isNaN(dt.getTime())) return value;
  return dt.toLocaleString(undefined, {
    month: "short",
    day: "numeric",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function addHoursToDateTime(dateStr, timeStr, hours) {
  if (!dateStr || !timeStr) return { date: "", time: "" };
  const [y, m, d] = dateStr.split("-").map(Number);
  const [hh, mm] = timeStr.split(":").map(Number);
  const dt = new Date(y, (m || 1) - 1, d || 1, hh || 0, mm || 0);
  dt.setHours(dt.getHours() + hours);
  const endDate = dt.getFullYear() + "-" + String(dt.getMonth() + 1).padStart(2, "0") + "-" + String(dt.getDate()).padStart(2, "0");
  const endTime = String(dt.getHours()).padStart(2, "0") + ":" + String(dt.getMinutes()).padStart(2, "0");
  return { date: endDate, time: endTime };
}

function combineDateTime(dateStr, timeStr) {
  if (!dateStr || !timeStr) return "";
  const [y, m, d] = dateStr.split("-").map(Number);
  const [hh, mm] = timeStr.split(":").map(Number);
  const dt = new Date(y, (m || 1) - 1, d || 1, hh || 0, mm || 0);
  return toLocalOffsetISOString(dt);
}

function toLocalOffsetISOString(date) {
  const pad = (n) => String(n).padStart(2, "0");
  const y = date.getFullYear();
  const m = pad(date.getMonth() + 1);
  const d = pad(date.getDate());
  const hh = pad(date.getHours());
  const mm = pad(date.getMinutes());
  const ss = pad(date.getSeconds());
  const tzMinutes = -date.getTimezoneOffset();
  const sign = tzMinutes >= 0 ? "+" : "-";
  const abs = Math.abs(tzMinutes);
  const tzh = pad(Math.floor(abs / 60));
  const tzm = pad(abs % 60);
  return `${y}-${m}-${d}T${hh}:${mm}:${ss}${sign}${tzh}:${tzm}`;
}

function isStartInPast() {
  if (!borrowForm.startDate || !borrowForm.startTime) return false;
  const now = new Date();
  const [y, m, d] = borrowForm.startDate.split("-").map(Number);
  const [hh, mm] = borrowForm.startTime.split(":").map(Number);
  const start = new Date(y, (m || 1) - 1, d || 1, hh || 0, mm || 0);
  return start < now;
}

function isTimeBefore(a, b) {
  if (!a || !b) return false;
  return a < b;
}

function dateOnly(value) {
  if (!value) return "";
  return String(value).split("T")[0];
}

function applyBorrowLength() {
  const option = LENGTH_OPTIONS.find((o) => o.key === borrowLength.value);
  if (!option || option.key === "custom") return;
  if (!borrowForm.startDate) borrowForm.startDate = todayString();
  if (!borrowForm.startTime) {
    borrowForm.startTime = timeString(roundToNext15Minutes(new Date()));
  }
  const end = addHoursToDateTime(borrowForm.startDate, borrowForm.startTime, option.hours);
  borrowForm.endDate = end.date;
  borrowForm.endTime = end.time;
}

function handleApiError(error, fallbackMessage) {
  const status = error?.response?.status;
  if (status === 401) {
    setNotice("error", "Session expired. Please sign in again.", 0);
    router.push({ name: "signin" });
    return;
  }
  const errors = error?.response?.data?.errors;
  const message = Array.isArray(errors) ? errors.join("\n") : fallbackMessage;
  setNotice("error", message, 0);
}

async function fetchCopies() {
  loading.copies = true;
  try {
    const { data } = await axiosClient.get("/boardgamecopies");
    copies.value = data ?? [];
  } catch (error) {
    handleApiError(error, "Failed to load available copies.");
  } finally {
    loading.copies = false;
  }
}

async function fetchOwnerCopies() {
  if (!ownerId.value) return;
  loading.ownerCopies = true;
  try {
    const { data } = await axiosClient.get(`/boardgamecopies/byplayer/${ownerId.value}`);
    ownerCopies.value = data ?? [];
  } catch (error) {
    handleApiError(error, "Failed to load your copies.");
  } finally {
    loading.ownerCopies = false;
  }
}

async function fetchPlayerRequests() {
  if (!playerId.value) return;
  loading.playerRequests = true;
  try {
    const { data } = await axiosClient.get(`/players/${playerId.value}/borrowrequests`);
    playerRequests.value = data ?? [];
  } catch (error) {
    handleApiError(error, "Failed to load your borrow requests.");
  } finally {
    loading.playerRequests = false;
  }
}

async function fetchOwnerRequests() {
  if (!ownerId.value) return;
  loading.ownerRequests = true;
  try {
    const { data } = await axiosClient.get(`/borrowrequests?ownerId=${ownerId.value}`);
    ownerRequests.value = data ?? [];
  } catch (error) {
    handleApiError(error, "Failed to load incoming borrow requests.");
  } finally {
    loading.ownerRequests = false;
  }
}

let intervalId = null;

onMounted(async () => {
  await Promise.all([fetchCopies(), fetchPlayerRequests()]);
  if (isOwner.value) {
    await Promise.all([fetchOwnerRequests(), fetchOwnerCopies()]);
    intervalId = setInterval(fetchOwnerRequests, 10000);
  }
});

onUnmounted(() => {
  if (intervalId) clearInterval(intervalId);
});

function selectCopy(copy) {
  if (selectedCopy.value?.boardGameCopyId === copy.boardGameCopyId) {
    selectedCopy.value = null;
    return;
  }
  selectedCopy.value = copy;
  borrowNotice.type = "";
  borrowNotice.message = "";
  borrowTouched.value = false;
  if (!borrowForm.startDate) {
    borrowForm.startDate = todayString();
  }
  if (!borrowForm.startTime) {
    borrowForm.startTime = timeString(roundToNext15Minutes(new Date()));
  }
  applyBorrowLength();
}

async function requestBorrow() {
  if (isGuest.value) {
    showDemoNotice("Demo mode: sign in to perform this action.");
    setNotice("error", "Sign in to request a borrow.", 0);
    return;
  }
  borrowTouched.value = true;
  if (!isBorrowValid.value) return;
  borrowSubmitting.value = true;
  try {
    await axiosClient.post("/borrowrequests", {
      startOfLoan: combineDateTime(borrowForm.startDate, borrowForm.startTime),
      endOfLoan: combineDateTime(borrowForm.endDate, borrowForm.endTime),
      borrowerID: playerId.value,
      specificGameID: selectedCopy.value.boardGameCopyId,
    });
    setNotice("success", "Borrow request sent.");
    borrowForm.startDate = "";
    borrowForm.startTime = "";
    borrowForm.endDate = "";
    borrowForm.endTime = "";
    borrowLength.value = "24h";
    await Promise.all([fetchPlayerRequests(), fetchCopies()]);
  } catch (error) {
    handleApiError(error, "Failed to create borrow request.");
  } finally {
    borrowSubmitting.value = false;
  }
}

function getStatusClass(status) {
  const map = {
    Pending: "status-pending",
    Accepted: "status-accepted",
    Denied: "status-denied",
    Done: "status-done",
    InProgress: "status-active",
    Cancelled: "status-cancelled",
  };
  return map[status] || "status-default";
}

function canConfirmGotGame(status, startDate, endDate) {
  const today = new Date();
  const todayStr = today.getFullYear() + "-" + String(today.getMonth() + 1).padStart(2, "0") + "-" + String(today.getDate()).padStart(2, "0");
  const start = dateOnly(startDate);
  const end = dateOnly(endDate);
  return status === "Accepted" && todayStr >= start && todayStr <= end;
}

function canCancelRequest(status, startDate) {
  const today = new Date();
  const todayStr = today.getFullYear() + "-" + String(today.getMonth() + 1).padStart(2, "0") + "-" + String(today.getDate()).padStart(2, "0");
  const start = dateOnly(startDate);
  return (status === "Accepted" || status === "InProgress") && todayStr >= start;
}

function isBorrowExpired(endDate) {
  if (!endDate) return false;
  const end = new Date(endDate);
  if (!Number.isNaN(end.getTime())) {
    return end.getTime() < Date.now();
  }
  const today = new Date();
  const todayStr = today.getFullYear() + "-" + String(today.getMonth() + 1).padStart(2, "0") + "-" + String(today.getDate()).padStart(2, "0");
  const endDay = dateOnly(endDate);
  return endDay && todayStr > endDay;
}

function canCancelPending(status) {
  return status === "Pending";
}

async function acceptRequest(id, name) {
  if (isGuest.value) {
    showDemoNotice("Demo mode: sign in to perform this action.");
    return;
  }
  try {
    await axiosClient.put(`/borrowrequests/${id}?action=accept`);
    setNotice("success", `Request from ${name} accepted successfully`);
    await fetchOwnerRequests();
  } catch (error) {
    handleApiError(error, "Failed to accept request.");
  }
}

async function declineRequest(id, name) {
  if (isGuest.value) {
    showDemoNotice("Demo mode: sign in to perform this action.");
    return;
  }
  try {
    await axiosClient.put(`/borrowrequests/${id}?action=decline`);
    setNotice("success", `Request from ${name} denied successfully!`);
    await fetchOwnerRequests();
  } catch (error) {
    handleApiError(error, "Failed to decline request.");
  }
}

async function confirmRequest(id, gameName) {
  if (isGuest.value) {
    showDemoNotice("Demo mode: sign in to perform this action.");
    return;
  }
  try {
    await axiosClient.put(`/borrowrequests/${id}/boardGameCopy?confirmOrCancel=confirm`);
    setNotice("success", `Borrow time started: confirmed ${gameName} was received`);
    await fetchPlayerRequests();
  } catch (error) {
    handleApiError(error, "Failed to confirm receipt.");
  }
}

async function cancelRequest(id, gameName) {
  if (isGuest.value) {
    showDemoNotice("Demo mode: sign in to perform this action.");
    return;
  }
  try {
    await axiosClient.put(`/borrowrequests/${id}/boardGameCopy?confirmOrCancel=cancel`);
    setNotice("info", `Cancelled early: ${gameName} borrowing was returned`);
    await fetchPlayerRequests();
  } catch (error) {
    handleApiError(error, "Failed to cancel request.");
  }
}

async function cancelPendingRequest(id) {
  if (isGuest.value) {
    showDemoNotice("Demo mode: sign in to perform this action.");
    return;
  }
  try {
    await axiosClient.delete(`/borrowrequests/${id}`);
    setNotice("success", "Request cancelled.");
    await fetchPlayerRequests();
  } catch (error) {
    handleApiError(error, "Failed to cancel request.");
  }
}

async function cancelBorrowRequest(id, gameName) {
  if (isGuest.value) {
    showDemoNotice("Demo mode: sign in to perform this action.");
    return;
  }
  try {
    await axiosClient.put(`/borrowrequests/${id}/boardGameCopy?confirmOrCancel=cancel`);
    setNotice("success", `Borrow time over: confirmed ${gameName} was returned`);
    await fetchOwnerRequests();
  } catch (error) {
    handleApiError(error, "Failed to confirm return.");
  }
}

watchEffect(() => {
  if (borrowLength.value !== "custom" && borrowForm.startDate) {
    applyBorrowLength();
  }
});

watchEffect(() => {
  if (!borrowForm.startDate) return;
  if (borrowForm.startDate === todayString() && isTimeBefore(borrowForm.startTime, startTimeMin.value)) {
    borrowForm.startTime = startTimeMin.value;
  }
  if (borrowForm.endDate && borrowForm.endDate < borrowForm.startDate) {
    borrowForm.endDate = borrowForm.startDate;
  }
  if (borrowForm.endDate === borrowForm.startDate && isTimeBefore(borrowForm.endTime, endTimeMin.value)) {
    borrowForm.endTime = endTimeMin.value;
  }
});

watchEffect(() => {
  if (borrowLength.value !== "custom") return;
  if (!borrowForm.startDate) borrowForm.startDate = todayString();
  if (!borrowForm.startTime) borrowForm.startTime = timeString(roundToNext15Minutes(new Date()));
  if (!borrowForm.endDate) borrowForm.endDate = borrowForm.startDate;
  if (!borrowForm.endTime) borrowForm.endTime = borrowForm.startTime;
});
</script>

<template>
  <div>
    <NavLandingSigned />

    <main class="page">
      <header class="page-header">
        <div class="head-row">
          <div class="title-wrap">
            <div>
              <h1>Borrow</h1>
              <p>Request a copy, track your requests, or manage incoming requests.</p>
            </div>
          </div>
        </div>

        <div class="tabs">
          <button
            v-for="t in visibleTabs"
            :key="t.key"
            class="tab wide"
            :class="{ active: activeTab === t.key }"
            @click="router.push({ name: 'borrowRequestMenu', query: { ...route.query, tab: t.key } })"
          >
            {{ t.label }}
          </button>
        </div>
      </header>

      <section class="card">
        <div v-if="borrowNotice.message" class="inline-banner" :class="borrowNotice.type">
          {{ borrowNotice.message }}
        </div>
        <div v-if="activeTab === 'browse'" class="browse-layout">
          <div>
            <div class="section-head">
              <div>
                <h2>Browse copies to borrow</h2>
                <p class="subtle">Select a copy to request a borrow period.</p>
              </div>
            </div>

            <div class="catalog-controls">
              <div class="search-wrap">
                <input
                  v-model="copyQuery"
                  type="text"
                  class="input"
                  placeholder="Search by game, owner, or spec..."
                />
              </div>
            </div>

            <div v-if="loading.copies" class="empty">Loading copies...</div>
            <div v-else class="grid copies">
              <button
                v-for="copy in filteredCopies"
                :key="copy.boardGameCopyId"
                type="button"
                class="copy-card"
                :class="{ selected: selectedCopy?.boardGameCopyId === copy.boardGameCopyId }"
                @click="selectCopy(copy)"
              >
                <div class="copy-cover">
                  <span>{{ (copy.boardGameName || "?").slice(0, 1) }}</span>
                </div>
                <div class="copy-body">
                  <h3 class="title">{{ copy.boardGameName }}</h3>
                  <div class="meta-text">Owner: {{ copy.playerName }}</div>
                  <div class="meta-text">{{ copy.specification }}</div>
                  <span class="badge" :class="copy.isAvailable ? 'ok' : 'warn'">
                    {{ copy.isAvailable ? "Available" : "Unavailable" }}
                  </span>
                </div>
              </button>

              <div v-if="filteredCopies.length === 0" class="empty">No copies found.</div>
            </div>
          </div>

          <aside class="panel">
            <div class="panel-card">
              <h3>Request details</h3>
              <p class="subtle">Select a copy to request a borrow.</p>

              <div v-if="selectedCopy" class="panel-info">
                <div class="panel-title">{{ selectedCopy.boardGameName }}</div>
                <div class="panel-meta">Owner: {{ selectedCopy.playerName }}</div>
                <div class="panel-meta">{{ selectedCopy.specification }}</div>
                <div class="panel-meta">
                  Status: <span :class="selectedCopy.isAvailable ? 'status-ok' : 'status-warn'">{{ selectedCopy.isAvailable ? "Available" : "Unavailable" }}</span>
                </div>
              </div>

              <div class="form-block" :class="{ disabled: !selectedCopy }">
                <label class="label">Start date</label>
                <input class="input" type="date" v-model="borrowForm.startDate" :min="startDateMin" :disabled="!selectedCopy" />

                <label class="label">Start time</label>
                <input class="input" type="time" v-model="borrowForm.startTime" :min="startTimeMin" :disabled="!selectedCopy" />

                <label class="label">Borrow length</label>
                <div class="length-chips">
                  <button
                    v-for="opt in LENGTH_OPTIONS"
                    :key="opt.key"
                    type="button"
                    class="chip"
                    :class="{ active: borrowLength === opt.key }"
                    :disabled="!selectedCopy"
                    @click="borrowLength = opt.key; applyBorrowLength()"
                  >
                    {{ opt.label }}
                  </button>
                </div>

                <div v-if="borrowLength === 'custom'">
                  <label class="label">End date</label>
                  <input class="input" type="date" v-model="borrowForm.endDate" :min="endDateMin" :disabled="!selectedCopy" />

                  <label class="label">End time</label>
                  <input class="input" type="time" v-model="borrowForm.endTime" :min="endTimeMin" :disabled="!selectedCopy" />
                </div>

                <small v-if="borrowSummary" class="summary">{{ borrowSummary }}</small>

                <small v-if="borrowError" class="err">{{ borrowError }}</small>

                  <button
                    class="btn primary"
                    type="button"
                    :disabled="!isBorrowValid || borrowSubmitting || !selectedCopy?.isAvailable || isGuest"
                    @click="requestBorrow"
                  >
                  {{ borrowSubmitting ? "Submitting..." : "Request borrow" }}
                </button>
              </div>
            </div>
          </aside>
        </div>

        <div v-else-if="activeTab === 'mine'">
          <div class="section-head">
            <div>
              <h2>My borrow requests</h2>
              <p class="subtle">Track your requests and take actions when available.</p>
            </div>
          </div>

          <div v-if="loading.playerRequests" class="empty">Loading requests...</div>
          <div v-else class="table-wrap">
            <table class="table">
              <thead>
                <tr>
                  <th>Game</th>
                  <th>Start</th>
                  <th>End</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="request in playerRequests" :key="request.requestId">
                  <td>{{ request.specificGameName }}</td>
                  <td>{{ formatDisplayDateTime(request.startOfLoan) }}</td>
                  <td>{{ formatDisplayDateTime(request.endOfLoan) }}</td>
                  <td>
                    <span class="status" :class="getStatusClass(request.requestStatus)">
                      {{ request.requestStatus }}
                    </span>
                  </td>
                  <td class="actions-cell">
                    <button
                      v-if="canConfirmGotGame(request.requestStatus, request.startOfLoan, request.endOfLoan)"
                      class="btn ghost"
                      @click="confirmRequest(request.requestId, request.specificGameName)"
                    >
                      Confirm
                    </button>
                    <button
                      v-if="canCancelRequest(request.requestStatus, request.startOfLoan)"
                      class="btn ghost"
                      :disabled="isBorrowExpired(request.endOfLoan)"
                      @click="cancelRequest(request.requestId, request.specificGameName)"
                    >
                      {{ isBorrowExpired(request.endOfLoan) ? "Borrow ended" : "End borrow" }}
                    </button>
                    <button
                      v-if="canCancelPending(request.requestStatus)"
                      class="btn action-decline"
                      @click="cancelPendingRequest(request.requestId)"
                    >
                      Cancel request
                    </button>
                  </td>
                </tr>
                <tr v-if="playerRequests.length === 0">
                  <td colspan="5" class="empty">No borrow requests yet.</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div v-else>
          <div class="section-head">
            <div>
              <h2>Manage incoming requests</h2>
              <p class="subtle">Review and respond to requests for your copies.</p>
            </div>
          </div>

          <div class="manage-grid">
            <div>
              <h3 class="subhead">Incoming requests</h3>
              <div v-if="loading.ownerRequests" class="empty">Loading incoming requests...</div>
              <div v-else class="table-wrap">
                <table class="table">
                  <thead>
                    <tr>
                      <th>Game</th>
                      <th>Borrower</th>
                      <th>Start</th>
                      <th>End</th>
                      <th>Status</th>
                      <th>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="request in ownerRequests" :key="request.requestId">
                      <td>{{ request.specificGameName }}</td>
                      <td>{{ request.borrowerName }}</td>
                      <td>{{ formatDisplayDateTime(request.startOfLoan) }}</td>
                      <td>{{ formatDisplayDateTime(request.endOfLoan) }}</td>
                      <td>
                        <span class="status" :class="getStatusClass(request.requestStatus)">
                          {{ request.requestStatus }}
                        </span>
                      </td>
                      <td class="actions-cell">
                        <button
                          v-if="request.requestStatus === 'Pending'"
                          class="btn action-accept"
                          @click="acceptRequest(request.requestId, request.borrowerName)"
                        >
                          Accept
                        </button>
                        <button
                          v-if="request.requestStatus === 'Pending'"
                          class="btn action-decline"
                          @click="declineRequest(request.requestId, request.borrowerName)"
                        >
                          Decline
                        </button>
                        <button
                          v-if="request.requestStatus === 'InProgress'"
                          class="btn ghost"
                          @click="cancelBorrowRequest(request.requestId, request.specificGameName)"
                        >
                          Confirm return
                        </button>
                      </td>
                    </tr>
                    <tr v-if="ownerRequests.length === 0">
                      <td colspan="6" class="empty">No incoming requests.</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <div>
              <h3 class="subhead">My copies</h3>
              <div v-if="loading.ownerCopies" class="empty">Loading your copies...</div>
              <div v-else class="grid copies">
                <div v-for="copy in ownerCopies" :key="copy.boardGameCopyId" class="copy-card">
                  <div class="copy-cover">
                    <span>{{ (copy.boardGameName || "?").slice(0, 1) }}</span>
                  </div>
                  <div class="copy-body">
                    <h3 class="title">{{ copy.boardGameName }}</h3>
                    <div class="meta-text">{{ copy.specification }}</div>
                    <span class="badge" :class="copy.isAvailable ? 'ok' : 'warn'">
                      {{ copy.isAvailable ? "Available" : "Unavailable" }}
                    </span>
                  </div>
                </div>
                <div v-if="ownerCopies.length === 0" class="empty">No copies yet.</div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
:deep(.landing-nav) { z-index: 9999; pointer-events: auto; }
:deep(.landing-nav * ) { pointer-events: auto; }
.page { position: relative; z-index: 0; margin-top: 96px; padding: 24px; }
.page-header, .card, .browse-layout, .manage-grid { position: relative; z-index: 0; }
.page-header { display: flex; flex-direction: column; gap: 10px; margin-bottom: 16px; }
.head-row { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
.title-wrap { display: flex; gap: 14px; align-items: center; }
.page-header h1 { font-size: 32px; margin: 0; color: #e8ecf7; font-weight: 800; }
.page-header p { opacity: .75; margin: 0; color: #c3cad9; }

.tabs { display: flex; gap: 8px; margin-top: 8px; }
.tab { padding: 8px 16px; border-radius: 10px; background: transparent; color: #d8deed; border: 1px solid #2f384a; font-weight: 600; }
.tab.wide { padding: 12px 26px; min-width: 120px; text-align: center; }
.tab.active { background: #fff; color: #0f1217; border-color: #ffffff; font-weight: 800; box-shadow: 0 2px 6px rgba(255,255,255,0.15); }
.tab:not(.active):hover { background: #f4f6fa; color: #0f1217; border-color: #f4f6fa; }

.card { border: 1px solid #293043; border-radius: 16px; background: #0f1217; color: #e9edf5; padding: 20px; }
.section-head { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 10px; }
.subtle { color: #aab3c3; margin: 0; }
.subhead { margin: 0 0 8px; font-weight: 800; }

.btn { padding: 10px 16px; border-radius: 10px; font-weight: 600; transition: all .2s ease; border: 1px solid transparent; cursor: pointer; }
.btn.primary { background: #ffffff; color: #0f1217; border-color: #ffffff; }
.btn.primary:hover { background: #f3f3f3; }
.btn.ghost { background: transparent; border: 1px solid #2f384a; color: #dfe5f4; }
.btn.ghost:hover { background: #182132; }
.btn.action-accept { background: #112218; color: #b7ffd1; border: 1px solid #3e7350; box-shadow: 0 8px 18px rgba(18, 62, 36, 0.35); }
.btn.action-accept:hover { background: #16301e; }
.btn.action-decline { background: #2a171b; color: #ffd6d6; border: 1px solid #5b2c2c; box-shadow: 0 8px 18px rgba(68, 24, 24, 0.35); }
.btn.action-decline:hover { background: #361a1f; }
.btn:disabled { opacity: .6; cursor: not-allowed; }
.btn.back { padding: 8px 12px; }

.input { width: 100%; background: #151a22; color: #f0f4ff; border: 1px solid #384054; border-radius: 10px;
  padding: 10px 12px; outline: none; transition: border-color .2s ease, box-shadow .2s ease; }
.input:focus { border-color: #72aaff; box-shadow: 0 0 0 2px rgba(114,170,255,0.25); }
.label { font-size: 13px; font-weight: 600; opacity: .85; color: #e2e6f2; }

.catalog-controls { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin: 10px 0 18px; flex-wrap: wrap; }
.search-wrap { flex: 1 1 320px; }

.length-chips { display: flex; gap: 8px; flex-wrap: wrap; }
.chip { padding: 8px 12px; border-radius: 999px; border: 1px solid #2f384a; background: transparent; color: #d8deed; font-weight: 700; }
.chip.active { background: #fff; color: #0f1217; border-color: #fff; }
.summary { display: block; color: #9aa2b2; margin-top: 4px; }

.inline-banner { margin-bottom: 12px; padding: 10px 12px; border-radius: 10px; border: 1px solid #2b3343; background: #0f1217; font-weight: 700; }
.inline-banner.success { border-color: #3e7350; background: #112218; color: #b7ffd1; }
.inline-banner.error { border-color: #8a2a2a; background: #1a1010; color: #ffd6d6; }
.inline-banner.info { border-color: #3b5b9e; background: #0f1625; color: #d7e5ff; }

.browse-layout { display: grid; grid-template-columns: 1.2fr 0.8fr; gap: 16px; }
.panel { position: sticky; top: 96px; align-self: start; }
.panel-card { border: 1px solid #1f2533; border-radius: 14px; padding: 16px; background: #0f1217; }
.panel-info { margin: 10px 0; }
.panel-title { font-weight: 900; font-size: 18px; color: #fff; }
.panel-meta { color: #c3cad9; margin-top: 4px; }
.form-block { display: grid; gap: 8px; margin-top: 10px; }
.form-block.disabled { opacity: .55; }
.form-block.disabled .input,
.form-block.disabled .chip,
.form-block.disabled .btn { cursor: not-allowed; }

.grid { display: grid; gap: 14px; }
.grid.copies { grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); }

.copy-card {
  border-radius: 16px;
  padding: 12px;
  background: #0f1217;
  border: 1px solid #1f2533;
  box-shadow: 0 10px 24px rgba(0,0,0,.35);
  text-align: left;
  transition: transform .18s ease, box-shadow .18s ease, border-color .18s ease;
}
.copy-card:hover { transform: translateY(-4px); border-color: #3a4256; box-shadow: 0 16px 32px rgba(0,0,0,.45); }
.copy-card.selected { border-color: #ffffff; box-shadow: 0 0 0 1px #ffffff inset; }
.copy-cover { height: 90px; border-radius: 12px; display: grid; place-items: center; background: linear-gradient(135deg, #151a22, #0f1217); border: 1px solid #1f2533; margin-bottom: 10px; }
.copy-cover span { font-size: 28px; font-weight: 900; color: #e6ecff; }
.copy-body { display: grid; gap: 4px; }
.title { margin: 0; font-size: 18px; font-weight: 900; color: #ffffff; }
.meta-text { color: #c6cedf; font-size: 12px; font-weight: 700; }

.badge { padding: 4px 10px; border-radius: 999px; font-size: 12px; font-weight: 800; border: 1px solid transparent; width: fit-content; }
.badge.ok { background: #112218; color: #b7ffd1; border-color: #3e7350; }
.badge.warn { background: #24180b; color: #ffe2b8; border-color: #6a4a23; }

.status { padding: 4px 10px; border-radius: 999px; font-weight: 800; border: 1px solid transparent; }
.status-pending { background: #0d1726; color: #cde6ff; border-color: #2e4a74; }
.status-accepted { background: #112218; color: #b7ffd1; border-color: #3e7350; }
.status-active { background: #0f2015; color: #c8ffd5; border-color: #2c5b38; }
.status-denied { background: #1f1111; color: #ffd6d6; border-color: #5b2c2c; }
.status-done { background: #0f1625; color: #d7e5ff; border-color: #3b5b9e; }
.status-cancelled { background: #2a171b; color: #ffd6d6; border-color: #5b2c2c; }
.status-default { background: #1c2230; color: #e2e6f2; border-color: #2f384a; }

.err { color: #ffd6d6; }
.empty { color: #9aa2b2; text-align: center; padding: 16px; }

.table-wrap { border: 1px solid #1f2533; border-radius: 12px; overflow: hidden; }
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 12px; text-align: left; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; color: #dfe5f4; }
.table thead { background: #161b24; }
.table tbody tr { border-top: 1px solid #1f2634; }
.actions-cell { display: flex; gap: 8px; flex-wrap: wrap; }

.manage-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }

.status-ok { color: #b7ffd1; font-weight: 700; }
.status-warn { color: #ffe2b8; font-weight: 700; }

@media (max-width: 980px) {
  .browse-layout { grid-template-columns: 1fr; }
  .panel { position: static; }
  .manage-grid { grid-template-columns: 1fr; }
}

@media (max-width: 640px) {
  .head-row { flex-direction: column; align-items: flex-start; }
  .title-wrap { flex-direction: column; align-items: flex-start; }
  .tab.wide { min-width: 90px; padding: 10px 16px; }
}
</style>





