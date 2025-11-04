import { createRouter, createWebHistory } from "vue-router";
import PresentationView from "../views/Presentation/PresentationView.vue";
import AboutView from "../views/LandingPages/AboutUs/AboutView.vue";
import ContactView from "../views/LandingPages/ContactUs/ContactView.vue";
import EventView from "../views/EventView.vue";
import SignInBasicView from "../views/LandingPages/SignIn/BasicView.vue";
import ProfileView from "../views/Profile.vue";
import PlayerBoardGameMenuView from "@/views/PlayerBoardGameMenuView.vue";
import OwnerBoardGameMenuView from "@/views/OwnerBoardGameMenuView.vue";
import BoardGameDetailView from "@/views/BoardGameDetailView.vue";
import AddBoardGameCopyView from "@/views/AddBoardGameCopyView.vue";
import AddBoardGameReviewView from "@/views/AddBoardGameReviewView.vue";
import AddBoardGameView from "@/views/AddBoardGameView.vue";
import UpdateBoardGameView from "@/views/UpdateBoardGameView.vue";
import UpdateBoardGameCopyView from "@/views/UpdateBoardGameCopyView.vue";
import EventDetailView from "@/views/EventDetailView.vue";
import FAQsView from "@/views/FAQsView.vue";
import BorrowRequestMenuView from "@/views/BorrowRequestMenuView.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        { path: "/", name: "presentation", component: PresentationView },

        { path: "/profile", name: "profile", component: ProfileView },

        // 👇 add flat aliases so /about and /contact also work
        {
            path: "/pages/landing-pages/about-us",
            name: "about",
            component: AboutView,
            alias: ["/about"],
        },
        {
            path: "/pages/landing-pages/contact-us",
            name: "contactus",
            component: ContactView,
            alias: ["/contact"],
        },

        { path: "/pages/event", name: "event", component: EventView },

        { path: "/pages/playerboardgame", name: "playerBoardGameMenu", component: PlayerBoardGameMenuView },
        { path: "/pages/ownerboardgame", name: "ownerBoardGameMenu", component: OwnerBoardGameMenuView },

        { path: "/pages/playerboardgame/:gamename", name: "playerBoardGameDetail", component: BoardGameDetailView },
        { path: "/pages/ownerboardgame/:gamename", name: "ownerBoardGameDetail", component: BoardGameDetailView },

        { path: "/pages/ownerboardgame/addboardgamecopy", name: "addBoardGameCopy", component: AddBoardGameCopyView },

        { path: "/pages/playerboardgame/:gamename/addreview", name: "playerAddReview", component: AddBoardGameReviewView },
        { path: "/pages/ownerboardgame/:gamename/addreview", name: "ownerAddReview", component: AddBoardGameReviewView },

        { path: "/pages/playerboardgame/addboardgame", name: "playerAddBoardGame", component: AddBoardGameView },
        { path: "/pages/ownerboardgame/addboardgame", name: "ownerAddBoardGame", component: AddBoardGameView },

        { path: "/pages/ownerboardgame/updateboardgame", name: "ownerUpdateBoardGame", component: UpdateBoardGameView },
        { path: "/pages/ownerboardgame/updateboardgamecopy", name: "ownerUpdateBoardGameCopy", component: UpdateBoardGameCopyView },

        { path: "/pages/event/:eventname", name: "eventDetail", component: EventDetailView },

        // small QoL aliases too
        { path: "/pages/landing-pages/basic", name: "signin", component: SignInBasicView, alias: ["/signin"] },
        { path: "/pages/faqs", name: "faqs", component: FAQsView, alias: ["/faqs"] },

        { path: "/pages/borrowrequests", name: "borrowRequestMenu", component: BorrowRequestMenuView },
    ],
});

export default router;
