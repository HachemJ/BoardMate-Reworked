<script setup>

import DefaultNavbar from "@/examples/navbars/NavbarDefault.vue";
import {onMounted, ref} from "vue";
import {useRoute} from "vue-router";
import axios from "axios";

const axiosClient = axios.create({
  baseURL: "http://localhost:8080"
});

const route = useRoute();
const eventName = route.params.eventname;
const eventDetails = ref({
  eventName: '',
  description: '',
  maxSpot: '',
  eventDate: '',
  startTime: '',
  endTime: '',
  location: '',
  ownerName: '',
  boardGameName: '',
});

async function fetchEventID() {
  try {
    const response = await axiosClient.get("/events");
    const event = response.data.find(event => event.name === eventName);
    if (event) {
      console.log("Event ID:", event.eventID); // Log the event ID
      return event.eventID;
    }
    return null; // Return null if game ID is not found
  } catch (error) {
    console.error("Error fetching event ID:", error);
  }
}

onMounted(async () => {
  try {
    const eventId = await fetchEventID();
    const response = await axiosClient.get("/events/" + eventId);
    eventDetails.value = response.data;
    console.log("Event Details:", eventDetails.value); // Log the event details
  } catch (error) {
    console.error(error);
  }
})

</script>

<template>
  <div>
    <!-- Top Navigation Bar -->
    <DefaultNavbar />

    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-10">

          <!-- Event Card -->
          <div class="card p-5 mb-4 shadow rounded-4">
            <div class="row align-items-start justify-content-between">

              <!-- Left: Event Name & Board Game -->
              <div class="col-md-6 text-start">
                <h1 class="fw-bold display-4">{{ eventName }}</h1>

                <p class="mt-3 text-muted" style="font-size: 1.1rem;">
                  <strong>Board Game:</strong>
                  {{ eventDetails.boardGameName }}
                </p>
              </div>

              <!-- Right: Event Info & Description -->
              <div class="col-md-6 text-start">
                <div class="row">
                  <!-- Date & Time -->
                  <div class="col-md-6">
                    <p><strong>Date:</strong> {{ eventDetails.eventDate }}</p>
                    <p><strong>Start Time:</strong> {{ eventDetails.startTime }}</p>
                    <p><strong>End Time:</strong> {{ eventDetails.endTime }}</p>
                  </div>

                  <!-- Description -->
                  <div class="col-md-6">
                    <p><strong>Description:</strong><br />
                      {{ eventDetails.description }}
                    </p>
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

<style scoped>

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

</style>
