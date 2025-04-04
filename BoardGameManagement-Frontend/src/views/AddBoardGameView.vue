<script setup>

import DefaultNavbar from "@/examples/navbars/NavbarDefault.vue";
import {useAuthStore} from "@/stores/authStore.js";
import {reactive} from "vue";
import axios from "axios";
import {useRouter} from "vue-router";

const axiosClient = axios.create({
  baseURL: "http://localhost:8080"
});

const boardGames = reactive([]);
const router = useRouter();
const boardGameData = reactive({
  name: "",
  minPlayers: "",
  maxPlayers: "",
  description: "",
})

async function createBoardGame() {
  const newBoardGame = {
    name: boardGameData.name,
    minPlayers: Number(boardGameData.minPlayers),
    maxPlayers: Number(boardGameData.maxPlayers),
    description: boardGameData.description,
  }

  try {
    await axiosClient.post("/boardgames", newBoardGame);
  } catch (e) {
    console.error(e);
  }

  boardGames.push(newBoardGame);

  if(useAuthStore().user.isAOwner){
    await router.push("/pages/ownerboardgame");
  }else {
    await router.push("/pages/playerboardgame");
  }
}

function submitBoardGame() {
  createBoardGame();
  console.log('Created Board Game:', boardGameData)
  alert('Board Game Created Successfully!')
  // Reset form after submission
  Object.keys(boardGameData).forEach(key => boardGameData[key] = '')
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
          <h2 class="d-flex justify-content-center">Complete the Form Below to Add a New Board Game</h2>
          <div class="col row-cols-md-2 d-flex justify-content-center bg-outline-secondary">
            <!-- Form -->
            <form @submit.prevent="submitBoardGame">

              <div class="mb-3">
                <label for="name" class="form-label">Board Game Name</label>
                <input type="text" class="form-control" id="name" v-model="boardGameData.name" required>
              </div>

              <div class="mb-3">
                <label for="minPlayers" class="form-label">Minimum Number of Players</label>
                <input type="number" class="form-control" id="minPlayers" v-model="boardGameData.minPlayers" required>
              </div>

              <div class="mb-3">
                <label for="maxPlayers" class="form-label">Maximum Number of Players</label>
                <input type="number" class="form-control" id="maxPlayers" v-model="boardGameData.maxPlayers" required>
              </div>

              <div class="mb-3">
                <label for="description" class="form-label">Description</label>
                <textarea maxlength="255" class="form-control" id="description" v-model="boardGameData.description" required></textarea>
                <small :style="{color: boardGameData.description.length >= 255 ? 'red' : 'gray'}">
                  {{ boardGameData.description.length }} / 255 characters
                </small>
              </div>

              <button type="submit" class="btn btn-info">Create Board Game</button>

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
