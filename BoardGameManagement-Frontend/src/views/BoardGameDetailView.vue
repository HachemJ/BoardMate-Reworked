<script setup>

import DefaultNavbar from "@/examples/navbars/NavbarDefault.vue";
import {ref, computed, onMounted, reactive} from "vue";
import {useRoute, useRouter} from "vue-router";
import axios from "axios";
import {useAuthStore} from "@/stores/authStore.js";

const axiosClient = axios.create({
  baseURL: "http://localhost:8080"
});

const authStore = useAuthStore();
const reviews = ref([]);
const boardGameCopies = ref([]);
const selectedGameId = ref(null);
const tabs = ['Board Game Copies', 'Reviews'];
const selectedTab = ref(tabs[0]);
const borrowRequestData = reactive({
  startOfLoan: "",
  endOfLoan: "",
  borrowerID: authStore.user.id,
  specificGameID: "",
});
const gameDetails = ref({
  minPlayers: "",
  maxPlayers: "",
  description: "",
});

async function fetchBoardGameID() {
  const gameName = route.params.gamename;
  //console.log("Game name from route:", gameName); // Log the game name
  try {
    const response = await axiosClient.get("/boardgames");
    //console.log("Response data:", response.data); // Log the response data
    const game = response.data.find(game => game.name === gameName);
    if (game) {
      console.log("Gaaaaame ID:", game.gameID); // Log the game ID
      return game.gameID;
    }
    return null; // Return null if game ID is not found
  } catch (error) {
    console.error("Error fetching game ID:", error);
  }
}

onMounted(async () => {
  try {
    const gameId = await fetchBoardGameID();
    const response = await axiosClient.get("/boardgamecopies/byboardgame/" + gameId);
    boardGameCopies.value = response.data;

    const response2 = await axiosClient.get("/reviews/byboardgame/" + gameId);
    console.log("Reviews:", response2.data); // Log the reviews data
    reviews.value = response2.data;

    const response3 = await axiosClient.get("/boardgames/" + gameId);
    gameDetails.value = response3.data;
  } catch (error) {
    console.error(error);
  }
})
const router = useRouter();
const route = useRoute();
const gameName = route.params.gamename;

const reviewRoute = computed(() => {
  const pathSegments = route.path.split("/");
  console.log("Path segments:", pathSegments);

  const roleSegment = pathSegments[2]; // Extract "playerboardgame" or "ownerboardgame"
  console.log("Role segment:", roleSegment);

  return roleSegment === "playerboardgame" ? "playerAddReview" : "ownerAddReview";
});

function selectGame(gameId) {
  selectedGameId.value = gameId;
}

async function confirmBorrow() {
  if (!selectedGameId.value) {
    alert("Please select a board game copy first.");
    return;
  }
  try {
    borrowRequestData.specificGameID = selectedGameId.value;
    const response = await axiosClient.post("/borrowrequests", borrowRequestData);

    selectedGameId.value = null;


    alert(`Borrow request sent for board game copy!`);
  }catch (error){
    console.log("Error: ", error);
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

        <!-- Content Area -->
        <div class="col-md-12">
          <!-- Page Title -->
          <h2 class="mb-3 text-center">{{ gameName }}</h2>

          <!-- Game Details -->
          <div class="mb-4 text-center">
            <p><strong>Minimum Players:</strong> {{ gameDetails.minPlayers }}</p>
            <p><strong>Maximum Players:</strong> {{ gameDetails.maxPlayers }}</p>
            <p><strong>Description:</strong> {{ gameDetails.description }}</p>
          </div>

          <!-- Tabs -->
          <ul class="nav nav-tabs mb-4">
            <li class="nav-item" v-for="(tab, index) in tabs" :key="index">
              <a
                  class="nav-link"
                  :class="{ active: selectedTab === tab }"
                  href="#"
                  @click.prevent="selectedTab = tab"
              >
                {{ tab }}
              </a>
            </li>
          </ul>

          <!-- Tab Content -->
          <div v-if="selectedTab === 'Board Game Copies'">
            <!-- First Table -->
            <div class="mb-4">
              <h4 class="text-center">Board Game Copies</h4>
              <table class="table">
                <thead>
                <tr>
                  <th>Copy Number</th>
                  <th>Specification</th>
                  <th>Owner</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="(game, index) in boardGameCopies"
                    :key="game.boardGameCopyId"
                    :class="{ 'table-active': selectedGameId === game.boardGameCopyId }"
                    @click="selectGame(game.boardGameCopyId)"
                    style="cursor: pointer;">
                  <td>{{ index + 1 }}</td>
                  <td>{{ game.specification }}</td>
                  <td>{{ game.playerName }}</td>
                </tr>
                </tbody>
              </table>
            </div>

            <!-- Borrow Button -->
            <div class="text-center"
                 v-if="selectedGameId"
            >
              <h6>Select Borrow Dates</h6>
              <div class="d-flex justify-content-center align-items-center gap-3">

                <!-- Start Date -->
                <div class="d-flex flex-column">
                  <label for="start-date" class="form-label">Start Date</label>
                  <input type="date" id="start-date" v-model="borrowRequestData.startOfLoan" class="form-control w-auto" />
                </div>

                <!-- End Date -->
                <div class="d-flex flex-column">
                  <label for="end-date" class="form-label">End Date</label>
                  <input type="date" id="end-date" v-model="borrowRequestData.endOfLoan" class="form-control w-auto" />
                </div>

                <!-- Borrow Button -->
                <button class="btn btn-info mt-4" @click="confirmBorrow" :disabled="!selectedGameId">
                  Borrow
                </button>
              </div>
            </div>
          </div>

          <div v-if="selectedTab === 'Reviews'">
            <!-- Second Table -->
            <div>
              <div class="d-flex justify-content-between align-items-center">
                <h4 class="text-center flex-grow-1">Reviews</h4>
                <router-link :to="{ name: reviewRoute }">
                  <button class="btn btn-info">Add Review</button>
                </router-link>
              </div>
              <table class="table">
                <thead>
                <tr>
                  <th>Comment</th>
                  <th>Rating</th>
                  <th>Reviewer</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="review in reviews">
                  <td>{{ review.comment }}</td>
                  <td>
                    <div class="stars">
                    <span v-for="n in 5" :key="n" class="star">
                      <span v-if="n <= review.rating">★</span>
                      <span v-else>☆</span>
                    </span>
                    </div>
                  </td>
                  <td>{{ review.author.name }}</td>
                </tr>
                </tbody>
              </table>
            </div>
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
  color: black !important;
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

.stars {
  font-size: 1.5rem;
  color: gold; /* Gold color for stars */
}

.star {
  margin-right: 2px; /* Adds space between stars */
}

.table-active {
  background-color: lightgreen !important; /* Light green background for selected row */
}

</style>
