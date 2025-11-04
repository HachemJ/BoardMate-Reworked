<script setup>
import NavLandingSigned from "@/components/NavLandingSigned.vue";
import axios from "axios";
import { computed, onMounted, ref } from "vue";
import { useAuthStore } from "@/stores/authStore.js";

const axiosClient = axios.create({ baseURL: "http://localhost:8080" });

const authStore = useAuthStore();
const tabs = ["View All Board Games", "My Board Game Copies"];
const selectedTab = ref(tabs[0]);

const searchQuery = ref("");
const boardGames = ref([]);
const myBoardGameCopies = ref([]);

const filteredGames = computed(() =>
    boardGames.value.filter((g) =>
        g.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
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
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h2 class="mb-0">Search and Browse Board Games</h2>
              <div class="d-flex gap-2">
                <router-link :to="{ name: 'playerAddBoardGame' }">
                  <button class="btn btn-info">Add Board Game</button>
                </router-link>
                <router-link :to="{ name: 'ownerUpdateBoardGame' }">
                  <button class="btn btn-info">Update Board Game</button>
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

            <div>
              <small>Click a board game name to view more details</small>
              <table class="table">
                <thead>
                <tr>
                  <th>Game Name</th>
                  <th>Min Players</th>
                  <th>Max Players</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="game in filteredGames" :key="game.name">
                  <td>
                    <router-link
                        :to="{ name: 'playerBoardGameDetail', params: { gamename: game.name } }"
                    >
                      {{ game.name }}
                    </router-link>
                  </td>
                  <td>{{ game.minPlayers }}</td>
                  <td>{{ game.maxPlayers }}</td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- My Copies -->
          <div v-else class="card p-4 shadow-sm">
            <div class="d-flex justify-content-between align-items-center">
              <h2 class="mb-0">My Collection</h2>
              <div class="d-flex gap-2">
                <router-link :to="{ name: 'addBoardGameCopy' }">
                  <button class="btn btn-info">Add Board Game Copy</button>
                </router-link>
                <router-link :to="{ name: 'ownerUpdateBoardGameCopy' }">
                  <button class="btn btn-info">Update Board Game Copy</button>
                </router-link>
              </div>
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
.table th:nth-child(1), .table td:nth-child(1) { width: 50%; }
.table th:nth-child(2), .table td:nth-child(2),
.table th:nth-child(3), .table td:nth-child(3) { width: 25%; }
.table td a { color: #8fb4ff; text-decoration: none; }
.table td a:hover { text-decoration: underline; }
.specification-cell { white-space: normal; max-width: 320px; }
</style>
