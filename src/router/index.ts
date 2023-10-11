import { createRouter, createWebHistory } from "vue-router";
import HomeView from "@/views/HomeView.vue";

const subtitle: string = "双创实验室管理系统";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: HomeView,
      meta: {
        title: "首页",
      },
    },
    {
      path: "/login",
      name: "login",
      component: () => import("@/views/LoginView.vue"),
      meta: {
        title: "登录",
      },
    },
    {
      path: "/console",
      name: "console",
      component: () => import("@/views/ConsoleView.vue"),
      meta: {
        title: "控制台",
      },
      children: [
        {
          path: "/console/admin/system",
          name: "admin-info",
          component: () => import("@/views/admin/SystemInfoView.vue"),
          meta: {
            title: "系统信息",
          },
        },
        {
          path:"/console/admin/log",
          name:"admin-log",
          component:()=>import("@/views/admin/LogManageView.vue"),
          meta:{
            title:"系统日志"
          }
        },
        {
          path:"/console/admin/cache",
          name:"admin-cache",
          component:()=>import("@/views/admin/CacheManageView.vue"),
          meta:{
            title:"缓存管理"
          }
        },
        {
          path:"/console/admin/user",
          name:"admin-user",
          component:()=>import("@/views/admin/UserManageView.vue"),
          meta:{
            title:"用户管理"
          }
        },
        {
          path:"/console/contest/lab",
          name:"contest-lab",
          component:()=>import("@/views/contest/LabMaintainView.vue"),
          meta:{
            title:"实验室管理"
          }
        }
      ],
    },
  ],
});

router.beforeEach((to, from, next) => {
  if (window.$loadingbar) window.$loadingbar.start();
  if (to.meta.title && typeof to.meta.title === "string") {
    document.title = to.meta.title + " - " + subtitle;
  }
  next();
});
router.afterEach(async (to, from) => {
  if (window.$loadingbar) window.$loadingbar.finish();
});

export default router;
