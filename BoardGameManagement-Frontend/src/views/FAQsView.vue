<script setup>

import DefaultNavbar from "@/examples/navbars/NavbarDefault.vue";

</script>

<template>
  <div>
    <!-- Top Navigation Bar -->
    <DefaultNavbar />

    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-10">

          <!-- FAQs content -->
          <h2 class="mb-4">Frequently Asked Questions</h2>
          <div class="accordion" id="faqAccordion">

            <div class="accordion-item">
              <h3 class="accordion-header" id="headingOne">
                  1. How can I join an event?
              </h3>
                <div class="accordion-body">
                  To join an event, navigate to Menu -> Event Menu, select the Browse Available Events tab on the left of the page, choose the event in that table that you wish to join, and click on the "Join" button. You will need to log in or create an account if you haven't already.
                </div>
            </div>

            <div class="accordion-item">
              <h3 class="accordion-header" id="headingTwo">
                  2. How can I become a game owner?
              </h3>
                <div class="accordion-body">
                  To become a game owner, you need to create an account selecting the option "I want to be an owner". If you already have an account, you can update your profile to become an owner by navigating to My Profile -> EDIT PROFILE. Once you are an owner, you can create and manage your own board game copies.
                </div>
            </div>

            <div class="accordion-item">
              <h3 class="accordion-header" id="headingThree">
                  3. How can I see all the copies of a board game?
              </h3>
                <div class="accordion-body">
                  To see all the copies of a board game type, navigate to Menu -> Board Game Menu, select the View All Board Games tab on the left of the page, and click on the board game you are interested in. You will see a list of all the copies available for that game.
                </div>
            </div>

            <div class="accordion-item">
              <h3 class="accordion-header" id="headingFour">
                  4. How can I borrow a board game copy?
              </h3>
                <div class="accordion-body">
                  To borrow a board game copy, you need to follow the 3rd instruction above. Once you see a list of board game copies, click the borrow button next to the copy you want to borrow. You will then need to fill out the start and end dates for the borrowing period. If you are the owner of the game, you will not be able to borrow button.
                </div>
            </div>

            <div class="accordion-item">
              <h3 class="accordion-header" id="headingFive">
                  5. As an owner, can I borrow a board game copy from another owner?
              </h3>
                <div class="accordion-body">
                  Absolutely! As an owner, you can borrow a board game copy from another owner.
                </div>
            </div>

            <div class="accordion-item">
              <h3 class="accordion-header" id="headingSix">
                  6. Can I create a new board game?
              </h3>
                <div class="accordion-body">
                  Yes, you can create a new board game by navigating to Menu -> Board Game Menu, selecting the View All Board Games tab on the left of the page, and clicking on the Add Board Game button. You will need to fill out the form with the board game details.
                </div>
            </div>

          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

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

.accordion-body {
  color: black;
}

</style>
