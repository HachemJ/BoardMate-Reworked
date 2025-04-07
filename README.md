# project-group-16


# Team Intro
  
| Name | Major | Fun Fact
| --- | --- | --- |
| Kathelina | Computer Engineering | I don’t know..I’m boring…I play(ed) piano?
| Tingyi | Computer Engineering | I fence épée
| Nizar | Software Engineering | I play Guitar
| Alex | Computer Engineering | I am left handed
| Maria | Computer Engineering | I like to read a lot of books
| Jad | Software Engineering | I love playing competitive games
| HongYi | Computer Engineering | lefty

<br />


# Scope and Purpose of the Project
<br />

## Overview
The Board Game Management Application is designed to help board game enthusiasts connect, share their games, and organize gaming events. The platform allows users to lend, borrow, and play board games while fostering a gaming community.

## Main Objectives
- Allow players to browse, borrow, and review board games.
- Allow game owners to manage their collection and track lending.
- Provide a platform for organizing board game events.
- Enable players and game owners to connect and interact.

## Core Features
### 👤 User Management
- Users must log in to access features.
- Two user types:
  - Players: Can borrow, review games, create events and register for events.
  - Game Owners: Can do everything players can do plus lend games they own.
- Each user has a profile page displaying their name, role, owned games, and event history.

### 🎲 Board Game Management
- Game Owners can add, update, or remove games from their collection.
- Players can browse available games and see who owns each game.
- Players can review and rate board games.
- System tracks game lending history.

### 📅 Event Management

- Users can create and register for events (if slots are available).
- Users can track their past and upcoming events.

### 📩 Borrowing System

- Players can send borrowing requests to Game Owners.
- Game Owners can accept or decline borrowing requests.
- System tracks borrowing history.

## Tech Stack

### 🌐 Backend
- **Language**: Java  
- **Framework**: Spring Boot  
- **Database Layer**: Spring Data JPA  
- **REST API**: Implemented via Spring MVC  
- **Database**: PostgreSQL

### 🖥️ Frontend
- **Framework**: Vue  
- **State Management**: Pinia  
- **Routing**: Vue Router  

### 🛠️ Build & Tooling
- **Backend Build Tool**: Gradle  
- **Frontend Dev Tool**: Vite  
- **Package Manager**: npm  
- **HTTP Client**: Axios


# Deliverables
The working application fulfilling all the abovementioned functionalities


## Project Roadmap and Timelines

### February 19: Deliverable 1 -- Backend Model and Database

| Name | Role & Responsibility | Hours
| --- | --- | --- |
| Kathelina | domain model + persistance testing (Borrow Request) | 11
| Tingyi | domain model + persistance testing (BoardGame Copy)+ Spring Boot | 11
| Nizar | domain model + persistance layer and testing (Event) | 11
| Alex | domain model + persistance testing (Review) + Wiki project Report | 10
| Maria | domain model + persistance testing (Registration) + project Report| 11
| Jad | domain model + persistance testing (BoardGame) + set up build system | 11
| HongYi | domain model + persistance testing (Player) + ReadMe | 10

### March 16: Deliverable 2 -- Service Logic and Functionalities of the Application

| Name | Role & Responsibility | Hours
| --- | --- | --- |
| Kathelina | • Design and implement Dtos, service layer, controller layer, service unit tests, and controller integration tests for borrowRequests <br> •  endpoint documentation for BorrowRequest related controllers <br> • contributed to the software quality assurance plan and report | 32 hours
| Tingyi | • Designed and implemented the BoardGameCopy service, controller, and DTOs.<br> • Developed unit and integration tests for BoardGameCopy-related functionalities.<br> • Documented BoardGameCopy REST API endpoints. <br> • Developed and tested login feature. | 30 Hours
| Nizar | • Designed and implemented the Event service, controller, and DTOs.<br> • Developed unit and integration tests for Event-related functionalities.<br> • Created and handled global exception management through the GlobalException class.<br> • Documented Event REST API endpoints.<br> • Contributed to the Software Quality Assurance Plan & Report| 32 Hours
| Alex | • Designed and implemented Review service, controller, DTOs, and associated unit and integration tests <br> • Documented Review REST API endpoints.<br>| 28 Hours
| Maria | • Designed and implemented the Registration service, controller, and DTOs.<br> • Developed unit and integration tests for Registration-related functionalities.<br>• Documented Event REST API endpoints for the Registration functionalities.<br> • Documented 2 Meeting Minutes. | 30 Hours
| Jad | • Designed and implemented the Board Game service, controller, and DTOs.<br> • Developed unit and integration tests for BoardGame-related functionalities.<br> • Documented BoardGame REST API endpoints.<br> | 28 Hours
| HongYi | • Completed DTOs, Service Layer, Controller layer, unit tests, as well as integration tests for Player <br> • RestFul API documentation <br> • Added content to Quality Assurance Report <br> • Documented 1 Meeting Minute. | 28 Hours


### April 6: Deliverable 3 -- Frontend Logic

| Name | Role & Responsibility | Hours
| --- | --- | --- |
| Kathelina | • Designed and implemented Managing and viewing borrow requests UI and backend linking <br> • Fix bugs and errors all around the different webpages <br> • Added error alerts representing the backend errors to provide more information on why requests were unsuccessful <br> • Added backend test cases and logic to implement business logics that were missed in the first two deliverables <br> • Writing the running of the application's documentation| 32 hours
| Tingyi | • Designed and implemented Board Game Menu UI and backend linking. <br> • Designed and implemented the FAQs page. <br> • Style modifications across the webpages. <br> • Designed and implemented Event and Board Game Detail page. | 30 Hours
| Nizar | • Modified and redesigned the UI template for the landing page to align with project branding and user experience goals.<br> • Updated the login and sign-up pages with a new layout and styling for improved usability and visual consistency.<br> • Created and implemented the UI design for the Event view page.<br> • Authored detailed meeting minutes to document team decisions and maintain clear communication across development phases.| 32 Hours
| Alex | • Helped design and implement board game UI and backend linking, including adding reviews, registration, updating board games and updating board game copies. <br> • Fixed contact us button submission <br> • Other QOL changes for the Events and Event Detailed View page <br> • Other various bug fixes and improvements | 30 Hours
| Maria | • Designed and implemented the Profile Page UI and backend linking.<br> • Fix bugs and errors in the Profile webpage. <br>• Added error alerts representing the backend errors to provide more information on why requests were unsuccessful in the Profile page. <br> | 30 Hours
| Jad | • Implemented the login functionality and integrated user session management using Pinia for consistent state tracking across the app.<br> • Worked on connecting frontend components to existing backend endpoints for event-related features, while addressing edge cases to support smoother user interactions (e.g., checkbox behaviors and subtle UI triggers). | 28 Hours
| HongYi | • Profile page UI layout <br> • Modifications for better style and user experience <br> • Worked on linking the frontend and backend for Event <br> • Worked on the presentation for project demo. | 28 Hours

## Project Report [🔗 Link](https://github.com/McGill-ECSE321-Winter2025/BoardGameManagement/wiki)
