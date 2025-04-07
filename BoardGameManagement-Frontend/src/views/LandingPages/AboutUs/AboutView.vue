<template>
  <div class="about-us-page">
    <DefaultNavbar />
    <div class="container">
      <section class="about-section">
        <h1>BoardMate & Us</h1>
        <p>
          BoardMate is your go-to platform for organizing and managing board game activities. Whether you’re a game owner lending your favorite titles or a player looking to discover new sessions, we’ve got you covered.
        </p>
        <p>
          We built this app to bring the community closer — connect players, share collections, and create unforgettable game nights.
        </p>
      </section>

      <h2 class="team-heading">Meet the Team</h2>

      <div class="team-grid">
        <div v-for="member in teamMembers" :key="member.name" class="team-card">
          <h5 class="card-title">{{ member.name }}</h5>

          <div class="profile-img-frame"></div>

          <p class="card-subtitle">{{ member.role }}</p>

          <a class="linkedin-btn" href="#" target="_blank">
            <svg xmlns="http://www.w3.org/2000/svg" height="16" viewBox="0 0 24 24" fill="white">
              <path d="M19 0h-14c-2.8 0-5 2.2-5 5v14c0 2.8 2.2 5 5 5h14c2.8 0 5-2.2 5-5v-14c0-2.8-2.2-5-5-5zm-11 19h-3v-10h3v10zm-1.5-11.3c-1 0-1.8-.8-1.8-1.7s.8-1.8 1.8-1.8c1 0 1.8.8 1.8 1.8s-.8 1.7-1.8 1.7zm13.5 11.3h-3v-5.5c0-1.3-.5-2.1-1.5-2.1s-1.6.9-1.6 2.1v5.5h-3v-10h3v1.3c.4-.7 1.4-1.5 2.8-1.5 2.1 0 3.3 1.4 3.3 4.2v6z"/>
            </svg>
            LinkedIn
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import DefaultNavbar from '@/examples/navbars/NavbarDefault.vue'

const teamMembers = [
  { name: 'Kathelina', role: 'Computer Engineering' },
  { name: 'Tingyi', role: 'Computer Engineering' },
  { name: 'Nizar', role: 'Software Engineering' },
  { name: 'Alex', role: 'Computer Engineering' },
  { name: 'Maria', role: 'Computer Engineering' },
  { name: 'Jad', role: 'Software Engineering' },
  { name: 'HongYi', role: 'Computer Engineering' }
]
</script>

<style scoped>
.about-us-page {
  padding: 20px;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.container {
  max-width: 1100px;
  margin: auto;
  padding: 40px 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.about-section h1 {
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.about-section p {
  font-size: 1rem;
  color: #444;
  line-height: 1.6;
  margin-bottom: 0.8rem;
}

.team-heading {
  margin-top: 3rem;
  font-size: 1.75rem;
  font-weight: 600;
  text-align: center;
}

.team-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-top: 2rem;
}

.team-card {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  height: 280px;
}

.profile-img-placeholder {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background-color: #dee2e6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  color: #6c757d;
  margin: 12px 0;
}

.card-title {
  font-size: 1.1rem;
  font-weight: 600;
}

.card-subtitle {
  font-size: 0.95rem;
  color: #6c757d;
  margin-bottom: 10px;
}

.linkedin-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  background-color: #0a66c2;
  color: #fff;
  border: none;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 0.85rem;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s ease-in-out;
}

.linkedin-btn:hover {
  background-color: #004182;
  transform: translateY(-1px);
}

@media (max-width: 992px) {
  .team-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .team-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 500px) {
  .team-grid {
    grid-template-columns: 1fr;
  }
}

.profile-img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  margin: 12px 0;
  border: 2px solid #dee2e6;
}

.profile-img-frame {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 2px solid #dee2e6;
  background-color: transparent;
  margin: 12px 0;
}


</style>
