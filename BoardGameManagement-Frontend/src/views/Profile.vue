<template>
  <div>
    <DefaultNavbar />

    <div class="container-fluid mt-4">
      <div class="row mb-4">
        <div class="col-md-3 text-center">
          <img
            :src="userProfile.profilePicture || '/default-avatar.png'"
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
          <div class="d-flex align-items-center mb-2">
            <span class="badge" :class="userProfile.status === 'owner' ? 'bg-primary' : 'bg-success'">
              {{ userProfile.status }}
            </span>
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

        <div
          v-if="selectedTab === 'borrow' && userProfile.status === 'owner'"
          class="tab-pane fade show active"
        >
          <div class="table-responsive">
            <table class="table">
              <thead>
                <tr>
                  <th>Game</th>
                  <th>Status</th>
                  <th>Request Date</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="request in borrowRequests" :key="request.id">
                  <td>{{ request.gameName }}</td>
                  <td>
                    <span class="badge" :class="getStatusBadgeClass(request.status)">
                      {{ request.status }}
                    </span>
                  </td>
                  <td>{{ formatDate(request.requestDate) }}</td>
                  <td>
                    <button
                      v-if="request.status === 'pending'"
                      class="btn btn-sm btn-danger"
                      @click="cancelRequest(request.id)"
                    >
                      Cancel
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div
          v-if="selectedTab === 'borrowed'"
          class="tab-pane fade show active"
        >
          <div class="table-responsive">
            <table class="table">
              <thead>
                <tr>
                  <th>Game</th>
                  <th>Borrowed Date</th>
                  <th>Return Date</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="game in borrowedGames" :key="game.id">
                  <td>{{ game.name }}</td>
                  <td>{{ formatDate(game.borrowedDate) }}</td>
                  <td>{{ formatDate(game.returnDate) }}</td>
                  <td>
                    <span class="badge" :class="getStatusBadgeClass(game.status)">
                      {{ game.status }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div
          v-if="selectedTab === 'events'"
          class="tab-pane fade show active"
        >
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

export default {
  name: "UserProfileView",
  components: {
    DefaultNavbar,
  },
  setup() {
    const authStore = useAuthStore();
    return { authStore };
  },
  data() {
    return {
      isEditing: false,
      editedProfile: {
        name: "",
        email: "",
      },
      _selectedTab: "borrowed",
      userBoardgames: [
        {
          id: 1,
          name: "Catan",
          description: "A strategy board game",
          status: "Available",
        },
        {
          id: 2,
          name: "Go",
          description: "An interesting board game",
          status: "Available",
        },
      ],
      borrowRequests: [
        {
          id: 1,
          gameName: "Monopoly",
          status: "pending",
          requestDate: "2024-03-27",
        },
      ],
      userEvents: [
        {
          id: 1,
          name: "Game Night",
          description: "Monthly board game gathering",
          date: "2024-04-01",
          startTime: "18:00",
          endTime: "22:00",
          status: "upcoming",
        },
      ],
      borrowedGames: [
        {
          id: 1,
          name: "Risk",
          borrowedDate: "2024-03-20",
          returnDate: "2024-04-20",
          status: "active",
        },
      ],
    };
  },
  computed: {
    userProfile() {
      return {
        name: this.authStore.user.username,
        email: this.authStore.user.email,
        status: this.authStore.user.isAOwner ? "owner" : "player",
        profilePicture: null,
      };
    },
    tabs() {
      if (this.userProfile.status === "owner") {
        return [
          { id: "boardgames", name: "My Boardgames" },
          { id: "borrow", name: "Borrow Request" },
          { id: "borrowed", name: "Borrowed Games" },
          { id: "events", name: "Events" },
        ];
      } else {
        return [
          { id: "borrowed", name: "Borrowed Games" },
          { id: "events", name: "Events" },
        ];
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
    this._selectedTab = this.userProfile.status === "owner" ? "boardgames" : "borrowed";
  },
  methods: {
    startEditing() {
      this.editedProfile = {
        name: this.userProfile.name,
        email: this.userProfile.email,
      };
      this.isEditing = true;
    },
    saveProfile() {
      this.authStore.user.username = this.editedProfile.name;
      this.authStore.user.email = this.editedProfile.email;
      this.isEditing = false;
    },
    cancelEditing() {
      this.isEditing = false;
    },
    getStatusBadgeClass(status) {
      const classes = {
        pending: "bg-warning",
        approved: "bg-success",
        rejected: "bg-danger",
        completed: "bg-info",
      };
      return classes[status] || "bg-secondary";
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
    cancelRequest(requestId) {
      console.log("Cancelling request:", requestId);
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
