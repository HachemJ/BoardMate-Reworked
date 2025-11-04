<!-- src/views/Profile.vue -->
<script>
import { useAuthStore } from "@/stores/authStore";
import axios from "axios";
import NavLandingSigned from "@/components/NavLandingSigned.vue";

const axiosClient = axios.create({ baseURL: "http://localhost:8080" });

export default {
  name: "UserProfileView",
  components: { NavLandingSigned },

  data() {
    return {
      authStore: null,

      // editing state
      isEditing: false,
      saving: false,

      // original role to detect change
      originalIsAOwner: false,

      // form model
      editedProfile: {
        name: "",
        email: "",
        isAOwner: false,
        // current password (required if role flips; can also be used to confirm edits)
        accountTypePassword: "",
      },

      // UI / feedback
      toast: { show: false, type: "info", message: "" }, // type: 'success' | 'error' | 'info'
      fieldErrors: { accountTypePassword: "" },

      // tabs
      _selectedTab: "events",

      // data
      userProfile: {
        name: "",
        email: "",
        status: "", // 'player' | 'owner'
        profilePicture: null,
        id: null,
      },
      userBoardgames: [],
      userEvents: [],
    };
  },

  computed: {
    tabs() {
      return this.userProfile.status === "owner"
          ? [
            { id: "boardgames", name: "My Board Games" },
            { id: "events", name: "Events" },
          ]
          : [{ id: "events", name: "Events" }];
    },
    selectedTab: {
      get() {
        return this._selectedTab;
      },
      set(v) {
        this._selectedTab = v;
      },
    },
    accountTypeChanged() {
      return this.editedProfile.isAOwner !== this.originalIsAOwner;
    },
    saveDisabled() {
      // If role changed, require password; also block while saving
      if (this.saving) return true;
      if (this.accountTypeChanged && !this.editedProfile.accountTypePassword) return true;
      // basic sanity: need name & email
      if (!this.editedProfile.name?.trim() || !this.editedProfile.email?.trim()) return true;
      return false;
    },
  },

  created() {
    this.authStore = useAuthStore();
    this._selectedTab = this.authStore.user?.isAOwner ? "boardgames" : "events";
    this.fetchUserData();
  },

  methods: {
    /* ---------- UX helpers ---------- */
    showToast(type, message, ms = 2800) {
      this.toast = { show: true, type, message };
      window.clearTimeout(this._toastTimer);
      this._toastTimer = window.setTimeout(() => (this.toast.show = false), ms);
    },
    clearFieldErrors() {
      this.fieldErrors.accountTypePassword = "";
    },

    /* ---------- API ---------- */
    async fetchUserData() {
      try {
        const userId = this.authStore.user.id;
        const { data } = await axiosClient.get(`/players/${userId}`);

        this.userProfile = {
          name: data.name,
          email: data.email,
          status: data.isAOwner ? "owner" : "player",
          profilePicture: null,
          id: data.id,
        };

        this.originalIsAOwner = !!data.isAOwner;

        this.editedProfile = {
          name: data.name,
          email: data.email,
          isAOwner: !!data.isAOwner,
          accountTypePassword: "",
        };

        await this.fetchUserEvents(userId);
        if (data.isAOwner) await this.fetchOwnedGames(userId);
      } catch (e) {
        console.error("Error fetching user data:", e);
        this.showToast("error", "Failed to load profile.");
      }
    },

    async fetchUserEvents(userId) {
      try {
        const regs = await axiosClient.get(`/registrations/players/${userId}`);
        const events = [];
        for (const r of regs.data ?? []) {
          try {
            const ev = await axiosClient.get(`/events/${r.eventID}`);
            events.push({
              id: ev.data.eventID,
              name: ev.data.name,
              description: ev.data.description,
              date: ev.data.eventDate,
              startTime: ev.data.startTime,
              endTime: ev.data.endTime,
              status: this.determineEventStatus(ev.data),
              maxSpot: ev.data.maxSpot,
            });
          } catch (err) {
            console.error("Event fetch failed", err);
          }
        }
        this.userEvents = events;
      } catch (e) {
        console.error("Error fetching user events:", e);
        this.userEvents = [];
      }
    },

    determineEventStatus(event) {
      const d = new Date(event.eventDate);
      const now = new Date();
      const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
      if (d < today) return "completed";
      if (d.getTime() === today.getTime()) return "ongoing";
      return "upcoming";
    },

    async fetchOwnedGames(userId) {
      try {
        const res = await axiosClient.get(`/boardgamecopies/byplayer/${userId}`);
        this.userBoardgames =
            (res.data ?? []).map((g) => ({
              id: g.boardGameCopyId,
              name: g.boardGameName,
              description: g.specification,
              status: g.isAvailable ? "Available" : "Unavailable",
              isAvailable: g.isAvailable,
            })) ?? [];
      } catch (e) {
        console.error("Error fetching owned games:", e);
        this.userBoardgames = [];
      }
    },

    /* ---------- Edit flow ---------- */
    startEditing() {
      this.isEditing = true;
      this.clearFieldErrors();
    },

    async saveProfile() {
      if (this.saving) return;
      this.saving = true;
      this.clearFieldErrors();

      try {
        const userId = this.authStore.user.id;
        const emailToVerify = this.editedProfile.email || this.userProfile.email;

        // If the account type (Player/Owner) changed: verify password + flip role on backend
        if (this.accountTypeChanged) {
          if (!this.editedProfile.accountTypePassword) {
            this.fieldErrors.accountTypePassword =
                "Current password is required to change account type.";
            this.saving = false;
            return;
          }

          // 1) Verify current password
          await axiosClient.post("/players/login", {
            email: emailToVerify,
            password: this.editedProfile.accountTypePassword,
          });

          // 2) Flip role explicitly
          await axiosClient.put(
              `/players/${userId}/toggle-owner`,
              null,
              { params: { q: this.editedProfile.isAOwner } }
          );
        }

        // Update name/email (and we pass current password only as proof; NOT changing it)
        const dto = {
          name: this.editedProfile.name,
          email: this.editedProfile.email,
          password: this.editedProfile.accountTypePassword || "", // backend requires non-blank
          isAOwner: this.editedProfile.isAOwner,
        };

        if (!dto.password) {
          // Require current password for safety even if role didn't change
          this.fieldErrors.accountTypePassword = "Enter your current password to save.";
          this.saving = false;
          return;
        }

        await axiosClient.put(`/players/${userId}`, dto);

        // reflect in UI + store
        this.userProfile.name = dto.name;
        this.userProfile.email = dto.email;
        this.userProfile.status = dto.isAOwner ? "owner" : "player";

        this.authStore.user.username = dto.name;
        this.authStore.user.email = dto.email;
        this.authStore.user.isAOwner = dto.isAOwner;

        // reset guards
        this.originalIsAOwner = dto.isAOwner;
        this.editedProfile.accountTypePassword = "";
        this.isEditing = false;

        this.showToast("success", "Profile updated.");
      } catch (e) {
        const status = e?.response?.status;
        if (status === 401) {
          this.fieldErrors.accountTypePassword = "Incorrect password.";
          this.showToast("error", "Incorrect password. Changes were not saved.");
        } else if (status === 404) {
          this.showToast("error", "Account not found. Please re-login.");
        } else {
          this.showToast("error", "Could not save changes. Please try again.");
        }
        console.error("Failed to save profile:", e);
      } finally {
        this.saving = false;
      }
    },

    cancelEditing() {
      this.editedProfile.name = this.userProfile.name;
      this.editedProfile.email = this.userProfile.email;
      this.editedProfile.isAOwner = this.userProfile.status === "owner";
      this.editedProfile.accountTypePassword = "";
      this.clearFieldErrors();
      this.isEditing = false;
    },

    getEventStatusBadgeClass(status) {
      return {
        upcoming: "badge badge--primary",
        ongoing: "badge badge--success",
        completed: "badge",
        cancelled: "badge badge--danger",
      }[status] || "badge";
    },

    formatDate(d) {
      return new Date(d).toLocaleDateString();
    },
  },
};
</script>

<template>
  <!-- Signed-in glossy navbar (no gray bar) -->
  <NavLandingSigned />

  <!-- Canvas -->
  <section class="profile-hero">
    <div class="layer bg-base"></div>
    <div class="layer bg-gradient"></div>
    <div class="layer bg-noise"></div>
    <div class="layer bg-vignette"></div>

    <!-- Toast -->
    <div
        v-if="toast.show"
        class="toast"
        :class="{
        'toast--success': toast.type === 'success',
        'toast--error': toast.type === 'error',
        'toast--info': toast.type === 'info'
      }"
    >
      <span>{{ toast.message }}</span>
      <button class="toast__close" @click="toast.show = false">×</button>
    </div>

    <div class="content">
      <!-- Header card -->
      <div class="header-card">
        <div class="header-left">
          <img
              :src="userProfile.profilePicture || '/default_avatar.jpg'"
              class="avatar"
              alt="Profile Picture"
          />
          <div class="id">
            <h2 class="name">{{ userProfile.name || "—" }}</h2>
            <p class="email">{{ userProfile.email }}</p>
            <span class="role-pill" :data-role="userProfile.status">
              {{ userProfile.status?.toUpperCase() }}
            </span>
          </div>
        </div>

        <div class="header-right">
          <button v-if="!isEditing" class="btn btn-ghost" @click="startEditing">
            Edit Profile
          </button>

          <div v-else class="edit-actions">
            <button class="btn btn-primary" :disabled="saveDisabled" @click="saveProfile">
              <span v-if="!saving">Save</span>
              <span v-else>Saving…</span>
            </button>
            <button class="btn btn-ghost" :disabled="saving" @click="cancelEditing">Cancel</button>
          </div>
        </div>
      </div>

      <!-- Edit panel -->
      <div v-if="isEditing" class="edit-card">
        <div class="grid">
          <label class="field">
            <span class="label">Name</span>
            <input v-model="editedProfile.name" type="text" class="input" />
          </label>

          <label class="field">
            <span class="label">Email</span>
            <input v-model="editedProfile.email" type="email" class="input" />
          </label>

          <label class="field">
            <span class="label">Account Type</span>
            <select v-model="editedProfile.isAOwner" class="input">
              <option :value="false">Player</option>
              <option :value="true">Owner</option>
            </select>
            <small class="hint">
              Changing account type requires your current password.
            </small>
          </label>

          <!-- Only visible if role flips OR when an error exists -->
          <label v-if="accountTypeChanged || fieldErrors.accountTypePassword" class="field">
            <span class="label">
              Current Password <span class="req">*</span>
            </span>
            <input
                v-model="editedProfile.accountTypePassword"
                type="password"
                class="input"
                :class="{ 'input--error': !!fieldErrors.accountTypePassword }"
                placeholder="Enter current password to confirm"
                autocomplete="current-password"
            />
            <small v-if="fieldErrors.accountTypePassword" class="error-msg">
              {{ fieldErrors.accountTypePassword }}
            </small>
          </label>
        </div>

        <div class="edit-actions">
          <button class="btn btn-primary" :disabled="saveDisabled" @click="saveProfile">
            <span v-if="!saving">Save</span>
            <span v-else>Saving…</span>
          </button>
          <button class="btn btn-ghost" :disabled="saving" @click="cancelEditing">Cancel</button>
        </div>
      </div>

      <!-- Tabs -->
      <div class="tabs">
        <button
            v-for="t in tabs"
            :key="t.id"
            class="tab"
            :class="{ 'tab--active': selectedTab === t.id }"
            @click="selectedTab = t.id"
        >
          {{ t.name }}
        </button>
      </div>

      <!-- Board games (owner) -->
      <div v-if="selectedTab === 'boardgames' && userProfile.status === 'owner'">
        <div class="section-head">
          <h3>My Board Games</h3>
        </div>

        <div v-if="userBoardgames.length === 0" class="empty">
          You don't have any board games yet.
        </div>

        <div class="cards">
          <article v-for="g in userBoardgames" :key="g.id" class="card">
            <div class="card-body">
              <h4 class="card-title">{{ g.name }}</h4>
              <p class="card-desc">{{ g.description }}</p>
              <div class="card-foot">
                <span
                    class="badge"
                    :class="g.isAvailable ? 'badge--success' : 'badge--danger'"
                >
                  {{ g.status }}
                </span>
              </div>
            </div>
          </article>
        </div>
      </div>

      <!-- Events -->
      <div v-if="selectedTab === 'events'">
        <div class="section-head">
          <h3>My Events</h3>
        </div>

        <div v-if="userEvents.length === 0" class="empty">
          You don't have any events yet.
        </div>

        <div class="cards cards--half">
          <article v-for="e in userEvents" :key="e.id" class="card">
            <div class="card-body">
              <h4 class="card-title">{{ e.name }}</h4>
              <p class="card-desc">{{ e.description }}</p>
              <div class="meta">
                <span class="muted">📅 {{ formatDate(e.date) }}</span>
                <span class="muted">⏰ {{ e.startTime }} – {{ e.endTime }}</span>
              </div>
              <div class="card-foot">
                <span :class="getEventStatusBadgeClass(e.status)">{{ e.status }}</span>
              </div>
            </div>
          </article>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
/* ===== Canvas (landing/auth look) ===== */
.profile-hero {
  position: relative;
  min-height: 100vh;
  background: #14171d;
}
.layer { position: absolute; inset: 0; }
.bg-base { background: #14171d; }
.bg-gradient {
  background:
      radial-gradient(1100px 750px at 18% 18%, rgba(245,247,250,.28) 0%, rgba(245,247,250,.10) 36%, rgba(245,247,250,0) 65%),
      linear-gradient(180deg, #14171d 0%, #1b2029 100%);
  animation: float 16s ease-in-out infinite alternate;
}
@keyframes float {
  0% { transform: translateY(0) translateX(0) scale(1); }
  100% { transform: translateY(-10px) translateX(6px) scale(1.01); }
}
.bg-noise {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='120' height='120'%3E%3Cfilter id='n'%3E%3CfeTurbulence baseFrequency='0.75' numOctaves='2' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='120' height='120' filter='url(%23n)' opacity='0.02'/%3E%3C/svg%3E");
  background-size: 120px 120px;
  mix-blend-mode: overlay;
}
.bg-vignette {
  background: radial-gradient(85% 70% at 50% 60%, transparent 0%, rgba(0,0,0,.35) 70%, rgba(0,0,0,.7) 100%);
}

/* ===== Toast ===== */
.toast {
  position: fixed;
  top: 18px;
  right: 18px;
  z-index: 2000;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: 12px;
  font-weight: 700;
  background: #151821;
  border: 1px solid #222a39;
  color: #e9edf5;
  box-shadow: 0 10px 24px rgba(0,0,0,.35);
  animation: toastIn .18s ease-out;
}
@keyframes toastIn { from { opacity: 0; transform: translateY(-8px); } to { opacity:1; transform: translateY(0);} }
.toast--success { background:#142319; border-color:#225232; color:#dbffe6; }
.toast--error   { background:#2b1618; border-color:#5a2a2f; color:#ffd6db; }
.toast--info    { background:#171f2d; border-color:#2a3854; color:#e6f0ff; }
.toast__close {
  appearance: none; border: none; background: transparent;
  color: inherit; font-size: 18px; font-weight: 900; cursor: pointer; line-height: 1;
}

/* ===== Content container ===== */
.content {
  position: relative; z-index: 2;
  max-width: 1100px; margin: 0 auto; padding: 96px 20px 60px;
  color: #e9edf5;
}

/* ===== Header card ===== */
.header-card {
  display: flex; align-items: center; justify-content: space-between;
  background: #0f1217; border: 1px solid #1f2533; border-radius: 16px;
  padding: 18px 20px; box-shadow: 0 10px 30px rgba(0,0,0,.35);
}
.header-left { display: flex; align-items: center; gap: 14px; }
.avatar {
  width: 72px; height: 72px; border-radius: 50%;
  object-fit: cover; background: #0c0f13; border: 1px solid #202636;
}
.id .name { margin: 0 0 4px; font-size: 24px; font-weight: 900; letter-spacing: .2px; }
.id .email { margin: 0; color: #b6becc; font-size: 14px; }
.role-pill {
  display: inline-block; margin-top: 8px; padding: 6px 10px; border-radius: 999px;
  font-size: 11px; font-weight: 800; letter-spacing: .4px; text-transform: uppercase;
  background: #1a2130; border: 1px solid #2a3242; color: #e9edf5;
}
.role-pill[data-role="owner"] { background:#16241a; border-color:#20462b; }
.header-right { display: flex; gap: 10px; }

/* ===== Buttons ===== */
.btn {
  padding: .7rem 1rem; border-radius: 12px; font-weight: 900; letter-spacing: .25px;
  transition: transform .18s ease, box-shadow .18s ease, filter .18s ease, background .18s ease, color .18s ease, border-color .18s ease;
  border: 1px solid transparent;
}
.btn-ghost { background: #171b22; color: #e9edf5; border-color:#2a3242; }
.btn-ghost:hover { transform: translateY(-2px); filter: brightness(1.02); }
.btn-primary {
  background: #ffffff; color:#0b0d10; border:1px solid rgba(0,0,0,.08);
  box-shadow: 0 6px 16px rgba(0,0,0,.14);
}
.btn-primary:hover { transform: translateY(-2px); filter:brightness(1.03); }

/* ===== Edit card ===== */
.edit-card {
  margin-top: 14px;
  background: #0f1217; border: 1px solid #1f2533; border-radius: 16px;
  padding: 16px 16px 6px; box-shadow: 0 10px 30px rgba(0,0,0,.35);
}
.grid { display: grid; grid-template-columns: repeat(2, minmax(0,1fr)); gap: 14px; }
.field { display: flex; flex-direction: column; gap: 6px; }
.label { font-size: 12px; font-weight: 800; color:#c8cfdb; }
.input {
  background:#0c0f13; color:#e7ebf3; border:1px solid #202636; border-radius:12px;
  padding:.75rem .9rem; outline:none; transition:border-color .2s, box-shadow .2s, background .2s;
}
.input:focus { border-color:#3a4256; box-shadow: 0 0 0 3px rgba(70,100,255,.12); background:#0d1116; }
.hint { color:#93a0b4; font-size: 12px; margin-top: 2px; }

/* inline errors */
.input--error { border-color: #b84b57 !important; box-shadow: 0 0 0 3px rgba(184,75,87,.18); }
.error-msg { color: #ffb3bb; font-size: 12px; margin-top: 2px; }
.req { color: #ff919c; }

/* ===== Tabs ===== */
.tabs { margin: 18px 0 10px; display:flex; gap:10px; flex-wrap:wrap; }
.tab {
  padding: .6rem .9rem; border-radius: 999px; font-weight: 800; font-size: 13px;
  color:#dbe1ed; background:#151821; border:1px solid #222a39; cursor:pointer;
  transition: transform .15s ease, background .2s ease, border-color .2s ease;
}
.tab--active { background:#1f2533; border-color:#2d3647; color:#fff; transform: translateY(-1px); }

/* ===== Sections ===== */
.section-head { display:flex; align-items:center; justify-content:space-between; margin:10px 0 12px; }
.section-head h3 { margin:0; font-size:20px; font-weight:900; letter-spacing:.2px; }
.empty { color:#9aa2b2; text-align:center; margin: 22px 0; }

/* ===== Cards grid ===== */
.cards { display:grid; grid-template-columns: repeat(3, minmax(0,1fr)); gap:14px; }
.cards--half { grid-template-columns: repeat(2, minmax(0,1fr)); }
@media (max-width: 900px) {
  .grid { grid-template-columns: 1fr; }
  .cards, .cards--half { grid-template-columns: 1fr; }
}

/* ===== Card ===== */
.card {
  background:#0f1217; border:1px solid #1f2533; border-radius:16px;
  box-shadow: 0 10px 30px rgba(0,0,0,.35);
  transition: transform .25s cubic-bezier(.2,.8,.2,1), box-shadow .25s;
}
.card:hover { transform: translateY(-6px); box-shadow: 0 16px 40px rgba(0,0,0,.38); }
.card-body { padding: 16px 16px 14px; }
.card-title { margin:0 0 6px; font-size:16px; font-weight:900; }
.card-desc { margin:0 0 10px; color:#c7cddd; }
.meta { display:flex; gap:12px; margin-bottom: 10px; }
.muted { color:#aab3c3; font-size:13px; }
.card-foot { display:flex; justify-content:flex-end; }

/* ===== Badges ===== */
.badge {
  display:inline-flex; align-items:center; justify-content:center;
  padding: 6px 10px; border-radius: 999px; font-size: 12px; font-weight: 800;
  background:#1a2130; border:1px solid #2a3242; color:#e9edf5;
}
.badge--success { background:#142319; border-color:#225232; color:#dbffe6; }
.badge--danger  { background:#2b1618; border-color:#5a2a2f; color:#ffd6db; }
.badge--primary { background:#171f2d; border-color:#2a3854; color:#e6f0ff; }
</style>
