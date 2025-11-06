<script setup>
import NavLandingSigned from "@/components/NavLandingSigned.vue";
import axios from "axios";
import { computed, onMounted, ref } from "vue";
import { useAuthStore } from "@/stores/authStore.js";
import { useRouter } from "vue-router";

/* --- Axios configured to avoid silent 403/CORS auth issues --- */
const authStore = useAuthStore();
const router = useRouter();

const axiosClient = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true, // cookie/CSRF sessions
});
axiosClient.interceptors.request.use((cfg) => {
  const token = authStore.token || authStore.user?.token;
  if (token) cfg.headers.Authorization = `Bearer ${token}`;
  return cfg;
});

/* --- Tabs + data --- */
const tabs = ["View All Board Games", "My Board Game Copies"];
const selectedTab = ref(tabs[0]);

const searchQuery = ref("");
const boardGames = ref([]);
const myBoardGameCopies = ref([]);

/* --- route guard helper so a missing route can't crash the page --- */
const hasRoute = (name) => router.getRoutes().some((r) => r.name === name);

/* --- filters --- */
const filteredGames = computed(() =>
    boardGames.value.filter((g) =>
        (g.name || "").toLowerCase().includes(searchQuery.value.toLowerCase())
    )
);

/* --- load data --- */
async function fetchBoardGamesAndCopies() {
  try {
    const [gamesRes, copiesRes] = await Promise.all([
      axiosClient.get("/boardgames"),
      axiosClient.get(`/boardgamecopies/byplayer/${authStore.user.id}`),
    ]);
    boardGames.value = gamesRes.data ?? [];
    myBoardGameCopies.value = copiesRes.data ?? [];
  } catch (error) {
    const status = error?.response?.status;
    const body = error?.response?.data;
    console.error("Fetch failed", status, body);
    // we still render the page; show empty state instead of crashing
    boardGames.value = boardGames.value ?? [];
    myBoardGameCopies.value = myBoardGameCopies.value ?? [];
  }
}

onMounted(fetchBoardGamesAndCopies);

/* --- actions --- */
async function deleteBoardGameCopy(id) {
  if (!confirm("Delete this board game copy? This will remove related borrow requests.")) return;
  try {
    await axiosClient.delete(`/boardgamecopies/${id}`);
    await fetchBoardGamesAndCopies();
    alert("Board game copy deleted.");
  } catch (error) {
    const msg =
        error?.response?.data?.errors?.join("\n") ||
        error?.response?.data?.message ||
        "Delete failed.";
    alert(msg);
  }
}
</script>

<template>
  <div>
    <NavLandingSigned />

    <div class="container-fluid page">
      <div class="row">
        <!-- Left Sidebar: Tabs -->
        <div class="col-md-3">
          <ul class="nav flex-column nav-pills">
            <li v-for="(tab, i) in tabs" :key="i" class="nav-item">
              <a
                  href="#"
                  class="nav-link"
                  :class="{ active: selectedTab === tab }"
                  @click.prevent="selectedTab = tab"
              >
                {{ tab }}
              </a>
            </li>
          </ul>
        </div>

        <!-- Right Content Area -->
        <div class="col-md-9">
          <!-- View All -->
          <div v-if="selectedTab === 'View All Board Games'" class="card p-4 shadow-sm">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h2 class="mb-0">Search and Browse Board Games</h2>

              <!-- Player page should NOT link to owner-only screens.
                   If you later want an Add button for owners, guard it with hasRoute('addBoardGame'). -->
              <template v-if="authStore.user?.isAOwner && hasRoute('addBoardGame')">
                <router-link :to="{ name: 'addBoardGame' }">
                  <button class="btn btn-info">Add Board Game</button>
                </router-link>
              </template>
            </div>

            <div class="mb-3">
              <input
                  v-model="searchQuery"
                  type="text"
                  class="form-control"
                  placeholder="Search by name or description..."
                  aria-label="Search board games"
              />
            </div>

            <div>
              <small>Click a name to view details</small>
              <table class="table mt-2">
                <thead>
                <tr>
                  <th>Game Name</th>
                  <th>Min</th>
                  <th>Max</th>
                  <th>Description</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="game in filteredGames" :key="game.name">
                  <td>
                    <template v-if="hasRoute('playerBoardGameDetail')">
                      <router-link
                          :to="{ name: 'playerBoardGameDetail', params: { gamename: game.name } }"
                      >
                        {{ game.name }}
                      </router-link>
                    </template>
                    <template v-else>
                      {{ game.name }}
                    </template>
                  </td>
                  <td>{{ game.minPlayers }}</td>
                  <td>{{ game.maxPlayers }}</td>
                  <td class="desc">{{ game.description }}</td>
                </tr>
                <tr v-if="filteredGames.length === 0">
                  <td colspan="4" class="text-center text-muted py-4">
                    No board games match your search.
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- My Copies -->
          <div v-else class="card p-4 shadow-sm">
            <div class="d-flex justify-content-between align-items-center">
              <h2 class="mb-0">My Collection</h2>
              <template v-if="authStore.user?.isAOwner && hasRoute('addBoardGameCopy')">
                <router-link :to="{ name: 'addBoardGameCopy' }">
                  <button class="btn btn-info">Add Board Game Copy</button>
                </router-link>
              </template>
            </div>

            <table class="table table-responsive-ms mt-3">
              <thead>
              <tr>
                <th>Game Name</th>
                <th>Specification</th>
                <th class="text-end">Actions</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="game in myBoardGameCopies" :key="game.boardGameCopyId">
                <td>{{ game.boardGameName }}</td>
                <td class="specification-cell">{{ game.specification }}</td>
                <td class="text-end">
                  <button class="btn btn-danger" @click="deleteBoardGameCopy(game.boardGameCopyId)">
                    Delete
                  </button>
                </td>
              </tr>
              <tr v-if="myBoardGameCopies.length === 0">
                <td colspan="3" class="text-center text-muted py-4">No copies yet.</td>
              </tr>
              </tbody>
            </table>

            <small v-if="myBoardGameCopies.length > 0" class="d-flex justify-content-center">
              Notice: deleting a board game copy will delete all borrow requests related to it.
            </small>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page { margin-top: 96px; }
.nav-link { cursor: pointer; margin-bottom: 6px; padding: 10px; text-align: center; color: #fff; border: 1px solid #2b3343; }
.nav-link.active { background: #198754; border-color: #198754; }
.card { border: 1px solid #293043; border-radius: 1rem; background-color: #0f1217; color: #e9edf5; }
.table { table-layout: fixed; width: 100%; }
.table th, .table td { vertical-align: middle; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; padding: 12px; }
.table th:nth-child(1), .table td:nth-child(1) { width: 30%; }
.table th:nth-child(2), .table td:nth-child(2) { width: 10%; }
.table th:nth-child(3), .table td:nth-child(3) { width: 10%; }
.table th:nth-child(4), .table td:nth-child(4) { width: 50%; }
.table td a { color: #8fb4ff; text-decoration: none; }
.table td a:hover { text-decoration: underline; }
.specification-cell { white-space: normal; max-width: 320px; }
.desc { white-space: normal; }
</style>
