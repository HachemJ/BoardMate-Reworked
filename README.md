🎲 BoardMate — Modernized Board Game Management Platform

A complete rework and modernization of the BoardGameManagement app originally built by McGill Project Group 16 (Winter 2025).
This fork represents my personal continuation of the project — where I redesigned the entire frontend from scratch, refined the backend integration, and elevated the overall experience to production quality.

🧭 The Story Behind This Fork

During the original Board Game Management Application project, our team successfully built a functional prototype that allowed players and owners to manage games, borrow requests, and events.

However, after the final deliverable, I realized that while the backend and business logic were solid, the frontend experience and integration could be far more intuitive, modern, and cohesive.

So I decided to take the project further — independently — and fully modernize it.

Here’s what I focused on:

🧱 Rebuilt the entire frontend architecture using Vue 3, Pinia, and Vite, replacing outdated routing and duplicated logic.

🎨 Redesigned every page (Board Games, Events, Borrow Requests, Profile, etc.) for a unified dark-themed UI that feels cleaner and consistent.

🔔 Integrated smart inline notifications for success/error handling (instead of generic browser alerts).

🧩 Reworked the event system — adding safeguards for ongoing/finished events, visibility for past/upcoming sessions, and intuitive registration feedback..

🧑‍💻 Enhanced UX for both owners and players, merging similar pages and reducing confusion between roles.

📦 Streamlined code structure — a single entry router, unified layout components, and clean route-based navigation.

This modernization keeps the original backend fully compatible, while transforming the front-end experience into something smooth, dynamic, and user-friendly — closer to a real production web app.

👥 Team Origin — McGill Project Group 16
Name	Major	Fun Fact
Kathelina	Computer Engineering	Plays piano 🎹
Tingyi	Computer Engineering	Fences épée ⚔️
Nizar	Software Engineering	Plays guitar 🎸
Alex	Computer Engineering	Left-handed ✋
Maria	Computer Engineering	Loves reading 📚
Jad (this fork)	Software Engineering	Competitive gamer 🎮
HongYi	Computer Engineering	Left-handed ✋
🧩 Project Overview

The BoardMate platform connects board game enthusiasts — helping them lend, borrow, and play games while organizing events and building communities.

🌟 Core Goals

Empower players to browse, borrow, and review games.

Enable owners to manage their collections and track lending.

Provide a platform for event scheduling and participation.

Create a social, fun space around board gaming.

💻 Modernized Features
Category	Description
🎨 UI/UX	Completely reworked with responsive design, clean dark theme, improved structure and accessibility.
⚡ Routing	Simplified navigation with clear routes (/boardgames, /events, /borrowrequests, /profile).
🔔 Notifications	Inline success/error messages for better feedback (e.g., registration success, invalid actions, locked deletions).
🕒 Event Logic	Shows ongoing, upcoming, and finished events directly in the interface with smart filtering.
🔐 Auth Integration	Connected login state via Pinia store and role-based conditional rendering (Owner/Player).
🧠 Backend Sync	Axios-based communication with structured API layers; full compatibility with the existing Spring Boot backend.
🛠️ Tech Stack

Frontend: Vue 3, Pinia, Vite, Axios, Bootstrap
Backend: Java Spring Boot, Spring Data JPA, PostgreSQL
Build Tools: npm (frontend), Gradle (backend)
Database: PostgreSQL (via JPA entities)

🚀 Running the Application
🖥️ Backend
cd backend
./gradlew bootRun


or on Windows:

gradlew bootRun


➡️ Runs on http://localhost:8080

💻 Frontend
cd frontend
npm install
npm run dev


➡️ Runs on http://localhost:3000

🔁 Project Evolution
Phase	Date	Description
Deliverable 1	Feb 19	Database design & entity persistence
Deliverable 2	Mar 16	Service logic, DTOs, controllers
Deliverable 3	Apr 6	Initial frontend with Vue 2
Modernization (this fork)	during Fall 2025 : Full UI/UX redesign, improved API linking, and new features
🧠 Future Ideas

🎟️ Waitlists for full events

💬 Direct owner-player chat

🏆 Leaderboards for game activity

📊 Analytics dashboard for owners

📜 Credits

Original development by McGill ECSE Project Group 16.  
Modernized and maintained by Jad El Hachem (2025).
