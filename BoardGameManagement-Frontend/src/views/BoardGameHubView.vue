<template>
  <div>
    <NavLandingSigned />

    <main class="hub-page">
      <header class="hub-header">
        <div class="header-left">
          <button class="btn ghost" type="button" @click="goBack">Back to catalog</button>
          <div>
            <h1>Board Game Details</h1>
            <p>Overview, reviews, and available copies.</p>
          </div>
        </div>
        <div class="header-actions">
          <button class="btn primary" type="button" @click="goToReviews">View reviews</button>
          <button class="btn ghost" type="button" @click="goToCopies">View copies</button>
        </div>
      </header>

      <section class="hero-card" v-if="!loading && !error">
        <div class="hero-banner">
          <div class="hero-initial">{{ (gameDetails.name || "?").slice(0, 1) }}</div>
          <div class="hero-badges">
            <span class="badge">Players: {{ gameDetails.minPlayers }}–{{ gameDetails.maxPlayers }}</span>
            <span class="badge">Copies: {{ copiesCount }}</span>
            <span class="badge">Rating: {{ ratingDisplay }}</span>
          </div>
        </div>
        <div class="hero-body">
          <div>
            <h2>{{ gameDetails.name || "Unnamed board game" }}</h2>
            <p class="desc">{{ gameDetails.description || "No description yet." }}</p>
          </div>
          <div class="meta">
            <span class="chip">Min {{ gameDetails.minPlayers }} players</span>
            <span class="chip">Max {{ gameDetails.maxPlayers }} players</span>
            <span class="chip">{{ ratingCount }} review{{ ratingCount === 1 ? "" : "s" }}</span>
          </div>
        </div>
      </section>

      <section v-if="loading" class="loading-card">
        <div class="skeleton hero"></div>
        <div class="skeleton line"></div>
        <div class="skeleton line short"></div>
      </section>

      <section v-if="error" class="error-card">
        <h2>Unable to load board game</h2>
        <p>{{ error }}</p>
        <button class="btn ghost" type="button" @click="fetchHubData">Try again</button>
      </section>

      <section class="hub-footer" v-if="!loading && !error">
        <div class="info-block">
          <h3>Copies</h3>
          <p v-if="copiesCount === 0">No copies available right now.</p>
          <p v-else>{{ copiesCount }} copy{{ copiesCount === 1 ? "" : "ies" }} available.</p>
        </div>
        <div class="info-block">
          <h3>Reviews</h3>
          <p v-if="ratingCount === 0">No reviews yet.</p>
          <p v-else>Average rating {{ ratingDisplay }} across {{ ratingCount }} review{{ ratingCount === 1 ? "" : "s" }}.</p>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";
import NavLandingSigned from "@/components/NavLandingSigned.vue";

const axiosClient = axios.create({ baseURL: import.meta.env.VITE_BACKEND_URL || "http://localhost:8080" });
const route = useRoute();
const router = useRouter();

const gameDetails = ref({});
const reviews = ref([]);
const copies = ref([]);
const loading = ref(true);
const error = ref("");

const gameId = computed(() => Number(route.params.id));

const copiesCount = computed(() => copies.value.length || 0);
const ratingCount = computed(() => reviews.value.length || 0);
const ratingAverage = computed(() => {
  if (!reviews.value.length) return 0;
  const total = reviews.value.reduce((sum, review) => sum + Number(review.rating || 0), 0);
  return total / reviews.value.length;
});
const ratingDisplay = computed(() => (ratingCount.value ? ratingAverage.value.toFixed(1) : "—"));

function goBack() {
  if (window.history.length > 1) {
    router.back();
    return;
  }
  router.push({ name: "boardgames" });
}

function goToReviews() {
  if (!gameDetails.value?.name) return;
  router.push({ name: "playerBoardGameDetail", params: { gamename: gameDetails.value.name }, query: { tab: "reviews" } });
}

function goToCopies() {
  if (!gameDetails.value?.name) return;
  router.push({ name: "playerBoardGameDetail", params: { gamename: gameDetails.value.name }, query: { tab: "copies" } });
}

async function fetchHubData() {
  if (!gameId.value) {
    error.value = "Invalid board game id.";
    loading.value = false;
    return;
  }

  loading.value = true;
  error.value = "";
  try {
    const [detailsRes, copiesRes, reviewsRes] = await Promise.all([
      axiosClient.get(`/boardgames/${gameId.value}`),
      axiosClient.get(`/boardgamecopies/byboardgame/${gameId.value}`),
      axiosClient.get(`/reviews/byboardgame/${gameId.value}`),
    ]);
    gameDetails.value = detailsRes.data || {};
    copies.value = copiesRes.data || [];
    reviews.value = reviewsRes.data || [];
  } catch (err) {
    error.value = err?.response?.data?.message || "Failed to load board game details.";
  } finally {
    loading.value = false;
  }
}

onMounted(fetchHubData);
</script>

<style scoped>
.hub-page {
  margin-top: 96px;
  padding: 0 24px 48px;
  color: #e9edf5;
}

.hub-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-left h1 {
  margin: 0;
  font-size: 32px;
}

.header-left p {
  margin: 4px 0 0;
  color: #9aa2b2;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.hero-card {
  border: 1px solid rgba(47, 56, 74, 0.6);
  border-radius: 18px;
  background: #0f141c;
  box-shadow: 0 18px 40px rgba(0,0,0,.45);
  overflow: hidden;
}

.hero-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  background: radial-gradient(circle at top left, rgba(90,111,160,0.35), transparent 60%),
              linear-gradient(135deg, rgba(22,28,38,0.95), rgba(14,18,26,0.95));
}

.hero-initial {
  width: 72px;
  height: 72px;
  border-radius: 18px;
  background: rgba(255,255,255,0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 700;
  color: #f1f5ff;
}

.hero-badges {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.badge {
  padding: 8px 14px;
  border-radius: 999px;
  border: 1px solid rgba(143, 180, 255, 0.4);
  color: #dbe5ff;
  font-weight: 600;
  font-size: 14px;
}

.hero-body {
  padding: 20px 24px 24px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
  flex-wrap: wrap;
}

.hero-body h2 {
  margin: 0 0 8px;
  font-size: 28px;
}

.desc {
  margin: 0;
  color: #b8c0d1;
  max-width: 600px;
}

.meta {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.chip {
  padding: 6px 12px;
  border-radius: 10px;
  border: 1px solid rgba(47, 56, 74, 0.8);
  color: #d2d9e6;
  font-size: 13px;
}

.loading-card,
.error-card {
  border: 1px solid rgba(47, 56, 74, 0.6);
  border-radius: 16px;
  background: #0f141c;
  padding: 24px;
  margin-top: 16px;
}

.error-card h2 {
  margin: 0 0 8px;
}

.skeleton {
  background: linear-gradient(90deg, #141a22 0%, #1b222e 50%, #141a22 100%);
  border-radius: 12px;
  animation: shimmer 1.6s infinite;
}

.skeleton.hero { height: 140px; margin-bottom: 16px; }
.skeleton.line { height: 18px; margin-bottom: 10px; }
.skeleton.line.short { width: 60%; }

@keyframes shimmer {
  0% { background-position: -200px 0; }
  100% { background-position: 200px 0; }
}

.hub-footer {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
  margin-top: 24px;
}

.info-block {
  border: 1px solid rgba(47, 56, 74, 0.6);
  border-radius: 14px;
  padding: 16px;
  background: rgba(16, 20, 28, 0.8);
}

.info-block h3 {
  margin: 0 0 6px;
  font-size: 16px;
}

.info-block p {
  margin: 0;
  color: #b0b9c8;
}

.btn {
  padding: 10px 16px;
  border-radius: 10px;
  font-weight: 600;
  border: 1px solid transparent;
  cursor: pointer;
}

.btn.primary {
  background: #3e79f7;
  color: #fff;
  box-shadow: 0 10px 20px rgba(62, 121, 247, 0.28);
}

.btn.ghost {
  background: transparent;
  border: 1px solid #2f384a;
  color: #dfe5f4;
}

@media (max-width: 900px) {
  .hub-header {
    flex-direction: column;
    align-items: flex-start;
  }
  .header-actions {
    width: 100%;
  }
  .header-actions .btn {
    flex: 1;
  }
}

@media (max-width: 600px) {
  .header-left {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
