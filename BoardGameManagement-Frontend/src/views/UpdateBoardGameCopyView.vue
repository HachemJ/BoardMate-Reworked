<script setup>

import DefaultNavbar from "@/examples/navbars/NavbarDefault.vue";
import {reactive} from "vue";
import axios from "axios";
import {useAuthStore} from "@/stores/authStore.js";

const axiosClient = axios.create({
  baseURL: "http://localhost:8080"
});

const authStore = useAuthStore();
const boardGameCopies = reactive([]);

const boardGameCopyData = reactive({
    boardGameCopyId: "",
    specification: "",
})

async function getBoardGameCopiesForId() {
  try {
    const playerId = authStore.user.id;
    const response = await axiosClient.get("/boardgamecopies/byplayer/" + playerId);
    console.log("Data:", response.data);
    boardGameCopies.splice(0, boardGameCopies.length, ...response.data);
  } catch (error) {
    console.error(error);
  }
}

async function updateBGC(specification) {
  const boardGameCopyId = boardGameCopyData.boardGameCopyId;
  try {
    await axiosClient.put("/boardgamecopies/" + boardGameCopyId, specification, {
      headers: {
        'Content-Type': 'text/plain'
      }
    });
  } catch (e) {
    console.error(e);
  }

  // Find the updated item and update it
  // const existingCopy = boardGameCopies.find(copy => copy.boardGameCopyId === boardGameCopyId);
  // if (existingCopy) {
  //   existingCopy.specification = boardGameCopyData.specification;
  // }
}

function updateBoardGame() {
  updateBGC(boardGameCopyData.specification);
  console.log('Updated Board Game:', boardGameCopyData)
  alert('Board Game Updated Successfully!')
  // Reset form after submission
  Object.keys(boardGameCopyData).forEach(key => boardGameCopyData[key] = '')
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
          <h2 class="mb-3">Complete the Form Below to Update a Board Game</h2>

          <!-- Form -->
          <form @submit.prevent="updateBoardGame">

            <div class="mb-3">
                <label for="boardGameSelect" class="form-label">Select Board Game Copy to Update</label>
                <select id="boardGameSelect" class="form-select" v-model="boardGameCopyData.boardGameCopyId" required @focus="getBoardGameCopiesForId">
                    <option value="" disabled>Select Board Game Copy...</option>
                    <option v-for="copy in boardGameCopies" :key="copy.boardGameCopyId" :value="copy.boardGameCopyId">
                        {{ copy.boardGameName }} - {{ copy.specification }}
                    </option>
                </select>
            </div>

            <div class="mb-3">
              <label for="specification" class="form-label">Specification</label>
              <textarea class="form-control" id="specification" v-model="boardGameCopyData.specification" required></textarea>
            </div>

            <button type="submit" class="btn btn-info">Update Board Game Copy</button>

          </form>

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
