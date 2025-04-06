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
                  <label for="nameInput" class="form-label">Name</label>
                  <input
                    type="text"
                    class="form-control"
                    v-model="editedProfile.name"
                    placeholder="Name"
                  />
                </div>
                <div class="mb-3">
                  <label for="emailInput" class="form-label">Email</label>
                  <input
                    type="email"
                    class="form-control"
                    v-model="editedProfile.email"
                    placeholder="Email"
                  />
                </div>
                <div class="mb-3">
                  <label for="passwordInput" class="form-label">Password</label>
                  <input
                    type="password"
                    class="form-control"
                    v-model="editedProfile.password"
                    placeholder="confirm/alter password"
                  />
                </div>
                <div class="mb-3">
                  <label for="roleSelect" class="form-label">Account Type</label>
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
            style="color: black !important;"
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
          <div class="d-flex justify-content-between mb-4">
            <h3>My Board Games</h3>
            <!-- Removed the Add New Game button -->
          </div>
          <div class="row">
            <div v-if="userBoardgames.length === 0" class="col-12 text-center">
              <p class="text-muted">You don't have any board games yet.</p>
            </div>
            <div v-for="game in userBoardgames" :key="game.id" class="col-md-4 mb-4">
              <div class="card h-100">
                <div class="card-body">
                  <h5 class="card-title">{{ game.name }}</h5>
                  <p class="card-text">{{ game.description }}</p>
                  <div class="d-flex justify-content-between align-items-center">
                    <span class="badge" :class="game.isAvailable ? 'bg-success' : 'bg-danger'">
                      {{ game.status }}
                    </span>
                   <!-- <button class="btn btn-sm btn-outline-primary">View Details</button> -->
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>


        <div v-if="selectedTab === 'events'" class="tab-pane fade show active">
          <div class="d-flex justify-content-between mb-4">
            <h3>My Events</h3>
          </div>
          <div class="row">
            <div v-if="userBoardgames.length === 0" class="col-12 text-center">
              <p class="text-muted">You don't have any events yet.</p>
            </div>
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
      try {
        console.log("Fetching events for user:", userId);
        // First, get all registrations for this user
        const registrationsRes = await axiosClient.get(`/registrations/players/${userId}`);
        console.log("Registrations API response:", registrationsRes.data);
        
        if (!registrationsRes.data || registrationsRes.data.length === 0) {
          console.log("No registrations found for this user");
          this.userEvents = [];
          return;
        }
        
        // For each registration, fetch the full event details
        const events = [];
        for (const registration of registrationsRes.data) {
          const eventId = registration.eventID;
          try {
            const eventRes = await axiosClient.get(`/events/${eventId}`);
            console.log("Event data:", eventRes.data);
            
            events.push({
              id: eventRes.data.eventID,
              name: eventRes.data.name,
              description: eventRes.data.description,
              date: eventRes.data.eventDate,
              startTime: eventRes.data.startTime,
              endTime: eventRes.data.endTime,
              status: this.determineEventStatus(eventRes.data),
              maxSpot: eventRes.data.maxSpot
            });
          } catch (error) {
            console.error(`Error fetching event ${eventId}:`, error);
          }
        }
        
        this.userEvents = events;
      } catch (error) {
        console.error("Error fetching user events:", error);
        this.userEvents = [];
      }
    },
    determineEventStatus(event) {
      const eventDate = new Date(event.eventDate);
      const now = new Date();
      const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
      
      if (eventDate < today) {
        return "completed";
      } else if (eventDate.getTime() === today.getTime()) {
        return "ongoing";
      } else {
        return "upcoming";
      }
    },
    async fetchOwnedGames(userId) {
      try {
        console.log("Fetching board games for owner:", userId);
        const res = await axiosClient.get(`/boardgamecopies/byplayer/${userId}`);
        console.log("Board games API response:", res.data);
        
        if (!res.data || res.data.length === 0) {
          console.log("No board games found for this user");
          this.userBoardgames = [];
          return;
        }
        
        this.userBoardgames = res.data.map(gameCopy => {
          return {
            id: gameCopy.boardGameCopyId,
            name: gameCopy.boardGameName,
            description: gameCopy.specification,
            status: gameCopy.isAvailable ? "Available" : "Unavailable",
            isAvailable: gameCopy.isAvailable
          };
        });
      } catch (error) {
        console.error("Error fetching owned games:", error);
        this.userBoardgames = [];
      }
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
        await axiosClient.put(`/players/${this.authStore.user.id}`, dto);
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

.nav-link {
  cursor: pointer;
  margin-bottom: 5px;
  padding: 10px;
  text-align: center;
  color: white !important;
}
.bg-secondary {
  background-color: grey !important;
}
.bg-success {
  background-color: green !important;
}

/* Input, Textarea, and Select Styles */
/* General input styles */
input[type="text"],
input[type="number"],
input[type="date"],
input[type="time"],
input[type="email"],
input[type="password"],
textarea,
select {
  width: 100%;
  padding: 0.5rem 0.75rem;
  font-size: 1rem;
  color: #495057;
  background-color: #fff;
  border: 1.5px solid #ced4da;
  border-radius: 0.5rem;
  transition: border-color 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
}

/* Glow on focus */
input[type="text"]:focus,
input[type="number"]:focus,
input[type="date"]:focus,
input[type="time"]:focus,
input[type="email"]:focus,
input[type="password"]:focus,
textarea:focus,
select:focus {
  border-color: #28a745;
  outline: 0;
  box-shadow: 0 0 0 0.2rem rgba(40, 167, 69, 0.25); /* Green glow */
}

/* Custom dropdown arrow */
select {
  background-image: url("data:image/svg+xml;charset=UTF-8,%3Csvg%20fill%3D'%23333'%20height%3D'24'%20viewBox%3D'0%200%2024%2024'%20width%3D'24'%20xmlns%3D'http%3A//www.w3.org/2000/svg'%3E%3Cpath%20d%3D'M7%2010l5%205%205-5z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  background-size: 1rem;
  padding-right: 2rem; /* Make space for the arrow */
}

/* Remove inner arrows in time input (Chrome) */
input[type="time"]::-webkit-inner-spin-button,
input[type="time"]::-webkit-calendar-picker-indicator,
select::-ms-expand {
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
}

/* Label spacing */
label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #343a40;
}
</style>
