<script setup>
import { ref } from "vue";
import axios from "axios";
import { useAuthStore } from "@/stores/authStore.js";
import { useRouter } from "vue-router";
const router = useRouter();

import DefaultNavbar from "@/examples/navbars/NavbarDefault.vue";
import MaterialSwitch from "@/components/MaterialSwitch.vue";
import MaterialButton from "@/components/MaterialButton.vue";

const isSignUp = ref(false);
const fullName = ref("");
const email = ref("");
const password = ref("");
const confirmPassword = ref("");

const axiosClient = axios.create({
  baseURL: "http://localhost:8080",
});

const authStore = useAuthStore();

async function handleAuth() {
  if (isSignUp.value) {
    // --- SIGNUP MODE ---
    if (password.value !== confirmPassword.value) {
      alert("Passwords do not match.");
      return;
    }

    try {
      const res = await axiosClient.post("/players", {
        name: fullName.value,
        email: email.value,
        password: password.value,
        isAOwner: false // always starts as a regular player
      });

      alert("Signup successful! Please log in.");
      isSignUp.value = false; // Switch back to login view
    } catch (error) {
      console.error("Signup failed:", error);
      alert("Signup failed: " + (error.response?.data?.message || error.message));
    }
  } else {
    // --- LOGIN MODE ---
    try {
      const res = await axiosClient.post("/players/login", {
        email: email.value,
        password: password.value,
      });

      const user = res.data;
      authStore.login(user.name, user.email, user.isAOwner);

      alert("Logged in successfully!");
      console.log("Logged in user:", authStore.user);
      router.push("/profile");
    } catch (error) {
      alert("Login failed: " + (error.response?.data?.message || error.message));
    }
  }
}
</script>


<template>
  <DefaultNavbar transparent />
  <Header>
    <div
      class="page-header align-items-start min-vh-100"
      :style="{
        backgroundImage:
          'url(https://images.unsplash.com/photo-1497294815431-9365093b7331?auto=format&fit=crop&w=1950&q=80)'
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
                </div>
              </div>

              <div class="card-body">
                <form role="form" class="text-start">
                  <div class="mb-4" v-if="isSignUp">
                    <label>Name</label>
                    <input
                      v-model="fullName"
                      type="text"
                      class="form-control"
                      placeholder="Enter Full Name"
                    />
                  </div>

                  <div class="mb-4">
                    <label>Email</label>
                    <input
                      v-model="email"
                      type="email"
                      class="form-control"
                      placeholder="Email"
                    />
                  </div>

                  <div class="mb-4">
                    <label>Password</label>
                    <input
                      v-model="password"
                      type="password"
                      class="form-control"
                      placeholder="Password"
                    />
                  </div>

                  <div class="mb-4" v-if="isSignUp">
                    <label>Confirm Password</label>
                    <input
                      v-model="confirmPassword"
                      type="password"
                      class="form-control"
                      placeholder="Confirm Password"
                    />
                  </div>

                  <MaterialSwitch
                    v-if="!isSignUp"
                    class="d-flex align-items-center mb-3"
                    id="rememberMe"
                    labelClass="mb-0 ms-3"
                    checked
                  >
                    Remember me
                  </MaterialSwitch>

                  <div class="text-center">
                     <MaterialButton
                       class="my-4 mb-2"
                       variant="gradient"
                       color="success"
                       fullWidth
                       @click.prevent="handleAuth"
                     >
                       {{ isSignUp ? "Sign up" : "Sign in" }}
                     </MaterialButton>
                  </div>

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
