// src/router/index.js
import { createRouter, createWebHistory } from "vue-router";

// Views
import PresentationView from "../views/Presentation/PresentationView.vue";
import AboutView from "../views/LandingPages/AboutUs/AboutView.vue";
import ContactView from "../views/LandingPages/ContactUs/ContactView.vue";

import EventView from "../views/EventView.vue";
import EventDetailView from "../views/EventDetailView.vue";

import FAQsView from "../views/FAQsView.vue";
import BorrowRequestMenuView from "../views/BorrowRequestMenuView.vue";

import SignInBasicView from "../views/LandingPages/SignIn/BasicView.vue";
import ProfileView from "../views/Profile.vue";

// Unified Board Games page
import PlayerBoardGameMenuView from "../views/PlayerBoardGameMenuView.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        { path: "/", name: "presentation", component: PresentationView },
        { path: "/profile", name: "profile", component: ProfileView },

        // Pretty aliases
        { path: "/pages/landing-pages/about-us", name: "about", component: AboutView, alias: ["/about"] },
        { path: "/pages/landing-pages/contact-us", name: "contactus", component: ContactView, alias: ["/contact"] },

        // Events
        { path: "/pages/event", name: "event", component: EventView },
        // NOTE: use numeric ID, not name, to avoid ambiguity
        { path: "/pages/event/:id(\\d+)", name: "eventDetail", component: EventDetailView },

        // Board Games
        { path: "/boardgames", name: "boardgames", component: PlayerBoardGameMenuView },

        // Misc
        { path: "/pages/landing-pages/basic", name: "signin", component: SignInBasicView, alias: ["/signin"], meta: { hidePublicNav: true } },
        { path: "/pages/faqs", name: "faqs", component: FAQsView, alias: ["/faqs"] },
        { path: "/pages/borrowrequests", name: "borrowRequestMenu", component: BorrowRequestMenuView },

        // Legacy redirects
        { path: "/pages/playerboardgame", redirect: "/boardgames" },
        { path: "/pages/ownerboardgame", redirect: "/boardgames" },

        // Catch-all → home
        { path: "/:pathMatch(.*)*", redirect: "/" },
    ],
});

export default router;
