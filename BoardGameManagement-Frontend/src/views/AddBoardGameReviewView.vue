<script setup>
import NavLandingSigned from "@/components/NavLandingSigned.vue";
import { useRoute, useRouter } from "vue-router";
import { reactive, computed } from "vue";
import axios from "axios";
import { useAuthStore } from "@/stores/authStore.js";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const axiosClient = axios.create({ baseURL: import.meta.env.VITE_BACKEND_URL || "http://localhost:8080" });
const gameName = computed(() => route.params.gamename || "");

const notice = reactive({ type: "", message: "" });
const reviewData = reactive({
  comment: "",
  rating: "",
});

const charCount = computed(() => reviewData.comment.length);
const isValid = computed(() => reviewData.comment.trim() && reviewData.rating);

function setNotice(type, message, timeout = 2600) {
  notice.type = type;
  notice.message = message;
  if (timeout) {
    setTimeout(() => {
      notice.type = "";
      notice.message = "";
    }, timeout);
  }
}

async function fetchBoardGameID(name) {
  try {
    const response = await axiosClient.get("/boardgames");
    const game = response.data.find((g) => g.name === name);
    return game ? game.gameID : null;
  } catch (error) {
    setNotice("error", "Failed to load board game.", 0);
    return null;
  }
}

async function createReview(comment, rating) {
  const gameId = await fetchBoardGameID(gameName.value);
  if (!gameId) {
    throw new Error("Selected board game not found.");
  }
  const newReview = {
    comment: comment,
    rating: Number(rating),
    commentDate: new Date().toISOString().split("T")[0],
    playerID: Number(authStore.user.id),
    boardGameID: gameId,
  };
  await axiosClient.post("/reviews", newReview);
}

async function submitReview() {
  try {
    await createReview(reviewData.comment, reviewData.rating);
    setNotice("success", "Review created successfully.");
    reviewData.comment = "";
    reviewData.rating = "";
    if (authStore.user.isAOwner) {
      router.push(`/pages/ownerboardgame/${gameName.value}`);
    } else {
      router.push(`/pages/playerboardgame/${gameName.value}`);
    }
  } catch (error) {
    const errors = error?.response?.data?.errors;
    const message = Array.isArray(errors) ? errors.join("\n") : error?.message || "Failed to create review.";
    setNotice("error", message, 0);
  }
}
</script>

<template>
  <div>
    <NavLandingSigned />

    <main class="page">
      <header class="page-header">
        <div class="head-row">
          <div class="title-wrap">
            <div>
              <h1>Add a review</h1>
              <p>Share your thoughts about {{ gameName }}.</p>
            </div>
          </div>
        </div>
      </header>

      <section class="card">
        <div v-if="notice.message" class="inline-banner" :class="notice.type">
          {{ notice.message }}
        </div>

        <form class="form-grid" @submit.prevent="submitReview">
          <div class="form-section">
            <h2>Review details</h2>
            <p class="subtle">Write a short review and select a rating.</p>

            <label class="label" for="comment">Comment</label>
            <textarea
              id="comment"
              maxlength="255"
              class="input textarea"
              v-model="reviewData.comment"
              placeholder="What did you enjoy about this game?"
              required
            ></textarea>
            <small class="counter" :class="{ warn: charCount >= 255 }">{{ charCount }} / 255</small>

            <label class="label" for="rating">Rating</label>
            <div class="rating-row">
              <select id="rating" v-model="reviewData.rating" class="input" required>
                <option value="" disabled>Select a rating</option>
                <option v-for="n in 5" :key="n" :value="n">{{ n }}</option>
              </select>
              <div class="rating-preview">
                <span v-for="n in 5" :key="n" class="star">
                  <span v-if="n <= Number(reviewData.rating || 0)">?</span>
                  <span v-else>?</span>
                </span>
              </div>
            </div>

            <div class="actions">
              <button class="btn ghost" type="button" @click="router.back()">Cancel</button>
              <button class="btn primary" type="submit" :disabled="!isValid">Submit review</button>
            </div>
          </div>

          <aside class="preview-card">
            <h3>Live preview</h3>
            <div class="preview-body">
              <div class="preview-title">{{ gameName }}</div>
              <div class="preview-stars">
                <span v-for="n in 5" :key="n" class="star">
                  <span v-if="n <= Number(reviewData.rating || 0)">?</span>
                  <span v-else>?</span>
                </span>
              </div>
              <p class="preview-comment">
                {{ reviewData.comment || "Your review will appear here." }}
              </p>
            </div>
          </aside>
        </form>
      </section>
    </main>
  </div>
</template>

<style scoped>
.page { position: relative; z-index: 0; margin-top: 96px; padding: 24px; }
.page-header { display: flex; flex-direction: column; gap: 10px; margin-bottom: 16px; }
.head-row { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
.title-wrap { display: flex; gap: 14px; align-items: center; }
.page-header h1 { font-size: 32px; margin: 0; color: #e8ecf7; font-weight: 800; }
.page-header p { opacity: .75; margin: 0; color: #c3cad9; }

.card { border: 1px solid #293043; border-radius: 16px; background: #0f1217; color: #e9edf5; padding: 20px; }
.form-grid { display: grid; grid-template-columns: 1.1fr 0.9fr; gap: 20px; }
.form-section { display: grid; gap: 12px; }
.form-section h2 { margin: 0; font-size: 20px; }
.subtle { color: #aab3c3; margin: 0; }

.input { width: 100%; background: #151a22; color: #f0f4ff; border: 1px solid #384054; border-radius: 10px; padding: 10px 12px; outline: none; transition: border-color .2s ease, box-shadow .2s ease; }
.input:focus { border-color: #72aaff; box-shadow: 0 0 0 2px rgba(114,170,255,0.25); }
.textarea { min-height: 140px; resize: vertical; }
.label { font-size: 13px; font-weight: 600; opacity: .85; color: #e2e6f2; }

.counter { color: #9aa2b2; }
.counter.warn { color: #ffd6d6; }

.rating-row { display: grid; grid-template-columns: 1fr auto; gap: 12px; align-items: center; }
.rating-preview { color: #f6d365; font-size: 18px; }
.star { margin-right: 2px; }

.actions { display: flex; gap: 12px; margin-top: 8px; }

.btn { padding: 10px 16px; border-radius: 10px; font-weight: 600; transition: all .2s ease; border: 1px solid transparent; cursor: pointer; }
.btn.primary { background: #ffffff; color: #0f1217; border-color: #ffffff; }
.btn.primary:hover { background: #f3f3f3; }
.btn.ghost { background: transparent; border: 1px solid #2f384a; color: #dfe5f4; }
.btn.ghost:hover { background: #182132; }
.btn:disabled { opacity: .6; cursor: not-allowed; }

.inline-banner { margin-bottom: 12px; padding: 10px 12px; border-radius: 10px; border: 1px solid #2b3343; background: #0f1217; font-weight: 700; }
.inline-banner.success { border-color: #3e7350; background: #112218; color: #b7ffd1; }
.inline-banner.error { border-color: #8a2a2a; background: #1a1010; color: #ffd6d6; }

.preview-card { border: 1px solid #1f2533; border-radius: 14px; padding: 16px; background: #0f1217; box-shadow: 0 10px 24px rgba(0,0,0,.35); }
.preview-body { display: grid; gap: 10px; }
.preview-title { font-weight: 900; font-size: 20px; }
.preview-stars { color: #f6d365; font-size: 18px; }
.preview-comment { color: #c3cad9; margin: 0; }

@media (max-width: 980px) {
  .form-grid { grid-template-columns: 1fr; }
}
</style>
