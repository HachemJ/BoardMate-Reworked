<!-- src/views/AddBoardGameView.vue -->
<script setup>
import { ref } from "vue";
import axios from "axios";
import NavLandingSigned from "@/components/NavLandingSigned.vue";

const axiosClient = axios.create({ baseURL: "http://localhost:8080" });

// simple in-app banner
const banner = ref({ show: false, kind: "success", text: "" });
const loading = ref(false);

// form state
const name = ref("");
const minPlayers = ref(null);
const maxPlayers = ref(null);
const description = ref("");

function showBanner(kind, text) {
  banner.value = { show: true, kind, text };
  // auto-hide after 4s
  setTimeout(() => (banner.value.show = false), 4000);
}

async function submit() {
  // front-end guard to avoid server roundtrip for obvious mistakes
  if (!name.value || !minPlayers.value || !maxPlayers.value) {
    showBanner("error", "Please fill all required fields.");
    return;
  }
  if (+maxPlayers.value < +minPlayers.value) {
    showBanner("error", "Maximum players must be greater than or equal to minimum players.");
    return;
  }

  loading.value = true;
  try {
    await axiosClient.post("/boardgames", {
      name: name.value,
      minPlayers: +minPlayers.value,
      maxPlayers: +maxPlayers.value,
      description: description.value || "",
    });

    showBanner("success", "Board game created successfully.");
    // reset the form
    name.value = "";
    minPlayers.value = null;
    maxPlayers.value = null;
    description.value = "";
  } catch (err) {
    const serverErrors =
        err?.response?.data?.errors && Array.isArray(err.response.data.errors)
            ? err.response.data.errors.join(" ")
            : err?.response?.data?.message || "Failed to create board game.";
    showBanner("error", serverErrors);
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div>
    <NavLandingSigned />

    <div class="container page">
      <div class="card p-4 shadow-sm">
        <h2 class="mb-3">Complete the Form to Create a Board Game</h2>

        <!-- in-app banner -->
        <div
            v-if="banner.show"
            class="banner"
            :class="banner.kind === 'success' ? 'banner-success' : 'banner-error'"
            role="status"
        >
          {{ banner.text }}
        </div>

        <form @submit.prevent="submit" class="form-grid">
          <label>
            <span>Board Game Name</span>
            <input v-model="name" type="text" placeholder="e.g., Catan" required />
          </label>

          <label>
            <span>Minimum Number of Players</span>
            <input v-model.number="minPlayers" type="number" min="1" step="1" required />
          </label>

          <label>
            <span>Maximum Number of Players</span>
            <input v-model.number="maxPlayers" type="number" min="1" step="1" required />
          </label>

          <label class="full">
            <span>Description</span>
            <textarea v-model="description" maxlength="255" placeholder="Optional description..." />
          </label>

          <div class="actions">
            <button class="btn btn-info" :disabled="loading">
              {{ loading ? 'Creating…' : 'Create Board Game' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page { margin-top: 96px; }
.card {
  border: 1px solid #293043;
  border-radius: 1rem;
  background-color: #0f1217;
  color: #e9edf5;
}

/* banner */
.banner {
  border-radius: 10px;
  padding: 10px 12px;
  margin-bottom: 14px;
  font-weight: 600;
}
.banner-success { background: #0f2a18; border: 1px solid #1a7f46; color: #b9ffd5; }
.banner-error   { background: #2a1013; border: 1px solid #a53b47; color: #ffd8dd; }

/* form */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px 18px;
}
label { display: flex; flex-direction: column; gap: 6px; }
label.full { grid-column: 1 / -1; }

input, textarea {
  background: #0b0e13;
  border: 1px solid #2b3343;
  color: #e9edf5;
  border-radius: 10px;
  padding: 10px 12px;
}
input:focus, textarea:focus {
  outline: none;
  border-color: #4c89ff;
  box-shadow: 0 0 0 2px rgba(76,137,255,.25);
}

.actions { grid-column: 1 / -1; display: flex; justify-content: flex-start; margin-top: 6px; }

/* button look */
.btn.btn-info {
  background: #0f6fe5; border: 1px solid #0f6fe5; color: #fff;
  padding: 10px 14px; border-radius: 10px; font-weight: 700;
}
.btn[disabled] { opacity: .65; cursor: not-allowed; }
</style>
