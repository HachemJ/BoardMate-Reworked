<!-- src/views/AdminDashboardView.vue -->
<template>
  <div>
    <NavLandingSigned />

    <main class="page">
      <header class="page-header">
        <div>
          <h1>Admin Dashboard</h1>
          <p>Manage data across players, games, events, reviews, and requests.</p>
        </div>
      </header>

      <div class="tabs">
        <button
          v-for="t in TABS"
          :key="t"
          class="tab wide"
          :class="{ active: tab === t }"
          @click="setTab(t)"
        >
          {{ t }}
        </button>
      </div>

      <section class="card">
        <div class="toolbar">
          <div class="title">{{ tab }}</div>
          <button class="btn ghost" @click="loadTab" :disabled="loading">
            {{ loading ? "Refreshing..." : "Refresh" }}
          </button>
        </div>

        <div v-if="error" class="alert-banner" role="alert">
          <div class="alert-dot"></div>
          <span class="alert-text">{{ error }}</span>
        </div>

        <div v-if="confirming" class="confirm-panel" role="alert">
          <div class="confirm-text">
            Delete <strong>{{ confirming.label }}</strong>? This cannot be undone.
          </div>
          <div class="confirm-actions">
            <button class="btn ghost" @click="confirming = null">Cancel</button>
            <button class="btn danger" @click="doDelete" :disabled="confirming.blocked">
              Delete
            </button>
          </div>
          <div v-if="confirming.blocked" class="confirm-note">
            You can’t delete your own admin account.
          </div>
        </div>

        <div v-if="loading" class="loading">Loading...</div>

        <div v-else class="table-wrap">
          <!-- Players -->
          <table v-if="tab === 'Players'">
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Owner</th>
                <th>Admin</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="p in data" :key="p.playerID">
                <td>{{ p.playerID }}</td>
                <td>{{ p.name }}</td>
                <td>{{ p.email }}</td>
                <td>{{ p.isAOwner ? "Yes" : "No" }}</td>
                <td>{{ p.isAdmin ? "Yes" : "No" }}</td>
                <td><button class="btn danger" @click="confirmDelete('players', p.playerID, p.name)">Delete</button></td>
              </tr>
            </tbody>
          </table>

          <!-- Board Games -->
          <table v-else-if="tab === 'Board Games'">
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Min</th>
                <th>Max</th>
                <th>Description</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="g in data" :key="g.gameID">
                <td>{{ g.gameID }}</td>
                <td>{{ g.name }}</td>
                <td>{{ g.minPlayers }}</td>
                <td>{{ g.maxPlayers }}</td>
                <td class="truncate">{{ g.description || "—" }}</td>
                <td><button class="btn danger" @click="confirmDelete('boardgames', g.gameID, g.name)">Delete</button></td>
              </tr>
            </tbody>
          </table>

          <!-- Copies -->
          <table v-else-if="tab === 'Copies'">
            <thead>
              <tr>
                <th>ID</th>
                <th>Game</th>
                <th>Owner</th>
                <th>Spec</th>
                <th>Available</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="c in data" :key="c.boardGameCopyId">
                <td>{{ c.boardGameCopyId }}</td>
                <td>{{ c.boardGameName }}</td>
                <td>{{ c.playerName }}</td>
                <td class="truncate">{{ c.specification || "—" }}</td>
                <td>{{ c.isAvailable ? "Yes" : "No" }}</td>
                <td><button class="btn danger" @click="confirmDelete('boardgamecopies', c.boardGameCopyId, c.boardGameName)">Delete</button></td>
              </tr>
            </tbody>
          </table>

          <!-- Events -->
          <table v-else-if="tab === 'Events'">
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Date</th>
                <th>Start</th>
                <th>End</th>
                <th>Owner</th>
                <th>Game</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="e in data" :key="e.eventID">
                <td>{{ e.eventID }}</td>
                <td>{{ e.name }}</td>
                <td>{{ e.eventDate }}</td>
                <td>{{ e.startTime }}</td>
                <td>{{ e.endTime }}</td>
                <td>{{ e.ownerName }}</td>
                <td>{{ e.boardGameName }}</td>
                <td><button class="btn danger" @click="confirmDelete('events', e.eventID, e.name)">Delete</button></td>
              </tr>
            </tbody>
          </table>

          <!-- Reviews -->
          <table v-else-if="tab === 'Reviews'">
            <thead>
              <tr>
                <th>ID</th>
                <th>Game</th>
                <th>Author</th>
                <th>Rating</th>
                <th>Date</th>
                <th>Comment</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="r in data" :key="r.reviewID">
                <td>{{ r.reviewID }}</td>
                <td>{{ r.boardGameName }}</td>
                <td>{{ r.authorName }}</td>
                <td>{{ r.rating }}</td>
                <td>{{ r.commentDate }}</td>
                <td class="truncate">{{ r.comment || "—" }}</td>
                <td><button class="btn danger" @click="confirmDelete('reviews', r.reviewID, r.boardGameName)">Delete</button></td>
              </tr>
            </tbody>
          </table>

          <!-- Borrow Requests -->
          <table v-else-if="tab === 'Borrow Requests'">
            <thead>
              <tr>
                <th>ID</th>
                <th>Game</th>
                <th>Borrower</th>
                <th>Start</th>
                <th>End</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="b in data" :key="b.requestId">
                <td>{{ b.requestId }}</td>
                <td>{{ b.specificGameName }}</td>
                <td>{{ b.borrowerName }}</td>
                <td>{{ b.startOfLoan }}</td>
                <td>{{ b.endOfLoan }}</td>
                <td>{{ b.requestStatus }}</td>
                <td><button class="btn danger" @click="confirmDelete('borrowrequests', b.requestId, b.specificGameName)">Delete</button></td>
              </tr>
            </tbody>
          </table>

          <!-- Registrations -->
          <table v-else-if="tab === 'Registrations'">
            <thead>
              <tr>
                <th>Event ID</th>
                <th>Player ID</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="r in data" :key="r.eventID + '-' + r.playerID">
                <td>{{ r.eventID }}</td>
                <td>{{ r.playerID }}</td>
                <td><button class="btn danger" @click="confirmDelete('registrations', r.eventID + ':' + r.playerID, `Event ${r.eventID}` , r)">Delete</button></td>
              </tr>
            </tbody>
          </table>

          <div v-if="!data.length && !loading" class="empty">
            No records found.
          </div>
        </div>
      </section>
    </main>

    <!-- Inline confirm panel used instead of modal -->
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import NavLandingSigned from "@/components/NavLandingSigned.vue";
import { useAuthStore } from "@/stores/authStore";

const auth = useAuthStore();
const TABS = ["Players", "Board Games", "Copies", "Events", "Reviews", "Borrow Requests", "Registrations"];
const tab = ref("Players");
const data = ref([]);
const loading = ref(false);
const error = ref(null);
const confirming = ref(null);

const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_URL || "http://localhost:8080",
});

function adminHeaders() {
  return { headers: { "X-Player-Id": auth.user?.id ?? 0 } };
}

function setTab(t) {
  tab.value = t;
  loadTab();
}

async function loadTab() {
  loading.value = true;
  error.value = null;
  data.value = [];
  try {
    const url = {
      "Players": "/admin/players",
      "Board Games": "/admin/boardgames",
      "Copies": "/admin/boardgamecopies",
      "Events": "/admin/events",
      "Reviews": "/admin/reviews",
      "Borrow Requests": "/admin/borrowrequests",
      "Registrations": "/admin/registrations",
    }[tab.value];
    const res = await axiosClient.get(url, adminHeaders());
    data.value = Array.isArray(res.data) ? res.data : [];
  } catch (e) {
    error.value = "Failed to load admin data.";
    console.error(e);
  } finally {
    loading.value = false;
  }
}

function confirmDelete(type, id, label, extra) {
  const isSelfAdminDelete = type === "players" && auth.user?.id === id;
  confirming.value = { type, id, label, extra, blocked: isSelfAdminDelete };
}

async function doDelete() {
  if (!confirming.value || confirming.value.blocked) return;
  const { type, id, extra } = confirming.value;
  try {
    let url = `/admin/${type}/${id}`;
    if (type === "registrations" && extra) {
      url = `/admin/registrations/${extra.eventID}/${extra.playerID}`;
    }
    await axiosClient.delete(url, adminHeaders());
    confirming.value = null;
    await loadTab();
  } catch (e) {
    error.value = "Delete failed. Please try again.";
    console.error(e);
  }
}

onMounted(loadTab);
</script>

<style scoped>
.page{
  padding: 110px 24px 80px;
  display:flex;
  flex-direction:column;
  gap:18px;
}
.page-header h1{ margin:0; font-size:28px; color:#eef2f8; }
.page-header p{ margin:6px 0 0; color:#9aa3b3; }
.tabs{ display:flex; gap:10px; flex-wrap:wrap; justify-content:center; }
.tab{
  padding:8px 14px; border-radius:12px;
  background:#0f1217; color:#d6dbe7; border:1px solid #242a38;
  font-weight:800;
}
.tab.active{ background:#ffffff; color:#0e1116; border-color:#ffffff; }
.card{
  background:#11151b; border:1px solid #1f2533; border-radius:16px;
  padding:18px;
  box-shadow:0 12px 30px rgba(0,0,0,.35);
}
.toolbar{ display:flex; align-items:center; justify-content:space-between; margin-bottom:10px; }
.title{ font-weight:900; color:#e9edf5; font-size:16px; }
.btn{
  padding:6px 12px; border-radius:10px; font-weight:800; border:1px solid #2a3140;
  background:#0f1217; color:#d8deea;
}
.btn.ghost{ background:transparent; }
.btn.danger{
  background:#2a1316; color:#ffd8dd; border-color:#4a1f25;
}
.btn.danger:hover{ filter:brightness(1.08); }
.table-wrap{ overflow:auto; }
table{ width:100%; border-collapse:collapse; color:#dfe5ef; font-size:13px; }
th, td{ padding:10px 8px; border-bottom:1px solid #1f2533; text-align:left; }
th{ color:#aab2c3; font-weight:800; }
.truncate{ max-width:240px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
.empty{ padding:18px; color:#8f98a8; text-align:center; }
.loading{ padding:14px; color:#a6afc0; }
.alert-banner{
  display:flex; align-items:center; gap:8px;
  background:#1b2029; color:#e9edf5;
  border:1px solid #2a3140; border-radius:12px;
  padding:8px 10px; margin-bottom:10px;
}
.alert-dot{ width:8px; height:8px; border-radius:50%; background:#ff6b6b; }
.confirm-panel{
  display:grid; grid-template-columns: 1fr auto; align-items:center;
  gap:10px 14px; padding:12px 14px; margin-bottom:12px;
  background:linear-gradient(180deg, #161c26, #121821);
  border:1px solid #2b3548; border-radius:14px;
  color:#e9edf5;
  box-shadow:0 10px 24px rgba(0,0,0,.35);
}
.confirm-text{ color:#e9edf5; font-weight:600; }
.confirm-actions{ display:flex; gap:10px; }
.confirm-note{ grid-column:1 / -1; color:#9aa3b3; font-size:12px; }
.btn.danger:disabled{
  opacity:.55; cursor:not-allowed; filter:none;
}
</style>
