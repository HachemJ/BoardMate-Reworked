<script setup>

import DefaultNavbar from "@/examples/navbars/NavbarDefault.vue";
import {useRoute} from "vue-router";
import {reactive} from "vue";
import axios from "axios";

const axiosClient = axios.create({
   baseURL: "http://localhost:8080"
 });

const route = useRoute();
const gameName = route.params.gamename;

const reviewData = reactive({
  comment: "",
  rating: "",
})

async function fetchBoardGameID(name) {
  console.log("Game name is:", name); // Log the game name
  try {
    const response = await axiosClient.get("/boardgames");
    const game = response.data.find(game => game.name === name);
    if (game) {
      return game.gameID;
    }
    
    return null; // Return null if game ID is not found
  } catch (error) {
    console.error("Error fetching game ID:", error);
  }
}

async function createReview(comment, rating) {
    const gameId = await fetchBoardGameID(gameName);
    const newReview = {
        comment: comment,
        rating: Number(rating),
        commentDate: new Date().toISOString().split('T')[0], // Produces '2025-03-31'
        playerID: Number(7642), //TODO PLACEHOLDER
        boardGameID: gameId,
    }

    try {
        await axiosClient.post("/reviews", newReview);
    } catch (e) {
        console.error(e);
    }

}

function submitReview() {
  console.log('Created Review:', reviewData)
  createReview(reviewData.comment, reviewData.rating);
  alert('Review Created Successfully!')
  // Reset form after submission
  Object.keys(reviewData).forEach(key => reviewData[key] = key === 'maxSpot' ? null : '')
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
          <h2 class="mb-3">Complete the Form Below to Add a Review for {{ gameName }}</h2>

          <!-- Form -->
          <form @submit.prevent="submitReview">

            <div class="mb-3">
              <label for="comment" class="form-label">Comment</label>
              <textarea class="form-control" id="comment" v-model="reviewData.comment" required></textarea>
            </div>

            <div class="mb-3">
              <label for="rating" class="form-label">Rating</label>
              <select id="rating" v-model="reviewData.rating" class="form-control" required>
                <option value="" disabled selected>Select a rating</option>
                <option v-for="n in 6" :key="n" :value="n - 1">{{ n - 1 }}</option>
              </select>
            </div>

            <button type="submit" class="btn btn-info">Submit Review</button>

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
