<script setup>
import NavLandingSigned from "@/components/NavLandingSigned.vue";
import { ref, computed, onMounted, reactive, watchEffect } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";
import { useAuthStore } from "@/stores/authStore.js";

const axiosClient = axios.create({ baseURL: import.meta.env.VITE_BACKEND_URL || "http://localhost:8080" });
const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();

const reviews = ref([]);
const boardGameCopies = ref([]);
const selectedCopy = ref(null);
const gameDetails = ref({ minPlayers: "", maxPlayers: "", description: "" });
const tabs = ["Board Game Copies", "Reviews"];
const selectedTab = ref(tabs[0]);
const notice = reactive({ type: "", message: "" });

const borrowLength = ref("24h");
const borrowForm = reactive({
  startDate: "",
  startTime: "",
  endDate: "",
  endTime: "",
});

const LENGTH_OPTIONS = [
  { key: "12h", label: "12h", hours: 12 },
  { key: "24h", label: "24h", hours: 24 },
  { key: "48h", label: "48h", hours: 48 },
  { key: "custom", label: "Custom", hours: 0 },
];

const gameName = computed(() => route.params.gamename || "");

const reviewRoute = computed(() => {
  const pathSegments = route.path.split("/");
  const roleSegment = pathSegments[2];
  return roleSegment === "playerboardgame" ? "playerAddReview" : "ownerAddReview";
});

const reviewCount = computed(() => reviews.value.length);

const borrowError = computed(() => {
  if (!selectedCopy.value) return "Select a copy to request a borrow.";
  if (!borrowForm.startDate || !borrowForm.startTime) return "Start date and time are required.";
  if (!borrowForm.endDate || !borrowForm.endTime) return "End date and time are required.";
  if (borrowForm.endDate < borrowForm.startDate) return "End date must be after start date.";
  if (borrowForm.endDate === borrowForm.startDate && borrowForm.endTime <= borrowForm.startTime) {
    return "End time must be after start time.";
  }
  if (isStartInPast()) return "Start time must be in the future.";
  return "";
});

const isBorrowValid = computed(() => !borrowError.value && !!selectedCopy.value);

const borrowSummary = computed(() => {
  if (!borrowForm.startDate || !borrowForm.startTime || !borrowForm.endDate || !borrowForm.endTime) return "";
  return `Borrowing from ${prettyDateTime(borrowForm.startDate, borrowForm.startTime)} -> ${prettyDateTime(borrowForm.endDate, borrowForm.endTime)}`;
});

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

function todayString() {
  const today = new Date();
  const y = today.getFullYear();
  const m = String(today.getMonth() + 1).padStart(2, "0");
  const d = String(today.getDate()).padStart(2, "0");
  return `${y}-${m}-${d}`;
}

function timeString(date) {
  const h = String(date.getHours()).padStart(2, "0");
  const m = String(date.getMinutes()).padStart(2, "0");
  return `${h}:${m}`;
}

function roundToNext15Minutes(date) {
  const rounded = new Date(date);
  const minutes = rounded.getMinutes();
  const delta = (15 - (minutes % 15)) % 15;
  rounded.setMinutes(minutes + delta, 0, 0);
  return rounded;
}

function addHoursToDateTime(dateStr, timeStr, hours) {
  if (!dateStr || !timeStr) return { date: "", time: "" };
  const [y, m, d] = dateStr.split("-").map(Number);
  const [hh, mm] = timeStr.split(":").map(Number);
  const dt = new Date(y, (m || 1) - 1, d || 1, hh || 0, mm || 0);
  dt.setHours(dt.getHours() + hours);
  const endDate = dt.getFullYear() + "-" + String(dt.getMonth() + 1).padStart(2, "0") + "-" + String(dt.getDate()).padStart(2, "0");
  const endTime = String(dt.getHours()).padStart(2, "0") + ":" + String(dt.getMinutes()).padStart(2, "0");
  return { date: endDate, time: endTime };
}

function prettyDateTime(dateStr, timeStr) {
  if (!dateStr || !timeStr) return "";
  const [y, m, d] = dateStr.split("-").map(Number);
  const [hh, mm] = timeStr.split(":").map(Number);
  const dt = new Date(y, (m || 1) - 1, d || 1, hh || 0, mm || 0);
  return dt.toLocaleString(undefined, { month: "short", day: "numeric", hour: "2-digit", minute: "2-digit" });
}

function combineDateTime(dateStr, timeStr) {
  if (!dateStr || !timeStr) return "";
  return `${dateStr}T${timeStr}:00`;
}

function isStartInPast() {
  if (!borrowForm.startDate || !borrowForm.startTime) return false;
  const now = new Date();
  const [y, m, d] = borrowForm.startDate.split("-").map(Number);
  const [hh, mm] = borrowForm.startTime.split(":").map(Number);
  const start = new Date(y, (m || 1) - 1, d || 1, hh || 0, mm || 0);
  return start < now;
}

function applyBorrowLength() {
  const option = LENGTH_OPTIONS.find((o) => o.key === borrowLength.value);
  if (!option || option.key === "custom") return;
  if (!borrowForm.startDate) borrowForm.startDate = todayString();
  if (!borrowForm.startTime) borrowForm.startTime = timeString(roundToNext15Minutes(new Date()));
  const end = addHoursToDateTime(borrowForm.startDate, borrowForm.startTime, option.hours);
  borrowForm.endDate = end.date;
  borrowForm.endTime = end.time;
}

watchEffect(() => {
  if (borrowLength.value !== "custom" && borrowForm.startDate && borrowForm.startTime) {
    applyBorrowLength();
  }
  if (borrowLength.value === "custom") {
    if (!borrowForm.startDate) borrowForm.startDate = todayString();
    if (!borrowForm.startTime) borrowForm.startTime = timeString(roundToNext15Minutes(new Date()));
    if (!borrowForm.endDate) borrowForm.endDate = borrowForm.startDate;
    if (!borrowForm.endTime) borrowForm.endTime = borrowForm.startTime;
  }
});

async function fetchBoardGameID() {
  const name = route.params.gamename;
  try {
    const response = await axiosClient.get("/boardgames");
    const game = response.data.find((g) => g.name === name);
    return game ? game.gameID : null;
  } catch (error) {
    setNotice("error", "Failed to load board game.", 0);
    return null;
  }
}

onMounted(async () => {
  try {
    const gameId = await fetchBoardGameID();
    if (!gameId) return;
    const copiesResponse = await axiosClient.get(`/boardgamecopies/byboardgame/${gameId}`);
    boardGameCopies.value = copiesResponse.data;

    const reviewsResponse = await axiosClient.get(`/reviews/byboardgame/${gameId}`);
    reviews.value = reviewsResponse.data;

    const detailsResponse = await axiosClient.get(`/boardgames/${gameId}`);
    gameDetails.value = detailsResponse.data;
  } catch (error) {
    setNotice("error", "Failed to load board game details.", 0);
  }
});

function selectCopy(copy) {
  if (selectedCopy.value?.boardGameCopyId === copy.boardGameCopyId) {
    selectedCopy.value = null;
    return;
  }
  selectedCopy.value = copy;
  if (!borrowForm.startDate) borrowForm.startDate = todayString();
  if (!borrowForm.startTime) borrowForm.startTime = timeString(roundToNext15Minutes(new Date()));
  applyBorrowLength();
}

async function confirmBorrow() {
  if (!isBorrowValid.value) {
    setNotice("error", borrowError.value || "Fill out the borrow details.", 0);
    return;
  }
  try {
    const payload = {
      startOfLoan: combineDateTime(borrowForm.startDate, borrowForm.startTime),
      endOfLoan: combineDateTime(borrowForm.endDate, borrowForm.endTime),
      borrowerID: authStore.user.id,
      specificGameID: selectedCopy.value.boardGameCopyId,
    };
    await axiosClient.post("/borrowrequests", payload);
    setNotice("success", "Borrow request sent.");
    selectedCopy.value = null;
    borrowForm.startDate = "";
    borrowForm.startTime = "";
    borrowForm.endDate = "";
    borrowForm.endTime = "";
    borrowLength.value = "24h";
  } catch (error) {
    const errors = error?.response?.data?.errors;
    const message = Array.isArray(errors) ? errors.join("\n") : "Failed to send borrow request.";
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
              <h1>{{ gameName }}</h1>
              <p>Explore copies and community reviews for this game.</p>
            </div>
          </div>
          <div class="meta-chips">
            <span class="chip neutral">Min Players: {{ gameDetails.minPlayers || "TBD" }}</span>
            <span class="chip neutral">Max Players: {{ gameDetails.maxPlayers || "TBD" }}</span>
          </div>
        </div>
        <p class="desc">{{ gameDetails.description || "No description provided yet." }}</p>

        <div class="tabs">
          <button
            v-for="tab in tabs"
            :key="tab"
            class="tab wide"
            :class="{ active: selectedTab === tab }"
            @click="selectedTab = tab"
          >
            {{ tab }}<span v-if="tab === 'Reviews'" class="tab-count">{{ reviewCount }}</span>
          </button>
        </div>
      </header>

      <section class="card">
        <div v-if="notice.message" class="inline-banner" :class="notice.type">
          {{ notice.message }}
        </div>

        <div v-if="selectedTab === 'Board Game Copies'" class="copies-layout">
          <div>
            <div class="section-head">
              <div>
                <h2>Available copies</h2>
                <p class="subtle">Select a copy to request a borrow period.</p>
              </div>
            </div>

            <div class="grid copies">
              <button
                v-for="copy in boardGameCopies"
                :key="copy.boardGameCopyId"
                type="button"
                class="copy-card"
                :class="{ selected: selectedCopy?.boardGameCopyId === copy.boardGameCopyId }"
                @click="selectCopy(copy)"
              >
                <div class="copy-cover">
                  <span>{{ (copy.boardGameName || gameName || "?").slice(0, 1) }}</span>
                </div>
                <div class="copy-body">
                  <h3 class="title">{{ copy.specification || "Copy" }}</h3>
                  <div class="meta-text">Owner: {{ copy.playerName }}</div>
                  <span class="badge" :class="copy.isAvailable ? 'ok' : 'warn'">
                    {{ copy.isAvailable ? "Available" : "Unavailable" }}
                  </span>
                </div>
              </button>

              <div v-if="boardGameCopies.length === 0" class="empty">No copies found.</div>
            </div>
          </div>

          <aside class="panel">
            <div class="panel-card">
              <h3>Request details</h3>
              <p class="subtle">Select a copy to request a borrow.</p>

              <div v-if="selectedCopy" class="panel-info">
                <div class="panel-title">{{ selectedCopy.specification || gameName }}</div>
                <div class="panel-meta">Owner: {{ selectedCopy.playerName }}</div>
                <div class="panel-meta">
                  Status:
                  <span :class="selectedCopy.isAvailable ? 'status-ok' : 'status-warn'">
                    {{ selectedCopy.isAvailable ? "Available" : "Unavailable" }}
                  </span>
                </div>
              </div>

              <div class="form-block" :class="{ disabled: !selectedCopy }">
                <label class="label">Start date</label>
                <input class="input" type="date" v-model="borrowForm.startDate" :min="todayString()" :disabled="!selectedCopy" />

                <label class="label">Start time</label>
                <input class="input" type="time" v-model="borrowForm.startTime" :disabled="!selectedCopy" />

                <label class="label">Borrow length</label>
                <div class="length-chips">
                  <button
                    v-for="opt in LENGTH_OPTIONS"
                    :key="opt.key"
                    type="button"
                    class="chip"
                    :class="{ active: borrowLength === opt.key }"
                    :disabled="!selectedCopy"
                    @click="borrowLength = opt.key; applyBorrowLength()"
                  >
                    {{ opt.label }}
                  </button>
                </div>

                <div v-if="borrowLength === 'custom'">
                  <label class="label">End date</label>
                  <input class="input" type="date" v-model="borrowForm.endDate" :min="borrowForm.startDate || todayString()" :disabled="!selectedCopy" />

                  <label class="label">End time</label>
                  <input class="input" type="time" v-model="borrowForm.endTime" :disabled="!selectedCopy" />
                </div>

                <small v-if="borrowSummary" class="summary">{{ borrowSummary }}</small>
                <small v-if="borrowError" class="err">{{ borrowError }}</small>

                <button
                  class="btn primary"
                  type="button"
                  :disabled="!isBorrowValid || !selectedCopy?.isAvailable"
                  @click="confirmBorrow"
                >
                  Request borrow
                </button>
              </div>
            </div>
          </aside>
        </div>

        <div v-else class="reviews-section">
          <div class="section-head">
            <div>
              <h2>Reviews</h2>
              <p class="subtle">See what players think about this game.</p>
            </div>
            <router-link :to="{ name: reviewRoute }">
              <button class="btn ghost">Add review</button>
            </router-link>
          </div>

          <div class="review-grid">
            <div v-for="review in reviews" :key="review.reviewId" class="review-card">
              <div class="review-stars">
                <span v-for="n in 5" :key="n" class="star">
                  <span v-if="n <= review.rating">★</span>
                  <span v-else>☆</span>
                </span>
              </div>
              <p class="review-comment">{{ review.comment }}</p>
              <div class="review-meta">
                <span class="review-author">{{ review.authorName }}</span>
                <span class="review-date">{{ review.commentDate || "Date TBD" }}</span>
              </div>
            </div>
            <div v-if="reviews.length === 0" class="empty">No reviews yet.</div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
.page { position: relative; z-index: 0; margin-top: 96px; padding: 24px; }
.page-header { display: flex; flex-direction: column; gap: 10px; margin-bottom: 16px; }
.head-row { display: flex; align-items: center; justify-content: space-between; gap: 12px; flex-wrap: wrap; }
.title-wrap { display: flex; gap: 14px; align-items: center; }
.page-header h1 { font-size: 32px; margin: 0; color: #e8ecf7; font-weight: 800; }
.page-header p { opacity: .75; margin: 0; color: #c3cad9; }
.desc { color: #9aa2b2; margin: 0; }

.tabs { display: flex; gap: 8px; margin-top: 8px; }
.tab { padding: 8px 16px; border-radius: 10px; background: transparent; color: #d8deed; border: 1px solid #2f384a; font-weight: 600; }
.tab.wide { padding: 12px 26px; min-width: 160px; text-align: center; }
.tab.active { background: #fff; color: #0f1217; border-color: #ffffff; font-weight: 800; box-shadow: 0 2px 6px rgba(255,255,255,0.15); }
.tab:not(.active):hover { background: #f4f6fa; color: #0f1217; border-color: #f4f6fa; }

.meta-chips { display: flex; gap: 8px; flex-wrap: wrap; }
.chip { padding: 6px 12px; border-radius: 999px; border: 1px solid #2f384a; background: transparent; color: #d8deed; font-weight: 700; }
.chip.neutral { background: #151a22; color: #e6ecff; border-color: #2f384a; }

.card { border: 1px solid #293043; border-radius: 16px; background: #0f1217; color: #e9edf5; padding: 20px; }
.section-head { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 10px; }
.subtle { color: #aab3c3; margin: 0; }

.btn { padding: 10px 16px; border-radius: 10px; font-weight: 600; transition: all .2s ease; border: 1px solid transparent; cursor: pointer; }
.btn.primary { background: #ffffff; color: #0f1217; border-color: #ffffff; }
.btn.primary:hover { background: #f3f3f3; }
.btn.ghost { background: transparent; border: 1px solid #2f384a; color: #dfe5f4; }
.btn.ghost:hover { background: #182132; }
.btn:disabled { opacity: .6; cursor: not-allowed; }

.inline-banner { margin-bottom: 12px; padding: 10px 12px; border-radius: 10px; border: 1px solid #2b3343; background: #0f1217; font-weight: 700; }
.inline-banner.success { border-color: #3e7350; background: #112218; color: #b7ffd1; }
.inline-banner.error { border-color: #8a2a2a; background: #1a1010; color: #ffd6d6; }

.copies-layout { display: grid; grid-template-columns: 1.2fr 0.8fr; gap: 16px; }
.panel { position: sticky; top: 96px; align-self: start; }
.panel-card { border: 1px solid #1f2533; border-radius: 14px; padding: 16px; background: #0f1217; }
.panel-info { margin: 10px 0; }
.panel-title { font-weight: 900; font-size: 18px; color: #fff; }
.panel-meta { color: #c3cad9; margin-top: 4px; }
.form-block { display: grid; gap: 8px; margin-top: 10px; }
.form-block.disabled { opacity: .55; }
.form-block.disabled .input,
.form-block.disabled .chip,
.form-block.disabled .btn { cursor: not-allowed; }

.input { width: 100%; background: #151a22; color: #f0f4ff; border: 1px solid #384054; border-radius: 10px; padding: 10px 12px; outline: none; transition: border-color .2s ease, box-shadow .2s ease; }
.input:focus { border-color: #72aaff; box-shadow: 0 0 0 2px rgba(114,170,255,0.25); }
.label { font-size: 13px; font-weight: 600; opacity: .85; color: #e2e6f2; }

.length-chips { display: flex; gap: 8px; flex-wrap: wrap; }
.chip.active { background: #fff; color: #0f1217; border-color: #fff; }
.summary { display: block; color: #9aa2b2; margin-top: 4px; }
.err { color: #ffd6d6; }

.grid { display: grid; gap: 14px; }
.grid.copies { grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); }
.copy-card {
  border-radius: 16px;
  padding: 12px;
  background: #0f1217;
  border: 1px solid #1f2533;
  box-shadow: 0 10px 24px rgba(0,0,0,.35);
  text-align: left;
  transition: transform .18s ease, box-shadow .18s ease, border-color .18s ease;
}
.copy-card:hover { transform: translateY(-4px); border-color: #3a4256; box-shadow: 0 16px 32px rgba(0,0,0,.45); }
.copy-card.selected { border-color: #ffffff; box-shadow: 0 0 0 1px #ffffff inset; }
.copy-cover { height: 90px; border-radius: 12px; display: grid; place-items: center; background: linear-gradient(135deg, #151a22, #0f1217); border: 1px solid #1f2533; margin-bottom: 10px; }
.copy-cover span { font-size: 28px; font-weight: 900; color: #e6ecff; }
.copy-body { display: grid; gap: 4px; }
.title { margin: 0; font-size: 18px; font-weight: 900; color: #ffffff; }
.meta-text { color: #c6cedf; font-size: 12px; font-weight: 700; }

.badge { padding: 4px 10px; border-radius: 999px; font-size: 12px; font-weight: 800; border: 1px solid transparent; width: fit-content; }
.badge.ok { background: #112218; color: #b7ffd1; border-color: #3e7350; }
.badge.warn { background: #24180b; color: #ffe2b8; border-color: #6a4a23; }

.status-ok { color: #b7ffd1; font-weight: 700; }
.status-warn { color: #ffe2b8; font-weight: 700; }

.review-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 16px; }
.review-card { border-radius: 16px; border: 1px solid #1f2533; background: #0f1217; padding: 16px; box-shadow: 0 10px 24px rgba(0,0,0,.35); }
.review-stars { color: #f6d365; font-size: 18px; margin-bottom: 8px; }
.review-comment { color: #dfe5f4; margin: 0 0 8px; }
.review-meta { display: flex; justify-content: space-between; gap: 12px; color: #9aa2b2; font-weight: 700; }
.review-author { color: inherit; }
.review-date { color: #9aa2b2; font-weight: 600; }
.tab-count { margin-left: 6px; font-weight: 800; }

.empty { color: #9aa2b2; text-align: center; padding: 16px; }

@media (max-width: 980px) {
  .copies-layout { grid-template-columns: 1fr; }
  .panel { position: static; }
}
</style>
