<script setup>
import { ref } from "vue";

// Components
import DefaultNavbar from "@/examples/navbars/NavbarDefault.vue";
import MaterialInput from "@/components/MaterialInput.vue";
import MaterialSwitch from "@/components/MaterialSwitch.vue";
import MaterialButton from "@/components/MaterialButton.vue";

// Track current mode
const isSignUp = ref(false);
const fullName = ref("");
const email = ref("");
const password = ref("");
const confirmPassword = ref("");

</script>

<template>
  <DefaultNavbar transparent />
  <Header>
    <div
      class="page-header align-items-start min-vh-100"
      :style="{
        backgroundImage:
          'url(https://images.unsplash.com/photo-1497294815431-9365093b7331?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1950&q=80)'
      }"
      loading="lazy"
    >
      <span class="mask bg-gradient-dark opacity-6"></span>
      <div class="container my-auto">
        <div class="row">
          <div class="col-lg-4 col-md-8 col-12 mx-auto">
            <div class="card z-index-0 fadeIn3 fadeInBottom">
              <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
                <div class="bg-gradient-success shadow-success border-radius-lg py-3 pe-1">
                  <h4 class="text-white font-weight-bolder text-center mt-2 mb-0">
                    {{ isSignUp ? "Sign up" : "Sign in" }}
                  </h4>
                  <div class="row mt-3">
                    <div class="col-2 text-center ms-auto">
                      <a class="btn btn-link px-3" href="javascript:;">
                        <i class="fa fa-facebook text-white text-lg"></i>
                      </a>
                    </div>
                    <div class="col-2 text-center px-1">
                      <a class="btn btn-link px-3" href="javascript:;">
                        <i class="fa fa-github text-white text-lg"></i>
                      </a>
                    </div>
                    <div class="col-2 text-center me-auto">
                      <a class="btn btn-link px-3" href="javascript:;">
                        <i class="fa fa-google text-white text-lg"></i>
                      </a>
                    </div>
                  </div>
                </div>
              </div>

              <div class="card-body">
                <form role="form" class="text-start">

                    <MaterialInput
                      v-if="isSignUp"
                      v-model="fullName"
                      class="input-group-static mb-4"
                      label="Name"
                      placeholder="Enter Full Name"
                    />

                    <MaterialInput
                      v-model="email"
                      class="input-group-static mb-4"
                      type="email"
                      label="Email"
                      placeholder="Email"
                    />

                    <MaterialInput
                      v-model="password"
                      class="input-group-static mb-4"
                      type="password"
                      label="Password"
                      placeholder="Password"
                    />

                    <MaterialInput
                      v-if="isSignUp"
                      v-model="confirmPassword"
                      class="input-group-static mb-4"
                      type="password"
                      label="Confirm Password"
                      placeholder="Confirm Password"
                    />


                  <MaterialSwitch
                    v-if="!isSignUp"
                    class="d-flex align-items-center mb-3"
                    id="rememberMe"
                    labelClass="mb-0 ms-3"
                    checked
                    >Remember me</MaterialSwitch
                  >

                  <div class="text-center">
                    <MaterialButton
                      class="my-4 mb-2"
                      variant="gradient"
                      color="success"
                      fullWidth
                    >
                      {{ isSignUp ? "Sign up" : "Sign in" }}
                    </MaterialButton>
                  </div>

                  <!-- Toggle -->
                  <p class="mt-4 text-sm text-center">
                    {{ isSignUp ? "Already have an account?" : "Don't have an account?" }}
                    <a
                      href="#"
                      @click.prevent="isSignUp = !isSignUp"
                      class="text-success text-gradient font-weight-bold"
                    >
                      {{ isSignUp ? "Sign in" : "Sign up" }}
                    </a>
                  </p>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Header>
</template>
