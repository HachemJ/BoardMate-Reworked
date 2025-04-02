import { ref, computed } from 'vue'
 import { defineStore } from 'pinia'
 
 export const useAuthStore = defineStore('auth', () => {
     const user = ref(
         {
             username: null,
             userEmail: null,
             isAuthenticated: false,
         }
     )
 
     function login(name, email, pwd) {
         if (!pwd) return;
         user.value.username = name;
         user.value.userEmail = email;
         user.value.isAuthenticated = true;
     }
 
     function logout() {
         user.value.username = null;
         user.value.userEmail = null;
         user.value.isAuthenticated = false;
     }
 
     return { user, login, logout }
 })
