<template>
  <div>
    <!-- Top Navigation Bar -->
    <DefaultNavbar />

    <div class="container-fluid mt-4">
      <div class="row">

        <!-- Right Content Area -->
        <div class="col-md-9">

          <div>
            <h1>Manage my borrow requests</h1>
            <table class="table">
              <thead>
              <tr>
                <th>Game requested</th>
                <th>Borrower name</th>
                <th>Borrower email</th>
                <th>Start of loan</th>
                <th>End of loan</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="(request, index) in requests" :key="request.id" >
                <td>{{ request.requestedGame }}</td>
                <td>{{ request.borrowerName }}</td>
                <td>{{ request.borrowerEmail }}</td>
                <td>{{ request.startDate }}</td>
                <td>{{ request.endDate}}</td>
                <td>
                  <button class="btn btn-info me-2"
                            @click="acceptRequest(request.borrowerName)"
                            >
                  Accept
                  </button>
                </td>
                <td>
                  <button class="btn btn-info me-2"
                          @click="declineRequest(request.borrowerName)"
                          >
                    Deny
                  </button>
                </td>


              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import DefaultNavbar from '@/examples/navbars/NavbarDefault.vue'


const requests = ref([])  // This will hold the array of events fetched from the database

requests.value = [
  {
    id: '1',
    requestedGame: 'Game name',
    borrowerName: 'person name',
    borrowerEmail: 'their email',
    startDate: '2023-08-20',
    endDate: '2023-09-20'
  },
  {
    id: '2',
    requestedGame: 'other game',
    borrowerName: 'name person',
    borrowerEmail: 'email@email',
    startDate: '2025-03-03',
    endDate: '2025-03-04'
  }
];


const selectedRequestId = ref("")


function acceptRequest(name) {

  console.log('Request accepted from ', name)
  alert(`Successfully accepted request from ${name}`)
}


function declineRequest(name) {

  console.log('Request denied from ', name)
  alert(`Successfully denied request from ${name}`)
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

