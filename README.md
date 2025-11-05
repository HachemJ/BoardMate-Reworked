# BoardMate — Modernized Board Game Management Platform

A complete rework and modernization of the BoardGameManagement app originally built by McGill Project Group 16 (Winter 2025).
This fork represents my personal continuation of the project — redesigned frontend, refined backend integration, and elevated the experience to production quality.

# Team Origin — McGill ECSE 321 Project Group 16 (Winter 2025)
| Name | Major | Fun Fact
| --- | --- | --- |
| Kathelina | Computer Engineering | I don’t know..I’m boring…I play(ed) piano? 🎹
| Tingyi | Computer Engineering | I fence épée ⚔️
| Nizar | Software Engineering | I play Guitar 🎸
| Alex | Computer Engineering | I am left handed ✋
| Maria | Computer Engineering | I like to read a lot of books 📚
| Jad | Software Engineering | I love playing competitive games 🎮
| HongYi | Computer Engineering | lefty ✋

## Project Overview
The Board Game Management Application is designed to help board game enthusiasts connect, share their games, and organize gaming events. The platform allows users to lend, borrow, and play board games while fostering a gaming community.

## Core Goals

- Empower players to browse, borrow, and review games.

- Enable owners to manage collections and track lending.

- Provide a platform for event scheduling and participation.

- Create a social, fun space around board gaming.

## Modernized Features

- 🎨 Completely reworked with responsive design, clean dark theme, improved structure and accessibility.
- ⚡ Routing	Simplified navigation with clear routes (/boardgames, /events, /borrowrequests, /profile).
- 🔔 Notifications	Inline success/error messages for better feedback (registration, invalid actions, locked deletions).
- 🕒 Event Logic	Shows ongoing, upcoming, and finished events directly in the interface with smart filtering.
- 🔐 Auth Integration	Login state via Pinia store with role-based conditional rendering (Owner/Player).
- 🧠 Backend Sync	Axios-based API communication; fully compatible with Spring Boot backend.

## Tech Stack

### 🖥️ Frontend: Vue 3, Pinia, Vite, Axios, Bootstrap
### 🌐 Backend: Java Spring Boot, Spring Data JPA, PostgreSQL
### 🛠️ Build Tools: npm (frontend), Gradle (backend)
### 🗄️  Database: PostgreSQL (via JPA entities)

## Running the Application

### Backend:

- cd BoardGameManagement-Backend
- ./gradlew bootRun   # or gradlew bootRun on Windows

➡️ Runs on http://localhost:8080

### Frontend:

- cd BoardGameManagement-Frontend
- npm install
- npm run dev

➡️ Runs on http://localhost:3000

## Project Evolution
- Deliverable 1 (Feb 19) : Database design & entity persistence

- Deliverable 2 (Mar 16) :	Service logic, DTOs, controllers

- Deliverable 3 (Apr 6) :	Initial frontend with Vue 2

**This modernized fork (Fall 2025)** : Full UI/UX redesign, improved API linking, and new features

## Future Ideas

### 🎟️ Waitlists for full events

### 💬 Direct owner-player chat

### 🏆 Leaderboards for game activity

### 📊 Analytics dashboard for owners

## Credits

📜 Original development by McGill ECSE Project Group 16.
Modernized and maintained by Jad El Hachem (2025).
