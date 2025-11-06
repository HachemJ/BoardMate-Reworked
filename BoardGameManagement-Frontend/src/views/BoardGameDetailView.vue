<!-- src/views/BoardGameDetailView.vue -->
<script setup>
import NavLandingSigned from "@/components/NavLandingSigned.vue";
import { ref, reactive, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";
import { useAuthStore } from "@/stores/authStore.js";

const api = axios.create({ baseURL: "http://localhost:8080" });
const auth = useAuthStore();
const route = useRoute();
const router = useRouter();

const gameName = route.params.gamename;

// data buckets
const gameId = ref(null);
const game = ref({ minPlayers: "", maxPlayers: "", description: "" });
const copies = ref([]);
const reviews = ref([]);
const selectedCopyId = ref(null);

// lightweight banner
const banner = ref({ show: false, kind: "info", text: "" });
function showBanner(kind, text, ms = 3000) {
  banner.value = { show: true, kind, text };
  clearTimeout(showBanner._t);
  showBanner._t = setTimeout(() => (banner.value.show = false), ms);
}

// Add-Review route detection (use whichever exists)
const routeNames = new Set(router.getRoutes().map(r => r.name));
const addReviewRoute = computed(() =>
    routeNames.has("playerAddReview") ? "playerAddReview"
        : routeNames.has("ownerAddReview") ? "ownerAddReview"
            : null
);

// fetch helpers
async function resolveGameIdByName(name) {
  const { data } = await api.get("/boardgames");
  const match = (data || []).find(g => g.name === name);
  return match?.gameID ?? null;
}
async function loadAll() {
  try {
    gameId.value = await resolveGameIdByName(gameName);
    if (!gameId.value) {
      showBanner("error", "Board game not found.");
      return;
    }
    const [copiesRes, reviewsRes, gameRes] = await Promise.all([
      api.get(`/boardgamecopies/byboardgame/${gameId.value}`),
      api.get(`/reviews/byboardgame/${gameId.value}`),
      api.get(`/boardgames/${gameId.value}`),
    ]);
    copies.value = copiesRes.data ?? [];
    reviews.value = reviewsRes.data ?? [];
    game.value = gameRes.data ?? {};
  } catch (e) {
    console.error(e);
    showBanner("error", "Failed to load board game details.");
  }
}

onMounted(loadAll);

// Borrow request state + actions
const borrow = reactive({
  startOfLoan: "",
  endOfLoan: "",
  borrowerID: auth.user?.id,
});

function pickCopy(id) {
  selectedCopyId.value = id;
}

async function confirmBorrow() {
  if (!selectedCopyId.value) {
    showBanner("info", "Select a board game copy first.");
    return;
  }
  if (!borrow.startOfLoan || !borrow.endOfLoan) {
    showBanner("info", "Choose a start and end date.");
    return;
  }
  try {
    await api.post("/borrowrequests", {
      ...borrow,
      specificGameID: selectedCopyId.value,
      playerName: auth.user?.username,
    });
    selectedCopyId.value = null;
    borrow.startOfLoan = "";
    borrow.endOfLoan = "";
    showBanner("success", "Borrow request sent!");
  } catch (e) {
    const errs = e?.response?.data?.errors;
    showBanner("error", (Array.isArray(errs) && errs.join(" ")) || "Borrow request failed.");
  }
}
</script>

<template>
  <div>
    <NavLandingSigned />

    <main class="page container-fluid">
      <!-- banner -->
      <div
          v-if="banner.show"
          class="banner"
          :class="{
          success: banner.kind === 'success',
          error: banner.kind === 'error',
          info: banner.kind === 'info'
        }"
      >
        {{ banner.text }}
      </div>

      <!-- header card -->
      <div class="card p-4 mb-4 shadow-sm">
        <div class="row justify-content-between">
          <div class="col-md-6">
            <h1 class="fw-bold">{{ gameName }}</h1>
          </div>
          <div class="col-md-6">
            <p><strong>Minimum Players:</strong> {{ game.minPlayers }}</p>
            <p><strong>Maximum Players:</strong> {{ game.maxPlayers }}</p>
            <p><strong>Description:</strong> {{ game.description }}</p>
          </div>
        </div>
      </div>

      <!-- tabs look (simple two blocks) -->
      <div class="row g-4">
        <!-- Copies -->
        <div class="col-12">
          <div class="card p-4 shadow-sm">
            <div class="d-flex justify-content-between align-items-center mb-2">
              <h4 class="mb-0">Board Game Copies</h4>
            </div>
            <table class="table">
              <thead>
              <tr>
                <th>Specification</th>
                <th>Owner</th>
                <th class="text-end">Borrow</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="c in copies" :key="c.boardGameCopyId">
                <td>{{ c.specification }}</td>
                <td>{{ c.playerName }}</td>
                <td class="text-end">
                  <button
                      v-if="auth.user?.username !== c.playerName"
                      class="btn btn-info"
                      @click="pickCopy(c.boardGameCopyId)"
                  >
                    Select
                  </button>
                </td>
              </tr>
              <tr v-if="copies.length === 0">
                <td colspan="3" class="text-center text-muted py-3">No copies available yet.</td>
              </tr>
              </tbody>
            </table>

            <div v-if="selectedCopyId" class="borrow-pane">
              <h6>Borrow this copy</h6>
              <div class="row g-3 align-items-end">
                <div class="col-sm-4">
                  <label class="form-label">Start Date</label>
                  <input type="date" v-model="borrow.startOfLoan" class="form-control" />
                </div>
                <div class="col-sm-4">
                  <label class="form-label">End Date</label>
                  <input type="date" v-model="borrow.endOfLoan" class="form-control" />
                </div>
                <div class="col-sm-4 d-flex justify-content-end">
                  <button class="btn btn-info" @click="confirmBorrow">Confirm Borrow</button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Reviews -->
        <div class="col-12">
          <div class="card p-4 shadow-sm">
            <div class="d-flex justify-content-between align-items-center">
              <h4 class="mb-0">Reviews</h4>
              <router-link v-if="addReviewRoute" :to="{ name: addReviewRoute, query: { game: gameName } }">
                <button class="btn btn-info">Add Review</button>
              </router-link>
            </div>
            <table class="table mt-3">
              <thead>
              <tr>
                <th>Comment</th>
                <th>Rating</th>
                <th>Reviewer</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="r in reviews" :key="r.reviewId || (r.author?.name + r.comment)">
                <td>{{ r.comment }}</td>
                <td>
                    <span class="stars">
                      <span v-for="n in 5" :key="n">{{ n <= (r.rating || 0) ? "★" : "☆" }}</span>
                    </span>
                </td>
                <td>{{ r.author?.name }}</td>
              </tr>
              <tr v-if="reviews.length === 0">
                <td colspan="3" class="text-center text-muted py-3">No reviews yet.</td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.page { margin-top: 96px; }

/* cards */
.card { border: 1px solid #293043; border-radius: 1rem; background-color: #0f1217; color: #e9edf5; }

/* banner */
.banner { border-radius: 10px; padding: 10px 12px; margin-bottom: 14px; font-weight: 600; }
.banner.success { background: #0f2a18; border: 1px solid #1a7f46; color: #b9ffd5; }
.banner.error   { background: #2a1013; border: 1px solid #a53b47; color: #ffd8dd; }
.banner.info    { background: #0f1625; border: 1px solid #3b5b9e; color: #d7e5ff; }

/* stars */
.stars { font-size: 1.2rem; color: gold; }

/* table tweaks */
.table th, .table td { vertical-align: middle; }

/* borrow pane */
.borrow-pane { border-top: 1px dashed #2b3343; margin-top: 12px; padding-top: 12px; }
</style>
