<script setup>
import { onMounted, onUnmounted } from "vue";
import NavLanding from "@/components/NavLanding.vue";

const body = document.getElementsByTagName("body")[0];
onMounted(() => {
  body.classList.add("presentation-page");
  body.style.overflow = "hidden"; // one screen
});
onUnmounted(() => {
  body.classList.remove("presentation-page");
  body.style.overflow = "";
});
</script>

<template>
  <!-- Landing-only navbar (About + Contact on right, logo/name left) -->
  <NavLanding />

  <section class="landing-hero">
    <!-- background layers -->
    <div class="layer bg-base fade-layer"></div>
    <div class="layer bg-gradient fade-layer"></div>
    <div class="layer bg-noise fade-layer"></div>
    <div class="layer bg-vignette fade-layer"></div>

    <div class="container position-relative content" style="z-index:2;">
      <div class="hero-grid">
        <!-- LEFT -->
        <div class="left-col">
          <h1 class="hero-heading reveal delay-040">Find your next board game night</h1>
          <p class="hero-sub reveal delay-080">
            Borrow from local owners, host game nights, and build your community.
          </p>

          <div class="cta-row reveal delay-120">
            <router-link to="/pages/landing-pages/basic" class="btn btn-ghost btn-raise">
              Get Started
            </router-link>
            <router-link to="/pages/playerboardgame" class="btn btn-ghost btn-raise">
              Explore Games
            </router-link>
          </div>
        </div>

        <!-- RIGHT (not clickable) -->
        <div class="right-col">
          <div class="feature-card reveal delay-160" aria-hidden="true">
            <div class="icon-bubble">🎲</div>
            <div>
              <h5>Browse Games</h5>
              <p>Discover titles nearby and see who owns each copy.</p>
            </div>
          </div>

          <div class="feature-card reveal delay-200" aria-hidden="true">
            <div class="icon-bubble">🤝</div>
            <div>
              <h5>Borrow or Lend</h5>
              <p>Request a borrow, track status, and leave trustworthy reviews.</p>
            </div>
          </div>

          <div class="feature-card reveal reveal-slow delay-240" aria-hidden="true">
            <div class="icon-bubble">📅</div>
            <div>
              <h5>Join Events</h5>
              <p>Create or register for game nights with open seats.</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- footer trademark -->
    <footer class="landing-footer">© 2025 BoardMate</footer>
  </section>
</template>

<style scoped>
/* ===== Canvas (no variables) ===== */
.landing-hero{
  position:relative; height:100vh; overflow:hidden;
  display:flex; align-items:center; justify-content:center;
  background:#14171d; /* base charcoal */
}
.layer{position:absolute; inset:0;}
.bg-base{background:#14171d;}
.bg-gradient{
  /* subtle spotlight so white text reads and feels premium, no blue */
  background:
      radial-gradient(1100px 760px at 26% 60%, rgba(245,247,250,.36) 0%, rgba(245,247,250,.2) 38%, rgba(245,247,250,0) 66%),
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
  background: radial-gradient(85% 70% at 50% 60%, transparent 0%, rgba(0,0,0,.35) 70%, rgba(0,0,0,.72) 100%);
}

/* prevent scroll footer overlap when viewport is short */
.landing-footer{
  position: absolute;
  bottom: 12px; left: 0; right: 0;
  text-align: center;
  color: #AEB6C5;
  font-size: 12px;
  opacity: .85;
  user-select: none;
}

/* ===== Layout ===== */
.content{max-width:1200px; width:100%; padding:0 24px;}
.hero-grid{display:grid; grid-template-columns: 1.1fr .9fr; gap: 36px; align-items:center;}

/* ===== Left (title + sub + ctas) ===== */
.hero-heading{
  margin:0 0 .6rem; line-height:1.04;
  font-family: "Poppins", "Inter", "Segoe UI", system-ui, -apple-system, sans-serif;
  font-weight: 900;
  letter-spacing: -0.6px;
  color:#f6f7fb;                        /* bright neutral */
  font-size: clamp(30px, 6.2vw, 64px);
  text-shadow: 0 0 1px rgba(255,255,255,0.03);
}
.hero-sub{
  margin:0 0 1.2rem;
  font-family: "Inter", "Poppins", "Segoe UI", system-ui, -apple-system, sans-serif;
  font-weight: 400;
  color:#c9ced8;                        /* soft slate */
  font-size: clamp(14px, 1.6vw, 18px);
  opacity:.95;
}

/* ===== Buttons (both “Explore Games” ghost style) ===== */
.cta-row{display:flex; gap:12px; flex-wrap:wrap;}
.btn{
  padding:.66rem 1.1rem; border-radius:12px; font-weight:800; letter-spacing:.2px;
  transition:
      transform .18s ease,
      box-shadow .18s ease,
      filter .18s ease,
      background .18s ease,
      color .18s ease,
      border-color .18s ease;
}
.btn-raise:hover{transform:translateY(-2px); box-shadow:0 12px 26px rgba(0,0,0,.22);}
.btn-ghost{
  background:#ffffff; color:#0b0d10; border:1px solid rgba(0,0,0,.10);
  box-shadow: 0 6px 16px rgba(0,0,0,.12);
  text-decoration: none;
}
.btn-ghost:hover{
  filter:brightness(1.03);
  border-color:rgba(0,0,0,.2);
  box-shadow: 0 10px 22px rgba(0,0,0,.18);
}

/* ===== Right (feature cards) — not clickable ===== */
.right-col{display:flex; flex-direction:column; gap:18px;}
.feature-card{
  display:flex; gap:14px; align-items:flex-start;
  min-height:136px; padding:22px 24px;
  background:#101318;                 /* deep, not pure black */
  color:#e9edf5;                       /* soft white */
  border:1px solid #1e2433;            /* slate border */
  border-radius:18px;
  box-shadow: 0 4px 14px rgba(0,0,0,.28);
  transform:translateY(0) scale(1);
  transition:
      transform .32s cubic-bezier(.2,.8,.2,1),
      box-shadow .28s ease,
      background .28s ease,
      color .28s ease,
      border-color .28s ease;
  cursor: default;                      /* feels non-interactive */
}
.feature-card:hover{
  transform: translateY(-10px) scale(1.035);  /* bigger on hover */
  background:#171b22;                          /* slightly lighter, still dark */
  border-color:#2b3244;
  box-shadow: 0 10px 26px rgba(0,0,0,.26);     /* reduced/lower spread look */
}
.icon-bubble{
  width:50px; height:50px; border-radius:12px;
  display:inline-flex; align-items:center; justify-content:center;
  background:#181b22;
  border:1px solid #2a3242;
  font-size:22px; color:#ffffff;
  transition: all .28s ease;
}
.feature-card:hover .icon-bubble{
  background:#202533;
  border-color:#3b4358;
  transform: scale(1.05);
}
.feature-card h5{margin:.1rem 0 .25rem; font-weight:800; color:inherit; font-size:1.02rem;}
.feature-card p{margin:0; color:inherit; opacity:.92;}

/* ===== Slower progressive reveal ===== */
@keyframes fadeUpSlow {
  0%   { opacity: 0; transform: translateY(22px); }
  40%  { opacity: 0.25; }
  100% { opacity: 1; transform: translateY(0); }
}
.reveal{
  opacity:0;
  animation-name: fadeUpSlow;
  animation-duration: 1.9s;
  animation-timing-function: cubic-bezier(0.16, 1, 0.3, 1);
  animation-fill-mode: both;
  will-change: opacity, transform;
}
.reveal-slow{ animation-duration: 2.2s; }
.delay-040 { animation-delay: .40s; }
.delay-080 { animation-delay: .80s; }
.delay-120 { animation-delay: 1.20s; }
.delay-160 { animation-delay: 1.60s; }
.delay-200 { animation-delay: 2.00s; }
.delay-240 { animation-delay: 2.40s; }

/* background layers fade a bit sooner */
@keyframes fadeInBg{ from{opacity:0; transform:scale(1.01);} to{opacity:1; transform:scale(1);} }
.fade-layer{ opacity:0; animation: fadeInBg 1.6s ease-out forwards; animation-delay:.2s; }

/* ===== Responsive ===== */
@media (max-width: 1024px){
  .landing-hero{height:auto; min-height:100vh;}
  .hero-grid{grid-template-columns: 1fr; gap: 20px;}
}
</style>
