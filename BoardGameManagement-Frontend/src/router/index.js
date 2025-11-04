// src/router/index.js
import { createRouter, createWebHistory } from "vue-router";

// Core pages (paths match your tree)
import PresentationView from "../views/Presentation/PresentationView.vue";
import AboutView from "../views/LandingPages/AboutUs/AboutView.vue";
import ContactView from "../views/LandingPages/ContactUs/ContactView.vue";
import EventView from "../views/EventView.vue";
import EventDetailView from "../views/EventDetailView.vue";
import FAQsView from "../views/FAQsView.vue";
import BorrowRequestMenuView from "../views/BorrowRequestMenuView.vue";
import SignInBasicView from "../views/LandingPages/SignIn/BasicView.vue";
import ProfileView from "../views/Profile.vue";

// Board games (unified page uses the Player menu view you already have)
import PlayerBoardGameMenuView from "../views/PlayerBoardGameMenuView.vue";
import OwnerBoardGameMenuView from "../views/OwnerBoardGameMenuView.vue"; // optional but we keep named routes working
import BoardGameDetailView from "../views/BoardGameDetailView.vue";
import AddBoardGameCopyView from "../views/AddBoardGameCopyView.vue";
import AddBoardGameReviewView from "../views/AddBoardGameReviewView.vue";
import AddBoardGameView from "../views/AddBoardGameView.vue";
import UpdateBoardGameView from "../views/UpdateBoardGameView.vue";
import UpdateBoardGameCopyView from "../views/UpdateBoardGameCopyView.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        // Home / Profile
        { path: "/",                    name: "presentation",      component: PresentationView },
        { path: "/profile",             name: "profile",           component: ProfileView },

        // Nice short aliases
        { path: "/pages/landing-pages/about-us",   name: "about",     component: AboutView,   alias: ["/about"] },
        { path: "/pages/landing-pages/contact-us", name: "contactus", component: ContactView, alias: ["/contact"] },

        // Events
        { path: "/pages/event",                name: "event",       component: EventView },
        { path: "/pages/event/:eventname",     name: "eventDetail", component: EventDetailView },

        // === Board Games (unified entry) ===
        { path: "/boardgames",                 name: "boardgames",  component: PlayerBoardGameMenuView },

        // --- Legacy / Owner & Player named routes so existing links keep working ---
        // Menus
        { path: "/pages/playerboardgame",      name: "playerBoardGameMenu", component: PlayerBoardGameMenuView },
        { path: "/pages/ownerboardgame",       name: "ownerBoardGameMenu",  component: OwnerBoardGameMenuView },

        // Details (same component, two names)
        { path: "/pages/playerboardgame/:gamename", name: "playerBoardGameDetail", component: BoardGameDetailView },
        { path: "/pages/ownerboardgame/:gamename",  name: "ownerBoardGameDetail",  component: BoardGameDetailView },

        // Reviews
        { path: "/pages/playerboardgame/:gamename/addreview", name: "playerAddReview", component: AddBoardGameReviewView },
        { path: "/pages/ownerboardgame/:gamename/addreview",  name: "ownerAddReview",  component: AddBoardGameReviewView },

        // Add / Update board games
        { path: "/pages/playerboardgame/addboardgame",        name: "playerAddBoardGame",       component: AddBoardGameView },
        { path: "/pages/ownerboardgame/addboardgame",         name: "ownerAddBoardGame",        component: AddBoardGameView },
        { path: "/pages/ownerboardgame/updateboardgame",      name: "ownerUpdateBoardGame",     component: UpdateBoardGameView },

        // Add / Update board game copies
        { path: "/pages/ownerboardgame/addboardgamecopy",     name: "addBoardGameCopy",         component: AddBoardGameCopyView },
        { path: "/pages/ownerboardgame/updateboardgamecopy",  name: "ownerUpdateBoardGameCopy", component: UpdateBoardGameCopyView },

        // Misc
        { path: "/pages/landing-pages/basic",  name: "signin", component: SignInBasicView, alias: ["/signin"], meta: { hidePublicNav: true } },
        { path: "/pages/faqs",                 name: "faqs",   component: FAQsView,        alias: ["/faqs"] },
        { path: "/pages/borrowrequests",       name: "borrowRequestMenu", component: BorrowRequestMenuView },

        // Catch-all → home (prevents blank on typos)
        { path: "/:pathMatch(.*)*", redirect: "/" },
    ],
});

export default router;
