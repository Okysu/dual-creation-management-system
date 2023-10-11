<script setup lang="ts">
import { onMounted, ref } from "vue";
import { getLabList, deleteLab } from "@/api/lab";
import { isWidthLessThan } from "@/utils/function";

const lab = ref<Lab[]>([]);
const getAllLab = () => {
  getLabList().then((res) => {
    const { data } = res;
    lab.value = data.data;
  });
};

const cols = ref(2);

onMounted(() => {
  getAllLab();
  isWidthLessThan(768, (is:boolean) =>{
    cols.value = is ? 1 : 2;
  });
});
</script>

<template>
  <n-page-header subtitle="包含现行活跃等所有创新实验室">
    <n-grid :cols="2">
      <n-gi>
        <n-statistic label="总实验室数量" :value="lab.length" />
      </n-gi>
      <n-gi>
        <n-statistic
          label="已删除的实验室"
          :value="lab.filter((e) => e.delFlag).length"
        />
      </n-gi>
    </n-grid>
    <template #title>
      <a href="#" style="text-decoration: none; color: inherit">实验室总览</a>
    </template>
  </n-page-header>
  <n-grid style="margin-top: 10px;" x-gap="5" y-gap="5" :cols="cols">
    <n-gi v-for="item in lab">
      <n-card :title="item.name">
        <template #header-extra>
          <n-space>
            <n-tag :type="item.delFlag === 0 ? 'success' : 'warning'">
              {{ item.delFlag === 0 ? "正常" : "已删除" }}
            </n-tag>
          </n-space>
        </template>
        {{ item.description }}
        <template #footer>
          创建日期： {{ new Date(item.createdTime).toLocaleString() }}
        </template>
        <template #action>
          <n-space>
            <n-button secondary strong type="primary">编辑 </n-button>
            <n-button secondary strong type="error"> 删除 </n-button>
          </n-space>
        </template>
      </n-card>
    </n-gi>
  </n-grid>
</template>

<style scoped>
.n-card {
  height: 100%;
}
</style>
