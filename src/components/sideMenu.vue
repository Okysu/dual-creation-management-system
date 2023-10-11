<template>
  <n-menu
    :collapsed-width="0"
    :options="menuOptions"
    :default-value="menuValue"
    :default-expanded-keys="expanded"
    @update:expanded-keys="(e: string[]) => { expanded = e }"
    @update:value="(v: string) => { menuValue = v, $router.push({ name:v } ) }"
  />
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { useAppConfig } from "@/stores/appConfig";
import { ref } from "vue";
import { useRoute } from "vue-router";
import renderMenu from "@/utils/menu";

const route = useRoute();

const menuValue = ref(route.path.replace("/console/", "").replace(/\//g, "-"));
const { expanded, menu } = storeToRefs(useAppConfig());
const menuOptions = ref<unknown>([]);
(async () => {
  menuOptions.value = await renderMenu(menu.value);
})();
</script>
