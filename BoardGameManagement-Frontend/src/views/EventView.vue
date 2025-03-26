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
            <form @submit.prevent="submitEvent">
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
                  <option v-for="event in events" :value="event.id" :key="event.id">{{ event.name }}</option>
                </select>
              </div>

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
                <tr v-for="(event, index) in events" :key="event.id" @click="selectEvent(event.id)" :class="{ 'table-active': selectedEventId === event.id }">
                  <td>{{ event.eventName }}</td>
                  <td>{{ event.description }}</td>
                  <td>{{ event.date }}</td>
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
import { ref, reactive } from 'vue'
import DefaultNavbar from '@/examples/navbars/NavbarDefault.vue'

const tabs = ['Create an Event', 'Update/Delete My Events', 'Browse Available Events']
const selectedTab = ref(tabs[0])
const eventData = reactive({
  eventName: '',
  description: '',
  maxSpot: '',
  date: '',
  startTime: '',
  endTime: '',
  location: ''
})
const events = ref([])  // This will hold the array of events fetched from the database


const selectedEventId = ref("")

function selectEvent(id) {
  selectedEventId.value = id
}

function registerForEvent() {
  if (!selectedEventId.value) {
    alert('Please select an event to register.')
    return
  }
  console.log('Registered for Event ID:', selectedEventId.value)
  alert('Registration successful!')
}

function cancelRegistration() {
  if (!selectedEventId.value) {
    alert("Please select an event to cancel.");
    return;
  }

  console.log("Cancelled registration for Event ID:", selectedEventId.value);
  alert("Registration canceled!");
}


function updateEvent() {
  console.log('Updated Event Data:', eventData)
  alert('Event Updated Successfully! Check the console for details.')
  // Add actual update logic here later
}

function submitEvent() {
  console.log('Created Event Data:', eventData)
  alert('Event Created Successfully! Check the console for details.')
  // Reset form after submission
  Object.keys(eventData).forEach(key => eventData[key] = key === 'maxSpot' ? null : '')
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

