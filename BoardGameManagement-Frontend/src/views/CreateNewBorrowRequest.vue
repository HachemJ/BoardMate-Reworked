<template>
  <div>
    <!-- Top Navigation Bar -->
    <DefaultNavbar />

    <div class="container-fluid mt-4">
      <div class="row">

        <!-- Right Content Area -->
        <div class="col-4 mx-auto">
          <h4>Create a new Borrow Request</h4>

          <form @submit.prevent="sendRequest">
            <div class="mb-3">
              <label for="borrowedGame" class="form-label"> Game to Borrow </label>
              This is the game you chose previously to borrow<br>
              if not the case then go back to change<br>
              maybe the owner will also be displayed<br>
              idk how to do that tho

            </div>
            <div class="mb-3">
              <label for="date" class="form-label">Start Date</label>
              <input type="date" class="form-control" id="startDate" v-model="requestData.startDate" required>
            </div>
            <div class="mb-3">
              <label for="date" class="form-label">Start Date</label>
              <input type="date" class="form-control" id="endDate" v-model="requestData.endDate" required>
            </div>
            <button type="submit" class="btn btn-info">Send request</button>
          </form>

        </div>
      </div>
    </div>
  </div>
</template>



<script setup>
import { ref, reactive } from 'vue'
import DefaultNavbar from '@/examples/navbars/NavbarDefault.vue'

const requestData = reactive({
  requestedGame: '',
  startDate: '',
  endDate: '',
  status: ''
})

function sendRequest() {
  console.log('Created Event Data:', requestData)
  alert('Event Created Successfully! Check the console for details.')
  // Reset form after submission
  Object.keys(requestData).forEach(key => requestData[key] = '')
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

