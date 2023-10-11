<template>
  <n-page-header subtitle="启动期间累计的接口请求日志">
    <n-grid :cols="3">
      <n-gi>
        <n-statistic label="总请求数量" :value="logs.length" />
      </n-gi>
      <n-gi>
        <n-statistic
          label="请求数量"
          :value="logs.filter((item) => item.status === '发起请求').length"
        />
      </n-gi>
      <n-gi>
        <n-statistic
          label="响应数量"
          :value="
            logs.length -
            logs.filter((item) => item.status === '发起请求').length
          "
        />
      </n-gi>
    </n-grid>
    <template #title>
      <a href="#" style="text-decoration: none; color: inherit">日志管理</a>
    </template>
  </n-page-header>
  <n-data-table
    :data="logs"
    :columns="columns"
    style="margin-top: 10px"
    :pagination="paginationReactive"
    remote
    max-height="calc(100vh - 320px)"
  />
</template>

<script lang="ts" setup>
import { ref, onMounted, reactive, h } from "vue";
import { getLogInfo } from "@/api/system";
const logs = ref<Log[]>([]);
const total = ref(0);
const getLog = async () => {
  const res = await getLogInfo({
    page: paginationReactive.page,
    size: paginationReactive.pageSize,
  });
  const { data } = res.data;
  total.value = data.total
  logs.value = data.logs;
  paginationReactive.pageCount = Math.ceil(
    total.value / paginationReactive.pageSize
  );
};
const paginationReactive = reactive({
  page: 1,
  pageSize: 10,
  showSizePicker: true,
  pageSizes: [5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 100],
  pageCount: 1,
  onChange: (page: number) => {
    paginationReactive.page = page;
    getLog();
  },
  onUpdatePageSize: (pageSize: number) => {
    paginationReactive.pageSize = pageSize;
    paginationReactive.page = 1;
    getLog();
  },
});
onMounted(() => {
  getLog();
});
const columns = [
  {
    title: "时间",
    key: "time",
    render(row: Log) {
      return h("span", null, new Date(row.time).toLocaleString());
    },
  },
  {
    title: "用户",
    key: "uid",
    width: 100,
    render(row: Log) {
      return h("span", null, row.uid !== -1 ? row.uid : "匿名用户");
    },
  },
  {
    title: "请求地址",
    key: "url",
  },
  {
    title: "请求方法",
    key: "method",
  },
  {
    title: "请求状态",
    key: "status",
  },
  {
    title: "请求IP",
    key: "ip",
  },
];
</script>
