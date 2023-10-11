<script setup lang="ts">
import { reactive, watch } from "vue";
import { RouterView } from "vue-router";
import {
  zhCN,
  dateZhCN,
  NConfigProvider,
  enUS,
  dateEnUS,
  lightTheme,
  darkTheme,
} from "naive-ui";
import { useAppConfig } from "@/stores/appConfig";
import { storeToRefs } from "pinia";
import globalMessage from "@/components/globalMessage.vue";

const config = useAppConfig();

const settings = reactive({
  locale: zhCN,
  dateLocale: dateZhCN,
  theme: lightTheme,
});

// set locale
const localeHandler = (locale: string) => {
  switch (locale) {
    case "zh-CN":
      settings.locale = zhCN;
      settings.dateLocale = dateZhCN;
      break;
    case "en-US":
      settings.locale = enUS;
      settings.dateLocale = dateEnUS;
      break;
    default:
      settings.locale = zhCN;
      settings.dateLocale = dateZhCN;
      break;
  }
};

// set theme
const themeHandler = (theme: string) => {
  switch (theme) {
    case "dark":
      settings.theme = darkTheme;
      break;
    case "light":
      settings.theme = lightTheme;
      break;
    case "auto":
      if (
        window.matchMedia &&
        window.matchMedia("(prefers-color-scheme: dark)").matches
      ) {
        settings.theme = darkTheme;
      } else {
        settings.theme = lightTheme;
      }
      break;
    default:
      if (
        window.matchMedia &&
        window.matchMedia("(prefers-color-scheme: dark)").matches
      ) {
        settings.theme = darkTheme;
      } else {
        settings.theme = lightTheme;
      }
      break;
  }
};

const { locale, theme } = storeToRefs(config);
localeHandler(locale.value);
themeHandler(theme.value);

watch(locale, (val) => {
  localeHandler(val);
});

watch(theme, (val) => {
  themeHandler(val);
});
</script>

<template>
  <n-config-provider
    :locale="settings.locale"
    :date-locale="settings.dateLocale"
    :theme="settings.theme"
    inline-theme-disabled
  >
    <n-global-style />
    <n-loading-bar-provider>
      <n-message-provider>
        <n-dialog-provider>
          <n-notification-provider>
            <globalMessage />
          </n-notification-provider>
        </n-dialog-provider>
      </n-message-provider>
    </n-loading-bar-provider>
    <RouterView />
  </n-config-provider>
</template>
