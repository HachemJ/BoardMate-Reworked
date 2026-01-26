<script setup>
import NavLandingSigned from "@/components/NavLandingSigned.vue";
import axios from "axios";
import { computed, onMounted, ref, watchEffect } from "vue";
import { useAuthStore } from "@/stores/authStore.js";
import { useRouter } from "vue-router";

const axiosClient = axios.create({ baseURL: import.meta.env.VITE_BACKEND_URL || "http://localhost:8080" });

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
const notice = ref(null);
const openMenuId = ref(null);
const showDeleteModal = ref(false);
const pendingDeleteCopy = ref(null);
const isDeleting = ref(false);
const lockedCopyIds = ref(new Set());
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
    const requests = [
      axiosClient.get("/boardgames"),
      axiosClient.get(`/boardgamecopies/byplayer/${authStore.user.id}`),
    ];
    if (authStore.user?.isAOwner) {
      requests.push(
        axiosClient.get(`/borrowrequests?ownerId=${authStore.user.id}`)
      );
    }

    const responses = await Promise.all(requests);
    const [gamesRes, copiesRes, ownerRequestsRes] = responses;

    boardGames.value = gamesRes.data ?? [];
    myBoardGameCopies.value = copiesRes.data ?? [];

    if (authStore.user?.isAOwner) {
      const locked = new Set();
      const ownerRequests = ownerRequestsRes?.data ?? [];
      ownerRequests.forEach((request) => {
        if (
          request?.requestStatus === "Accepted" ||
          request?.requestStatus === "InProgress"
        ) {
          locked.add(request.specificGameCopyId);
        }
      });
      lockedCopyIds.value = locked;
    } else {
      lockedCopyIds.value = new Set();
    }
  } catch (error) {
    console.error("Fetch board games/copies failed", error);
    if (error?.response?.status === 401) {
      notice.value = {
        type: "error",
        message: "Session expired. Please sign in again.",
      };
      router.push({ name: "signin" });
      return;
    }
    notice.value = {
      type: "error",
      message: "Failed to fetch board games or your copies.",
    };
  }
}

function isCopyLocked(id) {
  return lockedCopyIds.value.has(id);
}

onMounted(fetchBoardGames);

watchEffect(() => {
  if (!visibleTabs.value.includes(selectedTab.value)) {
    selectedTab.value = visibleTabs.value[0];
  }
});

function requestDelete(copy) {
  if (isCopyLocked(copy.boardGameCopyId)) {
    notice.value = {
      type: "error",
      message: "This copy is currently borrowed and cannot be deleted.",
    };
    return;
  }
  openMenuId.value = null;
  pendingDeleteCopy.value = copy;
  showDeleteModal.value = true;
}

function closeDeleteModal() {
  showDeleteModal.value = false;
  pendingDeleteCopy.value = null;
}

async function confirmDeleteCopy() {
  if (!pendingDeleteCopy.value || isDeleting.value) return;
  isDeleting.value = true;
  try {
    await axiosClient.delete(
      `/boardgamecopies/${pendingDeleteCopy.value.boardGameCopyId}`,
      {
        headers: { "X-Player-Id": authStore.user?.id },
      }
    );
    notice.value = {
      type: "success",
      message: "Board game copy deleted.",
    };
    await fetchBoardGames();
  } catch (error) {
    console.error(error);
    if (error?.response?.status === 403) {
      notice.value = {
        type: "error",
        message: "Only owners can manage board game copies.",
      };
      return;
    }
    const errors = error.response?.data?.errors;
    notice.value = {
      type: "error",
      message: errors ? errors.join("\n") : "Delete failed.",
    };
  } finally {
    isDeleting.value = false;
    closeDeleteModal();
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
            <div class="toolbar" v-if="authStore.user?.isAOwner">
              <router-link :to="{ name: 'addBoardGameCopy' }">
                <button class="btn primary">Add Copy</button>
              </router-link>
              <router-link :to="{ name: 'ownerUpdateBoardGameCopy' }">
                <button class="btn ghost">Update Copy</button>
              </router-link>
            </div>
          </div>

          <div v-if="notice" class="notice" :class="notice.type">
            <span>{{ notice.message }}</span>
            <button class="icon-btn" @click="notice = null">âœ•</button>
          </div>

          <div class="copies-grid">
            <div
              v-for="copy in myBoardGameCopies"
              :key="copy.boardGameCopyId"
              class="copy-card"
            >
              <div class="copy-top">
                <div>
                  <h3 class="copy-title">{{ copy.boardGameName }}</h3>
                  <p class="copy-spec">{{ copy.specification || "No copy notes yet." }}</p>
                </div>
                <div class="copy-actions">
                  <span class="badge" :class="copy.isAvailable ? 'ok' : 'warn'">
                    {{ copy.isAvailable ? "Available" : "Unavailable" }}
                  </span>
                  <button
                    v-if="authStore.user?.isAOwner"
                    class="icon-btn menu-btn"
                    @click="openMenuId = openMenuId === copy.boardGameCopyId ? null : copy.boardGameCopyId"
                    aria-label="Copy actions"
                  >
                    â‹¯
                  </button>
                  <div
                    v-if="openMenuId === copy.boardGameCopyId"
                    class="menu"
                  >
                    <button
                      class="menu-item"
                      :disabled="isCopyLocked(copy.boardGameCopyId)"
                      @click="requestDelete(copy)"
                    >
                      {{
                        isCopyLocked(copy.boardGameCopyId)
                          ? "Cannot delete while borrowed"
                          : "Delete copy"
                      }}
                    </button>
                    <p class="menu-hint">
                      {{
                        isCopyLocked(copy.boardGameCopyId)
                          ? "Return the copy before deleting."
                          : "Deletes related borrow requests."
                      }}
                    </p>
                  </div>
                </div>
              </div>
              <p class="copy-hint">Visible to borrowers when available.</p>
            </div>

            <div v-if="myBoardGameCopies.length === 0" class="empty-card">
              <div class="empty-icon">ðŸŽ²</div>
              <h3>No copies yet</h3>
              <p>Add your first board game copy to share it with borrowers.</p>
              <router-link
                v-if="authStore.user?.isAOwner"
                :to="{ name: 'addBoardGameCopy' }"
              >
                <button class="btn primary">Add your first board game copy</button>
              </router-link>
            </div>
          </div>

          <div v-if="showDeleteModal" class="modal-backdrop">
            <div class="modal-card" role="dialog" aria-modal="true">
              <h3>Delete this copy?</h3>
              <p>This will delete related borrow requests.</p>
              <div class="modal-actions">
                <button class="btn ghost" @click="closeDeleteModal">Cancel</button>
                <button class="btn danger" :disabled="isDeleting" @click="confirmDeleteCopy">
                  {{ isDeleting ? "Deleting..." : "Delete copy" }}
                </button>
              </div>
            </div>
          </div>
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
.actions { display: flex; gap: 10px; flex-wrap: wrap; justify-content: flex-end; }

.tabs { display: flex; gap: 8px; margin-top: 8px; }
.tab { padding: 8px 16px; border-radius: 10px; background: transparent; color: #d8deed; border: 1px solid #2f384a; font-weight: 600; }
.tab.wide { padding: 12px 26px; min-width: 120px; text-align: center; }
.tab.active { background: #fff; color: #0f1217; border-color: #ffffff; font-weight: 800; box-shadow: 0 2px 6px rgba(255,255,255,0.15); }
.tab:not(.active):hover { background: #f4f6fa; color: #0f1217; border-color: #f4f6fa; }

.card {
  border: 1px solid rgba(47, 56, 74, 0.45);
  border-radius: 18px;
  background: linear-gradient(180deg, rgba(18,22,30,0.98), rgba(15,18,23,0.98));
  color: #e9edf5;
  padding: 20px;
}
.section-head { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 10px; }
.subtle { color: #aab3c3; margin: 0; }
.toolbar { display: flex; gap: 10px; flex-wrap: wrap; justify-content: flex-end; }

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
.copies-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
}
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
  position: relative;
  border-radius: 18px;
  padding: 16px;
  background: linear-gradient(160deg, rgba(24,30,41,0.9), rgba(15,18,23,0.98));
  border: 1px solid rgba(89, 104, 133, 0.25);
  box-shadow: 0 18px 30px rgba(0,0,0,.35);
  transition: transform .18s ease, box-shadow .18s ease, border-color .18s ease;
}
.copy-card:hover {
  transform: translateY(-4px);
  border-color: rgba(114,170,255,0.45);
  box-shadow: 0 20px 34px rgba(0,0,0,.45);
}
.copy-top { display: flex; justify-content: space-between; gap: 12px; align-items: flex-start; }
.copy-title { margin: 0; font-size: 20px; font-weight: 800; color: #f5f7ff; }
.copy-spec { color: #aab3c3; margin: 6px 0 0; font-size: 13px; }
.copy-hint { margin: 12px 0 0; font-size: 12px; color: #8590a6; }
.copy-actions { display: flex; align-items: center; gap: 8px; position: relative; }
.badge {
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
  border: 1px solid transparent;
}
.badge.ok { background: #112218; color: #b7ffd1; border-color: #3e7350; }
.badge.warn { background: #1b1f27; color: #b7bdc9; border-color: #3a4256; }
.menu-btn { font-size: 18px; }
.menu {
  position: absolute;
  top: 28px;
  right: 0;
  background: #121722;
  border: 1px solid #2a3347;
  border-radius: 12px;
  padding: 8px;
  min-width: 160px;
  box-shadow: 0 12px 24px rgba(0,0,0,.35);
  z-index: 10;
}
.menu-item {
  width: 100%;
  background: transparent;
  border: none;
  color: #f2f5ff;
  text-align: left;
  padding: 8px 6px;
  border-radius: 8px;
  cursor: pointer;
}
.menu-item:hover { background: #1a2232; }
.menu-item:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}
.menu-item:disabled:hover { background: transparent; }
.menu-hint { margin: 6px 6px 0; font-size: 11px; color: #9aa2b2; }

.notice {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 12px;
  border: 1px solid transparent;
  margin-bottom: 12px;
  font-size: 13px;
}
.notice.success { background: #111f17; border-color: #2f5f43; color: #c6f7d7; }
.notice.error { background: #241618; border-color: #6c3a3f; color: #ffd0d4; }

.empty { color: #9aa2b2; text-align: center; padding: 16px; grid-column: 1 / -1; }
.empty-card {
  grid-column: 1 / -1;
  text-align: center;
  padding: 32px;
  border-radius: 18px;
  border: 1px dashed #2b3346;
  background: rgba(15,18,23,0.6);
}
.empty-card h3 { margin: 12px 0 6px; color: #f5f7ff; }
.empty-card p { margin: 0 0 16px; color: #9aa2b2; }
.empty-icon { font-size: 28px; }

.icon-btn {
  background: transparent;
  border: 1px solid #2f384a;
  color: #dfe5f4;
  border-radius: 10px;
  padding: 4px 8px;
  cursor: pointer;
  line-height: 1;
}
.icon-btn:hover { background: #182132; }

.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(5,7,12,0.7);
  display: grid;
  place-items: center;
  z-index: 50;
}
.modal-card {
  width: min(420px, 90vw);
  background: #101520;
  border: 1px solid #2f384a;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 20px 40px rgba(0,0,0,.45);
  color: #e9edf5;
}
.modal-card h3 { margin: 0 0 8px; }
.modal-card p { margin: 0 0 16px; color: #aab3c3; }
.modal-actions { display: flex; justify-content: flex-end; gap: 10px; }

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




