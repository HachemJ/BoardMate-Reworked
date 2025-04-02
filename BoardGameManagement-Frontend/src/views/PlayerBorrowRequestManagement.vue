
<script setup>
import {ref, onMounted, watchEffect} from 'vue'
import DefaultNavbar from '@/examples/navbars/NavbarDefault.vue'
import axios from "axios";
import {useRoute} from "vue-router";


const axiosClient = axios.create({
  // NOTE: it's baseURL, not baseUrl
  baseURL: "http://localhost:8080"
});

const requests = ref([]);  // This will hold the array of events fetched from the database
const requestUpdated = ref(false); // keep track whether requests have been updated
const playerId = useRoute().params.playerId;
const date = new Date();
const today = date.getFullYear() + '-'
    + (date.getMonth() + 1).toString().padStart(2, '0') + '-'
    + date.getDate().toString().padStart(2, '0');



const fetchRequests = async () => {
  try {
    const response = await axiosClient.get(`/players/${playerId}/borrowrequests`);
    console.log(response.data);
    requests.value = response.data;
  } catch (error) {
    console.error("Error fetching requests:", error);
  }

};


// Fetch data when component is mounted
onMounted(fetchRequests);

// Automatically refresh when `requestUpdated` changes
watchEffect(() => {
  if (requestUpdated.value) {
    fetchRequests();
    requestUpdated.value = false; // Reset the state after fetching
  }
});


async function confirmRequest(id, gameName) {
  try {
    await axiosClient.put(`/borrowrequests/${id}/boardGameCopy?confirmOrCancel=confirm`);
    alert(`Borrow time started: confirmed ${gameName} was received`);
    // Trigger a refresh after the request is accepted
    requestUpdated.value = true;  // This will trigger the `watchEffect` to re-fetch the data

  } catch (error) {
    alert(`Error encountered type ${error.response.status}: ${error.response.request.response}`);
    console.error("Error declining request:", error);
  }
}


async function cancelRequest(id, gameName) {
  try {
    await axiosClient.put(`/borrowrequests/${id}/boardGameCopy?confirmOrCancel=cancel`);
    alert(`Premature Cancelling: confirms ${gameName} borrowing was cancelled and returned`);
    // Trigger a refresh after the request is accepted
    requestUpdated.value = true;  // This will trigger the `watchEffect` to re-fetch the data

  } catch (error) {
    alert(`Error encountered type ${error.response.status}: ${error.response.request.response}`);
    console.error("Error declining request:", error);
  }
}


function canConfirmGotGame(status, startDate, endDate){
  console.log(today, status, status === "Accepted" , today >= startDate, today < endDate);
  return status === "Accepted" & today >= startDate & today <= endDate
}

function canCancelRequest(status, startDate){
  return status === "InProgress" & today >= startDate

}

function isRequestInactive(status, startDate){
  return status === "Done" | status === "Denied" | today < startDate
}


function getStatusBadgeClass(status) {
  const classes = {
    Pending: 'bg-warning',
    Accepted: 'bg-success',
    Denied: 'bg-danger',
    Done: 'bg-info'
  };
  return classes[status] || 'bg-secondary';
}

</script>






<template>
  <div>
    <!-- Top Navigation Bar -->
    <DefaultNavbar />

    <div class="container-fluid mt-4">
      <div class="row">

        <!-- Right Content Area -->
        <div class="col-md-9">

          <div>
            <h1>See my borrow requests</h1>
            <br>
            <table class="table">
              <thead>
              <tr>
                <th>Game requested</th>
                <th>Start of loan</th>
                <th>End of loan</th>
                <th>Status</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="(request, index) in requests" :key="request.requestId"
                  :style="{color: isRequestInactive(request.requestStatus, request.startOfLoan) ? 'lightGrey' : 'grey'}"
              >
                <td>{{ request.specificGameName }}</td>
                <td>{{ request.startOfLoan }}</td>
                <td>{{ request.endOfLoan}}</td>
                <td>
                  <span class="badge" :class="getStatusBadgeClass(requests.requestStatus)">
                      {{ request.requestStatus}}
                  </span>
                </td>
                <td>
                  <button class="btn btn-info me-2"
                          v-if="canConfirmGotGame(request.requestStatus, request.startOfLoan, request.endOfLoan)"
                          style="bottom: 20px;"
                          @click="confirmRequest(request.requestId, request.specificGameName)"
                  >
                    Confirm
                  </button>

                  <button class="btn btn-info me-2"
                          v-if="canCancelRequest(request.requestStatus, request.startOfLoan)"
                          style="bottom: 20px;"
                          @click="cancelRequest(request.requestId, request.specificGameName)"
                  >
                    Cancel
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

