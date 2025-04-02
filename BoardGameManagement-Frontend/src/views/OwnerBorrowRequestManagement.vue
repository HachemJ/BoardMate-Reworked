
<script setup>
import {ref, onMounted, watchEffect, onUnmounted} from 'vue'
import DefaultNavbar from '@/examples/navbars/NavbarDefault.vue'
import axios from "axios";
import {useRoute} from "vue-router";


const axiosClient = axios.create({
  // NOTE: it's baseURL, not baseUrl
  baseURL: "http://localhost:8080"
});

const requests = ref([]);  // This will hold the array of events fetched from the database
const requestUpdated = ref(false); // keep track whether requests have been updated
const ownerId = useRoute().query.ownerId;

let intervalId = null;

const fetchRequests = async () => {
  try {
    const response = await axiosClient.get(`/borrowrequests?ownerId=${ownerId}`);
    console.log(response.data);
    requests.value = response.data;
  } catch (error) {
    console.error("Error fetching requests:", error);
  }

};


// Fetch data when the component mounts
onMounted(() => {
  fetchRequests(); // Fetch data immediately

  // Refresh data every 10 seconds
  intervalId = setInterval(fetchRequests, 10000);
});
// Automatically refresh when `requestUpdated` changes
watchEffect(() => {
  if (requestUpdated.value) {
    fetchRequests();
    requestUpdated.value = false; // Reset the state after fetching
  }
});

// Cleanup interval when component is unmounted
onUnmounted(() => {
  if (intervalId) clearInterval(intervalId);
});

async function acceptRequest(id, name) {
  try {
    await axiosClient.put(`/borrowrequests/${id}?action=accept`);
    alert(`Request from ${name} accepted successfully`);

    // Trigger a refresh after the request is accepted
    requestUpdated.value = true;  // This will trigger the `watchEffect` to re-fetch the data

  } catch (error) {
    alert(`Error encounteed type ${error.response.status}: ${error.response.request.response}`);
    console.error("Error accepting request:", error);
  }
}

async function declineRequest(id, name) {

  try {
    await axiosClient.put(`/borrowrequests/${id}?action=decline`);
    alert(`Request from ${name} denied successfully!`);
    // Trigger a refresh after the request is accepted
    requestUpdated.value = true;  // This will trigger the `watchEffect` to re-fetch the data

  } catch (error) {

    alert(`Error encountered type ${error.response.status}: ${error.response.request.response}`);
    console.error("Error declining request:", error);
  }
}


async function cancelBorrowRequest(id, gameName){
  try {
    await axiosClient.put(`/borrowrequests/${id}/boardGameCopy?confirmOrCancel=cancel`);
    alert(`Borrow time over: confirmed ${gameName} was returned`);
    // Trigger a refresh after the request is accepted
    requestUpdated.value = true;  // This will trigger the `watchEffect` to re-fetch the data

  } catch (error) {
    alert(`Error encountered type ${error.response.status}: ${error.response.request.response}`);
    console.error("Error declining request:", error);
  }
}

function shouldShowButton(status){
  console.log(status);
  console.log(status === `Pending`);
  return status === `Pending`
}

function isRequestEndDate(endDate, status){
  const date = new Date();
  const today = date.getFullYear() + '-'
      + (date.getMonth() + 1).toString().padStart(2, '0') + '-'
      + date.getDate().toString().padStart(2, '0');

  console.log(today, today === endDate)
  //const today = "2025-01-03"; // for testing only, use line above in real code
  return today === endDate & status === "InProgress"
}

function isRequestDone(status){
  return status === "Done" | status === "Denied"
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
            <h1>Manage my borrow requests</h1>
            <br>
            <table class="table">
              <thead>
              <tr>
                <th>Game requested</th>
                <th>Borrower name</th>
                <th>Borrower email</th>
                <th>Start of loan</th>
                <th>End of loan</th>
                <th>Action</th>
              </tr>
              </thead>
              <tbody >
              <tr v-for="(request, index) in requests" :key="request.requestId"
                  :style="{color: isRequestDone(request.requestStatus) ? 'lightGrey' : 'grey'}"
              >
                <td>{{ request.specificGameName }}</td>
                <td>{{ request.borrowerName }}</td>
                <td>{{ request.borrowerEmail }}</td>
                <td>{{ request.startOfLoan }}</td>
                <td>{{ request.endOfLoan}}</td>
                <td v-if="shouldShowButton(request.requestStatus)">
                  <button class="btn btn-info me-2"
                          id="acceptRequestButton"
                          style="bottom: 20px;"
                          @click="acceptRequest(request.requestId, request.borrowerName)"
                          >
                  Accept
                  </button>

                  <button class="btn btn-info me-2 "
                          id="declineRequestButton"
                          style="bottom: 20px;"
                          @click="declineRequest(request.requestId, request.borrowerName)"
                          >
                    Deny
                  </button>
                </td>
                <td v-else>
                  <span>
                      request {{ request.requestStatus }}
                    </span>
                </td>
                <td v-if="isRequestEndDate(request.endOfLoan, request.requestStatus)">
                  <button class="btn btn-info me-2"
                          @click="cancelBorrowRequest(request.requestId, request.specificGameName)"
                          style="bottom: 20px;"
                  >
                    confirm game returned
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

