<script setup>
import { RouterLink, useRouter } from "vue-router";
import { ref, watch } from "vue";
import { useWindowsWidth } from "../../assets/js/useWindowsWidth";
import {useAuthStore} from "@/stores/authStore.js";

// images
import ArrDark from "@/assets/img/down-arrow-dark.svg";
import downArrow from "@/assets/img/down-arrow.svg";
import DownArrWhite from "@/assets/img/down-arrow-white.svg";


const props = defineProps({
  action: {
    type: Object,
    route: String,
    color: String,
    label: String,
    default: () => ({
      route: "signin",
      color: "bg-gradient-success",
      label: "Login / Sign Up"
    })
  },
  transparent: {
    type: Boolean,
    default: false
  },
  light: {
    type: Boolean,
    default: false
  },
  dark: {
    type: Boolean,
    default: false
  },
  sticky: {
    type: Boolean,
    default: false
  },
  darkText: {
    type: Boolean,
    default: false
  }
});

const router = useRouter();
const authStore = useAuthStore();

function logout() {
  authStore.logout();
  router.push("/");
}

// set arrow  color
function getArrowColor() {
  if (props.transparent && textDark.value) {
    return ArrDark;
  } else if (props.transparent) {
    return DownArrWhite;
  } else {
    return ArrDark;
  }
}

// set text color
const getTextColor = () => {
  let color;
  if (props.transparent && textDark.value) {
    color = "text-dark";
  } else if (props.transparent) {
    color = "text-white";
  } else {
    color = "text-dark";
  }
  return color;
};

// set nav color on mobile && desktop

let textDark = ref(props.darkText);
const { type } = useWindowsWidth();

if (type.value === "mobile") {
  textDark.value = true;
} else if (type.value === "desktop" && textDark.value == false) {
  textDark.value = false;
}

watch(
  () => type.value,
  (newValue) => {
    if (newValue === "mobile") {
      textDark.value = true;
    } else {
      textDark.value = false;
    }
  }
);

//constant that will need to be linked with whether user is an owner or not
const isOwner = ref(authStore.user.isAOwner);  // This will control if the RouterLink is shown or not


</script>



<template>
  <nav
    class="navbar navbar-expand-lg top-0"
    :class="{
      'z-index-3 w-100 shadow-none navbar-transparent position-absolute my-3': props.transparent,
      'my-3 blur border-radius-lg z-index-3 py-2 shadow py-2 start-0 end-0 mx-4 position-absolute mt-4': props.sticky,
      'navbar-light bg-white py-3': props.light,
      ' navbar-dark bg-gradient-dark z-index-3 py-3': props.dark
    }"
  >
    <div :class="props.transparent || props.light || props.dark ? 'container' : 'container-fluid px-0'">
      <RouterLink
        class="navbar-brand d-none d-md-block bg-dark text-white font-weight-bolder fs-4 ms-sm-1 px-5 py-1 rounded"
        :to="{ name: 'presentation' }"
        rel="tooltip"
        title="Click to navigate to home page"
        data-placement="bottom"
      >
        BGM
      </RouterLink>
      <button
        class="navbar-toggler shadow-none ms-2"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navigation"
        aria-controls="navigation"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-icon mt-2">
          <span class="navbar-toggler-bar bar1"></span>
          <span class="navbar-toggler-bar bar2"></span>
          <span class="navbar-toggler-bar bar3"></span>
        </span>
      </button>
      <div class="collapse navbar-collapse w-100 pt-3 pb-2 py-lg-0" id="navigation">
        <ul class="navbar-nav navbar-nav-hover ms-auto">
          <li class="nav-item dropdown dropdown-hover mx-2">
            <a
              role="button"
              class="nav-link ps-2 d-flex cursor-pointer align-items-center"
              :class="getTextColor()"
              id="dropdownMenuPages"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              <i class="material-icons opacity-6 me-2 text-md" :class="getTextColor()">dashboard</i>
              Menu
              <img :src="getArrowColor()" alt="down-arrow" class="arrow ms-2 d-lg-block d-none" />
              <img :src="getArrowColor()" alt="down-arrow" class="arrow ms-1 d-lg-none d-block ms-auto" />
            </a>
            <div
              class="dropdown-menu dropdown-menu-animation ms-n3 dropdown-md p-3 border-radius-xl mt-0 mt-lg-3"
              aria-labelledby="dropdownMenuPages"
            >
              <RouterLink :to="{ name: 'event' }" class="dropdown-item border-radius-md">
                <span>Event Menu</span>
              </RouterLink>
              <RouterLink :to="{ name: 'profile' }" class="dropdown-item border-radius-md">
                <span>Profile Menu</span>
              </RouterLink>


              <RouterLink
                  v-if="isOwner"
                  :to="{ name: 'owner borrow request'}"
                  class="dropdown-item border-radius-md"

              >
                <span>Manage Incoming Borrow Requests</span>
              </RouterLink>
 

              <RouterLink
                  :to="{ name: 'player borrow request'}"
                  class="dropdown-item border-radius-md"
              >
                <span>See my Borrow Requests</span>
              </RouterLink>

              <RouterLink
                v-if="isOwner"
                :to="{ name: 'ownerBoardGameMenu' }"
                class="dropdown-item border-radius-md"
              >
                <span>Board Game Menu</span>
              </RouterLink>

              <RouterLink
                v-if="!isOwner"
                :to="{ name: 'playerBoardGameMenu' }"
                class="dropdown-item border-radius-md"
              >
                <span>Board Game Menu</span>
              </RouterLink>

            </div>
          </li>
          <li class="nav-item mx-2">
            <RouterLink
              :to="{ name: 'contactus' }"
              class="nav-link ps-2 d-flex cursor-pointer align-items-center"
              :class="getTextColor()"
            >
              <i class="material-icons opacity-6 me-2 text-md" :class="getTextColor()">article</i>
              Contact Us
            </RouterLink>

          </li>

          <li class="nav-item dropdown dropdown-hover mx-2">
            <RouterLink
              :to="{ name: 'about' }"
              class="nav-link d-flex cursor-pointer align-items-center"
            >
              <svg
                width="20px"
                height="20px"
                class="material-icons me-2 opacity-6"
                viewBox="0 0 24 24"
                aria-hidden="true"
                data-testid="GitHubIcon"
                :fill="props.transparent && '#fff'"
              >
                <path
                  d="M12 1.27a11 11 0 00-3.48 21.46c.55.09.73-.28.73-.55v-1.84c-3.03.64-3.67-1.46-3.67-1.46-.55-1.29-1.28-1.65-1.28-1.65-.92-.65.1-.65.1-.65 1.1 0 1.73 1.1 1.73 1.1.92 1.65 2.57 1.2 3.21.92a2 2 0 01.64-1.47c-2.47-.27-5.04-1.19-5.04-5.5 0-1.1.46-2.1 1.2-2.84a3.76 3.76 0 010-2.93s.91-.28 3.11 1.1c1.8-.49 3.7-.49 5.5 0 2.1-1.38 3.02-1.1 3.02-1.1a3.76 3.76 0 010 2.93c.83.74 1.2 1.74 1.2 2.94 0 4.21-2.57 5.13-5.04 5.4.45.37.82.92.82 2.02v3.03c0 .27.1.64.73.55A11 11 0 0012 1.27"
                ></path>
              </svg>
              About Us
            </RouterLink>

          </li>
        </ul>
        <ul class="navbar-nav d-lg-block d-none">
          <li class="nav-item" v-if="authStore.user.isAuthenticated">
            <div class="d-flex align-items-center gap-3">
              <span class="text-black fw-bold">Hello, {{ authStore.user.username }}</span>
              <button class="btn btn-sm btn-danger mb-0" @click="logout">Sign Out</button>
            </div>
          </li>
          <li class="nav-item" v-else>
            <RouterLink
              :to="{ name: action.route }"
              class="btn btn-sm mb-0"
              :class="action.color"
            >
              {{ action.label }}
            </RouterLink>
          </li>
        </ul>
      </div>
    </div>
  </nav>
  <!-- End Navbar -->

</template>


