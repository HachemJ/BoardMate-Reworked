import { createRouter, createWebHistory } from "vue-router";
import PresentationView from "../views/Presentation/PresentationView.vue";
import AboutView from "../views/LandingPages/AboutUs/AboutView.vue";
import ContactView from "../views/LandingPages/ContactUs/ContactView.vue";
import EventView from "../views/EventView.vue";
import SignInBasicView from "../views/LandingPages/SignIn/BasicView.vue";
import ProfileView from "../views/Profile.vue";
import PageHeaders from "../layouts/sections/page-sections/page-headers/HeadersView.vue";
import PageFeatures from "../layouts/sections/page-sections/features/FeaturesView.vue";
import NavigationNavbars from "../layouts/sections/navigation/navbars/NavbarsView.vue";
import NavigationNavTabs from "../layouts/sections/navigation/nav-tabs/NavTabsView.vue";
import NavigationPagination from "../layouts/sections/navigation/pagination/PaginationView.vue";
import InputAreasInputs from "../layouts/sections/input-areas/inputs/InputsView.vue";
import InputAreasForms from "../layouts/sections/input-areas/forms/FormsView.vue";
import ACAlerts from "../layouts/sections/attention-catchers/alerts/AlertsView.vue";
import ACModals from "../layouts/sections/attention-catchers/modals/ModalsView.vue";
import ACTooltipsPopovers from "../layouts/sections/attention-catchers/tooltips-popovers/TooltipsPopoversView.vue";
import ElAvatars from "../layouts/sections/elements/avatars/AvatarsView.vue";
import ElBadges from "../layouts/sections/elements/badges/BadgesView.vue";
import ElBreadcrumbs from "../layouts/sections/elements/breadcrumbs/BreadcrumbsView.vue";
import ElButtons from "../layouts/sections/elements/buttons/ButtonsView.vue";
import ElButtonGroups from "../layouts/sections/elements/button-groups/ButtonGroupsView.vue";
import ElDropdowns from "../layouts/sections/elements/dropdowns/DropdownsView.vue";
import ElProgressBars from "../layouts/sections/elements/progress-bars/ProgressBarsView.vue";
import ElToggles from "../layouts/sections/elements/toggles/TogglesView.vue";
import ElTypography from "../layouts/sections/elements/typography/TypographyView.vue";
import OwnerBorrowRequestManagement from "@/views/OwnerBorrowRequestManagement.vue";
import PlayerBorrowRequestManagement from "@/views/PlayerBorrowRequestManagement.vue";
import CreateNewBorrowRequest from "@/views/CreateNewBorrowRequest.vue";
import PlayerBoardGameMenuView from "@/views/PlayerBoardGameMenuView.vue";
import OwnerBoardGameMenuView from "@/views/OwnerBoardGameMenuView.vue";
import BoardGameDetailView from "@/views/BoardGameDetailView.vue";
import AddBoardGameCopyView from "@/views/AddBoardGameCopyView.vue";
import AddBoardGameReviewView from "@/views/AddBoardGameReviewView.vue";
import AddBoardGameView from "@/views/AddBoardGameView.vue";
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "presentation",
      component: PresentationView,
    },
    {
      path: "/profile",
      name: "profile",
      component: ProfileView,
    },
    {
      path: "/pages/landing-pages/about-us",
      name: "about",
      component: AboutView,
    },
    {
      path: "/pages/landing-pages/contact-us",
      name: "contactus",
      component: ContactView,
    },
    {
      path: "/pages/event",
      name: "event",
      component: EventView,
    },
    {
      path: "/pages/ownerborrowrequests",
      name: "owner borrow request",
      component: OwnerBorrowRequestManagement,
    },

    {
      path: "/pages/playerborrowrequests",
      name: "player borrow request",
      component: PlayerBorrowRequestManagement,
    },

    {
      path: "/pages/playerboardgame",
      name: "playerBoardGameMenu",
      component: PlayerBoardGameMenuView,
    },

    {
      path: "/pages/ownerboardgame",
      name: "ownerBoardGameMenu",
      component: OwnerBoardGameMenuView,
    },

    {
      path: "/pages/playerboardgame/:gamename",
      name: "playerBoardGameDetail",
      component: BoardGameDetailView,
    },

    {
      path: "/pages/ownerboardgame/:gamename",
      name: "ownerBoardGameDetail",
      component: BoardGameDetailView,
    },

    {
      path: "/pages/ownerboardgame/addboardgamecopy",
      name: "addBoardGameCopy",
      component: AddBoardGameCopyView,
    },

    {
      path: "/pages/playerboardgame/:gamename/addreview",
      name: "playerAddReview",
      component: AddBoardGameReviewView,
    },

    {
      path: "/pages/ownerboardgame/:gamename/addreview",
      name: "ownerAddReview",
      component: AddBoardGameReviewView,
    },

    {
      path: "/pages/playerboardgame/addboardgame",
      name: "playerAddBoardGame",
      component: AddBoardGameView,
    },

    {
      path: "/pages/ownerboardgame/addboardgame",
      name: "ownerAddBoardGame",
      component: AddBoardGameView,
    },

    {
      path: "/pages/landing-pages/basic",
      name: "signin",
      component: SignInBasicView,
    },
    {
      path: "/sections/page-sections/page-headers",
      name: "page-headers",
      component: PageHeaders,
    },
    {
      path: "/sections/page-sections/features",
      name: "page-features",
      component: PageFeatures,
    },
    {
      path: "/sections/navigation/navbars",
      name: "navigation-navbars",
      component: NavigationNavbars,
    },
    {
      path: "/sections/navigation/nav-tabs",
      name: "navigation-navtabs",
      component: NavigationNavTabs,
    },
    {
      path: "/sections/navigation/pagination",
      name: "navigation-pagination",
      component: NavigationPagination,
    },
    {
      path: "/sections/input-areas/inputs",
      name: "inputareas-inputs",
      component: InputAreasInputs,
    },
    {
      path: "/sections/input-areas/forms",
      name: "inputareas-forms",
      component: InputAreasForms,
    },
    {
      path: "/sections/attention-catchers/alerts",
      name: "ac-alerts",
      component: ACAlerts,
    },
    {
      path: "/sections/attention-catchers/modals",
      name: "ac-modals",
      component: ACModals,
    },
    {
      path: "/sections/attention-catchers/tooltips-popovers",
      name: "ac-tooltips-popovers",
      component: ACTooltipsPopovers,
    },
    {
      path: "/sections/elements/avatars",
      name: "el-avatars",
      component: ElAvatars,
    },
    {
      path: "/sections/elements/badges",
      name: "el-badges",
      component: ElBadges,
    },
    {
      path: "/sections/elements/breadcrumbs",
      name: "el-breadcrumbs",
      component: ElBreadcrumbs,
    },
    {
      path: "/sections/elements/buttons",
      name: "el-buttons",
      component: ElButtons,
    },
    {
      path: "/sections/elements/button-groups",
      name: "el-button-groups",
      component: ElButtonGroups,
    },
    {
      path: "/sections/elements/dropdowns",
      name: "el-dropdowns",
      component: ElDropdowns,
    },
    {
      path: "/sections/elements/progress-bars",
      name: "el-progress-bars",
      component: ElProgressBars,
    },
    {
      path: "/sections/elements/toggles",
      name: "el-toggles",
      component: ElToggles,
    },
    {
      path: "/sections/elements/typography",
      name: "el-typography",
      component: ElTypography,
    },
  ],
});

export default router;
