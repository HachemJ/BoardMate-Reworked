<script setup>
import NavLandingSigned from "@/components/NavLandingSigned.vue";
import { computed, onMounted, reactive, ref, watch } from "vue";
import axios from "axios";
import { useAuthStore } from "@/stores/authStore";
import { useRouter } from "vue-router";

const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_URL || "http://localhost:8080",
});

const boardGames = ref([]);
const router = useRouter();
const authStore = useAuthStore();
const boardGameData = reactive({
  name: "",
  newName: "",
  minPlayers: "",
  maxPlayers: "",
  description: "",
});

const notice = ref(null);
const isSubmitting = ref(false);

const selectedGame = computed(() =>
  boardGames.value.find((game) => game.name === boardGameData.name)
);

const editingLabel = computed(() =>
  selectedGame.value ? `Currently editing: ${selectedGame.value.name}` : ""
);

const descriptionCount = computed(() => boardGameData.description.length);

const validation = computed(() => {
  if (!boardGameData.name) return "Select a board game to update.";
  if (!boardGameData.newName.trim()) return "Name cannot be empty.";
  const min = Number(boardGameData.minPlayers);
  const max = Number(boardGameData.maxPlayers);
  if (!Number.isFinite(min) || !Number.isFinite(max)) {
    return "Min and max players are required.";
  }
  if (min <= 0 || max <= 0) return "Player counts must be positive.";
  if (min > max) return "Minimum players cannot exceed maximum players.";
  return "";
});

const isValid = computed(() => !validation.value);

const previewData = computed(() => ({
  title: boardGameData.newName || selectedGame.value?.name || "Board game title",
  minPlayers: boardGameData.minPlayers || selectedGame.value?.minPlayers || "-",
  maxPlayers: boardGameData.maxPlayers || selectedGame.value?.maxPlayers || "-",
  description:
    boardGameData.description ||
    selectedGame.value?.description ||
    "Add a short description to preview this game card.",
}));

async function getBoardGames() {
  try {
    const response = await axiosClient.get("/boardgames");
    boardGames.value = response.data ?? [];
  } catch (error) {
    console.error(error);
    notice.value = {
      type: "error",
      message: "Failed to load board games.",
    };
  }
}

async function fetchBoardGameID(boardGameName) {
  try {
    const response = await axiosClient.get("/boardgames");
    const game = response.data.find((game) => game.name === boardGameName);
    if (game) {
      return game.gameID;
    }
    return null;
  } catch (error) {
    console.error("Error fetching game ID:", error);
    return null;
  }
}

async function updateBG() {
  if (!isValid.value || isSubmitting.value) return;

  const newBoardGame = {
    name: boardGameData.newName,
    minPlayers: Number(boardGameData.minPlayers),
    maxPlayers: Number(boardGameData.maxPlayers),
    description: boardGameData.description,
  };

  const gameId = await fetchBoardGameID(boardGameData.name);
  if (!gameId) {
    notice.value = {
      type: "error",
      message: "Unable to find the selected board game.",
    };
    return;
  }

  try {
    isSubmitting.value = true;
    await axiosClient.put(`/boardgames/${gameId}`, newBoardGame, {
      headers: { "X-Player-Id": authStore.user?.id },
    });

    notice.value = {
      type: "success",
      message: "Board game updated successfully.",
    };

    boardGames.value.push(newBoardGame);
    Object.keys(boardGameData).forEach((key) => (boardGameData[key] = ""));

    if (useAuthStore().user.isAOwner) {
      await router.push("/pages/ownerboardgame");
    } else {
      await router.push("/pages/playerboardgame");
    }
  } catch (e) {
    console.error(e);
    if (e?.response?.status === 403) {
      notice.value = {
        type: "error",
        message: "Only owners can update board games.",
      };
      return;
    }
    const errors = e.response?.data?.errors;
    notice.value = {
      type: "error",
      message: errors ? errors.join("\n") : "Update failed.",
    };
  } finally {
    isSubmitting.value = false;
  }
}

function resetForm() {
  if (selectedGame.value) {
    boardGameData.newName = selectedGame.value.name;
    boardGameData.minPlayers = selectedGame.value.minPlayers;
    boardGameData.maxPlayers = selectedGame.value.maxPlayers;
    boardGameData.description = selectedGame.value.description;
  }
}

watch(
  () => boardGameData.name,
  () => {
    notice.value = null;
    if (selectedGame.value) {
      resetForm();
    }
  }
);

onMounted(getBoardGames);
</script>

<template>
  <div>
    <NavLandingSigned />

    <main class="page">
      <header class="page-header">
        <div class="head-row">
          <div>
            <h1>Update Board Game</h1>
            <p>Refresh your game details to keep the catalog accurate.</p>
          </div>
        </div>
        <span v-if="editingLabel" class="chip">{{ editingLabel }}</span>
      </header>

      <section class="card">
        <div class="layout">
          <form class="form" @submit.prevent="updateBG">
            <div v-if="notice" class="notice" :class="notice.type">
              <span>{{ notice.message }}</span>
              <button class="icon-btn" @click="notice = null" type="button">âœ•</button>
            </div>

            <div class="form-section">
              <div class="section-title">
                <h2>Select Game</h2>
                <p>Pick the board game you want to update.</p>
              </div>
              <label for="boardGameSelect">Board game</label>
              <select
                id="boardGameSelect"
                class="input"
                v-model="boardGameData.name"
                required
                @focus="getBoardGames"
              >
                <option value="" disabled>Select board game...</option>
                <option v-for="game in boardGames" :key="game.gameID" :value="game.name">
                  {{ game.name }}
                </option>
              </select>
            </div>

            <div class="form-section">
              <div class="section-title">
                <h2>Details</h2>
                <p>Update the visible information for this game.</p>
              </div>
              <label for="name">Game name</label>
              <input
                id="name"
                type="text"
                class="input"
                v-model="boardGameData.newName"
                placeholder="Enter game name"
                required
              />
              <label for="description">Description</label>
              <textarea
                id="description"
                maxlength="255"
                class="input textarea"
                v-model="boardGameData.description"
                placeholder="Tell players what makes this game special."
                required
              />
              <small
                class="count"
                :class="{ warn: descriptionCount >= 255 }"
              >
                {{ descriptionCount }} / 255 characters
              </small>
            </div>

            <div class="form-section">
              <div class="section-title">
                <h2>Player Count</h2>
                <p>Set the minimum and maximum player range.</p>
              </div>
              <div class="field-row">
                <div>
                  <label for="minPlayers">Min players</label>
                  <input
                    id="minPlayers"
                    type="number"
                    class="input"
                    v-model="boardGameData.minPlayers"
                    placeholder="2"
                    required
                  />
                </div>
                <div>
                  <label for="maxPlayers">Max players</label>
                  <input
                    id="maxPlayers"
                    type="number"
                    class="input"
                    v-model="boardGameData.maxPlayers"
                    placeholder="6"
                    required
                  />
                </div>
              </div>
            </div>

            <div class="validation" v-if="validation">
              {{ validation }}
            </div>

            <div class="action-row">
              <button class="btn ghost" type="button" @click="resetForm">
                Reset
              </button>
              <button
                class="btn primary"
                type="submit"
                :disabled="!isValid || isSubmitting"
              >
                {{ isSubmitting ? "Updating..." : "Update Board Game" }}
              </button>
            </div>
          </form>

          <aside class="preview">
            <div class="preview-card">
              <div class="preview-hero">
                <div class="preview-initial">
                  {{ previewData.title.slice(0, 1) }}
                </div>
                <div class="preview-meta">
                  <span class="pill">{{ previewData.minPlayers }}-{{ previewData.maxPlayers }} players</span>
                </div>
              </div>
              <div class="preview-body">
                <h3>{{ previewData.title }}</h3>
                <p class="preview-desc">{{ previewData.description }}</p>
              </div>
              <div v-if="!selectedGame" class="preview-empty">
                Select a game to see a live preview.
              </div>
            </div>
          </aside>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
.page { margin-top: 96px; padding: 24px; }
.page-header { display: flex; flex-direction: column; gap: 10px; margin-bottom: 18px; }
.head-row { display: flex; align-items: center; justify-content: space-between; gap: 16px; }
.page-header h1 { font-size: 34px; margin: 0; color: #e8ecf7; font-weight: 800; }
.page-header p { opacity: .75; margin: 0; color: #c3cad9; }
.chip {
  align-self: flex-start;
  background: #1a2232;
  border: 1px solid #2f384a;
  color: #dfe5f4;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.card {
  border: 1px solid rgba(47, 56, 74, 0.45);
  border-radius: 18px;
  background: linear-gradient(180deg, rgba(18,22,30,0.98), rgba(15,18,23,0.98));
  color: #e9edf5;
  padding: 22px;
}

.layout {
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(280px, 1fr);
  gap: 22px;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-section {
  padding: 16px;
  border-radius: 16px;
  border: 1px solid #1f2533;
  background: #0f1217;
}

.section-title h2 { margin: 0 0 4px; font-size: 18px; }
.section-title p { margin: 0 0 12px; color: #9aa2b2; font-size: 13px; }

label { display: block; margin: 12px 0 6px; color: #cbd3e6; font-weight: 600; }

.input {
  width: 100%;
  background: #151a22;
  color: #f0f4ff;
  border: 1px solid #384054;
  border-radius: 12px;
  padding: 10px 12px;
  outline: none;
  transition: border-color .2s ease, box-shadow .2s ease;
}
.input:focus {
  border-color: #72aaff;
  box-shadow: 0 0 0 2px rgba(114,170,255,0.25);
}
.textarea { min-height: 120px; resize: vertical; }

.count { display: block; margin-top: 6px; color: #8f98aa; font-size: 12px; }
.count.warn { color: #ffb3b3; }

.field-row { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; }

.validation {
  padding: 10px 12px;
  border-radius: 12px;
  background: #241618;
  border: 1px solid #6c3a3f;
  color: #ffd0d4;
  font-size: 13px;
}

.notice {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 12px;
  border: 1px solid transparent;
  font-size: 13px;
}
.notice.success { background: #111f17; border-color: #2f5f43; color: #c6f7d7; }
.notice.error { background: #241618; border-color: #6c3a3f; color: #ffd0d4; }

.action-row { display: flex; gap: 12px; justify-content: flex-end; }

.btn { padding: 10px 16px; border-radius: 10px; font-weight: 600; transition: all .2s ease; border: 1px solid transparent; cursor: pointer; }
.btn.primary { background: #ffffff; color: #0f1217; border-color: #ffffff; }
.btn.primary:hover { background: #f3f3f3; }
.btn.primary:disabled { opacity: 0.6; cursor: not-allowed; }
.btn.ghost { background: transparent; border: 1px solid #2f384a; color: #dfe5f4; }
.btn.ghost:hover { background: #182132; }

.icon-btn {
  background: transparent;
  border: 1px solid #2f384a;
  color: #dfe5f4;
  border-radius: 10px;
  padding: 4px 8px;
  cursor: pointer;
  line-height: 1;
}

.preview { position: relative; }
.preview-card {
  border-radius: 18px;
  border: 1px solid #1f2533;
  background: #0f1217;
  box-shadow: 0 18px 30px rgba(0,0,0,.35);
  overflow: hidden;
}
.preview-hero {
  height: 160px;
  background: linear-gradient(135deg, #151a22, #0f1217);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
}
.preview-initial {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  font-size: 26px;
  font-weight: 800;
  color: #e6ecff;
  background: rgba(255,255,255,0.08);
}
.preview-meta { display: flex; gap: 8px; }
.pill {
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255,255,255,0.1);
  color: #e6ecff;
  font-size: 12px;
  font-weight: 700;
  border: 1px solid rgba(255,255,255,0.2);
}
.preview-body { padding: 16px; }
.preview-body h3 { margin: 0 0 8px; font-size: 20px; }
.preview-desc {
  margin: 0;
  color: #b6becc;
  font-size: 13px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.preview-empty {
  border-top: 1px solid #1f2533;
  padding: 12px 16px;
  font-size: 12px;
  color: #8f98aa;
}

@media (max-width: 960px) {
  .layout { grid-template-columns: 1fr; }
  .action-row { justify-content: flex-start; }
  .head-row { flex-direction: column; align-items: flex-start; }
}

</style>
