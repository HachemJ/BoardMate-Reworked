<!-- src/views/Events.vue -->
<script setup>
import { ref, reactive, computed, watch } from "vue";
import axios from "axios";
import NavLandingSigned from "@/components/NavLandingSigned.vue";
import { useAuthStore } from "@/stores/authStore";

const axiosClient = axios.create({ baseURL: "http://localhost:8080" });
const auth = useAuthStore();

/* ------------------------------------------------
   UI state: tabs, toasts
------------------------------------------------- */
const tabs = computed(() => {
  // owners see all 3 tabs; players only see Browse
  return auth.user?.isAOwner
      ? ["Browse", "Create", "Manage"]
      : ["Browse"];
});
const selectedTab = ref("Browse");

const toast = reactive({
  show: false,
  type: "success", // success | danger | warning | info
  text: "",
});
function showToast(type, text, ms = 3000) {
  toast.type = type;
  toast.text = text;
  toast.show = true;
  window.clearTimeout(showToast.timer);
  showToast.timer = window.setTimeout(() => (toast.show = false), ms);
}

/* ------------------------------------------------
   Data models
------------------------------------------------- */
const events = ref([]);
const myEvents = ref([]);
const boardGames = ref([]);

const selectedEventId = ref("");

const eventForm = reactive({
  // shared between Create / Manage forms (we always reset when switching)
  name: "",
  description: "",
  maxSpot: "",
  date: "",
  startTime: "",
  endTime: "",
  location: "",
  boardGameId: "",
});

function resetForm() {
  eventForm.name = "";
  eventForm.description = "";
  eventForm.maxSpot = "";
  eventForm.date = "";
  eventForm.startTime = "";
  eventForm.endTime = "";
  eventForm.location = "";
  eventForm.boardGameId = "";
}

const todayISO = new Date().toISOString().slice(0, 10);
const minDate = todayISO;

const startTimeOrDefault = computed(() =>
    /^\d{2}:\d{2}$/.test(eventForm.startTime) ? eventForm.startTime : "00:00"
);

watch(
    () => eventForm.startTime,
    () => {
      if (eventForm.endTime && eventForm.endTime < eventForm.startTime) {
        eventForm.endTime = "";
      }
    }
);

/* ------------------------------------------------
   Registration chips (per event)
------------------------------------------------- */
const regByEventId = ref({}); // id -> "Registered" | "Not Registered" | "Error"

/* ------------------------------------------------
   Fetchers
------------------------------------------------- */
async function fetchEvents() {
  try {
    const { data } = await axiosClient.get("/events");
    events.value = data ?? [];
    // pre-compute registration chip for each row
    for (const ev of events.value) await computeRegChip(ev.eventID);
  } catch (e) {
    console.error(e);
    showToast("danger", "Failed to fetch events.");
  }
}
async function fetchMyEvents() {
  if (!auth.user) return;
  if (!auth.user.isAOwner) {
    myEvents.value = [];
    return;
  }
  try {
    const { data } = await axiosClient.get(`/events/owner/${auth.user.id}`);
    myEvents.value = data ?? [];
  } catch (e) {
    console.error(e);
    showToast("danger", "Failed to fetch your events.");
  }
}
async function fetchBoardGames() {
  try {
    const { data } = await axiosClient.get("/boardgames");
    boardGames.value = data ?? [];
  } catch (e) {
    console.error(e);
    showToast("danger", "Failed to fetch board games.");
  }
}

async function computeRegChip(eventId) {
  try {
    const { data } = await axiosClient.get(
        `/registrations/${auth.user.id}/${eventId}`
    );
    regByEventId.value[eventId] = data ? "Registered" : "Not Registered";
  } catch (e) {
    if (e?.response?.status === 404) {
      regByEventId.value[eventId] = "Not Registered";
    } else {
      regByEventId.value[eventId] = "Error";
    }
  }
}

async function init() {
  await Promise.all([fetchEvents(), fetchBoardGames(), fetchMyEvents()]);
}
init();

/* ------------------------------------------------
   Helpers
------------------------------------------------- */
function selectRow(id) {
  selectedEventId.value = id;

  // Hydrate form when switching to Manage
  const ev =
      events.value.find((e) => e.eventID === id) ||
      myEvents.value.find((e) => e.eventID === id);
  if (ev) {
    eventForm.name = ev.name;
    eventForm.description = ev.description;
    eventForm.maxSpot = ev.maxSpot;
    eventForm.date = ev.eventDate;
    eventForm.startTime = (ev.startTime || "").slice(0, 5);
    eventForm.endTime = (ev.endTime || "").slice(0, 5);
    eventForm.location = ev.location;
    eventForm.boardGameId = ev.boardGameId || "";
  }
}

watch(selectedTab, (t) => {
  resetForm();
  if (t === "Manage") selectedEventId.value = "";
});

/* ------------------------------------------------
   Actions: Create / Update / Delete
------------------------------------------------- */
async function createEvent() {
  if (!auth.user?.isAOwner) return showToast("warning", "Owners only.");
  try {
    const dto = {
      name: eventForm.name,
      description: eventForm.description,
      maxSpot: String(eventForm.maxSpot),
      eventDate: eventForm.date,
      startTime: `${eventForm.startTime}:00`,
      endTime: `${eventForm.endTime}:00`,
      location: eventForm.location,
      ownerId: Number(auth.user.id),
      boardGameId: Number(eventForm.boardGameId),
    };
    await axiosClient.post("/events", dto);
    showToast("success", "Event created.");
    resetForm();
    await fetchEvents();
    await fetchMyEvents();
    selectedTab.value = "Browse";
  } catch (e) {
    console.error(e);
    showToast("danger", "Failed to create event.");
  }
}

async function updateEvent() {
  if (!auth.user?.isAOwner) return showToast("warning", "Owners only.");
  if (!selectedEventId.value) return showToast("info", "Pick an event first.");

  try {
    const dto = {
      name: eventForm.name,
      description: eventForm.description,
      maxSpot: String(eventForm.maxSpot),
      eventDate: eventForm.date,
      startTime: `${eventForm.startTime}:00`,
      endTime: `${eventForm.endTime}:00`,
      location: eventForm.location,
      ownerId: Number(auth.user.id),
      boardGameId: Number(eventForm.boardGameId),
    };
    await axiosClient.put(`/events/${selectedEventId.value}`, dto);
    showToast("success", "Event updated.");
    await fetchEvents();
    await fetchMyEvents();
  } catch (e) {
    console.error(e);
    showToast("danger", "Failed to update event.");
  }
}

async function deleteEvent() {
  if (!auth.user?.isAOwner) return showToast("warning", "Owners only.");
  if (!selectedEventId.value) return showToast("info", "Pick an event first.");

  try {
    await axiosClient.delete(`/events/${selectedEventId.value}`);
    showToast("success", "Event deleted.");
    selectedEventId.value = "";
    resetForm();
    await fetchEvents();
    await fetchMyEvents();
  } catch (e) {
    console.error(e);
    showToast("danger", "Failed to delete event.");
  }
}

/* ------------------------------------------------
   Actions: Register / Cancel
------------------------------------------------- */
async function registerForEvent() {
  if (!selectedEventId.value) return showToast("info", "Pick an event first.");
  if (regByEventId.value[selectedEventId.value] === "Registered")
    return showToast("info", "Already registered.");

  try {
    const payload = {
      playerID: Number(auth.user.id),
      eventID: Number(selectedEventId.value),
    };
    await axiosClient.post("/registrations", payload);
    showToast("success", "Registration successful.");
    await computeRegChip(selectedEventId.value);
  } catch (e) {
    console.error(e);
    showToast("danger", "Registration failed.");
  }
}

async function cancelRegistration() {
  if (!selectedEventId.value) return showToast("info", "Pick an event first.");
  if (regByEventId.value[selectedEventId.value] !== "Registered")
    return showToast("info", "You’re not registered.");

  try {
    await axiosClient.delete(
        `/registrations/${auth.user.id}/${selectedEventId.value}`
    );
    showToast("success", "Registration cancelled.");
    await computeRegChip(selectedEventId.value);
  } catch (e) {
    console.error(e);
    showToast("danger", "Could not cancel registration.");
  }
}
</script>

<template>
  <section class="events-hero">
    <NavLandingSigned />

    <!-- Background layers -->
    <div class="layer bg-base"></div>
    <div class="layer bg-gradient"></div>
    <div class="layer bg-noise"></div>
    <div class="layer bg-vignette"></div>

    <div class="content">
      <!-- Toast -->
      <transition name="fade">
        <div v-if="toast.show" class="toast" :data-variant="toast.type">
          {{ toast.text }}
        </div>
      </transition>

      <!-- Head -->
      <header class="page-head">
        <h1>Events</h1>
        <p class="muted">
          Discover public sessions, register or cancel, and (if you’re an owner)
          create and manage your own.
        </p>
      </header>

      <!-- Tabs -->
      <nav class="tabs">
        <button
            v-for="t in tabs"
            :key="t"
            class="tab"
            :class="{ 'tab--active': selectedTab === t }"
            @click="selectedTab = t"
        >
          {{ t }}
        </button>
      </nav>

      <!-- Browse -->
      <section v-if="selectedTab === 'Browse'" class="panel">
        <div class="panel-head">
          <h3>Browse Available Events</h3>
        </div>

        <div class="table-wrap">
          <table class="pretty">
            <thead>
            <tr>
              <th>Event</th>
              <th>Date</th>
              <th>Time</th>
              <th>Location</th>
              <th>Status</th>
            </tr>
            </thead>
            <tbody>
            <tr
                v-for="ev in events"
                :key="ev.eventID"
                :class="{ selected: ev.eventID === selectedEventId }"
                @click="selectRow(ev.eventID)"
            >
              <td class="truncate">
                <router-link
                    class="link"
                    :to="{ name: 'eventDetail', params: { eventname: ev.name } }"
                    @click.stop
                >{{ ev.name }}</router-link
                >
              </td>
              <td>{{ ev.eventDate }}</td>
              <td>{{ (ev.startTime || '').slice(0,5) }}–{{ (ev.endTime || '').slice(0,5) }}</td>
              <td class="truncate">{{ ev.location }}</td>
              <td>
                  <span
                      class="chip"
                      :data-variant="regByEventId[ev.eventID] === 'Registered' ? 'success' : (regByEventId[ev.eventID] === 'Error' ? 'danger' : 'default')"
                  >
                    {{ regByEventId[ev.eventID] || '…' }}
                  </span>
              </td>
            </tr>
            <tr v-if="!events.length">
              <td colspan="5" class="empty">No events yet.</td>
            </tr>
            </tbody>
          </table>
        </div>

        <div class="actions">
          <button
              class="btn btn-primary"
              :disabled="!selectedEventId"
              @click="registerForEvent"
          >
            Register
          </button>
          <button
              class="btn btn-ghost"
              :disabled="!selectedEventId"
              @click="cancelRegistration"
          >
            Cancel Registration
          </button>
        </div>
      </section>

      <!-- Create (owner) -->
      <section v-if="selectedTab === 'Create'" class="panel">
        <div class="panel-head">
          <h3>Create a New Event</h3>
        </div>

        <div class="grid-2">
          <label class="field">
            <span class="label">Event name</span>
            <input v-model="eventForm.name" class="input" type="text" />
          </label>

          <label class="field">
            <span class="label">Board game</span>
            <select v-model="eventForm.boardGameId" class="input">
              <option disabled value="">Select a game</option>
              <option
                  v-for="g in boardGames"
                  :key="g.gameID"
                  :value="g.gameID"
              >
                {{ g.name }}
              </option>
            </select>
          </label>

          <label class="field">
            <span class="label">Max spots</span>
            <input v-model="eventForm.maxSpot" class="input" type="number" />
          </label>

          <label class="field">
            <span class="label">Date</span>
            <input
                v-model="eventForm.date"
                class="input"
                type="date"
                :min="minDate"
                @keydown.prevent
            />
          </label>

          <label class="field">
            <span class="label">Start time</span>
            <input v-model="eventForm.startTime" class="input" type="time" />
          </label>

          <label class="field">
            <span class="label">End time</span>
            <input
                v-model="eventForm.endTime"
                class="input"
                type="time"
                :min="startTimeOrDefault"
            />
          </label>

          <label class="field col-span-2">
            <span class="label">Location</span>
            <input v-model="eventForm.location" class="input" type="text" />
          </label>

          <label class="field col-span-2">
            <span class="label">Description</span>
            <textarea v-model="eventForm.description" class="input textarea" />
          </label>
        </div>

        <div class="actions">
          <button class="btn btn-primary" @click="createEvent">Create</button>
          <button class="btn btn-ghost" @click="resetForm">Reset</button>
        </div>

        <p v-if="!auth.user?.isAOwner" class="hint">
          Only owners can create events.
        </p>
      </section>

      <!-- Manage (owner) -->
      <section v-if="selectedTab === 'Manage'" class="panel">
        <div class="panel-head">
          <h3>Update / Delete My Events</h3>
        </div>

        <div class="pick">
          <label class="field">
            <span class="label">Select one of your events</span>
            <select
                class="input"
                v-model="selectedEventId"
                @change="selectRow(selectedEventId)"
            >
              <option disabled value="">Choose…</option>
              <option
                  v-for="e in myEvents"
                  :key="e.eventID"
                  :value="e.eventID"
              >
                {{ e.name }}
              </option>
            </select>
          </label>
        </div>

        <div class="grid-2">
          <label class="field">
            <span class="label">Event name</span>
            <input v-model="eventForm.name" class="input" type="text" />
          </label>

          <label class="field">
            <span class="label">Board game</span>
            <select v-model="eventForm.boardGameId" class="input">
              <option disabled value="">Select a game</option>
              <option
                  v-for="g in boardGames"
                  :key="g.gameID"
                  :value="g.gameID"
              >
                {{ g.name }}
              </option>
            </select>
          </label>

          <label class="field">
            <span class="label">Max spots</span>
            <input v-model="eventForm.maxSpot" class="input" type="number" />
          </label>

          <label class="field">
            <span class="label">Date</span>
            <input
                v-model="eventForm.date"
                class="input"
                type="date"
                :min="minDate"
                @keydown.prevent
            />
          </label>

          <label class="field">
            <span class="label">Start time</span>
            <input v-model="eventForm.startTime" class="input" type="time" />
          </label>

          <label class="field">
            <span class="label">End time</span>
            <input
                v-model="eventForm.endTime"
                class="input"
                type="time"
                :min="startTimeOrDefault"
            />
          </label>

          <label class="field col-span-2">
            <span class="label">Location</span>
            <input v-model="eventForm.location" class="input" type="text" />
          </label>

          <label class="field col-span-2">
            <span class="label">Description</span>
            <textarea v-model="eventForm.description" class="input textarea" />
          </label>
        </div>

        <div class="actions">
          <button
              class="btn btn-primary"
              :disabled="!selectedEventId"
              @click="updateEvent"
          >
            Update
          </button>
          <button
              class="btn btn-ghost danger"
              :disabled="!selectedEventId"
              @click="deleteEvent"
          >
            Delete
          </button>
        </div>

        <p v-if="!auth.user?.isAOwner" class="hint">
          Only owners can manage events.
        </p>
      </section>
    </div>
  </section>
</template>

<style scoped>
/* ===== Canvas (uniform style) ===== */
.events-hero { position: relative; min-height: 100vh; background:#14171d; }
.layer { position:absolute; inset:0; }
.bg-base{ background:#14171d; }
.bg-gradient{
  background:
      radial-gradient(1100px 750px at 18% 18%, rgba(245,247,250,.28) 0%, rgba(245,247,250,.10) 36%, rgba(245,247,250,0) 65%),
      linear-gradient(180deg, #14171d 0%, #1b2029 100%);
  animation: float 16s ease-in-out infinite alternate;
}
@keyframes float{ 0%{transform:translateY(0) translateX(0) scale(1)} 100%{transform:translateY(-10px) translateX(6px) scale(1.01)}}
.bg-noise{
  background-image:url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='120' height='120'%3E%3Cfilter id='n'%3E%3CfeTurbulence baseFrequency='0.75' numOctaves='2' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='120' height='120' filter='url(%23n)' opacity='0.02'/%3E%3C/svg%3E");
  background-size:120px 120px; mix-blend-mode:overlay;
}
.bg-vignette{ background: radial-gradient(85% 70% at 50% 60%, transparent 0%, rgba(0,0,0,.35) 70%, rgba(0,0,0,.7) 100%); }

.content{ position:relative; z-index:2; max-width:1100px; margin:0 auto; padding:96px 20px 72px; color:#e9edf5; }

/* ===== Toast ===== */
.toast{
  position: sticky; top: 16px; margin-bottom: 14px;
  border-radius: 12px; padding: 12px 14px; font-weight: 800; letter-spacing: .2px;
  background:#17202b; border:1px solid #273345; box-shadow: 0 8px 20px rgba(0,0,0,.25);
}
.toast[data-variant="success"]{ background:#132317; border-color:#24472d; color:#dcffe6; }
.toast[data-variant="danger"]{ background:#2a1517; border-color:#5a2a2f; color:#ffdbe1; }
.toast[data-variant="warning"]{ background:#2a2314; border-color:#5a4b2a; color:#fff1cc; }
.fade-enter-active,.fade-leave-active{ transition: opacity .2s ease }
.fade-enter-from,.fade-leave-to{ opacity:0 }

/* ===== Head & tabs ===== */
.page-head h1{ margin:0 0 6px; font-size:30px; font-weight:900; }
.muted{ color:#aeb5c3; }

.tabs{ display:flex; gap:10px; margin:18px 0 12px; flex-wrap:wrap; }
.tab{
  padding:.6rem .95rem; border-radius:999px; font-weight:900; font-size:13px; letter-spacing:.2px;
  background:#151821; border:1px solid #222a39; color:#dbe1ed; cursor:pointer;
  transition:transform .15s ease, background .2s ease, border-color .2s ease;
}
.tab--active{ background:#1f2533; border-color:#2d3647; color:#fff; transform:translateY(-1px); }

/* ===== Panels ===== */
.panel{
  background:#0f1217; border:1px solid #1f2533; border-radius:16px;
  padding:16px 16px 14px; box-shadow:0 10px 30px rgba(0,0,0,.35);
}
.panel + .panel{ margin-top:14px; }
.panel-head{ display:flex; align-items:center; justify-content:space-between; margin-bottom:12px; }
.panel-head h3{ margin:0; font-size:20px; font-weight:900; }

/* ===== Table ===== */
.table-wrap{ overflow:auto; border-radius:12px; border:1px solid #1f2533; }
.pretty{ width:100%; border-collapse:separate; border-spacing:0; }
.pretty thead th{
  text-align:left; font-weight:900; font-size:12px; letter-spacing:.3px; color:#c2cad6;
  background:#121720; position:sticky; top:0; z-index:1; padding:10px 12px;
  border-bottom:1px solid #1f2533;
}
.pretty tbody td{ padding:12px; border-bottom:1px solid #1a2130; color:#e7ebf3; }
.pretty tr:hover{ background:#10151d; }
.pretty tr.selected{ outline:2px solid #2b3854; background:#121a26; }
.pretty .truncate{ max-width:280px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }

.link{ color:#9fc1ff; text-decoration:none; }
.link:hover{ text-decoration:underline; }

/* Chips */
.chip{
  display:inline-block; padding:6px 10px; border-radius:999px; font-size:12px; font-weight:800;
  background:#1a2130; border:1px solid #2a3242; color:#e9edf5;
}
.chip[data-variant="success"]{ background:#142319; border-color:#225232; color:#dbffe6; }
.chip[data-variant="danger"]{ background:#2b1618; border-color:#5a2a2f; color:#ffd6db; }
.empty{ text-align:center; color:#9aa2b2; }

/* ===== Forms ===== */
.grid-2{ display:grid; grid-template-columns:repeat(2,minmax(0,1fr)); gap:14px; }
.col-span-2{ grid-column:span 2; }
.field{ display:flex; flex-direction:column; gap:6px; }
.label{ font-size:12px; font-weight:800; color:#c8cfdb; }
.input{
  background:#0c0f13; color:#e7ebf3; border:1px solid #202636; border-radius:12px;
  padding:.75rem .9rem; outline:none; transition:border-color .2s, box-shadow .2s, background .2s;
}
.input:focus{ border-color:#3a4256; box-shadow:0 0 0 3px rgba(70,100,255,.12); background:#0d1116; }
.textarea{ min-height:110px; resize:vertical; }

/* pick line */
.pick{ margin-bottom:12px; }

/* Buttons */
.actions{ display:flex; gap:10px; margin-top:12px; }
.btn{
  padding:.75rem 1rem; border-radius:12px; font-weight:900; letter-spacing:.25px; border:1px solid transparent;
  transition: transform .18s ease, box-shadow .18s ease, filter .18s ease, background .18s ease, color .18s ease, border-color .18s ease;
}
.btn-primary{ background:#fff; color:#0b0d10; border:1px solid rgba(0,0,0,.08); box-shadow:0 6px 16px rgba(0,0,0,.14); }
.btn-primary:hover{ transform:translateY(-2px); filter:brightness(1.03); }
.btn-ghost{ background:#171b22; color:#e9edf5; border:1px solid #2a3242; }
.btn-ghost:hover{ transform:translateY(-2px); filter:brightness(1.02); }
.btn-ghost.danger{ color:#ffd6db; border-color:#5a2a2f; background:#2b1618; }

.hint{ margin-top:10px; color:#aeb5c3; font-size:13px; }

@media (max-width: 900px){
  .grid-2{ grid-template-columns:1fr; }
}
</style>
