import { ref, watch } from "vue";
import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", () => {
  const storedUser = JSON.parse(localStorage.getItem("authUser")) || {
    id: null,
    username: null,
    email: null,
    isAOwner: false,
    isAuthenticated: false,
  };

  const user = ref(storedUser);

  function login(id, username, email, isAOwner) {
    user.value.id = id;
    user.value.username = username;
    user.value.email = email;
    user.value.isAOwner = isAOwner;
    user.value.isAuthenticated = true;
  }

  function logout() {
    user.value.id = null;
    user.value.username = null;
    user.value.email = null;
    user.value.isAOwner = false;
    user.value.isAuthenticated = false;
  }

  watch(
    user,
    (newVal) => {
      localStorage.setItem("authUser", JSON.stringify(newVal));
    },
    { deep: true }
  );

  return { user, login, logout };
});
