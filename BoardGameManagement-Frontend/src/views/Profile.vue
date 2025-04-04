<template>
  <div>
    <DefaultNavbar />

    <div class="container-fluid mt-4">
      <div class="row mb-4">
        <div class="col-md-3 text-center">
          <img
            :src="userProfile.profilePicture || '/default_avatar.jpg'"
            class="rounded-circle"
            style="width: 150px; height: 150px; object-fit: cover;"
            alt="Profile Picture"
          />
        </div>
        <div class="col-md-9">
          <div class="d-flex justify-content-between align-items-start">
            <div>
              <template v-if="!isEditing">
                <h2>{{ userProfile.name }}</h2>
                <p class="text-muted">{{ userProfile.email }}</p>
                <span class="badge bg-success">{{ userProfile.status.toUpperCase() }}</span>
              </template>
              <template v-else>
                <div class="mb-3">
                  <input
                    type="text"
                    class="form-control"
                    v-model="editedProfile.name"
                    placeholder="Name"
                  />
                </div>
                <div class="mb-3">
                  <input
                    type="email"
                    class="form-control"
                    v-model="editedProfile.email"
                    placeholder="Email"
                  />
                </div>
                <div class="mb-3">
                  <input
                    type="password"
                    class="form-control"
                    v-model="editedProfile.password"
                    placeholder="Password"
                  />
                </div>
                <div class="mb-3">
                  <select v-model="editedProfile.isAOwner" class="form-control">
                    <option :value="false">Player</option>
                    <option :value="true">Owner</option>
                  </select>
                </div>
              </template>
            </div>
            <div>
              <template v-if="!isEditing">
                <button class="btn btn-outline-primary" @click="startEditing">
                  <i class="fas fa-edit"></i> Edit Profile
                </button>
              </template>
              <template v-else>
                <button class="btn btn-success me-2" @click="saveProfile">
                  <i class="fas fa-save"></i> Save
                </button>
                <button class="btn btn-secondary" @click="cancelEditing">
                  <i class="fas fa-times"></i> Cancel
                </button>
              </template>
            </div>
          </div>
        </div>
      </div>

      <ul class="nav nav-tabs mb-4">
        <li class="nav-item" v-for="tab in tabs" :key="tab.id">
          <a
            class="nav-link"
            :class="{ active: selectedTab === tab.id }"
            href="#"
            @click.prevent="selectedTab = tab.id"
          >
            {{ tab.name }}
          </a>
        </li>
      </ul>

      <div class="tab-content">
        <div
          v-if="selectedTab === 'boardgames' && userProfile.status === 'owner'"
          class="tab-pane fade show active"
        >
          <div class="row">
            <div v-for="game in userBoardgames" :key="game.id" class="col-md-4 mb-4">
              <div class="card">
                <div class="card-body">
                  <h5 class="card-title">{{ game.name }}</h5>
                  <p class="card-text">{{ game.description }}</p>
                  <div class="d-flex justify-content-between align-items-center">
                    <span class="badge bg-info">{{ game.status }}</span>
                    <button class="btn btn-sm btn-outline-primary">View Details</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="selectedTab === 'events'" class="tab-pane fade show active">
          <div class="row">
            <div v-for="event in userEvents" :key="event.id" class="col-md-6 mb-4">
              <div class="card">
                <div class="card-body">
                  <h5 class="card-title">{{ event.name }}</h5>
                  <p class="card-text">{{ event.description }}</p>
                  <div class="d-flex justify-content-between align-items-center">
                    <div>
                      <small class="text-muted">
                        <i class="fas fa-calendar"></i> {{ formatDate(event.date) }}
                      </small>
                      <br />
                      <small class="text-muted">
                        <i class="fas fa-clock"></i> {{ event.startTime }} - {{ event.endTime }}
                      </small>
                    </div>
                    <span class="badge" :class="getEventStatusBadgeClass(event.status)">
                      {{ event.status }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import DefaultNavbar from "@/examples/navbars/NavbarDefault.vue";
import { useAuthStore } from "@/stores/authStore";
import axios from "axios";

const axiosClient = axios.create({
  baseURL: "http://localhost:8080",
});

export default {
  name: "UserProfileView",
  components: {
    DefaultNavbar,
  },
  data() {
    return {
      authStore: null,
      isEditing: false,
      editedProfile: {
        name: "",
        email: "",
        password: "",
        isAOwner: false,
      },
      _selectedTab: "events",
      userProfile: {
        name: "",
        email: "",
        status: "",
        profilePicture: null,
        id: null,
      },
      userBoardgames: [],
      userEvents: [],
    };
  },
  computed: {
    tabs() {
      if (this.userProfile.status === "owner") {
        return [
          { id: "boardgames", name: "My Boardgames" },
          { id: "events", name: "Events" },
        ];
      } else {
        return [{ id: "events", name: "Events" }];
      }
    },
    selectedTab: {
      get() {
        return this._selectedTab;
      },
      set(value) {
        this._selectedTab = value;
      },
    },
  },
  created() {
    this.authStore = useAuthStore();
    this.fetchUserData();
    this._selectedTab = this.authStore.user.isAOwner ? "boardgames" : "events";
  },
  methods: {
    async fetchUserData() {
      try {
        const userId = this.authStore.user.id;
        const response = await axiosClient.get(`/players/${userId}`);
        this.userProfile = {
          name: response.data.name,
          email: response.data.email,
          status: response.data.isAOwner ? "owner" : "player",
          profilePicture: null,
          id: response.data.id,
        };
        this.editedProfile = {
          name: response.data.name,
          email: response.data.email,
          password: "",
          isAOwner: response.data.isAOwner,
        };
        await this.fetchUserEvents(userId);
        if (response.data.isAOwner) {
          await this.fetchOwnedGames(userId);
        }
      } catch (error) {
        console.error("Error fetching user data:", error);
      }
    },
    async fetchUserEvents(userId) {
      const res = await axiosClient.get(`/registrations/players/${userId}`);
      this.userEvents = res.data;
    },
    async fetchOwnedGames(userId) {
      const res = await axiosClient.get(`/boardgamecopies/byplayer/${userId}`);
      this.userBoardgames = res.data;
    },
    startEditing() {
      this.isEditing = true;
    },
    async saveProfile() {
      try {
        const dto = {
          name: this.editedProfile.name,
          email: this.editedProfile.email,
          password: this.editedProfile.password,
          isAOwner: this.editedProfile.isAOwner,
        };
        await axiosClient.put(`/players/${this.userProfile.id}`, dto);
        this.userProfile.name = dto.name;
        this.userProfile.email = dto.email;
        this.userProfile.status = dto.isAOwner ? "owner" : "player";
        this.authStore.user.username = dto.name;
        this.authStore.user.email = dto.email;
        this.authStore.user.isAOwner = dto.isAOwner;
        this.isEditing = false;
        this.editedProfile.password = "";
      } catch (error) {
        console.error("Failed to update profile:", error);
      }
    },
    cancelEditing() {
      this.editedProfile.name = this.userProfile.name;
      this.editedProfile.email = this.userProfile.email;
      this.editedProfile.password = "";
      this.editedProfile.isAOwner = this.userProfile.status === "owner";
      this.isEditing = false;
    },
    getEventStatusBadgeClass(status) {
      const classes = {
        upcoming: "bg-primary",
        ongoing: "bg-success",
        completed: "bg-secondary",
        cancelled: "bg-danger",
      };
      return classes[status] || "bg-secondary";
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString();
    },
  },
};
</script>

<style scoped>
.nav-tabs .nav-link {
  color: #495057;
  border: none;
  border-bottom: 2px solid transparent;
  padding: 0.5rem 1rem;
}
.nav-tabs .nav-link.active {
  color: #0d6efd;
  border-bottom: 2px solid #0d6efd;
  background: none;
}
.card {
  transition: transform 0.2s;
}
.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
.badge {
  font-size: 0.8rem;
  padding: 0.5em 0.8em;
}
</style>
