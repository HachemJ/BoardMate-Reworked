<script setup>

import DefaultNavbar from "@/examples/navbars/NavbarDefault.vue";
import axios from "axios";
import {computed, onMounted, ref} from "vue";
import {useAuthStore} from "@/stores/authStore.js";

const axiosClient = axios.create({
  baseURL: "http://localhost:8080"
});

const filteredGames = computed(() => {
  return boardGames.value.filter(game =>
      game.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

const authStore = useAuthStore();
const tabs = ['View All Board Games', 'My Board Game Copies']
const selectedTab = ref(tabs[0])
const searchQuery = ref("");
const boardGames = ref([]);
const myBoardGameCopies = ref([]);


async function fetchBoardGames(){
  try {
    const response = await axiosClient.get("/boardgames");
    boardGames.value = response.data;

    const response2 = await axiosClient.get("/boardgamecopies/byplayer/" + authStore.user.id);
    myBoardGameCopies.value = response2.data;
  } catch (error) {
    console.error(error);
  }
}

onMounted(async () => {
  await fetchBoardGames()
})

async function deleteBoardGameCopy(id) {
  try {
  await axiosClient.delete(`boardgamecopies/${id}`);
  console.log("Deleting board game copy with id: " + id);
  alert("Board game copy deleted!");
  await fetchBoardGames();
  }catch(error){
    console.log(error)
    const errors = error.response.data.errors; // Extract the errors array
    alert(`Error with status ${error.response.status} :\n${errors.join("\n")}`);
  }
}

</script>

<template>
  <div>
    <!-- Top Navigation Bar -->
    <DefaultNavbar />

    <div class="container-fluid mt-4">
      <div class="row">

        <!-- Left Sidebar: Tabs -->
        <div class="col-md-3">
          <ul class="nav flex-column nav-pills">
            <li v-for="(tab, index) in tabs" :key="index" class="nav-item">
              <a
                  href="#"
                  class="nav-link"
                  :class="{'active bg-success': selectedTab === tab, 'bg-secondary': selectedTab !== tab}"
                  @click.prevent="selectedTab = tab"
              >
                {{ tab }}
              </a>
            </li>
          </ul>
        </div>

        <!-- Right Content Area -->
        <div class="col-md-9">

          <div v-if="selectedTab === 'View All Board Games'">

            <!-- Page Title and Add Board Game Button-->
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h2 class="mb-0">Search and Browse Board Games</h2>
              <router-link :to="{ name: 'playerAddBoardGame' }">
                <button class="btn btn-info">Add Board Game</button>
              </router-link>
              <router-link :to="{ name: 'ownerUpdateBoardGame' }">
                <button class="btn btn-info">Update Board Game</button>
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
              <small> Notice: Click on the board game name to view more details</small>
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
                    <router-link :to="{ name: 'ownerBoardGameDetail', params: { gamename: game.name }}">
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

          <div v-if="selectedTab === 'My Board Game Copies'">
            <div class="d-flex justify-content-between align-items-center">
              <h2>My Collection</h2>
              <br>
              <br>
              <router-link :to="{ name: 'addBoardGameCopy' }">
                <button class="btn btn-info">Add Board Game Copy</button>
              </router-link>
              <router-link :to="{ name: 'ownerUpdateBoardGameCopy' }">
                <button class="btn btn-info">Update Board Game Copy</button>
              </router-link>
            </div>
            <table class="table table-responsive-ms">
              <thead>
              <tr>
                <th>Game Name</th>
                <th>Specification</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="game in myBoardGameCopies" :key="game.boardGameCopyId">
                <td>{{ game.boardGameName }}</td>
                <td class="specification-cell">{{ game.specification }}</td>
                <td class="button-cell">
                  <button class="btn btn-info me-2"
                          @click="deleteBoardGameCopy(game.boardGameCopyId)"
                  >
                    Delete
                  </button>
                </td>
              </tr>
              </tbody>
            </table>
            <small v-if="myBoardGameCopies.length > 0" class="d-flex justify-content-center">
              Notice: deleting the board game copy will delete all borrow requests related to it
            </small>
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

/* Board game table style */

table {
  width: 110%;
}

.table td a {
  color: #0d6efd;
  text-decoration: none;
}

.table td a:hover {
  text-decoration: underline;
  color: darkblue;
}

.table td {
  padding-right: 5px;
  padding-left: 5px;
}

.specification-cell {
  word-wrap: break-word;
  white-space: normal;
  max-width: 200px;
  overflow-wrap: break-word;
}

/* === Clean and aligned table layout === */
.table {
  table-layout: fixed;
  width: 100%;
  border-collapse: collapse;
}

/* Header and cell alignment */
.table th,
.table td {
  vertical-align: middle;
  text-align: left;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding: 12px;
  border-bottom: 1px solid #dee2e6;
}

/* Optional row hover effect */
.table tbody tr:hover {
  background-color: #f8f9fa;
  cursor: pointer;
}

/* Clean router-links inside the table */
.table td a {
  color: #0d6efd;
  display: inline-block;
  width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-decoration: none;
}

.table td a:hover {
  text-decoration: underline;
  color: darkblue;
}

/* Column widths for Game Name, Min Players, Max Players */
.table th:nth-child(1),
.table td:nth-child(1) {
  width: 50%;
}

.table th:nth-child(2),
.table td:nth-child(2) {
  width: 25%;
}

.table th:nth-child(3),
.table td:nth-child(3) {
  width: 25%;
}


</style>
