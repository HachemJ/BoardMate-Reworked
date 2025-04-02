<script setup>

import axios from "axios";
import DefaultNavbar from "@/examples/navbars/NavbarDefault.vue";
import {ref, computed, onMounted} from "vue";

const axiosClient = axios.create({
  baseURL: "http://localhost:8080"
});

const filteredGames = computed(() => {
  return boardGames.value.filter(game =>
      game.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

const searchQuery = ref("");
const boardGames = ref([]);

onMounted(async () => {
  try {
    const response = await axiosClient.get("/boardgames");
    boardGames.value = response.data;
  } catch (error) {
    console.error(error);
  }
})

</script>

<template>
  <div>
    <!-- Top Navigation Bar -->
    <DefaultNavbar />

    <div class="container-fluid mt-4">
      <div class="row">

        <!-- Content Area -->
        <div class="col-md-9">

          <!-- Page Title and Add Board Game Button-->
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h2 class="mb-0">Search and Browse Board Games</h2>
            <router-link :to="{ name: 'playerAddBoardGame' }">
              <button class="btn btn-primary">Add Board Game</button>
            </router-link>
          </div>

          <!-- Search Bar -->
          <div class="mb-3">
            <input
                v-model="searchQuery"
                type="text"
                class="form-control"
                placeholder="Search..."
            />
          </div>

          <!-- Board Game Table -->
          <div>
            <table class="table">
            <thead>
              <tr>
                <th>Game Name</th>
                <th>Minimum Number of Player</th>
                <th>Maximum Number of Player</th>
              </tr>
            </thead>
              <tbody>
                <tr v-for="game in filteredGames" :key="game.name">
                  <td>
                    <router-link :to="{ name: 'playerBoardGameDetail', params: { gamename: game.name }}">
                      {{ game.name }}
                    </router-link>
                  </td>
                  <td>{{ game.minPlayers }}</td>
                  <td>{{ game.maxPlayers }}</td>
                </tr>
              </tbody>
            </table>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

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
input[type="text"],
input[type="number"],
input[type="date"],
input[type="time"],
textarea,
select {  /* Added select here */
  border: 2px solid #ced4da; /* Bootstrap's default border color for inputs */
  border-radius: 0.25rem; /* Bootstrap's default border-radius for inputs */
  padding: 0.375rem 0.75rem; /* Bootstrap's default padding for inputs */
}

input[type="text"]:focus,
input[type="number"]:focus,
input[type="date"]:focus,
input[type="time"]:focus,
textarea:focus,
select:focus {  /* Added select here */
  border-color: #80bdff; /* Bootstrap's default focus border color */
  outline: 0;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25); /* Bootstrap's default focus shadow */
}

/* Label Styles */
label {
  display: block;
  margin-bottom: 0.5rem;
  color: #495057; /* Bootstrap's default text color for labels */
}

/* Button Styles */
button.btn {
  margin-top: 1rem; /* Add some top margin for the button */
}

/* Board game name style in the table */
.table td a {
  font-size: 20px;
  color: blue;
  text-decoration: none;
}

.table td a:hover {
  text-decoration: underline;
  color: darkblue;
}

table {
  width: 160%;
}

</style>
