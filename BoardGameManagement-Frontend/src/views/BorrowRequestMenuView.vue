
<script setup>
import {ref, onMounted, watchEffect, onUnmounted, computed} from 'vue'
import DefaultNavbar from '@/examples/navbars/NavbarDefault.vue'
import axios from "axios";
import {useAuthStore} from "@/stores/authStore.js";
import { useRoute, useRouter } from "vue-router";


const authStore = useAuthStore();
const axiosClient = axios.create({
  // NOTE: it's baseURL, not baseUrl
  baseURL: "http://localhost:8080"
});

const tabs = [
  { key: "mine", label: "See My Borrow Requests", ownerOnly: false },
  { key: "incoming", label: "Manage Incoming Borrow Requests", ownerOnly: true },
];

const ownerRequests = ref([]);
const ownerRequestUpdated = ref(false); // keep track whether requests have been updated
const ownerId = authStore.user.id;

const playerId = authStore.user.id;
const playerRequests = ref([]);  // This will hold the array of events fetched from the database
const playerRequestUpdated = ref(false); // keep track whether requests have been updated
const date = new Date();
const today = date.getFullYear() + '-'
    + (date.getMonth() + 1).toString().padStart(2, '0') + '-'
    + date.getDate().toString().padStart(2, '0');
let intervalId = null;
const route = useRoute();
const router = useRouter();

function handleApiError(error, fallbackMessage) {
  const status = error?.response?.status;
  if (status === 401) {
    alert("Session expired. Please sign in again.");
    router.push({ name: "signin" });
    return;
  }
  const errors = error?.response?.data?.errors;
  const message = Array.isArray(errors) ? errors.join("\n") : fallbackMessage;
  alert(message);
}

const activeTab = computed(() => {
  const raw = String(route.query.tab || "").toLowerCase();
  if (raw === "incoming" && authStore.user.isAOwner) return "incoming";
  return "mine";
});

watchEffect(() => {
  const current = String(route.query.tab || "").toLowerCase();
  if (current !== activeTab.value) {
    router.replace({
      name: "borrowRequestMenu",
      query: { ...route.query, tab: activeTab.value },
    });
  }
});

const fetchPlayerRequests = async () => {
  try {
    const response = await axiosClient.get(`/players/${playerId}/borrowrequests`);
    console.log(response.data);
    playerRequests.value = response.data;
  } catch (error) {
    console.error("Error fetching requests:", error);
    handleApiError(error, "Failed to load your borrow requests.");
  }
};

const fetchOwnerRequests = async () => {
  try {
    const response = await axiosClient.get(`/borrowrequests?ownerId=${ownerId}`);
    console.log(response.data);
    ownerRequests.value = response.data;
  } catch (error) {
    console.error("Error fetching requests:", error);
    handleApiError(error, "Failed to load incoming borrow requests.");
  }

};


// Fetch data when the component mounts
onMounted(() => {
  if (authStore.user.isAOwner) {
    fetchOwnerRequests();
    intervalId = setInterval(fetchOwnerRequests, 10000);
  }
  fetchPlayerRequests();
});
// Automatically refresh when `requestUpdated` changes
watchEffect(() => {
  if (ownerRequestUpdated.value) {
    fetchOwnerRequests();
    ownerRequestUpdated.value = false; // Reset the state after fetching
  }
  if (playerRequestUpdated.value) {
    fetchPlayerRequests();
    playerRequestUpdated.value = false; // Reset the state after fetching
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
    ownerRequestUpdated.value = true;  // This will trigger the `watchEffect` to re-fetch the data

  } catch (error) {
    console.error("Error accepting request:", error);
    handleApiError(error, "Failed to accept request.");
  }
}

async function declineRequest(id, name) {

  try {
    await axiosClient.put(`/borrowrequests/${id}?action=decline`);
    alert(`Request from ${name} denied successfully!`);
    // Trigger a refresh after the request is accepted
    ownerRequestUpdated.value = true;  // This will trigger the `watchEffect` to re-fetch the data

  } catch (error) {
    console.error("Error declining request:", error);
    handleApiError(error, "Failed to decline request.");
  }
}


async function cancelBorrowRequest(id, gameName){
  try {
    await axiosClient.put(`/borrowrequests/${id}/boardGameCopy?confirmOrCancel=cancel`);
    alert(`Borrow time over: confirmed ${gameName} was returned`);
    // Trigger a refresh after the request is accepted
    ownerRequestUpdated.value = true;  // This will trigger the `watchEffect` to re-fetch the data

  } catch (error) {
    console.error("Error declining request:", error);
    handleApiError(error, "Failed to confirm return.");
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
  return today === endDate && status === "InProgress"
}

function isRequestDone(status){
  return status === "Done" || status === "Denied"
}

async function confirmRequest(id, gameName) {
  try {
    await axiosClient.put(`/borrowrequests/${id}/boardGameCopy?confirmOrCancel=confirm`);
    alert(`Borrow time started: confirmed ${gameName} was received`);
    // Trigger a refresh after the request is accepted
    playerRequestUpdated.value = true;  // This will trigger the `watchEffect` to re-fetch the data

  } catch (error) {
    console.error("Error accepting request:", error);
    handleApiError(error, "Failed to confirm receipt.");
  }
}


async function cancelRequest(id, gameName) {
  try {
    await axiosClient.put(`/borrowrequests/${id}/boardGameCopy?confirmOrCancel=cancel`);
    alert(`Premature Cancelling: confirms ${gameName} borrowing was cancelled and returned`);
    // Trigger a refresh after the request is accepted
    playerRequestUpdated.value = true;  // This will trigger the `watchEffect` to re-fetch the data

  } catch (error) {
    console.error("Error accepting request:", error);
    handleApiError(error, "Failed to cancel request.");
  }
}


function canConfirmGotGame(status, startDate, endDate){
  console.log(today, status, status === "Accepted" , today >= startDate, today < endDate);
  return status === "Accepted" && today >= startDate && today <= endDate
}

function canCancelRequest(status, startDate){
  return (status === "Accepted" || status === "InProgress") && today >= startDate
}

function isRequestInactive(status, startDate){
  return status === "Done" || status === "Denied"
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

        <!-- Left Sidebar: Tabs -->
        <div class="col-md-3">
          <ul class="nav flex-column nav-pills">
            <li
                v-for="tab in tabs"
                :key="tab.key"
                class="nav-item"
                v-if="!tab.ownerOnly || authStore.user.isAOwner"
            >
              <RouterLink
                  :to="{ name: 'borrowRequestMenu', query: { ...route.query, tab: tab.key } }"
                  class="nav-link"
                  :class="{ 'active bg-success': activeTab === tab.key, 'bg-secondary': activeTab !== tab.key }"
              >
                {{ tab.label }}
              </RouterLink>
            </li>
          </ul>
        </div>


        <!-- Right Content Area -->
        <div class="col-md-9">

          <div v-if="activeTab === 'mine'">
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
                <tr v-for="(request, index) in playerRequests" :key="request.requestId"
                    :style="{color: isRequestInactive(request.requestStatus, request.startOfLoan) ? 'lightGrey' : 'grey'}"
                >
                  <td>{{ request.specificGameName }}</td>
                  <td>{{ request.startOfLoan }}</td>
                  <td>{{ request.endOfLoan}}</td>
                  <td>
                  <span class="badge" :class="getStatusBadgeClass(request.requestStatus)">
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

          <div v-else-if="activeTab === 'incoming'">
            <div>
              <h1>Manage my borrow requests</h1>
              <br>
              <table class="table">
                <thead>
                <tr>
                  <th>Game requested</th>
                  <th>Borrower name</th>
                  <th>Start of loan</th>
                  <th>End of loan</th>
                  <th>Action</th>
                </tr>
                </thead>
                <tbody >
                <tr v-for="(request, index) in ownerRequests" :key="request.requestId"
                    :style="{color: isRequestDone(request.requestStatus) ? 'lightGrey' : 'grey'}"
                >
                  <td>{{ request.specificGameName }}</td>
                  <td>{{ request.borrowerName }}</td>
                  <td>{{ request.startOfLoan }}</td>
                  <td>{{ request.endOfLoan}}</td>
                  <td v-if="shouldShowButton(request.requestStatus)">
                    <button class="btn btn-info me-2"
                            id="acceptRequestButton"
                            @click="acceptRequest(request.requestId, request.borrowerName)"
                    >
                      Accept
                    </button>

                    <button class="btn btn-info me-2 "
                            id="declineRequestButton"
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

/* === Clean table layout and alignment === */
.table {
  table-layout: fixed;
  width: 100%;
  border-collapse: collapse;
}

/* Header + cell styles */
.table th,
.table td {
  vertical-align: middle;
  text-align: left;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding: 12px;
  border-bottom: 1px solid #dee2e6;
}

/* Optional row hover effect */
.table tbody tr:hover {
  background-color: #f8f9fa;
  cursor: pointer;
}

/* Column widths for 4-column table (See My Borrow Requests) */
.table th:nth-child(1),
.table td:nth-child(1) {
  width: 30%;
}

.table th:nth-child(2),
.table td:nth-child(2) {
  width: 20%;
}

.table th:nth-child(3),
.table td:nth-child(3) {
  width: 20%;
}

.table th:nth-child(4),
.table td:nth-child(4) {
  width: 15%;
}

/* Column widths for 6-column table (Manage Incoming Requests) */
.table th:nth-child(5),
.table td:nth-child(5) {
  width: 10%;
}

.table th:nth-child(6),
.table td:nth-child(6) {
  width: 25%;
}


</style>

