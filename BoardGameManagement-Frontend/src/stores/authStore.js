import { ref, watch } from "vue";
import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", () => {
  const storedUser = JSON.parse(localStorage.getItem("authUser"));
  const storedMode = localStorage.getItem("authMode");

  const user = ref(
    storedUser?.isAuthenticated
      ? storedUser
      : storedMode === "guest"
      ? {
          id: null,
          username: "Guest",
          email: null,
          isAOwner: false,
          isAuthenticated: false,
          isGuest: true,
        }
      : {
          id: null,
          username: null,
          email: null,
          isAOwner: false,
          isAuthenticated: false,
          isGuest: false,
        }
  );

  function login(id, username, email, isAOwner) {
    user.value = {
      id,
      username,
      email,
      isAOwner,
      isAuthenticated: true,
      isGuest: false,
    };
    localStorage.setItem("authUser", JSON.stringify(user.value));
    localStorage.removeItem("authMode");
  }

  function enterGuest() {
    user.value = {
      id: null,
      username: "Guest",
      email: null,
      isAOwner: false,
      isAuthenticated: false,
      isGuest: true,
    };
    localStorage.setItem("authMode", "guest");
    localStorage.setItem("authUser", JSON.stringify(user.value));
  }

  function logout() {
    user.value = {
      id: null,
      username: null,
      email: null,
      isAOwner: false,
      isAuthenticated: false,
      isGuest: false,
    };
    localStorage.removeItem("authUser");
    localStorage.removeItem("authMode");
  }

  watch(
    user,
    (newVal) => {
      if (newVal.isAuthenticated) {
        localStorage.setItem("authUser", JSON.stringify(newVal));
      } else if (newVal.isGuest) {
        localStorage.setItem("authUser", JSON.stringify(newVal));
      }
    },
    { deep: true }
  );

  return { user, login, enterGuest, logout };
});
