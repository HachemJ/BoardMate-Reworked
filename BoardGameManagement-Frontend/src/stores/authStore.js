import { ref, watch } from "vue";
import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", () => {
  const storedUser = JSON.parse(localStorage.getItem("authUser"));

  const user = ref(
    storedUser?.isAuthenticated
      ? storedUser
      : {
          id: null,
          username: null,
          email: null,
          isAOwner: false,
          isAuthenticated: false,
        }
  );

  function login(id, username, email, isAOwner) {
    user.value = {
      id,
      username,
      email,
      isAOwner,
      isAuthenticated: true,
    };
    localStorage.setItem("authUser", JSON.stringify(user.value));
  }

  function logout() {
    user.value = {
      id: null,
      username: null,
      email: null,
      isAOwner: false,
      isAuthenticated: false,
    };
    localStorage.removeItem("authUser");
  }

  watch(
    user,
    (newVal) => {
      if (newVal.isAuthenticated) {
        localStorage.setItem("authUser", JSON.stringify(newVal));
      }
    },
    { deep: true }
  );

  return { user, login, logout };
});
