<!-- src/views/LandingPages/ContactUs/ContactView.vue -->
<script setup>
import { ref } from "vue";
import NavLanding from "@/components/NavLandingSigned.vue";               // glossy public navbar
import contactImg from "@/assets/contactus.jpg";
import NavLandingSigned from "@/components/NavLandingSigned.vue";                    // your existing image

const name = ref("");
const email = ref("");
const message = ref("");
const sending = ref(false);

const banner = ref({ type: "", text: "" }); // "success" | "error" | ""

function validateEmail(v) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v);
}

async function sendMessage() {
  banner.value = { type: "", text: "" };

  // simple validation
  if (!name.value.trim() || !email.value.trim() || !message.value.trim()) {
    banner.value = { type: "error", text: "Please fill out all fields." };
    return;
  }
  if (!validateEmail(email.value)) {
    banner.value = { type: "error", text: "Please enter a valid email address." };
    return;
  }

  try {
    sending.value = true;
    // TODO: POST to your backend endpoint here if you add one:
    // await axios.post("/contact", { name: name.value, email: email.value, message: message.value });

    // simulate network
    await new Promise(r => setTimeout(r, 650));

    banner.value = { type: "success", text: "Thanks! Your message was sent. We’ll get back to you soon." };
    name.value = "";
    email.value = "";
    message.value = "";
  } catch (e) {
    banner.value = { type: "error", text: "Something went wrong. Please try again in a moment." };
    console.error(e);
  } finally {
    sending.value = false;
  }
}
</script>

<template>
  <NavLandingSigned />

  <section class="contact-hero">
    <!-- background layers (same vibe as landing/auth/profile) -->
    <div class="layer bg-base"></div>
    <div class="layer bg-gradient"></div>
    <div class="layer bg-noise"></div>
    <div class="layer bg-vignette"></div>

    <div class="content">
      <div class="shell">
        <!-- Left: Illustration -->
        <aside class="art">
          <img :src="contactImg" alt="Contact illustration" />
        </aside>

        <!-- Right: Card form -->
        <main class="card">
          <header class="card-head">
            <h1>Contact us</h1>
            <p class="sub">Questions, feedback, or issues — we’re here to help.</p>
          </header>

          <div v-if="banner.type" class="banner" :class="`banner--${banner.type}`">
            {{ banner.text }}
          </div>

          <form @submit.prevent="sendMessage" class="form">
            <label class="field">
              <span class="label">Full name</span>
              <input v-model="name" type="text" class="input" placeholder="Jane Doe" autocomplete="name" />
            </label>

            <label class="field">
              <span class="label">Email</span>
              <input v-model="email" type="email" class="input" placeholder="jane@boardmate.app" autocomplete="email" />
            </label>

            <label class="field">
              <span class="label">Message</span>
              <textarea v-model="message" class="textarea" rows="6" placeholder="Tell us how we can help…"></textarea>
            </label>

            <button class="btn btn-primary" :disabled="sending">
              {{ sending ? "Sending…" : "Send message" }}
            </button>
          </form>
        </main>
      </div>
    </div>
  </section>
</template>

<style scoped>
/* ===== Canvas (shared theme) ===== */
.contact-hero { position: relative; min-height: 100vh; background:#14171d; }
.layer { position:absolute; inset:0; }
.bg-base { background:#14171d; }
.bg-gradient{
  background:
      radial-gradient(1100px 750px at 18% 18%, rgba(245,247,250,.28) 0%, rgba(245,247,250,.10) 36%, rgba(245,247,250,0) 65%),
      linear-gradient(180deg, #14171d 0%, #1b2029 100%);
  animation: float 16s ease-in-out infinite alternate;
}
@keyframes float{ 0%{transform:translateY(0) translateX(0) scale(1)} 100%{transform:translateY(-10px) translateX(6px) scale(1.01)} }
.bg-noise{
  background-image:url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='120' height='120'%3E%3Cfilter id='n'%3E%3CfeTurbulence baseFrequency='0.75' numOctaves='2' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='120' height='120' filter='url(%23n)' opacity='0.02'/%3E%3C/svg%3E");
  background-size:120px 120px; mix-blend-mode:overlay;
}
.bg-vignette{ background: radial-gradient(85% 70% at 50% 60%, transparent 0%, rgba(0,0,0,.35) 70%, rgba(0,0,0,.7) 100%); }

/* ===== Layout ===== */
.content{ position:relative; z-index:2; padding:110px 20px 60px; }
.shell{
  margin:0 auto; max-width:1100px; display:grid; gap:18px;
  grid-template-columns: 1.05fr 1fr;
}
@media (max-width: 980px){
  .shell{ grid-template-columns: 1fr; }
}

/* Illustration */
.art{
  background:#0f1217; border:1px solid #1f2533; border-radius:16px;
  box-shadow:0 10px 30px rgba(0,0,0,.35); overflow:hidden; display:flex; align-items:center; justify-content:center;
}
.art img{ width:100%; height:100%; object-fit:cover; display:block; }

/* Card */
.card{
  background:#0f1217; color:#e9edf5;
  border:1px solid #1f2533; border-radius:16px;
  box-shadow:0 10px 30px rgba(0,0,0,.35);
  padding:18px 18px 16px;
}
.card-head h1{ margin:0 0 6px; font-size:24px; font-weight:900; letter-spacing:.2px; }
.sub{ margin:0 0 10px; color:#b7c0cf; }

/* Banner */
.banner{
  border-radius:12px; padding:10px 12px; font-weight:700; margin-bottom:10px; border:1px solid transparent;
}
.banner--success{ background:#142319; color:#dcffe7; border-color:#225232; }
.banner--error{ background:#2b1618; color:#ffd8de; border-color:#5a2a2f; }

/* Form */
.form{ display:flex; flex-direction:column; gap:12px; }
.field{ display:flex; flex-direction:column; gap:6px; }
.label{ font-size:12px; font-weight:800; color:#c8cfdb; }
.input, .textarea{
  background:#0c0f13; color:#e7ebf3; border:1px solid #202636; border-radius:12px;
  padding:.8rem .9rem; outline:none; transition:border-color .2s, box-shadow .2s, background .2s;
}
.input::placeholder, .textarea::placeholder{ color:#8f98a8; }
.input:focus, .textarea:focus{
  border-color:#3a4256; box-shadow:0 0 0 3px rgba(70,100,255,.12); background:#0d1116;
}

/* Button */
.btn{
  align-self:flex-start; margin-top:6px;
  padding:.85rem 1rem; border-radius:12px; font-weight:900; letter-spacing:.3px; border:1px solid rgba(0,0,0,.08);
  transition:transform .18s ease, filter .18s ease, box-shadow .18s ease;
}
.btn-primary{ background:#fff; color:#0b0d10; box-shadow:0 6px 16px rgba(0,0,0,.14); }
.btn-primary:hover{ transform:translateY(-2px); filter:brightness(1.03); }
.btn[disabled]{ opacity:.6; pointer-events:none; }
</style>
