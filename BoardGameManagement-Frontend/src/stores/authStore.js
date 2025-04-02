import { ref, watch } from "vue";
import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", () => {
  // Load from localStorage or use default
  const storedUser = JSON.parse(localStorage.getItem("authUser")) || {
    username: null,
    email: null,
    isAuthenticated: false,
  };

  const user = ref(storedUser);

  function login(username, email) {
    user.value.username = username;
    user.value.email = email;
    user.value.isAuthenticated = true;
  }

  function logout() {
    user.value.username = null;
    user.value.email = null;
    user.value.isAuthenticated = false;
  }

  // Persist every time user changes
  watch(
    user,
    (newVal) => {
      localStorage.setItem("authUser", JSON.stringify(newVal));
    },
    { deep: true }
  );

  return { user, login, logout };
});
