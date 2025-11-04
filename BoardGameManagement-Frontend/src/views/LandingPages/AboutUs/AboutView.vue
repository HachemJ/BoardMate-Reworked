<!-- src/views/LandingPages/AboutUs/AboutView.vue -->
<script setup>
import NavLandingSigned from "@/components/NavLandingSigned.vue";

/**
 * Put your real image files (square preferred) in /src/assets/team/
 * and update the 'img' paths below. If an image is missing, we render initials.
 */
const team = [
  { name: "Kathelina Wei", role: "Computer Engineering",  img: "/src/assets/team/kathelina.jpg", linkedin: "https://www.linkedin.com/in/kathelina-wei-455890317/" },
  { name: "Tingyi Chen",   role: "Computer Engineering",  img: "/src/assets/team/tingyi.jpg",    linkedin: "https://www.linkedin.com/in/tingyi-chen-a16354242/" },
  { name: "Nizar Azmi",   role: "Software Engineering",  img: "/src/assets/team/nizar.jpg",     linkedin: "https://www.linkedin.com/in/nizar-azmi-a8a3a4347/" },
  { name: "Alexander Fou",     role: "Computer Engineering",  img: "/src/assets/team/alex.jpg",      linkedin: "https://www.linkedin.com/in/alexanderfou/" },
  { name: "Maria El Hage",   role: "Computer Engineering",  img: "/src/assets/team/maria.jpg",     linkedin: "https://www.linkedin.com/in/mariael-hage/" },
  { name: "Jad El Hachem",role: "Software Engineering",  img: "/src/assets/team/jad.jpg",       linkedin: "https://www.linkedin.com/in/jad-el-hachem" },
  { name: "HongYi Ye",    role: "Computer Engineering",  img: "/src/assets/team/hongyi.jpg",    linkedin: "https://www.linkedin.com" },
];

function initials(name) {
  const parts = (name || "").trim().split(/\s+/).slice(0,2);
  return parts.map(p => p[0]?.toUpperCase() || "").join("");
}
</script>

<template>
  <NavLandingSigned />

  <section class="about-hero">
    <div class="layer bg-base"></div>
    <div class="layer bg-gradient"></div>
    <div class="layer bg-noise"></div>
    <div class="layer bg-vignette"></div>

    <div class="shell">
      <header class="intro">
        <h1 class="title">BoardMate &nbsp;<span class="amp">&</span>&nbsp;Us</h1>
        <p class="lead">
          BoardMate is your go-to platform for organizing and managing board-game activities.
          Whether you’re a game owner lending your favorite titles or a player looking to discover
          new sessions, we’ve got you covered. We built this to bring the community closer—connect
          players, share collections, and create unforgettable game nights.
        </p>
      </header>

      <h2 class="section-title">Meet the Team</h2>

      <div class="grid">
        <article v-for="m in team" :key="m.name" class="card">
          <div class="avatar-wrap">
            <img
                v-if="m.img"
                :src="m.img"
                :alt="m.name"
                class="avatar"
                @error="$event.target.style.display = 'none'"
            />
            <div v-else class="avatar-fallback">{{ initials(m.name) }}</div>

            <a
                v-if="m.linkedin"
                class="action"
                :href="m.linkedin"
                target="_blank"
                rel="noopener noreferrer"
                aria-label="LinkedIn"
            >
              <!-- simple linkedIn glyph -->
              <svg viewBox="0 0 24 24" class="in">
                <path d="M4.98 3.5C4.98 4.88 3.86 6 2.5 6S0 4.88 0 3.5 1.12 1 2.5 1 4.98 2.12 4.98 3.5zM.22 8.99h4.56V24H.22zM8.9 8.99h4.37v2.05h.06c.61-1.16 2.1-2.39 4.33-2.39 4.63 0 5.49 3.05 5.49 7.01V24h-4.75v-6.28c0-1.5-.03-3.43-2.09-3.43-2.09 0-2.41 1.63-2.41 3.32V24H8.9z"/>
              </svg>
            </a>
          </div>

          <div class="meta">
            <h3 class="name">{{ m.name }}</h3>
            <p class="role">{{ m.role }}</p>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>

<style scoped>
/* ===== Canvas (same vibe as sign-in/profile) ===== */
.about-hero { position:relative; min-height:100vh; background:#14171d; }
.layer{ position:absolute; inset:0; }
.bg-base{ background:#14171d; }
.bg-gradient{
  background:
      radial-gradient(1100px 750px at 18% 18%, rgba(245,247,250,.28) 0%, rgba(245,247,250,.10) 36%, rgba(245,247,250,0) 65%),
      linear-gradient(180deg, #14171d 0%, #1b2029 100%);
  animation: float 16s ease-in-out infinite alternate;
}
@keyframes float{ 0%{transform:translateY(0) translateX(0) scale(1);} 100%{transform:translateY(-10px) translateX(6px) scale(1.01);} }
.bg-noise{
  background-image:url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='120' height='120'%3E%3Cfilter id='n'%3E%3CfeTurbulence baseFrequency='0.75' numOctaves='2' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='120' height='120' filter='url(%23n)' opacity='0.02'/%3E%3C/svg%3E");
  background-size:120px 120px; mix-blend-mode:overlay;
}
.bg-vignette{
  background: radial-gradient(85% 70% at 50% 60%, transparent 0%, rgba(0,0,0,.35) 70%, rgba(0,0,0,.7) 100%);
}

/* ===== Layout ===== */
.shell{ position:relative; z-index:2; max-width:1100px; margin:0 auto; padding:96px 20px 60px; color:#e9edf5; }

.intro{ text-align:left; margin-bottom:18px; }
.title{ margin:0 0 8px; font-size:34px; line-height:1.15; font-weight:900; letter-spacing:.2px; }
.amp{ opacity:.7; }
.lead{ margin:0; color:#c9d0de; max-width:860px; }

.section-title{
  margin:24px 0 12px; font-size:22px; font-weight:900; letter-spacing:.2px; color:#e9edf5;
}

/* ===== Team grid ===== */
.grid{
  display:grid; gap:16px;
  grid-template-columns: repeat(4, minmax(0,1fr));
}
@media (max-width: 1000px){ .grid{ grid-template-columns: repeat(3, minmax(0,1fr)); } }
@media (max-width: 720px){ .grid{ grid-template-columns: repeat(2, minmax(0,1fr)); } }
@media (max-width: 460px){ .grid{ grid-template-columns: 1fr; } }

.card{
  background:#0f1217; border:1px solid #1f2533; border-radius:16px;
  box-shadow:0 10px 30px rgba(0,0,0,.35);
  padding:16px; display:flex; flex-direction:column; align-items:center; gap:10px;
  transition: transform .25s cubic-bezier(.2,.8,.2,1), box-shadow .25s;
}
.card:hover{ transform: translateY(-6px); box-shadow: 0 16px 40px rgba(0,0,0,.38); }

/* Avatar with hover grow + action reveal */
.avatar-wrap{
  position:relative; width:96px; height:96px; border-radius:50%;
  display:grid; place-items:center; overflow:hidden; background:#0c0f13; border:1px solid #202636;
}
.avatar{ width:100%; height:100%; object-fit:cover; transition: transform .25s ease; }
.card:hover .avatar{ transform: scale(1.06); }

.avatar-fallback{
  width:100%; height:100%; display:grid; place-items:center;
  font-weight:900; letter-spacing:.5px; font-size:24px; color:#e9edf5;
}

/* LinkedIn pill — appears on hover */
.action{
  position:absolute; bottom:6px; right:6px; display:inline-flex; align-items:center; justify-content:center;
  width:30px; height:30px; border-radius:999px; background:#ffffff; border:1px solid rgba(0,0,0,.08);
  box-shadow: 0 6px 16px rgba(0,0,0,.18); opacity:0; transform: translateY(6px);
  transition: opacity .18s ease, transform .18s ease, filter .18s ease;
}
.card:hover .action{ opacity:1; transform: translateY(0); }
.action:hover{ filter:brightness(1.03); }
.in{ width:18px; height:18px; fill:#0a66c2; }

/* Meta */
.meta{ text-align:center; }
.name{ margin:6px 0 2px; font-size:16px; font-weight:900; }
.role{ margin:0; color:#b9c1d0; font-size:13px; }
</style>
