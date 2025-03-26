<template>
  <div class="about-us-page">
    <DefaultNavbar /> <!-- Include the navigation bar here -->
    <div class="container">
      <h1>About Us</h1>
      <p>
        The Board Game Management Application is designed to help board game enthusiasts connect, share their games, and organize gaming events. The platform allows users to lend, borrow, and play board games while fostering a gaming community.
      </p>
    </div>
  </div>
</template>


<script>

import DefaultNavbar from '@/examples/navbars/NavbarDefault.vue';

export default {
  name: 'AboutUs',
  components: {
    DefaultNavbar
  }
};
</script>


<style scoped>
.about-us-page {
  padding: 20px;
  background-color: #f5f5f5; /* Light grey background */
  min-height: 100vh; /* Full view height */
}

.container {
  max-width: 1000px; /* or even 100% if you want full width */
  margin: auto;
  padding: 40px;
  background: white;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

</style>
