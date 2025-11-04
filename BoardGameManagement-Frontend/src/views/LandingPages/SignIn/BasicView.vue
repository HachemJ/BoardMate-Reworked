<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import axios from "axios";
import { useAuthStore } from "@/stores/authStore.js";
import { useRouter } from "vue-router";
import Logo from "@/assets/img/favicon.png";
import NavLanding from "@/components/NavLanding.vue";

const router = useRouter();
const authStore = useAuthStore();

/* state */
const isSignUp = ref(false);
const fullName = ref("");
const email = ref("");
const password = ref("");
const confirmPassword = ref("");
const isAOwner = ref(false);

/* inline error banner */
const errorMsg = ref(null);
let errorTimer = null;
function showError(msg) {
  errorMsg.value = msg;
  if (errorTimer) clearTimeout(errorTimer);
  errorTimer = window.setTimeout(() => (errorMsg.value = null), 5000);
}

/* axios */
const axiosClient = axios.create({ baseURL: "http://localhost:8080" });

/* landing-style canvas + no scroll */
const body = document.getElementsByTagName("body")[0];
onMounted(() => {
  body.classList.add("presentation-page");
  body.style.overflow = "hidden";
});
onUnmounted(() => {
  body.classList.remove("presentation-page");
  body.style.overflow = "";
});

/* handlers */
async function handleAuth() {
  if (isSignUp.value) {
    if (password.value !== confirmPassword.value) {
      showError("Passwords don’t match.");
      return;
    }
    try {
      await axiosClient.post("/players", {
        name: fullName.value,
        email: email.value,
        password: password.value,
        isAOwner: isAOwner.value,
      });

      // Auto-login after signup
      const { data: user } = await axiosClient.post("/players/login", {
        email: email.value,
        password: password.value,
      });
      authStore.login(user.playerID, user.name, user.email, user.isAOwner);
      router.push("/profile");
    } catch (error) {
      const status = error?.response?.status;
      const errs = error?.response?.data?.errors;
      if (Array.isArray(errs) && errs.length) {
        showError(errs.join(" · "));
      } else if (status === 401) {
        showError("Sign up succeeded but login failed (401). Check your credentials.");
      } else {
        showError("Something went wrong. Please try again.");
      }
      console.error(error);
    }
    return;
  }

  // LOGIN
  try {
    const { data: user } = await axiosClient.post("/players/login", {
      email: email.value,
      password: password.value,
    });
    authStore.login(user.playerID, user.name, user.email, user.isAOwner);
    router.push("/profile");
  } catch (error) {
    const status = error?.response?.status;
    const errs = error?.response?.data?.errors;
    if (Array.isArray(errs) && errs.length) {
      showError(errs.join(" · "));
    } else if (status === 401) {
      showError("Login failed (401): incorrect email or password.");
    } else {
      showError("Couldn’t sign you in. Please try again.");
    }
    console.error(error);
  }
}
</script>

<template>
  <!-- same navbar as landing page -->
  <NavLanding />

  <section class="auth-hero">
    <!-- background layers -->
    <div class="layer bg-base fade-layer"></div>
    <div class="layer bg-gradient fade-layer"></div>
    <div class="layer bg-noise fade-layer"></div>
    <div class="layer bg-vignette fade-layer"></div>

    <!-- centered card -->
    <div class="center">
      <div class="auth-card title-fade" style="animation-delay:.12s">
        <div class="brand-top">
          <img :src="Logo" alt="BoardMate" class="logo" />
        </div>

        <form class="form" @submit.prevent="handleAuth">
          <!-- SIGN UP extra fields -->
          <template v-if="isSignUp">
            <div class="field">
              <label class="label" for="fullName">Name</label>
              <input
                  id="fullName"
                  v-model="fullName"
                  type="text"
                  class="input"
                  placeholder="Your full name"
                  required
                  autocomplete="name"
              />
            </div>
          </template>

          <div class="field">
            <label class="label" for="email">Email</label>
            <input
                id="email"
                v-model="email"
                type="email"
                class="input"
                placeholder="you@example.com"
                autocomplete="email"
                required
            />
          </div>

          <div class="field">
            <label class="label" for="password">Password</label>
            <input
                id="password"
                v-model="password"
                type="password"
                class="input"
                autocomplete="current-password"
                required
            />
          </div>

          <template v-if="isSignUp">
            <div class="field">
              <label class="label" for="confirm">Confirm Password</label>
              <input
                  id="confirm"
                  v-model="confirmPassword"
                  type="password"
                  class="input"
                  required
                  autocomplete="new-password"
              />
            </div>

            <label class="check">
              <input type="checkbox" v-model="isAOwner" />
              <span>I want to be an owner</span>
            </label>
          </template>

          <!-- inline error banner (replaces alert boxes) -->
          <div v-if="errorMsg" class="alert-banner" role="alert">
            <div class="alert-dot"></div>
            <span class="alert-text">{{ errorMsg }}</span>
            <button class="alert-close" @click.prevent="errorMsg = null" aria-label="Dismiss">×</button>
          </div>

          <button type="submit" class="btn btn-primary btn-raise">
            {{ isSignUp ? "Create account" : "Sign in" }}
          </button>

          <div class="alt">
            <span class="muted">
              {{ isSignUp ? "Already have an account?" : "Don’t have an account?" }}
            </span>
            <a href="#" class="link" @click.prevent="isSignUp = !isSignUp">
              {{ isSignUp ? "Sign in" : "Create one" }}
            </a>
          </div>
        </form>
      </div>
    </div>
  </section>
</template>

<style scoped>
/* ===== Canvas (landing theme) ===== */
.auth-hero{
  position:relative; height:100vh; overflow:hidden;
  display:flex; align-items:center; justify-content:center;
  background:#14171d;
}
.layer{position:absolute; inset:0;}
.bg-base{background:#14171d;}
.bg-gradient{
  background:
      radial-gradient(900px 650px at 50% 45%, rgba(245,247,250,.28) 0%, rgba(245,247,250,.10) 36%, rgba(245,247,250,0) 62%),
      linear-gradient(180deg, #14171d 0%, #1b2029 100%);
  animation: float 16s ease-in-out infinite alternate;
}
@keyframes float{
  0%{transform:translateY(0) translateX(0) scale(1);}
  100%{transform:translateY(-10px) translateX(6px) scale(1.01);}
}
.bg-noise{
  background-image:url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='120' height='120'%3E%3Cfilter id='n'%3E%3CfeTurbulence baseFrequency='0.75' numOctaves='2' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='120' height='120' filter='url(%23n)' opacity='0.02'/%3E%3C/svg%3E");
  background-size:120px 120px; mix-blend-mode:overlay;
}
.bg-vignette{
  background: radial-gradient(85% 70% at 50% 60%, transparent 0%, rgba(0,0,0,.35) 70%, rgba(0,0,0,.7) 100%);
}

/* ===== Positioning ===== */
.center{
  position:relative; z-index:2;
  width: 100%;
  display:flex; align-items:center; justify-content:center;
  padding: 24px;
}

/* ===== Card ===== */
.auth-card{
  width:min(520px, 92vw);
  background:#0f1217; color:#e9edf5;
  border:1px solid #1f2533; border-radius:16px;
  box-shadow: 0 10px 30px rgba(0,0,0,.35);
  padding: 22px 22px 18px;
}

/* small icon header */
.brand-top{display:flex; align-items:center; justify-content:center; margin-bottom:6px;}
.logo{width:40px; height:40px; border-radius:10px; box-shadow: 0 6px 18px rgba(0,0,0,.3);}

/* ===== Form layout ===== */
.form{display:flex; flex-direction:column; gap:14px; margin-top:6px;}
.field{display:flex; flex-direction:column; gap:6px; width:100%;}
.label{font-size:12px; font-weight:700; letter-spacing:.2px; color:#c8cfdb;}
.input{
  width:100%; box-sizing:border-box; display:block;
  background:#0c0f13; color:#e7ebf3;
  border:1px solid #202636; border-radius:12px;
  padding:.75rem .9rem; outline:none;
  transition:border-color .2s ease, box-shadow .2s ease, background .2s ease;
}
.input::placeholder{color:#8f98a8;}
.input:focus{
  border-color:#3a4256;
  box-shadow: 0 0 0 3px rgba(70,100,255,.12);
  background:#0d1116;
}
.check{display:flex; align-items:center; gap:8px; margin-top:6px; color:#c4cada; font-size:13px;}
.check input{accent-color:#ffffff;}

/* inline alert banner */
.alert-banner{
  display:flex; align-items:center; gap:10px;
  background: #1b2029;
  color:#e9edf5;
  border:1px solid #2a3140;
  border-radius:12px;
  padding:.65rem .75rem;
  box-shadow: 0 8px 20px rgba(0,0,0,.25);
  animation: fadeIn .25s ease-out;
}
.alert-dot{
  width:10px; height:10px; border-radius:50%;
  background:#ff6b6b; box-shadow:0 0 10px rgba(255,107,107,.6);
  flex: 0 0 auto;
}
.alert-text{ flex:1 1 auto; font-size:13px; line-height:1.3; }
.alert-close{
  all:unset; cursor:pointer; padding:.2rem .45rem; border-radius:8px; color:#cdd3df;
}
.alert-close:hover{ background: rgba(255,255,255,.06); }

/* button */
.btn{
  margin-top:8px;
  width:100%;
  padding:.85rem 1rem; border-radius:12px; font-weight:900; letter-spacing:.3px;
  transition:transform .18s ease, box-shadow .18s ease, filter .18s ease, background .18s ease, color .18s ease, border-color .18s ease;
}
.btn-raise:hover{transform:translateY(-2px); box-shadow:0 12px 28px rgba(0,0,0,.28);}
.btn-primary{
  background:#ffffff; color:#0b0d10; border:1px solid rgba(0,0,0,.08);
  box-shadow: 0 6px 16px rgba(0,0,0,.14);
}
.btn-primary:hover{
  filter:brightness(1.03);
  border-color:rgba(0,0,0,.2);
  box-shadow: 0 10px 26px rgba(0,0,0,.2);
}

/* link row */
.alt{display:flex; gap:8px; align-items:center; justify-content:center; margin-top:12px;}
.muted{color:#9aa2b2; font-size:13px;}
.link{color:#e9edf5; text-decoration:none; border-bottom:1px dashed rgba(233,237,245,.35); padding-bottom:2px;}
.link:hover{opacity:.85;}

/* animations */
@keyframes fadeUp{0%{opacity:0;transform:translateY(16px);}40%{opacity:.3;}100%{opacity:1;transform:translateY(0);}}
.title-fade{animation:fadeUp 1.15s cubic-bezier(0.16,1,0.3,1) both;}
.fade-layer{opacity:0; animation:fadeIn 1.4s ease-out forwards;}
@keyframes fadeIn{from{opacity:0; transform:scale(1.01);} to{opacity:1; transform:scale(1);}}
</style>
