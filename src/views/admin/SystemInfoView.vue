<script lang="ts" setup>
import { binarySize, describeTime } from "@/utils/strings";
import { isWidthLessThan } from "@/utils/function";
import { getSystemInfo, getLoginCount, getNewUserCount } from "@/api/system";
import { onMounted, ref } from "vue";
import * as echarts from "echarts/core";
import { LineChart } from "echarts/charts";
import {
  TitleComponent,
  TooltipComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent,
} from "echarts/components";
import { LabelLayout, UniversalTransition } from "echarts/features";
import { CanvasRenderer } from "echarts/renderers";
echarts.use([
  TitleComponent,
  TooltipComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent,
  LineChart,
  LabelLayout,
  UniversalTransition,
  CanvasRenderer,
]);

const info = ref({
  time: 0,
  memory: 0,
  version: "",
});

const cols = ref({
  header: 3,
  charts: 2,
});

getSystemInfo().then((res) => {
  const { data } = res;
  info.value = data.data;
});

onMounted(() => {
  getLoginCount().then((res) => {
    const loginCount = document.getElementById("login") as HTMLDivElement;
    const { data } = res;
    const listDays = () => {
      const today = new Date();
      const days = [];
      days[0] = "今天";
      days[1] = "昨天";
      days[2] = "前天";
      for (let i = 3; i < 7; i++) {
        const date = new Date(today);
        date.setDate(today.getDate() - i);
        days[i] = `${date.getMonth() + 1}-${date.getDate()}`;
      }
      return days;
    };
    const option = {
      xAxis: {
        type: "category",
        data: listDays(),
      },
      yAxis: {
        type: "value",
      },
      series: [
        {
          data: data.data,
          type: "line",
        },
      ],
    };
    const chart = echarts.init(loginCount);
    chart.setOption(option);
    isWidthLessThan(768, (val: boolean) => {
      if (val) {
        chart.resize({ width: 300 });
      } else {
        chart.resize({ width: 600 });
      }
    });
  });

  getNewUserCount().then((res) => {
    const { data } = res;
    const count = data.data as JSON;
    const keys = Object.keys(count);
    const values = Object.values(count) as number[];
    const newCount = document.getElementById("new") as HTMLDivElement;
    const option = {
      xAxis: {
        type: "category",
        data: keys,
      },
      yAxis: {
        type: "value",
      },
      series: [
        {
          data: values,
          type: "line",
        },
      ],
    };
    const chart = echarts.init(newCount);
    chart.setOption(option);
    isWidthLessThan(768, (val: boolean) => {
      if (val) {
        chart.resize({ width: 300 });
      } else {
        chart.resize({ width: 600 });
      }
    });
  });

  const resetCols = (isMobile: boolean) => {
    if (isMobile) {
      cols.value.header = 1;
      cols.value.charts = 1;
    } else {
      cols.value.header = 3;
      cols.value.charts = 2;
    }
  };

  isWidthLessThan(768, (val: boolean) => {
    resetCols(val);
  })
    ? resetCols(true)
    : resetCols(false);
});
</script>

<template>
  <n-page-header subtitle="系统信息总览">
    <n-grid :cols="cols.header">
      <n-gi>
        <n-statistic
          label="运行时间"
          :value="describeTime(new Date(info.time))"
        />
      </n-gi>
      <n-gi>
        <n-statistic label="内存占用" :value="binarySize(info.memory)" />
      </n-gi>
      <n-gi>
        <n-statistic label="系统版本" :value="info.version" />
      </n-gi>
    </n-grid>
    <template #title>
      <a href="#" style="text-decoration: none; color: inherit">系统信息</a>
    </template>
  </n-page-header>
  <n-grid x-gap="5" y-gap="5" :cols="cols.charts" style="margin-top: 15px">
    <n-gi>
      <n-card title="过去7天登录次数">
        <div id="login" style="width: 100%; height: 400px"></div>
      </n-card>
    </n-gi>
    <n-gi>
      <n-card title="过去5周新增用户">
        <div id="new" style="width: 100%; height: 400px"></div>
      </n-card>
    </n-gi>
  </n-grid>
</template>
