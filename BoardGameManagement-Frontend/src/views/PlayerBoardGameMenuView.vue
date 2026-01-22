<script setup>
import NavLandingSigned from "@/components/NavLandingSigned.vue";
import axios from "axios";
import { computed, onMounted, ref, watchEffect } from "vue";
import { useAuthStore } from "@/stores/authStore.js";
import { useRouter } from "vue-router";

const axiosClient = axios.create({ baseURL: "http://localhost:8080" });

const authStore = useAuthStore();
const router = useRouter();
const tabs = ["View All Board Games", "My Board Game Copies"];
const visibleTabs = computed(() =>
    authStore.user?.isAOwner ? tabs : [tabs[0]]
);
const selectedTab = ref(visibleTabs.value[0]);

const searchQuery = ref("");
const boardGames = ref([]);
const myBoardGameCopies = ref([]);
const playerFilters = ["Any", "2+", "4+", "6+"];
const playerFilter = ref("Any");
const cardMode = computed(() => {
  const count = filteredGames.value.length;
  if (count <= 4) return "large";
  return "compact";
});

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

watchEffect(() => {
  if (!visibleTabs.value.includes(selectedTab.value)) {
    selectedTab.value = visibleTabs.value[0];
  }
});

async function deleteBoardGameCopy(id) {
  if (!confirm("Delete this board game copy? This will remove related borrow requests.")) return;
  try {
    await axiosClient.delete(`/boardgamecopies/${id}`, {
      headers: { "X-Player-Id": authStore.user?.id },
    });
    alert("Board game copy deleted!");
    await fetchBoardGames();
  } catch (error) {
    console.error(error);
    if (error?.response?.status === 403) {
      alert("Only owners can manage board game copies.");
      return;
    }
    const errors = error.response?.data?.errors;
    alert(errors ? errors.join("\n") : "Delete failed.");
  }
}
</script>

<template>
  <div>
    <NavLandingSigned />

    <main class="page">
      <header class="page-header">
        <div class="head-row">
          <div>
            <h1>Board Games</h1>
            <p>Browse the catalog or manage the copies you own.</p>
          </div>
          <div class="actions" v-if="authStore.user?.isAOwner">
            <router-link :to="{ name: 'playerAddBoardGame' }">
              <button class="btn primary">Add Board Game</button>
            </router-link>
            <router-link :to="{ name: 'ownerUpdateBoardGame' }">
              <button class="btn ghost">Update Board Game</button>
            </router-link>
          </div>
        </div>

        <div class="tabs">
          <button
              v-for="t in visibleTabs"
              :key="t"
              class="tab wide"
              :class="{ active: selectedTab === t }"
              @click="selectedTab = t"
          >
            {{ t === 'View All Board Games' ? 'Catalog' : 'My Copies' }}
          </button>
        </div>
      </header>

      <section class="card">
        <div v-if="selectedTab === 'View All Board Games'">
          <div class="section-head">
            <div>
              <h2>Board Game Catalog</h2>
              <p class="subtle">Open a title to see details and available copies.</p>
            </div>
          </div>

          <div class="catalog-controls">
            <div class="search-wrap">
              <input
                  v-model="searchQuery"
                  type="text"
                  class="input"
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

          <div class="grid" :class="`grid-${cardMode}`">
            <router-link
                v-for="game in filteredGames"
                :key="game.name"
                :to="{ name: 'playerBoardGameDetail', params: { gamename: game.name } }"
                class="game-card"
                :class="`card-${cardMode}`"
            >
              <div class="cover placeholder">
                <span>{{ (game.name || "?").slice(0, 1) }}</span>
              </div>
              <div class="card-body">
                <h3 class="title">{{ game.name }}</h3>
                <div class="meta-text">{{ game.minPlayers }}-{{ game.maxPlayers }} players</div>
                <p v-if="game.description" class="desc">{{ game.description }}</p>
              </div>
            </router-link>
            <div v-if="filteredGames.length === 0" class="empty">
              No games match your search.
            </div>
          </div>
        </div>

        <div v-else>
          <div class="section-head">
            <div>
              <h2>My Board Game Copies</h2>
              <p class="subtle">Copies you own (inventory and availability).</p>
            </div>
            <div class="actions" v-if="authStore.user?.isAOwner">
              <router-link :to="{ name: 'addBoardGameCopy' }">
                <button class="btn primary">Add Copy</button>
              </router-link>
              <router-link :to="{ name: 'ownerUpdateBoardGameCopy' }">
                <button class="btn ghost">Update Copy</button>
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
                <button
                    v-if="authStore.user?.isAOwner"
                    class="btn danger"
                    @click="deleteBoardGameCopy(copy.boardGameCopyId)"
                >
                  Delete
                </button>
              </div>
            </div>

            <div v-if="myBoardGameCopies.length === 0" class="empty">
              You don't own any copies yet.
            </div>
          </div>

          <small v-if="myBoardGameCopies.length > 0" class="subtle notice">
            Notice: deleting a board game copy will delete all borrow requests related to it.
          </small>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
.page { margin-top: 96px; padding: 24px; }
.page-header { display: flex; flex-direction: column; gap: 10px; margin-bottom: 16px; }
.head-row { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
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

.catalog-controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin: 10px 0 18px;
  flex-wrap: wrap;
}
.search-wrap { flex: 1 1 320px; }
.input { width: 100%; background: #151a22; color: #f0f4ff; border: 1px solid #384054; border-radius: 10px;
  padding: 10px 12px; outline: none; transition: border-color .2s ease, box-shadow .2s ease; }
.input:focus { border-color: #72aaff; box-shadow: 0 0 0 2px rgba(114,170,255,0.25); }

.filter-chips { display: flex; gap: 8px; flex-wrap: wrap; }
.chip {
  padding: 8px 12px;
  border-radius: 999px;
  border: 1px solid #2f384a;
  background: transparent;
  color: #d8deed;
  font-weight: 700;
}
.chip.active { background: #fff; color: #0f1217; border-color: #fff; }

.grid {
  display: grid;
  gap: 14px;
  justify-content: center;
}
.grid.copies { grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); }
.grid-large { grid-template-columns: repeat(3, minmax(0, 320px)); }
.grid-medium { grid-template-columns: repeat(4, minmax(0, 260px)); }
.grid-compact { grid-template-columns: repeat(5, minmax(0, 220px)); }

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
.title { margin: 0 0 6px; font-size: 20px; font-weight: 900; color: #ffffff; }
.meta-text { color: #f3f6ff; font-size: 13px; font-weight: 700; }
.desc {
  margin: 6px 0 0;
  color: #b6becc;
  font-size: 12px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.card-large .cover { height: 190px; }
.card-large .title { font-size: 22px; }
.card-large .card-body { padding: 14px; }

.card-medium .cover { height: 160px; }
.card-medium .title { font-size: 20px; }

.card-compact .cover { height: 120px; }
.card-compact .title { font-size: 18px; }

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
.notice { display: block; text-align: center; margin-top: 8px; }

.btn { padding: 10px 16px; border-radius: 10px; font-weight: 600; transition: all .2s ease; border: 1px solid transparent; cursor: pointer; }
.btn.primary { background: #ffffff; color: #0f1217; border-color: #ffffff; }
.btn.primary:hover { background: #f3f3f3; }
.btn.ghost { background: transparent; border: 1px solid #2f384a; color: #dfe5f4; }
.btn.ghost:hover { background: #182132; }
.btn.danger { background: #d44d4d; border-color: #d44d4d; color: #fff; }
.btn.danger:hover { background: #c04343; }

@media (max-width: 900px) {
  .head-row { flex-direction: column; align-items: flex-start; }
  .section-head { flex-direction: column; align-items: flex-start; }
  .grid-large { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .grid-medium { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .grid-compact { grid-template-columns: repeat(2, minmax(0, 1fr)); }
}

@media (max-width: 640px) {
  .grid-large { grid-template-columns: 1fr; }
  .grid-medium { grid-template-columns: 1fr; }
  .grid-compact { grid-template-columns: 1fr; }
}
</style>




