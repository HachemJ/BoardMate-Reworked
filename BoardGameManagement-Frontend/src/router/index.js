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
import BoardGameDetailView from "../views/BoardGameDetailView.vue";
import AddBoardGameView from "../views/AddBoardGameView.vue";
import UpdateBoardGameView from "../views/UpdateBoardGameView.vue";
import AddBoardGameCopyView from "../views/AddBoardGameCopyView.vue";
import UpdateBoardGameCopyView from "../views/UpdateBoardGameCopyView.vue";
import AddBoardGameReviewView from "../views/AddBoardGameReviewView.vue";

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
        { path: "/pages/ownerboardgame", name: "ownerBoardGameMenu", redirect: "/boardgames" },
        { path: "/pages/playerboardgame", name: "playerBoardGameMenu", redirect: "/boardgames" },
        { path: "/pages/ownerboardgame/add", name: "ownerAddBoardGame", component: AddBoardGameView },
        { path: "/pages/playerboardgame/add", name: "playerAddBoardGame", component: AddBoardGameView },
        { path: "/pages/ownerboardgame/update", name: "ownerUpdateBoardGame", component: UpdateBoardGameView },
        { path: "/pages/ownerboardgame/:gamename", name: "ownerBoardGameDetail", component: BoardGameDetailView },
        { path: "/pages/playerboardgame/:gamename", name: "playerBoardGameDetail", component: BoardGameDetailView },
        { path: "/pages/ownerboardgame/:gamename/review", name: "ownerAddReview", component: AddBoardGameReviewView },
        { path: "/pages/playerboardgame/:gamename/review", name: "playerAddReview", component: AddBoardGameReviewView },
        { path: "/pages/boardgamecopy/add", name: "addBoardGameCopy", component: AddBoardGameCopyView },
        { path: "/pages/boardgamecopy/update", name: "ownerUpdateBoardGameCopy", component: UpdateBoardGameCopyView },

        // Misc
        { path: "/pages/landing-pages/basic", name: "signin", component: SignInBasicView, alias: ["/signin"], meta: { hidePublicNav: true } },
        { path: "/pages/faqs", name: "faqs", component: FAQsView, alias: ["/faqs"] },
        { path: "/pages/borrowrequests", name: "borrowRequestMenu", component: BorrowRequestMenuView },
        // Catch-all → home
        { path: "/:pathMatch(.*)*", redirect: "/" },
    ],
});

export default router;

