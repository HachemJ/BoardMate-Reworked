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
          <div v-if="selectedTab === 'Create an Event'">
            <h4>Complete the Form Below to Create a New Event</h4>
            <form @submit.prevent="createEvent">
              <div class="mb-3">
                <label for="eventName" class="form-label">Event Name</label>
                <input type="text" class="form-control" id="eventName" v-model="eventData.eventName" required>
              </div>
              <div class="mb-3">
                <label for="description" class="form-label">Description</label>
                <textarea class="form-control" id="description" v-model="eventData.description" required></textarea>
              </div>
              <div class="mb-3">
                <label for="maxSpot" class="form-label">Maximum Spots</label>
                <input type="number" class="form-control" id="maxSpot" v-model="eventData.maxSpot" required>
              </div>
              <div class="mb-3">
                <label for="date" class="form-label">Date</label>
                <input type="date" class="form-control" id="date" v-model="eventData.date" required>
              </div>
              <div class="mb-3">
                <label for="startTime" class="form-label">Start Time</label>
                <input type="time" class="form-control" id="startTime" v-model="eventData.startTime" required>
              </div>
              <div class="mb-3">
                <label for="endTime" class="form-label">End Time</label>
                <input type="time" class="form-control" id="endTime" v-model="eventData.endTime" required>
              </div>
              <div class="mb-3">
                <label for="location" class="form-label">Location</label>
                <input type="text" class="form-control" id="location" v-model="eventData.location" required>
              </div>
              <div class="mb-3">
                <label for="boardGame" class="form-label">Select Board Game</label>
                <select class="form-control" v-model="eventData.boardGameId" required>
                  <option disabled value="">Choose a board game</option>
                  <option
                    v-for="game in boardGames"
                    :key="game.gameID"
                    :value="game.gameID"
                  >
                    {{ game.name }}
                  </option>
                </select>
              </div>
              <button type="submit" class="btn btn-info">Create Event</button>
            </form>
          </div>

          <div v-else-if="selectedTab === 'Update/Delete My Events'">
            <h4>Update/Delete an Existing Event</h4>
            <form @submit.prevent="updateEvent">
              <!-- Dropdown Menu for Selecting an Event -->
              <div class="mb-3">
                <select class="form-control" id="selectedEvent" v-model="selectedEventId">
                  <option disabled value="">Select the Event to be Updated/Deleted</option>
                  <option v-for="event in myEvents" :value="event.eventID" :key="event.eventID">{{ event.name }}</option>
                </select>
              </div>

              <div class="mb-3">
                <label for="eventName" class="form-label">Event Name</label>
                <input type="text" class="form-control" id="eventName" v-model="eventData.eventName" 
                :placeholder="events.find(event => event.eventID === selectedEventId)?.name || 'Event Name'" 
                required>
              </div>
              <div class="mb-3">
                <label for="description" class="form-label">Description</label>
                <textarea class="form-control" id="description" v-model="eventData.description"
                :placeholder="events.find(event => event.eventID === selectedEventId)?.description || 'Description'" 
                  required></textarea>
              </div>
              <div class="mb-3">
                <label for="maxSpot" class="form-label">Maximum Spots</label>
                <input type="number" class="form-control" id="maxSpot" v-model="eventData.maxSpot" 
                :placeholder="events.find(event => event.eventID === selectedEventId)?.maxSpot || 'Maximum Spots'" 
                required>
              </div>
              <div class="mb-3">
                <label for="date" class="form-label">Date</label>
                <input type="date" class="form-control" id="date" v-model="eventData.date" required>
              </div>
              <div class="mb-3">
                <label for="startTime" class="form-label">Start Time</label>
                <input type="time" class="form-control" id="startTime" v-model="eventData.startTime" required>
              </div>
              <div class="mb-3">
                <label for="endTime" class="form-label">End Time</label>
                <input type="time" class="form-control" id="endTime" v-model="eventData.endTime" required>
              </div>
              <div class="mb-3">
                <label for="location" class="form-label">Location</label>
                <input type="text" class="form-control" id="location" v-model="eventData.location" 
                :placeholder="events.find(event => event.eventID === selectedEventId)?.location || 'Location'" 
                required>
              </div>
              <div class="mb-3">
                <label for="boardGameUpdate" class="form-label">Select Board Game</label>
                <select class="form-control" id="boardGameUpdate" v-model="eventData.boardGameId" required>
                  <option disabled value="">Choose a board game</option>
                  <option v-for="game in boardGames" :key="game.gameID" :value="game.gameID">
                    {{ game.name }}
                  </option>
                </select>
              </div>
              <button
                type="submit"
                class="btn btn-info me-2"
                :disabled="!selectedEventId"
              >
                Update Event
              </button>

              <button
                type="button"
                class="btn btn-danger"
                :disabled="!selectedEventId"
                @click="deleteEvent"
              >
                Delete Event
              </button>
            </form>
          </div>

          <div v-else-if="selectedTab === 'Browse Available Events'">
            <h4>Select an Event from the Table Below</h4>
            <table class="table">
              <thead>
                <tr>
                  <th>Event Name</th>
                  <th>Description</th>
                  <th>Date</th>
                  <th>Start Time</th>
                  <th>End Time</th>
                  <th>Location</th>
                  <th>Max Spots</th>
                  <th>Registration Status</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="event in events" :key="event.eventID" @click="selectEvent(event.eventID)" :class="{ 'table-active': selectedEventId === event.eventID }">
                  <td>{{ event.name }}</td>
                  <td>{{ event.description }}</td>
                  <td>{{ event.eventDate }}</td>
                  <td>{{ event.startTime }}</td>
                  <td>{{ event.endTime }}</td>
                  <td>{{ event.location }}</td>
                  <td>{{ event.maxSpot }}</td>
                    <td>
                    <span v-if="registrationStatus[event.eventID] === undefined">Loading...</span>
                    <span v-else>{{ registrationStatus[event.eventID] }}</span>
                    </td>
                </tr>
              </tbody>
            </table>
            <button
              class="btn btn-info me-2"
              @click="registerForEvent"
              :disabled="!selectedEventId"
            >
              Register
            </button>

            <button
              class="btn btn-danger"
              @click="cancelRegistration"
              :disabled="!selectedEventId"
            >
              Cancel Registration
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import {useAuthStore} from "@/stores/authStore.js";
import DefaultNavbar from '@/examples/navbars/NavbarDefault.vue'
import axios from "axios";
import { watch } from 'vue';

const axiosClient = axios.create({
  baseURL: "http://localhost:8080"
});

const authStore = useAuthStore();
const tabs = ['Create an Event', 'Update/Delete My Events', 'Browse Available Events']
const selectedTab = ref(tabs[0])
const eventData = reactive({
  eventName: '',
  description: '',
  maxSpot: '',
  date: '',
  startTime: '',
  endTime: '',
  location: '',
  ownerId: null, // Will be set from logged-in user? not sure how to do that
  boardGameId: null // Will be set from selected board game; the sys fetches all boardgame and put them in dropdown list for user to select
})

const events = ref([])  // This will hold the array of events fetched from the database
const myEvents = ref([])
const selectedEventId = ref("")
const boardGames = ref([]) // Will hold available board games
const selectedBoardGame = ref(null)
const registrationStatus = ref({}) // To hold registration status for each event

//auto-fill with the values from the selected event.
watch(selectedEventId, (newId) => {
  if (newId) {
    selectEvent(newId);
  }
});

// Fetch all events
async function fetchEvents() {
  try {
    const response = await axiosClient.get("/events");
    events.value = response.data;
    
    for (const event of events.value) {
      //console.log("Event owner ID:", event.ownerName);
      await getRegistrationStatus(event.eventID);
    }
  } catch (error) {
    console.error("Error fetching events:", error);
    alert('Failed to fetch events. Please try again.');
  }
}

async function fetchMyEvents() {
  try {
    const response = await axiosClient.get("/events/owner/" + authStore.user.id);
    myEvents.value = response.data;
  } catch (error) {
    console.error("Error fetching my events:", error);
    alert('Failed to fetch your events. Please try again.');
  }
}

// Fetch available board games
async function fetchBoardGames() {
  try {
    const response = await axiosClient.get("/boardgames");
    boardGames.value = response.data;
  } catch (error) {
    console.error("Error fetching board games:", error);
    alert('Failed to fetch board games. Please try again.');
  }
}


// Initialize data when component is mounted
async function initializeData() {
  await Promise.all([
    fetchEvents(),
    fetchBoardGames(),
    fetchMyEvents(),
  ]);
}

// Call initializeData when component is mounted
initializeData();

// Create new event
async function createEvent() {
  try {
    if (!eventData.boardGameId) {
      alert('Please select a board game');
      return;
    }

    const eventDto = {
      name: eventData.eventName,
      description: eventData.description,
      maxSpot: eventData.maxSpot.toString(),
      eventDate: eventData.date,
      startTime: `${eventData.startTime}:00`,
      endTime: `${eventData.endTime}:00`,
      location: eventData.location,
      ownerId: Number(authStore.user.id), 
      boardGameId: eventData.boardGameId
    };
  
    console.log("Creating event, owenr ID is: ", eventDto.ownerId);

    await axiosClient.post("/events", eventDto);
    alert('Event Created Successfully!');

    Object.keys(eventData).forEach(key => eventData[key] = key === 'maxSpot' ? null : '');
    await fetchEvents();
  } catch (error) {
    console.error("Error creating event:", error);
    console.log("Full error:", error.response?.data);
    alert('Failed to create event. Please try again.');
  }
}


// Update existing event
async function updateEvent() {
  if (!selectedEventId.value) {
    alert('Please select an event to update.');
    return;
  }

  try {
    const eventDto = {
      name: eventData.eventName,
      description: eventData.description,
      maxSpot: eventData.maxSpot,
      eventDate: eventData.date,
      startTime: eventData.startTime,
      endTime: eventData.endTime,
      location: eventData.location,
      ownerId: authStore.user.id,
      boardGameId: eventData.boardGameId
    };

    await axiosClient.put(`/events/${selectedEventId.value}`, eventDto);
    alert('Event Updated Successfully!');
    
    // Reset form
    Object.keys(eventData).forEach(key => eventData[key] = key === 'maxSpot' ? null : '');
    selectedEventId.value = "";
    
    // Refresh events list
    await fetchEvents();
  } catch (error) {
    console.error("Error updating event:", error);
    alert('Failed to update event. Please try again.');
  }
}

const deleteEvent = async () => {
  if (!selectedEventId.value) {
    alert("Please select an event to delete.");
    return;
  }

  if (!confirm("Are you sure you want to delete this event?")) {
    return;
  }

  try {
    console.log("Attempting to delete event:", selectedEventId.value);
    const response = await axiosClient.delete(`/events/${selectedEventId.value}`);

    if (response.status === 200 || response.status === 204) {
      alert("Event deleted successfully!");
      selectedEventId.value = "";
      await fetchEvents();

      // Clear the form
      Object.keys(eventData).forEach(
        key => (eventData[key] = key === "maxSpot" ? null : "")
      );
      selectedBoardGame.value = null;
    } else {
      alert("Unexpected response. Please check the console.");
      console.log("Response:", response);
    }
  } catch (err) {
    console.error("Delete error", err);
    alert("Failed to delete event. Please try again.");
  }
};



function selectEvent(id) {
  selectedEventId.value = id;
  // Find the selected event and populate the form
  const event = events.value.find(e => e.eventID === id);
  if (event) {
    eventData.eventName = event.name;
    eventData.description = event.description;
    eventData.maxSpot = event.maxSpot;
    eventData.date = event.eventDate;
    eventData.startTime = event.startTime;
    eventData.endTime = event.endTime;
    eventData.location = event.location;
    eventData.ownerId = event.ownerId;
    eventData.boardGameId = event.boardGameId;

  }
}

//original functions by Niz
async function registerForEvent() {
  if (!selectedEventId.value) {
    alert('Please select an event to register.')
    return
  }
  console.log('Registered for Event ID:', selectedEventId.value)

  const registration = {
    playerID: Number(authStore.user.id),
    eventID: selectedEventId.value,
  }
    try {
        await axiosClient.post("/registrations", registration);
    } catch (e) {
        console.error(e);
    }
  for (const event of events.value) {
    await getRegistrationStatus(event.eventID);
  }
  alert('Registration successful!')
}

async function cancelRegistration() {
  if (!selectedEventId.value) {
    alert("Please select an event to cancel.");
    return;
  }

  try {
    await axiosClient.delete("/registrations/" + authStore.user.id + "/" + selectedEventId.value);
  } catch (e) {
    console.error(e);
    alert("Failed to cancel registration. Please try again.");
  }

  console.log("Cancelled registration for Event ID:", selectedEventId.value);
    for (const event of events.value) {
    await getRegistrationStatus(event.eventID);
  }
  alert("Registration canceled!");
}

async function getRegistrationStatus(id) {
  try {
    const response = await axiosClient.get(`/registrations/${authStore.user.id}/${id}`);
    if (response.data === null) {
      registrationStatus.value[id] = "Not Registered";
    } else {
      registrationStatus.value[id] = "Registered";
    }
  } catch (error) {
    if (error.response?.status === 404) {
      registrationStatus.value[id] = "Not Registered";
    } else {
      registrationStatus.value[id] = "Error";
    }
  }
}


</script>

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

