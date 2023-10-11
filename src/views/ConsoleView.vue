<script lang="ts" setup>
import { RouterView, useRouter } from "vue-router";
import { storeToRefs } from "pinia";
import { useUserConfig } from "@/stores/userConfig";
import { useAppConfig } from "@/stores/appConfig";
import { describeTime } from "@/utils/strings";
import { logout as userlogout } from "@/api/user";
import topMenu from "@/components/topMenu.vue";
import sideMenu from "@/components/sideMenu.vue";
import { onMounted, ref } from "vue";
import { debounce } from "@/utils/function";

import { MenuOutline } from "@vicons/ionicons5";

const router = useRouter();

const { theme } = storeToRefs(useAppConfig());
const userConfig = useUserConfig();
const { user, signAt } = storeToRefs(userConfig);

const themeOptions = [
  { label: "系统", value: "auto" },
  { label: "亮色", value: "light" },
  { label: "暗色", value: "dark" },
];

const logout = () => {
  userlogout()
    .then(() => {
      window.$message.success("退出登录成功");
    })
    .catch((err) => {
      const { msg } = err.response;
      window.$message.error(msg);
    })
    .finally(() => {
      userConfig.logout();
      router.push({ name: "login" });
    });
};
const isMobile = ref(false);
const resize = () => {
  isMobile.value = window.innerWidth < 768;
};
onMounted(() => {
  resize();
  window.addEventListener("resize", debounce(resize, 300));
});

const collapsed = ref(true);
</script>

<template>
  <n-layout style="height: 100vh">
    <n-layout-header style="height: 64px" bordered>
      <div class="header-bar">
        <topMenu v-if="!isMobile" />
        <n-button v-else strong secondary @click="collapsed = !collapsed">
          <n-icon size="large">
            <MenuOutline />
          </n-icon>
        </n-button>
        <div class="header-bar-right">
          <n-popover trigger="hover" style="width: 250px">
            <template #trigger>
              <n-avatar>
                {{ user.name ? user.name[0] : "未登录" }}
              </n-avatar>
            </template>
            <n-list>
              <n-list-item>
                <template #prefix>
                  <n-avatar :size="48">
                    <span style="font-size: 24px">
                      {{ user.name ? user.name[0] : "未登录" }}
                    </span>
                  </n-avatar>
                </template>
                <h3>{{ user.name }}</h3>
                <p>登录时间：{{ describeTime(new Date(signAt)) }}</p>
              </n-list-item>
              <n-list-item>
                <div style="display: flex; justify-content: space-between">
                  <n-select
                    style="width: 80px"
                    v-model:value="theme"
                    size="small"
                    :options="themeOptions"
                  />
                  <n-button size="small" type="error" tertiary @click="logout">
                    退出登录
                  </n-button>
                </div>
              </n-list-item>
            </n-list>
          </n-popover>
        </div>
      </div>
    </n-layout-header>
    <n-layout position="absolute" style="top: 64px" :has-sider="isMobile">
      <n-layout-sider
        v-if="isMobile"
        :collapsed-width="0"
        bordered
        collapse-mode="width"
        width="100%"
        :native-scrollbar="false"
        v-model:collapsed="collapsed"
      >
        <sideMenu @update:value="collapsed = true" />
      </n-layout-sider>
      <n-layout content-style="padding: 24px;" :native-scrollbar="false">
        <div class="main-box">
          <RouterView />
        </div>
      </n-layout>
    </n-layout>
  </n-layout>
</template>

<style scoped>
.header-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 64px;
  position: relative;
}

@media screen and (max-width: 768px) {
  .header-bar {
    padding: 0 24px;
    justify-content: flex-start;
  }
}

.header-bar-right {
  position: absolute;
  right: 24px;
}

.main-box {
  max-width: 1200px;
  margin: 0 auto;
}

@media screen and (max-width: 1200px) {
  .main-box {
    padding: 0 12px;
  }
}
</style>
