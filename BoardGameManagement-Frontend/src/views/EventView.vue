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
                <input type="text" class="form-control" id="eventName" v-model="eventData.name" required>
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
                <input type="date" class="form-control" id="date" v-model="eventData.eventDate" required>
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
                <select class="form-control" id="boardGame" v-model="selectedBoardGame" required>
                  <option disabled value="">Choose a board game</option>
                  <option v-for="game in boardGames" :key="game.gameID" :value="game.gameID">
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
                  <option v-for="event in events" :value="event.eventID" :key="event.eventID">{{ event.name }}</option>
                </select>
              </div>

              <div class="mb-3">
                <label for="eventName" class="form-label">Event Name</label>
                <input type="text" class="form-control" id="eventName" v-model="eventData.name" required>
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
                <input type="date" class="form-control" id="date" v-model="eventData.eventDate" required>
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
                <select class="form-control" id="boardGame" v-model="selectedBoardGame" required>
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
import { ref, reactive, onMounted } from 'vue'
import {useAuthStore} from "@/stores/authStore.js";
import DefaultNavbar from '@/examples/navbars/NavbarDefault.vue'
import axios from "axios";

const axiosClient = axios.create({
  baseURL: "http://localhost:8080"
});

const authStore = useAuthStore();
const tabs = ['Create an Event', 'Update/Delete My Events', 'Browse Available Events']
const selectedTab = ref(tabs[0])
const eventData = reactive({
  name: '',
  description: '',
  maxSpot: '',
  eventDate: '',
  startTime: '',
  endTime: '',
  location: '',
  ownerId: null,
  boardGameId: null
})

const events = ref([])  // This will hold the array of events fetched from the database
const selectedEventId = ref("")
const boardGames = ref([]) // Will hold available board games
const selectedBoardGame = ref(null)

// Fetch all events
async function fetchEvents() {
  try {
    const response = await axiosClient.get("/events");
    events.value = response.data;
  } catch (error) {
    console.error("Error fetching events:", error);
    alert('Failed to fetch events. Please try again.');
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

// Get logged-in user ID
async function getCurrentUserId() {
  try {
    // Get the user ID from the auth store
    const userId = authStore.user.id;
    if (!userId) {
      throw new Error('User not logged in');
    }
    eventData.ownerId = userId;
    console.log('Using logged-in user ID:', eventData.ownerId);
  } catch (error) {
    console.error("Error getting user ID:", error);
    alert('Failed to get user information. Please make sure you are logged in.');
  }
}

// Initialize data when component is mounted
async function initializeData() {
  await Promise.all([
    fetchEvents(),
    fetchBoardGames(),
    getCurrentUserId()
  ]);
}

// Call initializeData when component is mounted
onMounted(initializeData);

// Create new event
async function createEvent() {
  try {
    // Validate required fields
    if (!eventData.name || !eventData.description || !eventData.maxSpot || 
        !eventData.eventDate || !eventData.startTime || !eventData.endTime || 
        !eventData.location || !selectedBoardGame.value) {
      alert('Please fill in all required fields');
      return;
    }

    // Validate maxSpot is a positive number
    if (parseInt(eventData.maxSpot) <= 0) {
      alert('Maximum spots must be a positive number');
      return;
    }

    // Ensure we have a user ID before proceeding
    await getCurrentUserId();

    // Format date and time
    const eventDate = new Date(eventData.eventDate);
    const startTime = new Date(`1970-01-01T${eventData.startTime}`);
    const endTime = new Date(`1970-01-01T${eventData.endTime}`);

    // Validate date is not in the past
    if (eventDate < new Date()) {
      alert('Event date cannot be in the past');
      return;
    }

    // Validate end time is after start time
    if (endTime <= startTime) {
      alert('End time must be after start time');
      return;
    }

    const eventDto = {
      name: eventData.name.trim(),
      description: eventData.description.trim(),
      maxSpot: eventData.maxSpot.toString(),
      eventDate: eventDate.toISOString().split('T')[0], // Format: YYYY-MM-DD
      startTime: `${eventData.startTime}:00`, // Format: HH:mm:ss
      endTime: `${eventData.endTime}:00`, // Format: HH:mm:ss
      location: eventData.location.trim(),
      ownerId: eventData.ownerId,
      boardGameId: selectedBoardGame.value
    };

    console.log('Sending event data:', eventDto);

    const response = await axiosClient.post("/events", eventDto);
    
    if (response.status === 201) {
      alert('Event Created Successfully!');
      
      // Reset form
      Object.keys(eventData).forEach(key => eventData[key] = key === 'maxSpot' ? null : '');
      selectedBoardGame.value = null;
      
      // Refresh events list
      await fetchEvents();
    }
  } catch (error) {
    console.error("Error creating event:", error);
    if (error.response) {
      const errorMessage = error.response.data.errors?.join('\n') || 'Failed to create event. Please try again.';
      alert(errorMessage);
    } else if (error.request) {
      alert('No response from server. Please check your connection.');
    } else {
      alert('Error setting up the request. Please try again.');
    }
  }
}

// Update existing event
async function updateEvent() {
  if (!selectedEventId.value) {
    alert('Please select an event to update');
    return;
  }

  try {
    // Validate required fields
    if (!eventData.name || !eventData.description || !eventData.maxSpot || 
        !eventData.eventDate || !eventData.startTime || !eventData.endTime || 
        !eventData.location || !selectedBoardGame.value) {
      alert('Please fill in all required fields');
      return;
    }

    // Validate maxSpot is a positive number
    if (parseInt(eventData.maxSpot) <= 0) {
      alert('Maximum spots must be a positive number');
      return;
    }

    // Validate board game ID is a positive number
    if (!selectedBoardGame.value || selectedBoardGame.value <= 0) {
      alert('Please select a valid board game');
      return;
    }

    // Format date and time
    const eventDate = new Date(eventData.eventDate);
    const startTime = new Date(`1970-01-01T${eventData.startTime}`);
    const endTime = new Date(`1970-01-01T${eventData.endTime}`);

    // Validate end time is after start time
    if (endTime <= startTime) {
      alert('End time must be after start time');
      return;
    }

    const eventDto = {
      name: eventData.name.trim(),
      description: eventData.description.trim(),
      maxSpot: eventData.maxSpot.toString(),
      eventDate: eventDate.toISOString().split('T')[0], // Format: YYYY-MM-DD
      startTime: `${eventData.startTime}:00`, // Format: HH:mm:ss
      endTime: `${eventData.endTime}:00`, // Format: HH:mm:ss
      location: eventData.location.trim(),
      ownerId: eventData.ownerId,
      boardGameId: parseInt(selectedBoardGame.value) // Ensure it's a number
    };

    const response = await axiosClient.put(`/events/${selectedEventId.value}`, eventDto);
    
    if (response.status === 200) {
      alert('Event Updated Successfully!');
      await fetchEvents();
    }
  } catch (error) {
    console.error("Error updating event:", error);
    if (error.response) {
      const errorMessage = error.response.data.errors?.join('\n') || 'Failed to update event. Please try again.';
      alert(errorMessage);
    } else if (error.request) {
      alert('No response from server. Please check your connection.');
    } else {
      alert('Error setting up the request. Please try again.');
    }
  }
}

// Delete event
async function deleteEvent() {
  if (!selectedEventId.value) {
    alert('Please select an event to delete');
    return;
  }

  if (!confirm('Are you sure you want to delete this event?')) {
    return;
  }

  try {
    const response = await axiosClient.delete(`/events/${selectedEventId.value}`);
    
    if (response.status === 200) {
      alert('Event Deleted Successfully!');
      selectedEventId.value = "";
      await fetchEvents();
    }
  } catch (error) {
    console.error("Error deleting event:", error);
    if (error.response) {
      const errorMessage = error.response.data.errors?.join('\n') || 'Failed to delete event. Please try again.';
      alert(errorMessage);
    } else if (error.request) {
      alert('No response from server. Please check your connection.');
    } else {
      alert('Error setting up the request. Please try again.');
    }
  }
}

// Load event data when an event is selected
async function loadEventData(eventId) {
  try {
    const response = await axiosClient.get(`/events/${eventId}`);
    const event = response.data;
    
    // Update form data
    eventData.name = event.name;
    eventData.description = event.description;
    eventData.maxSpot = event.maxSpot;
    eventData.eventDate = event.eventDate;
    eventData.startTime = event.startTime.substring(0, 5); // Format HH:mm
    eventData.endTime = event.endTime.substring(0, 5); // Format HH:mm
    eventData.location = event.location;
    selectedBoardGame.value = event.boardGameId;
    
    console.log('Loaded event data:', event);
    console.log('Selected board game:', selectedBoardGame.value);
  } catch (error) {
    console.error("Error loading event data:", error);
    alert('Failed to load event data. Please try again.');
  }
}

// Handle event selection
function selectEvent(id) {
  selectedEventId.value = id;
  loadEventData(id);
}

// Register for an event
async function registerForEvent() {
  if (!selectedEventId.value) {
    alert('Please select an event to register.');
    return;
  }

  try {
    // Ensure we have a user ID
    await getCurrentUserId();

    const response = await axiosClient.post(`/events/${selectedEventId.value}/register`, {
      playerId: eventData.ownerId
    });

    if (response.status === 201) {
      alert('Registration successful!');
      await fetchEvents(); // Refresh the events list
    }
  } catch (error) {
    console.error("Error registering for event:", error);
    if (error.response) {
      const errorMessage = error.response.data.errors?.join('\n') || 'Failed to register for event. Please try again.';
      alert(errorMessage);
    } else if (error.request) {
      alert('No response from server. Please check your connection.');
    } else {
      alert('Error setting up the request. Please try again.');
    }
  }
}

// Cancel registration for an event
async function cancelRegistration() {
  if (!selectedEventId.value) {
    alert("Please select an event to cancel registration.");
    return;
  }

  try {
    // Ensure we have a user ID
    await getCurrentUserId();

    const response = await axiosClient.delete(`/events/${selectedEventId.value}/register`, {
      data: { playerId: eventData.ownerId }
    });

    if (response.status === 200) {
      alert("Registration cancelled successfully!");
      await fetchEvents(); // Refresh the events list
    }
  } catch (error) {
    console.error("Error cancelling registration:", error);
    if (error.response) {
      const errorMessage = error.response.data.errors?.join('\n') || 'Failed to cancel registration. Please try again.';
      alert(errorMessage);
    } else if (error.request) {
      alert('No response from server. Please check your connection.');
    } else {
      alert('Error setting up the request. Please try again.');
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


