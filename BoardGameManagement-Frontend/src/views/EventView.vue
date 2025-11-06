<!-- src/views/EventView.vue -->
<template>
  <div>
    <NavLandingSigned />

    <main class="page">
      <header class="page-header">
        <div class="head-row">
          <div>
            <h1>Events</h1>
            <p>Discover public sessions, register or cancel, and (if you’re an owner) create and manage your own.</p>
          </div>
          <button class="btn cta" @click="tab = 'Create'">＋ New Event</button>
        </div>

        <div class="tabs">
          <button v-for="t in TABS" :key="t" class="tab wide" :class="{ active: tab === t }" @click="tab = t">
            {{ t }}
          </button>
        </div>
      </header>

      <!-- ========== BROWSE ========== -->
      <section v-if="tab === 'Browse'" class="card">
        <!-- toolbar: search + filter toggle + actions -->
        <div class="toolbar">
          <input v-model="search" class="input" placeholder="Search by name, location, date, owner..." />

          <button class="btn ghost filter-btn" @click="toggleFilters" :aria-expanded="showFilters">
            <svg class="ficon" viewBox="0 0 24 24" aria-hidden="true">
              <path d="M3 5h18v2H3V5zm4 6h10v2H7v-2zm3 6h4v2h-4v-2z" fill="currentColor"/>
            </svg>
            Filter
          </button>

          <div class="actions">
            <button class="btn primary" :disabled="!selectedBrowseId" @click="registerForSelected">Register</button>
            <button class="btn danger" :disabled="!selectedBrowseId" @click="cancelSelected">Cancel</button>
          </div>
        </div>

        <!-- filter chips (collapsed by default) -->
        <div v-if="showFilters" class="filters under-toolbar">
          <button
              v-for="f in FILTERS"
              :key="f"
              class="chip"
              :class="{ active: browseFilter === f }"
              @click="browseFilter = f"
          >
            {{ f }}
          </button>
        </div>

        <div v-if="action.visible" class="inline-banner" :class="action.kind">{{ action.msg }}</div>
        <small class="hint">Click once to select, double-click to view details, or use the “View details” button.</small>

        <div class="table-wrap">
          <table class="table">
            <thead>
            <tr>
              <th>Event</th>
              <th class="num">Date</th>
              <th class="num">Time</th>
              <th>
                  <span class="loc-head">
                    <svg class="pin" viewBox="0 0 24 24" aria-hidden="true">
                      <path d="M12 2C8.686 2 6 4.686 6 8c0 4.418 6 12 6 12s6-7.582 6-12c0-3.314-2.686-6-6-6zm0 8.5a2.5 2.5 0 1 1 0-5 2.5 2.5 0 0 1 0 5z" fill="currentColor"/>
                    </svg>
                    Location
                  </span>
              </th>
              <th>Owner</th>
              <th>State</th>
              <th>Registration</th>
              <th class="ta-center">Details</th>
            </tr>
            </thead>
            <tbody>
            <tr
                v-for="ev in filteredEvents"
                :key="ev.eventID"
                :class="{ selected: selectedBrowseId === ev.eventID }"
                @click="selectBrowse(ev.eventID)"
                @dblclick="openDetail(ev)"
                :title="'Double-click to view details for ' + ev.name"
            >
              <td class="name-cell"><span class="name">{{ ev.name }}</span></td>
              <td class="num">{{ ev.eventDate }}</td>
              <td class="num">{{ prettyTime(ev) }}</td>
              <td>{{ ev.location }}</td>
              <td>{{ ev.ownerName ?? '—' }}</td>
              <td><span class="state" :class="eventStateClass(ev)">{{ eventState(ev) }}</span></td>
              <td>
                  <span v-if="capacityMap[ev.eventID]">
                    {{ capacityMap[ev.eventID].current }} / {{ capacityMap[ev.eventID].max }}
                    <span v-if="capacityMap[ev.eventID].full" class="badge badge-full" title="All spots taken">Full</span>
                  </span>
                <span v-else>…</span>
              </td>
              <td class="ta-center">
                <button type="button" class="btn details" @click.stop="openDetail(ev)">View details</button>
              </td>
            </tr>
            <tr v-if="filteredEvents.length === 0">
              <td colspan="8" class="empty">No events found.</td>
            </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- ========== CREATE ========== -->
      <section v-else-if="tab === 'Create'" class="card">
        <h3>Create a New Event</h3>
        <form class="grid" @submit.prevent="createEvent">
          <div class="col">
            <label class="label">Event name</label>
            <input class="input" v-model.trim="createForm.name" required />

            <label class="label">Max spots</label>
            <input class="input" type="number" min="1" v-model.number="createForm.maxSpot" required />

            <label class="label">Start time</label>
            <input class="input" type="time" v-model="createForm.startTime" required @keydown.prevent />

            <label class="label">Location</label>
            <input class="input" v-model.trim="createForm.location" required />
          </div>

          <div class="col">
            <label class="label">Board game</label>
            <select class="input" v-model="createForm.boardGameId" required>
              <option disabled value="">Choose a board game</option>
              <option v-for="g in boardGames" :key="g.gameID" :value="g.gameID">{{ g.name }}</option>
            </select>

            <label class="label">Date</label>
            <input class="input" type="date" :min="minDate" v-model="createForm.date" required @keydown.prevent />

            <label class="label">End time</label>
            <input
                class="input"
                type="time"
                v-model="createForm.endTime"
                :min="createForm.startTime || undefined"
                required
                @keydown.prevent
            />

            <label class="label">Description</label>
            <textarea
                class="input"
                rows="3"
                v-model.trim="createForm.description"
                :class="{ invalid: createTouched && !createForm.description }"
            ></textarea>
            <small v-if="createTouched && !createForm.description" class="err">Description is required.</small>
          </div>

          <div class="col col-2">
            <button type="submit" class="btn primary" :disabled="!isCreateValid">Create</button>
            <button type="button" class="btn" @click="resetCreate">Reset</button>
          </div>
        </form>

        <div class="toasts">
          <div v-for="t in toasts" :key="t.id" class="toast" :class="t.kind">{{ t.msg }}</div>
        </div>
      </section>

      <!-- ========== MANAGE ========== -->
      <section v-else class="card">
        <h3>Manage My Events</h3>

        <div class="grid">
          <div class="col">
            <label class="label">Select event</label>
            <select class="input" v-model="manage.selectedId">
              <option disabled value="">— select —</option>
              <option v-for="ev in myEvents" :key="ev.eventID" :value="ev.eventID">
                {{ ev.name }} — {{ ev.eventDate }}
              </option>
            </select>
          </div>
        </div>

        <div class="grid" v-if="lock.visible">
          <div class="col col-2">
            <div class="inline-banner danger">{{ lock.message }}</div>
          </div>
        </div>

        <form class="grid" @submit.prevent="updateEvent" v-if="selectedManageEvent">
          <div class="col">
            <label class="label">Event name</label>
            <input class="input" v-model.trim="manage.form.name" required />

            <label class="label">Max spots</label>
            <input class="input" type="number" min="1" v-model.number="manage.form.maxSpot" required />

            <label class="label">Start time</label>
            <input class="input" type="time" v-model="manage.form.startTime" required @keydown.prevent />

            <label class="label">Location</label>
            <input class="input" v-model.trim="manage.form.location" required />
          </div>

          <div class="col">
            <label class="label">Board game</label>
            <select class="input" v-model="manage.form.boardGameId" required>
              <option disabled value="">Choose a board game</option>
              <option v-for="g in boardGames" :key="g.gameID" :value="g.gameID">{{ g.name }}</option>
            </select>

            <label class="label">Date</label>
            <input class="input" type="date" :min="minDate" v-model="manage.form.date" required @keydown.prevent />

            <label class="label">End time</label>
            <input
                class="input"
                type="time"
                v-model="manage.form.endTime"
                :min="manage.form.startTime || undefined"
                required
                @keydown.prevent
            />

            <label class="label">Description</label>
            <textarea class="input" rows="3" v-model.trim="manage.form.description"></textarea>
          </div>

          <div class="col col-2">
            <button type="submit" class="btn primary">Update</button>
            <button type="button" class="btn danger" :disabled="!manage.selectedId" @click="deleteEvent">Delete</button>
          </div>
        </form>

        <div v-else class="empty">Select one of your events to manage.</div>

        <div class="toasts">
          <div v-for="t in toasts" :key="t.id" class="toast" :class="t.kind">{{ t.msg }}</div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import NavLandingSigned from "@/components/NavLandingSigned.vue";
import axios from "axios";
import { ref, reactive, computed, onMounted, onBeforeUnmount } from "vue";
import { useAuthStore } from "@/stores/authStore.js";
import { useRouter } from "vue-router";

const api = axios.create({ baseURL: "http://localhost:8080" });
const router = useRouter();

const auth = useAuthStore();
const userId = computed(() => Number(auth.user?.id));

const TABS = ["Browse", "Create", "Manage"];
const FILTERS = ["All", "Upcoming", "Ongoing", "Past"];

// Default: UPCOMING
const tab = ref("Browse");
const browseFilter = ref("Upcoming");
const showFilters = ref(false);
const toggleFilters = () => (showFilters.value = !showFilters.value);

const events = ref([]);
const myEvents = ref([]);
const boardGames = ref([]);
const regStatus = reactive({});
const capacityMap = reactive({}); // { [id]: { current, max, full } }

const search = ref("");
const selectedBrowseId = ref(null);

const action = reactive({ visible: false, kind: "success", msg: "" });
let actionTimer;
function showAction(kind, msg, ms = 2200) {
  action.visible = true;
  action.kind = kind;
  action.msg = msg;
  clearTimeout(actionTimer);
  actionTimer = setTimeout(() => (action.visible = false), ms);
}

const today = new Date();
const minDate = today.toISOString().slice(0, 10);
const createForm = reactive({
  name: "",
  description: "",
  maxSpot: 1,
  date: minDate,
  startTime: "",
  endTime: "",
  location: "",
  boardGameId: "",
});
const createTouched = ref(false);
const isCreateValid = computed(() => {
  return (
      !!createForm.name &&
      !!createForm.boardGameId &&
      !!createForm.date &&
      !!createForm.startTime &&
      !!createForm.endTime &&
      !!createForm.location &&
      !!createForm.description
  );
});
function resetCreate() {
  createTouched.value = false;
  Object.assign(createForm, {
    name: "",
    description: "",
    maxSpot: 1,
    date: minDate,
    startTime: "",
    endTime: "",
    location: "",
    boardGameId: "",
  });
}

const manage = reactive({
  selectedId: "",
  form: {
    name: "",
    description: "",
    maxSpot: 1,
    date: minDate,
    startTime: "",
    endTime: "",
    location: "",
    boardGameId: "",
  },
});

const now = ref(Date.now());
let tickHandle = null;
const lock = reactive({ visible: false, message: "" });

function parseLocalDateTime(dateStr, timeStr) {
  if (!dateStr || !timeStr) return null;
  const [y, m, d] = dateStr.split("-").map(Number);
  const [hh, mm] = timeStr.split(":").map(Number);
  const dt = new Date(y, (m || 1) - 1, d || 1, hh || 0, mm || 0, 0);
  return isNaN(dt.getTime()) ? null : dt;
}
function fmtTime(hhmm) {
  if (!hhmm) return "";
  const [hh, mm] = hhmm.split(":").map(Number);
  const t = new Date();
  t.setHours(hh || 0, mm || 0, 0, 0);
  return t.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
}
function eventState(ev) {
  const start = parseLocalDateTime(ev.eventDate, (ev.startTime || "").slice(0, 5));
  const end = parseLocalDateTime(ev.eventDate, (ev.endTime || "").slice(0, 5));
  const t = now.value;
  if (!start || !end) return "Upcoming";
  if (t < start.getTime()) return "Upcoming";
  if (t >= start.getTime() && t < end.getTime()) return "Ongoing";
  return "Finished";
}
function eventStateClass(ev) {
  const s = eventState(ev);
  return s === "Ongoing" ? "ongoing" : s === "Finished" ? "finished" : "upcoming";
}
function prettyTime(ev) {
  const s = (ev.startTime || "").slice(0, 5);
  const e = (ev.endTime || "").slice(0, 5);
  if (!s && !e) return "—";
  return `${s || "??:??"}–${e || "??:??"}`;
}

const toasts = ref([]);
function pushToast(kind, msg, ms = 2600) {
  const id = Math.random().toString(36).slice(2);
  toasts.value.push({ id, kind, msg });
  setTimeout(() => (toasts.value = toasts.value.filter((t) => t.id !== id)), ms);
}

const selectedManageEvent = computed(() =>
    events.value.find((e) => Number(e.eventID) === Number(manage.selectedId))
);

onMounted(async () => {
  await Promise.all([fetchEvents(), fetchMyEvents(), fetchBoardGames()]);
  tickHandle = setInterval(() => (now.value = Date.now()), 30000);
});
onBeforeUnmount(() => {
  if (tickHandle) clearInterval(tickHandle);
  clearTimeout(actionTimer);
});

async function fetchEvents() {
  try {
    const { data } = await api.get("/events");
    events.value = data ?? [];
    await Promise.all(
        events.value.map(async (e) => {
          await refreshReg(e.eventID);
          await refreshCapacity(e.eventID, Number(e.maxSpot || e.maxSpots || e.max));
        })
    );
  } catch {
    pushToast("error", "Failed to load events");
  }
}
async function fetchMyEvents() {
  if (!userId.value) return;
  try {
    const { data } = await api.get(`/events/owner/${userId.value}`);
    myEvents.value = data ?? [];
  } catch {
    pushToast("error", "Failed to load your events");
  }
}
async function fetchBoardGames() {
  try {
    const { data } = await api.get("/boardgames");
    boardGames.value = data ?? [];
  } catch {
    pushToast("error", "Failed to load board games");
  }
}
async function refreshReg(eventId) {
  try {
    const { data } = await api.get(`/registrations/${userId.value}/${eventId}`);
    regStatus[eventId] = data === null ? "Not Registered" : "Registered";
  } catch (e) {
    if (e?.response?.status === 404) regStatus[eventId] = "Not Registered";
    else regStatus[eventId] = "Error";
  }
}
async function refreshCapacity(eventId, maxFromEvent) {
  const max = Number(maxFromEvent || 0);
  let current = 0;

  try {
    const { data } = await api.get(`/registrations/count/${eventId}`);
    current = Number(data || 0);
  } catch {
    let arr = [];
    const tryPaths = [
      `/registrations/event/${eventId}`,
      `/registrations/by-event/${eventId}`,
      `/registrations/events/${eventId}`,
    ];
    for (const p of tryPaths) {
      try {
        const { data } = await api.get(p);
        if (Array.isArray(data)) { arr = data; break; }
      } catch {}
    }
    current = Array.isArray(arr) ? arr.length : 0;
  }
  capacityMap[eventId] = { current, max, full: max > 0 ? current >= max : false };
}

const filteredEvents = computed(() => {
  const q = search.value.trim().toLowerCase();
  let arr = events.value.slice();
  if (browseFilter.value !== "All") {
    arr = arr.filter((e) => {
      const s = eventState(e);
      if (browseFilter.value === "Past") return s === "Finished";
      if (browseFilter.value === "Ongoing") return s === "Ongoing";
      if (browseFilter.value === "Upcoming") return s === "Upcoming";
      return true;
    });
  }
  if (!q) return arr;
  return arr.filter((e) => {
    const blob = [e.name, e.location, e.ownerName, e.eventDate].filter(Boolean).join(" ").toLowerCase();
    return blob.includes(q);
  });
});

function selectBrowse(id) {
  selectedBrowseId.value = id;
}

/** Navigate to details by natural key: name + date + start(HH:mm). */
function openDetail(ev) {
  const id = Number(ev?.eventID);
  if (!id || Number.isNaN(id)) return; // optional guard
  router.push({ name: "eventDetail", params: { id } });
}

async function registerForSelected() {
  const id = selectedBrowseId.value;
  if (!id) return;
  const ev = events.value.find((e) => e.eventID === id);
  const st = eventState(ev);
  if (st !== "Upcoming") {
    showAction("error", `Registration not allowed: event is ${st.toLowerCase()}.`);
    return;
  }
  if (capacityMap[id]?.full) {
    showAction("info", "Registration closed: event is full.");
    return;
  }
  if (regStatus[id] === "Registered") {
    showAction("info", "You’re already registered.");
    return;
  }
  try {
    await api.post("/registrations", { playerID: userId.value, eventID: id });
    await Promise.all([refreshReg(id), refreshCapacity(id, ev.maxSpot)]);
    showAction("success", "Successfully registered.");
  } catch {
    showAction("error", "Registration failed.");
  }
}

async function cancelSelected() {
  const id = selectedBrowseId.value;
  if (!id) return;
  const ev = events.value.find((e) => e.eventID === id);
  if (regStatus[id] === "Not Registered") {
    showAction("info", "You’re not registered for this event.");
    return;
  }
  try {
    await api.delete(`/registrations/${userId.value}/${id}`);
    await Promise.all([refreshReg(id), refreshCapacity(id, ev?.maxSpot)]);
    showAction("success", "Registration cancelled.");
  } catch (e) {
    const msg = (e?.response?.data?.message || "").toLowerCase?.() || "";
    if (msg.includes("active") || msg.includes("ongoing") || msg.includes("started")) {
      showAction("error", "You can’t cancel because the event is active.");
    } else {
      showAction("error", "Cancel failed.");
    }
  }
}

async function createEvent() {
  createTouched.value = true;
  if (!isCreateValid.value) {
    pushToast("error", "Please fill all required fields (description is required).");
    return;
  }
  const dupe = events.value.find(
      (e) =>
          e.name.trim().toLowerCase() === createForm.name.trim().toLowerCase() &&
          e.eventDate === createForm.date &&
          (e.startTime || "").slice(0, 5) === (createForm.startTime || "").slice(0, 5)
  );
  if (dupe) {
    pushToast("info", "An event with the same name, date, and start time already exists.");
    return;
  }
  try {
    await api.post("/events", {
      name: createForm.name,
      description: createForm.description,
      maxSpot: String(createForm.maxSpot),
      eventDate: createForm.date,
      startTime: `${createForm.startTime}:00`,
      endTime: `${createForm.endTime}:00`,
      location: createForm.location,
      ownerId: userId.value,
      boardGameId: createForm.boardGameId,
    });
    // refresh lists
    await Promise.all([fetchEvents(), fetchMyEvents()]);
    // switch to Browse and show success banner there
    tab.value = "Browse";
    showFilters.value = false;
    browseFilter.value = "Upcoming";
    showAction("success", "Event created.");
    // reset form after navigating away
    resetCreate();
  } catch (e) {
    const msg = e?.response?.data?.message || "Failed to create event";
    pushToast("error", msg);
  }
}

async function updateEvent() {
  if (!manage.selectedId) return;
  try {
    await api.put(`/events/${manage.selectedId}`, {
      name: manage.form.name,
      description: manage.form.description,
      maxSpot: manage.form.maxSpot,
      eventDate: manage.form.date,
      startTime: manage.form.startTime,
      endTime: manage.form.endTime,
      location: manage.form.location,
      ownerId: userId.value,
      boardGameId: manage.form.boardGameId,
    });
    pushToast("success", "Event updated");
    await Promise.all([fetchEvents(), fetchMyEvents()]);
  } catch (e) {
    const msg = e?.response?.data?.message || "Failed to update event";
    pushToast("error", msg);
  }
}

async function deleteEvent() {
  if (!manage.selectedId) return;
  lock.visible = false; lock.message = "";
  try {
    await api.delete(`/events/${manage.selectedId}`);
    pushToast("success", "Event deleted");
    manage.selectedId = "";
    await Promise.all([fetchEvents(), fetchMyEvents()]);
  } catch (e) {
    const status = e?.response?.status;
    const rawMsg =
        e?.response?.data?.message ||
        (Array.isArray(e?.response?.data?.errors) ? e.response.data.errors.join(", ") : "") ||
        "";
    if (status === 409) {
      const m = rawMsg.toLowerCase();
      if (m.includes("start") || m.includes("ongoing") || m.includes("active")) {
        lock.visible = true; lock.message = "Cannot delete while the event is ongoing.";
        pushToast("error", lock.message); return;
      }
      if (m.includes("register")) {
        lock.visible = true; lock.message = "Cannot delete: players are still registered.";
        pushToast("error", lock.message); return;
      }
    }
    const finalMsg = rawMsg || "Delete failed. Please try again.";
    lock.visible = true; lock.message = finalMsg; pushToast("error", finalMsg);
  }
}
</script>

<style scoped>
/* layout */
.page { margin-top: 96px; padding: 24px; }
.page-header { display: flex; flex-direction: column; gap: 10px; margin-bottom: 16px; }
.head-row { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
.page-header h1 { font-size: 32px; margin: 0; color: #e8ecf7; font-weight: 800; }
.page-header p { opacity: .75; margin: 0; color: #c3cad9; }

/* CTA */
.btn.cta { background: #4478ff; color: #fff; border: 1px solid #4478ff; font-weight: 800; padding: 12px 18px; border-radius: 12px; }
.btn.cta:hover { filter: brightness(1.05); }

/* tabs (wider) */
.tabs { display: flex; gap: 8px; margin-top: 8px; }
.tab { padding: 8px 16px; border-radius: 10px; background: transparent; color: #d8deed; border: 1px solid #2f384a; font-weight: 600; }
.tab.wide { padding: 12px 26px; min-width: 120px; text-align: center; }
.tab.active { background: #fff; color: #0f1217; border-color: #ffffff; font-weight: 800; box-shadow: 0 2px 6px rgba(255,255,255,0.15); }
.tab:not(.active):hover { background: #f4f6fa; color: #0f1217; border-color: #f4f6fa; }

/* cards */
.card { border: 1px solid #293043; border-radius: 16px; background: #0f1217; color: #e9edf5; padding: 20px; }

/* grid */
.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-top: 8px; }
.col { display: flex; flex-direction: column; gap: 10px; }
.col-2 { grid-column: span 2; display: flex; gap: 10px; }

/* inputs */
.label { font-size: 13px; font-weight: 600; opacity: .85; color: #e2e6f2; }
.input, textarea.input, select.input { width: 100%; background: #151a22; color: #f0f4ff; border: 1px solid #384054; border-radius: 10px;
  padding: 10px 12px; outline: none; transition: border-color .2s ease, box-shadow .2s ease; }
.input:focus, textarea.input:focus, select.input:focus { border-color: #72aaff; box-shadow: 0 0 0 2px rgba(114,170,255,0.25); }
.input.invalid { border-color: #c94949; box-shadow: 0 0 0 2px rgba(201,73,73,0.25); }
.input::-webkit-calendar-picker-indicator { filter: invert(1); }

/* toolbar (search + filter + actions) */
.toolbar { display: flex; gap: 10px; align-items: center; margin-bottom: 10px; flex-wrap: wrap; }
.toolbar .input { flex: 1 1 520px; }
.filter-btn { display: inline-flex; align-items: center; gap: 8px; }
.ficon { width: 16px; height: 16px; }

/* filter chips (shown beneath toolbar when toggled) */
.filters.under-toolbar { display: flex; gap: 6px; margin: 6px 0 10px; }
.chip { padding: 8px 12px; border-radius: 999px; border: 1px solid #2f384a; background: transparent; color: #d8deed; font-weight: 700; }
.chip.active { background: #fff; color: #0f1217; border-color: #fff; }

.actions { display: flex; gap: 8px; }

/* inline banner */
.inline-banner { margin-bottom: 10px; padding: 10px 12px; border-radius: 10px; border: 1px solid #2b3343; background: #0f1217; font-weight: 700; }
.inline-banner.success { border-color: #3e7350; background: #112218; color: #b7ffd1; }
.inline-banner.error { border-color: #8a2a2a; background: #1a1010; color: #ffd6d6; }
.inline-banner.info { border-color: #3b5b9e; background: #0f1625; color: #d7e5ff; }
.inline-banner.danger { border-color: #8a2a2a; background: #1a1010; color: #ffd6d6; }

/* buttons */
.btn { padding: 10px 16px; border-radius: 10px; font-weight: 600; transition: all .2s ease; border: 1px solid transparent; cursor: pointer; }
.btn.primary { background: #ffffff; color: #0f1217; border-color: #ffffff; }
.btn.primary:hover { background: #f3f3f3; }
.btn.danger { background: #d44d4d; border-color: #d44d4d; color: #fff; }
.btn.danger:hover { background: #c04343; }
.btn.ghost { background: transparent; border: 1px solid #2f384a; color: #dfe5f4; }
.btn.ghost:hover { background: #182132; }
.btn.details { padding: 10px 18px; font-weight: 800; border-color: #2f384a; background: #182132; color: #e6ecff; }
.btn.details:hover { background: #203045; }
.btn:disabled { opacity: .6; cursor: not-allowed; }

/* table */
.table-wrap { border: 1px solid #1f2533; border-radius: 12px; overflow: hidden; }
.table { width: 100%; border-collapse: collapse; }
.table th, .table td {
  padding: 12px; text-align: left; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; color: #dfe5f4;
  vertical-align: middle; font-variant-numeric: tabular-nums;
}
.table thead { background: #161b24; }
.table tbody tr { border-top: 1px solid #1f2634; cursor: pointer; transition: background .15s ease; height: 48px; }
.table tbody tr:hover { background: #1a2030; }
.table tbody tr.selected { background: #212836; }
.name-cell .name { font-weight: 700; color: #e6ecff; }
.ta-center { text-align: center; }
.num { font-variant-numeric: tabular-nums; }

/* header location icon */
.loc-head { display: inline-flex; gap: 6px; align-items: center; }
.pin { width: 14px; height: 14px; opacity: .9; }

/* badges */
.state { padding: 4px 10px; border-radius: 999px; font-weight: 800; border: 1px solid transparent; }
.state.upcoming { color: #cde6ff; border-color: #2e4a74; background: #0d1726; }
.state.ongoing  { color: #c8ffd5; border-color: #2c5b38; background: #0f2015; }
.state.finished { color: #ffd6d6; border-color: #5b2c2c; background: #1f1111; }
.badge { padding: 2px 8px; border-radius: 999px; font-weight: 800; margin-left: 6px; }
.badge-full { background: #2a171b; color: #ffd6d6; border: 1px solid #5b2c2c; }

/* misc */
.hint { display: block; margin: 4px 0 8px; opacity: .7; color: #b5bfd5; }
.err { color: #ffd6d6; margin-top: -6px; }

/* toasts */
.toasts { position: fixed; right: 14px; bottom: 14px; display: flex; flex-direction: column; gap: 8px; z-index: 1000; }
.toast { padding: 10px 14px; border-radius: 10px; border: 1px solid #2b3343; background: #0f1217; color: #e9edf5; font-weight: 600;
  box-shadow: 0 4px 12px rgba(0,0,0,0.35); }
.toast.success { border-color: #3e7350; background: #112218; color: #b7ffd1; }
.toast.error   { border-color: #8a2a2a; background: #1a1010; color: #ffd6d6; }
.toast.info    { border-color: #3b5b9e; background: #0f1625; color: #d7e5ff; }
</style>
