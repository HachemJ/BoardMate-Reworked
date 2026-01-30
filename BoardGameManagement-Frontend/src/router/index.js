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
import BoardGameHubView from "../views/BoardGameHubView.vue";
import AddBoardGameView from "../views/AddBoardGameView.vue";
import UpdateBoardGameView from "../views/UpdateBoardGameView.vue";
import AddBoardGameCopyView from "../views/AddBoardGameCopyView.vue";
import UpdateBoardGameCopyView from "../views/UpdateBoardGameCopyView.vue";
import AddBoardGameReviewView from "../views/AddBoardGameReviewView.vue";
import AdminDashboardView from "../views/AdminDashboardView.vue";
import { showDemoNotice } from "@/utils/demoNotice";

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
        { path: "/pages/event/calendar", name: "eventCalendar", component: EventView },
        // NOTE: use numeric ID, not name, to avoid ambiguity
        { path: "/pages/event/:id(\\d+)", name: "eventDetail", component: EventDetailView },

        // Board Games
        { path: "/boardgames", name: "boardgames", component: PlayerBoardGameMenuView },
        { path: "/boardgames/:id(\\d+)", name: "boardgameHub", component: BoardGameHubView },
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
        { path: "/admin", name: "adminDashboard", component: AdminDashboardView, meta: { requiresAdmin: true } },
        // Catch-all → home
        { path: "/:pathMatch(.*)*", redirect: "/" },
    ],
});

function isGuestMode() {
    return localStorage.getItem("authMode") === "guest";
}

function demoRedirect(target) {
    showDemoNotice("Demo mode: sign in to perform this action.");
    return target;
}

router.beforeEach((to) => {
    if (!isGuestMode()) return true;

    const path = to.path || "";
    const tab = String(to.query.tab || "").toLowerCase();

    // Allow: public + browse-only routes
    if (
        path === "/" ||
        path.startsWith("/about") ||
        path.startsWith("/contact") ||
        path.startsWith("/signin") ||
        path.startsWith("/pages/landing-pages/basic")
    ) {
        return true;
    }

    if (path.startsWith("/pages/event")) {
        if (tab === "create" || tab === "manage") {
            return demoRedirect({ name: "event", query: { tab: "browse" } });
        }
        return true;
    }

    if (path.startsWith("/pages/borrowrequests")) {
        if (tab && tab !== "browse") {
            return demoRedirect({ name: "borrowRequestMenu", query: { tab: "browse" } });
        }
        return true;
    }

    if (path.startsWith("/boardgames") || path.startsWith("/pages/playerboardgame")) {
        if (path.endsWith("/review") || path.endsWith("/add") || path.endsWith("/update")) {
            return demoRedirect({ name: "boardgames" });
        }
        return true;
    }

    if (
        path.startsWith("/profile") ||
        path.startsWith("/pages/ownerboardgame") ||
        path.startsWith("/pages/boardgamecopy")
    ) {
        return demoRedirect({ name: "event", query: { tab: "browse" } });
    }

    return true;
});

function isAdminUser() {
    const stored = JSON.parse(localStorage.getItem("authUser") || "null");
    return !!stored?.isAdmin;
}

router.beforeEach((to) => {
    if (!to.meta?.requiresAdmin) return true;
    if (isAdminUser()) return true;
    showDemoNotice("Admin access required.");
    return { name: "profile" };
});

export default router;

