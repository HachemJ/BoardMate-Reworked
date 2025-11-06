// src/router/index.js
import { createRouter, createWebHistory } from "vue-router";

// Core views
import PresentationView from "../views/Presentation/PresentationView.vue";
import AboutView from "../views/LandingPages/AboutUs/AboutView.vue";
import ContactView from "../views/LandingPages/ContactUs/ContactView.vue";
import SignInBasicView from "../views/LandingPages/SignIn/BasicView.vue";
import ProfileView from "../views/Profile.vue";

// Events
import EventView from "../views/EventView.vue";
import EventDetailView from "../views/EventDetailView.vue";

// Board games (unified & details)
import PlayerBoardGameMenuView from "../views/PlayerBoardGameMenuView.vue";
import OwnerBoardGameMenuView from "../views/OwnerBoardGameMenuView.vue"; // if you still use it anywhere
import BoardGameDetailView from "../views/BoardGameDetailView.vue";

// CRUD
import AddBoardGameView from "../views/AddBoardGameView.vue";
import UpdateBoardGameView from "../views/UpdateBoardGameView.vue";
import AddBoardGameCopyView from "../views/AddBoardGameCopyView.vue";
import UpdateBoardGameCopyView from "../views/UpdateBoardGameCopyView.vue";

// Borrow
import BorrowRequestMenuView from "../views/BorrowRequestMenuView.vue";

// FAQs
import FAQsView from "../views/FAQsView.vue";

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
        // use numeric ID to avoid ambiguity
        { path: "/pages/event/:id(\\d+)", name: "eventDetail", component: EventDetailView },

        // Board games – unified listing
        { path: "/boardgames", name: "boardgames", component: PlayerBoardGameMenuView },

        // Board game details (same component, different names to keep your links working)
        { path: "/boardgames/player/:gamename", name: "playerBoardGameDetail", component: BoardGameDetailView },
        { path: "/boardgames/owner/:gamename",  name: "ownerBoardGameDetail",  component: BoardGameDetailView },

        // Board game CRUD
        { path: "/boardgames/add",    name: "addBoardGame",    component: AddBoardGameView },
        { path: "/boardgames/update", name: "updateBoardGame", component: UpdateBoardGameView },

        // Board game copy CRUD
        { path: "/boardgamecopies/add",    name: "addBoardGameCopy",    component: AddBoardGameCopyView },
        { path: "/boardgamecopies/update", name: "updateBoardGameCopy", component: UpdateBoardGameCopyView },

        // Borrow
        { path: "/pages/borrowrequests", name: "borrowRequestMenu", component: BorrowRequestMenuView },

        // Misc
        { path: "/pages/landing-pages/basic", name: "signin", component: SignInBasicView, alias: ["/signin"], meta: { hidePublicNav: true } },
        { path: "/pages/faqs", name: "faqs", component: FAQsView, alias: ["/faqs"] },

        // Legacy redirects
        { path: "/pages/playerboardgame", redirect: "/boardgames" },
        { path: "/pages/ownerboardgame",  redirect: "/boardgames" },

        // Catch-all → home
        { path: "/:pathMatch(.*)*", redirect: "/" },
    ],
});

export default router;
