<script setup>
import NavLandingSigned from "@/components/NavLandingSigned.vue";
import axios from "axios";
import { computed, onMounted, ref } from "vue";
import { useAuthStore } from "@/stores/authStore.js";
import { useRouter } from "vue-router";

const axiosClient = axios.create({ baseURL: "http://localhost:8080" });

const authStore = useAuthStore();
const router = useRouter();
const tabs = ["View All Board Games", "My Board Game Copies"];
const selectedTab = ref(tabs[0]);

const searchQuery = ref("");
const boardGames = ref([]);
const myBoardGameCopies = ref([]);
const playerFilters = ["Any", "2+", "4+", "6+"];
const playerFilter = ref("Any");

const filteredGames = computed(() =>
    boardGames.value.filter((g) => {
      const name = (g.name || "").toLowerCase();
      const queryOk = name.includes(searchQuery.value.toLowerCase());
      if (!queryOk) return false;
      if (playerFilter.value === "Any") return true;
      const required = Number(playerFilter.value.replace("+", "")) || 0;
      const maxPlayers = Number(g.maxPlayers || 0);
      return maxPlayers >= required;
    })
);

async function fetchBoardGames() {
  try {
    const [gamesRes, copiesRes] = await Promise.all([
      axiosClient.get("/boardgames"),
      axiosClient.get(`/boardgamecopies/byplayer/${authStore.user.id}`),
    ]);
    boardGames.value = gamesRes.data ?? [];
    myBoardGameCopies.value = copiesRes.data ?? [];
  } catch (error) {
    console.error("Fetch board games/copies failed", error);
    if (error?.response?.status === 401) {
      alert("Session expired. Please sign in again.");
      router.push({ name: "signin" });
      return;
    }
    alert("Failed to fetch board games or your copies.");
  }
}

onMounted(fetchBoardGames);

async function deleteBoardGameCopy(id) {
  if (!confirm("Delete this board game copy? This will remove related borrow requests.")) return;
  try {
    await axiosClient.delete(`/boardgamecopies/${id}`);
    alert("Board game copy deleted!");
    await fetchBoardGames();
  } catch (error) {
    console.error(error);
    const errors = error.response?.data?.errors;
    alert(errors ? errors.join("\n") : "Delete failed.");
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
            <div class="catalog-head">
              <div>
                <h2 class="mb-1">Board Game Catalog</h2>
                <p class="subtle">Browse all titles and open details for each game.</p>
              </div>
              <div class="d-flex gap-2">
                <router-link :to="{ name: 'playerAddBoardGame' }">
                  <button class="btn btn-info">Add Board Game</button>
                </router-link>
                <router-link :to="{ name: 'ownerUpdateBoardGame' }">
                  <button class="btn btn-info">Update Board Game</button>
                </router-link>
              </div>
            </div>

            <div class="catalog-controls">
              <div class="search-wrap">
                <input
                    v-model="searchQuery"
                    type="text"
                    class="form-control search-input"
                    placeholder="Search by name..."
                    aria-label="Search board games"
                />
              </div>
              <div class="filter-chips">
                <button
                    v-for="f in playerFilters"
                    :key="f"
                    class="chip"
                    :class="{ active: playerFilter === f }"
                    @click="playerFilter = f"
                >
                  {{ f }}
                </button>
              </div>
            </div>

            <div class="grid">
              <router-link
                  v-for="game in filteredGames"
                  :key="game.name"
                  :to="{ name: 'playerBoardGameDetail', params: { gamename: game.name } }"
                  class="game-card"
              >
                <div class="cover placeholder">
                  <span>{{ (game.name || "?").slice(0, 1) }}</span>
                </div>
                <div class="card-body">
                  <h3 class="title">{{ game.name }}</h3>
                  <div class="meta">
                    <span class="meta-pill">{{ game.minPlayers }} min</span>
                    <span class="meta-pill">{{ game.maxPlayers }} max</span>
                  </div>
                </div>
              </router-link>
              <div v-if="filteredGames.length === 0" class="empty">
                No games match your search.
              </div>
            </div>
          </div>

          <!-- My Copies -->
          <div v-else class="card p-4 shadow-sm">
            <div class="catalog-head">
              <div>
                <h2 class="mb-1">My Board Game Copies</h2>
                <p class="subtle">Copies you own (inventory and availability).</p>
              </div>
              <div class="d-flex gap-2">
                <router-link :to="{ name: 'addBoardGameCopy' }">
                  <button class="btn btn-info">Add Copy</button>
                </router-link>
                <router-link :to="{ name: 'ownerUpdateBoardGameCopy' }">
                  <button class="btn btn-info">Update Copy</button>
                </router-link>
              </div>
            </div>

            <div class="grid copies">
              <div v-for="copy in myBoardGameCopies" :key="copy.boardGameCopyId" class="copy-card">
                <div class="copy-head">
                  <div>
                    <h3 class="title">{{ copy.boardGameName }}</h3>
                    <p class="spec">{{ copy.specification }}</p>
                  </div>
                  <span class="badge" :class="copy.isAvailable ? 'ok' : 'warn'">
                    {{ copy.isAvailable ? "Available" : "Unavailable" }}
                  </span>
                </div>
                <div class="actions">
                  <button class="btn btn-danger" @click="deleteBoardGameCopy(copy.boardGameCopyId)">
                    Delete
                  </button>
                </div>
              </div>

              <div v-if="myBoardGameCopies.length === 0" class="empty">
                You don't own any copies yet.
              </div>
            </div>

            <small v-if="myBoardGameCopies.length > 0" class="d-flex justify-content-center subtle">
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

.catalog-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}
.subtle { color: #aab3c3; margin: 0; }

.catalog-controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin: 10px 0 18px;
  flex-wrap: wrap;
}
.search-wrap { flex: 1 1 320px; }
.search-input { background: #10151d; color: #e9edf5; border: 1px solid #2b3343; }
.search-input::placeholder { color: #98a4b8; }

.filter-chips { display: flex; gap: 8px; flex-wrap: wrap; }
.chip {
  padding: 6px 12px;
  border-radius: 999px;
  border: 1px solid #2b3343;
  background: transparent;
  color: #dfe5f4;
  font-weight: 700;
}
.chip.active { background: #ffffff; color: #0f1217; border-color: #ffffff; }

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 14px;
}
.grid.copies { grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); }

.game-card {
  display: flex;
  flex-direction: column;
  text-decoration: none;
  border-radius: 16px;
  overflow: hidden;
  background: #0f1217;
  border: 1px solid #1f2533;
  box-shadow: 0 10px 24px rgba(0,0,0,.35);
  transition: transform .18s ease, box-shadow .18s ease, border-color .18s ease;
}
.game-card:hover {
  transform: translateY(-4px) scale(1.01);
  border-color: #3a4256;
  box-shadow: 0 16px 32px rgba(0,0,0,.45);
}
.cover {
  height: 140px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #151a22, #0f1217);
  border-bottom: 1px solid #1f2533;
}
.cover.placeholder span {
  font-size: 36px;
  font-weight: 900;
  color: #e6ecff;
}
.card-body { padding: 12px; }
.title { margin: 0 0 8px; font-size: 16px; font-weight: 900; color: #e9edf5; }
.meta { display: flex; gap: 8px; flex-wrap: wrap; }
.meta-pill {
  padding: 4px 8px;
  border-radius: 999px;
  border: 1px solid #2a3242;
  color: #cbd5e6;
  font-size: 12px;
  font-weight: 700;
}

.copy-card {
  border-radius: 16px;
  padding: 14px;
  background: #0f1217;
  border: 1px solid #1f2533;
  box-shadow: 0 10px 24px rgba(0,0,0,.35);
}
.copy-head { display: flex; justify-content: space-between; gap: 12px; }
.spec { color: #aab3c3; margin: 6px 0 0; font-size: 13px; }
.badge {
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
  border: 1px solid transparent;
}
.badge.ok { background: #112218; color: #b7ffd1; border-color: #3e7350; }
.badge.warn { background: #24180b; color: #ffe2b8; border-color: #6a4a23; }
.actions { display: flex; justify-content: flex-end; margin-top: 10px; }

.empty { color: #9aa2b2; text-align: center; padding: 16px; grid-column: 1 / -1; }

@media (max-width: 900px) {
  .catalog-head { flex-direction: column; align-items: flex-start; }
}
</style>
