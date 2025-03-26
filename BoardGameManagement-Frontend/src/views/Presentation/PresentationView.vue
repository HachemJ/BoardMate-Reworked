<script setup>
import { onMounted, onUnmounted } from "vue";

import NavbarDefault from "../..//examples/navbars/NavbarDefault.vue";

import mainpage from "@/assets/main.jpg";


//hooks
const body = document.getElementsByTagName("body")[0];
onMounted(() => {
  body.classList.add("presentation-page");
  body.classList.add("bg-gray-200");
});
onUnmounted(() => {
  body.classList.remove("presentation-page");
  body.classList.remove("bg-gray-200");
});
</script>

<template>
  <div class="container position-sticky z-index-sticky top-0">
    <div class="row">
      <div class="col-12">
        <NavbarDefault :sticky="true" />
      </div>
    </div>
  </div>
<Header>
  <div
    class="page-header vh-100 position-relative"
    :style="`background-image: url(${mainpage}); background-size: cover; background-position: center; background-repeat: no-repeat;`"
  >
    <!-- Dark overlay -->
    <div
      class="position-absolute top-0 start-0 w-100 h-100"
      style="background-color: rgba(0, 0, 0, 0.7); z-index: 1;"
    ></div>

    <!-- Content goes here, above the overlay -->
    <div class="container position-relative" style="z-index: 2;">
      <div class="row">
        <div class="col-lg-7 text-center mx-auto position-relative">
          <h1 class="text-white pt-3 mt-n5 me-2">
            Welcome to BGM
          </h1>
          <p class="text-white">
            Please Login or Create an Account to Get Started
          </p>
        </div>
      </div>
    </div>
  </div>
</Header>

</template>
