<script setup>

import DefaultNavbar from "@/examples/navbars/NavbarDefault.vue";
import {reactive, ref, onMounted} from "vue";
import axios from "axios";
import {useAuthStore} from "@/stores/authStore.js";
import {useRouter} from "vue-router";

const axiosClient = axios.create({
  baseURL: "http://localhost:8080"
});

const router = useRouter();
const authStore = useAuthStore();
const boardGames = ref([]);

onMounted(async () => {
  try {
    const response = await axiosClient.get("/boardgames");
    boardGames.value = response.data;
  } catch (error) {
    console.error(error);
  }
})

const boardGameCopyData = reactive({
  boardGameName: "",
  specification: "",
});

async function fetchBoardGameID(boardGameName) {
  console.log("Game name is:", boardGameName); // Log the game name
  try {
    const response = await axiosClient.get("/boardgames");
    const game = response.data.find(game => game.name === boardGameName);
    if (game) {
      return game.gameID;
    }
    
    return null; // Return null if game ID is not found
  } catch (error) {
    console.error("Error fetching game ID:", error);
  }
}

async function createBoardGameCopy(boardGameName, specification) {
  const gameId = await fetchBoardGameID(boardGameName);
  const newBoardGameCopy = {
    specification: specification,
    isAvailable: true,
    playerId: Number(authStore.user.id),
    boardGameId: gameId,
  }

  try {
    await axiosClient.post("/boardgamecopies", newBoardGameCopy);
  } catch (e) {
    console.error(e);
  }

  if(useAuthStore().user.isAOwner){
    await router.push("/pages/ownerboardgame");
  }else {
    await router.push("/pages/playerboardgame");
  }

}

function submitBoardGameCopy() {
  console.log('Created Board Game Copy:', boardGameCopyData)
  createBoardGameCopy(boardGameCopyData.boardGameName, boardGameCopyData.specification);
  alert('Board Game Copy Created Successfully!')
  // Reset form after submission
  Object.keys(boardGameCopyData).forEach(key => boardGameCopyData[key] = key === 'maxSpot' ? null : '')
}

</script>

<template>
  <div>
    <!-- Top Navigation Bar -->
    <DefaultNavbar />

    <div class="container-fluid mt-4">
      <div class="row">

        <!-- Content Area -->
        <div class="row-cols-md-auto">
          <h2 class="d-flex justify-content-center">Complete the Form Below to Add a New Board Game Copy</h2>
          <br>

          <div class="col row-cols-md-2 d-flex justify-content-center bg-outline-secondary">
            <!-- Page Title -->

            <!-- Form -->
            <form @submit.prevent="submitBoardGameCopy">

              <div class="mb-3">
                <label for="boardGame" class="form-label">Select Board Game</label>
                <select class="form-control" id="boardGame" v-model="boardGameCopyData.boardGameName" required>
                  <option value="" disabled>Select a game</option>
                  <option v-for="game in boardGames">
                    {{ game.name }}
                  </option>
                </select>
              </div>

              <div class="mb-3">
                <label for="specification" class="form-label">Specification</label>
                <textarea maxlength="255" class="form-control" id="specification" v-model="boardGameCopyData.specification" required></textarea>
                <small :style="{color: boardGameCopyData.specification.length >= 255 ? 'red' : 'gray'}">
                  {{ boardGameCopyData.specification.length }} / 255 characters
                </small>
              </div>

              <button type="submit"
                      class="btn btn-info">
                Create Board Game Copy
              </button>

            </form>

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

</style>
