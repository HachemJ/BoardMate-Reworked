import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useAuthStore = defineStore('auth', () => {
  const user = ref(JSON.parse(localStorage.getItem("user")) || null);

  function login(name, email, isAOwner) {
    user.value = { name, email, isAOwner };
    localStorage.setItem("user", JSON.stringify(user.value)); // Persist
  }

  function logout() {
    user.value = null;
    localStorage.removeItem("user"); // Clear persisted user
  }

  return { user, login, logout };
});
