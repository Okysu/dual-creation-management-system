<script setup lang="ts">
import { h, reactive, ref } from "vue";
import {
  getRedisCache,
  deleteRedisCache,
  updateRedisCache,
} from "@/api/system";
import { NButton, NSpace } from "naive-ui";
import dialogForm, { type Field } from "@/components/dialogForm.vue";
import { deepClone } from "@/utils/function";
import { describeTime } from "@/utils/strings";

const reidsCache = ref<RedisRecord[]>([]);

const getCache = async () => {
  const res = await getRedisCache();
  const { data } = res.data;
  reidsCache.value = data;
};

(async () => {
  getCache();
})();

const columns = [
  {
    title: "键",
    key: "key",
    ellipsis: {
      tooltip: true,
    },
  },
  {
    title: "值",
    key: "value",
    ellipsis: {
      tooltip: true,
    },
  },
  {
    title: "过期时间",
    key: "ttl",
    render(row: RedisRecord) {
      return row.ttl! > 0
        ? `${describeTime(new Date(row.ttl * 1000 + Date.now()))}`
        : "永久";
    },
    ellipsis: {
      tooltip: true,
    },
  },
  {
    title: "操作",
    key: "action",
    render(row: RedisRecord) {
      const del = h(
        NButton,
        {
          strong: true,
          tertiary: true,
          type: "error",
          size: "small",
          onClick: () => {
            window.$dialog.warning({
              title: "删除",
              content: `确定要删除${row.key}吗？`,
              positiveText: "确定",
              negativeText: "取消",
              transformOrigin: "center",
              onPositiveClick: async () => {
                deleteRedisCache(row.key)
                  .then(() => {
                    window.$message.success("删除成功");
                  })
                  .catch(() => {
                    window.$message.error("删除失败");
                  })
                  .finally(() => {
                    getCache();
                  });
              },
            });
          },
        },
        { default: () => "删除" }
      );
      const edit = h(
        NButton,
        {
          strong: true,
          tertiary: true,
          type: "info",
          size: "small",
          onClick: () => {
            const data = deepClone(row) as RedisRecord;
            updateForm.value?.openModal(data);
          },
        },
        { default: () => "修改" }
      );
      const space = h(NSpace, null, {
        default: () => [del, edit],
      });
      return space;
    },
  },
];

const pagination = reactive({
  page: 1,
  pageSize: 10,
  showSizePicker: true,
  pageSizes: [10, 15, 20, 25, 30, 35, 40, 45, 50, 100],
  onChange: (page: number) => {
    pagination.page = page;
  },
  onUpdatePageSize: (pageSize: number) => {
    pagination.pageSize = pageSize;
    pagination.page = 1;
  },
});

const updateForm = ref<InstanceType<typeof dialogForm> | null>(null);

const fields: Field[] = [
  {
    label: "键",
    field: "key",
    type: "input",
  },
  {
    label: "值",
    field: "value",
    type: "input",
  },
  {
    label: "过期时间",
    field: "ttl",
    type: "input-number",
  },
];

const rules = {
  key: [
    {
      required: true,
      message: "键不能为空",
    },
  ],
  value: [
    {
      required: true,
      message: "值不能为空",
    },
  ],
  ttl: [
    {
      required: true,
      message: "过期时间不能为空",
    },
  ],
};

const formSubmit = (data: RedisRecord, valid: boolean) => {
  if (!valid) {
    window.$message.error("请检查表单");
    return;
  }
  updateRedisCache(data)
    .then(() => {
      window.$message.success("更新成功");
    })
    .catch(() => {
      window.$message.error("更新失败");
    })
    .finally(() => {
      getCache();
    });
};
</script>

<template>
  <n-page-header subtitle="这些是比较重要的登录信息缓存">
    <n-grid :cols="3">
      <n-gi>
        <n-statistic label="总数量" :value="reidsCache.length" />
      </n-gi>
      <n-gi>
        <n-statistic
          label="一天内"
          :value="reidsCache.filter(e => e.ttl! > 0 && e.ttl! <= 60 * 60 * 24).length"
        />
      </n-gi>
      <n-gi>
        <n-statistic
          label="一周内"
          :value="reidsCache.filter(e => e.ttl! > 0 && e.ttl! <= 60 * 60 * 24 * 7).length"
        />
      </n-gi>
    </n-grid>
    <template #title>
      <a href="#" style="text-decoration: none; color: inherit">缓存管理</a>
    </template>
  </n-page-header>
  <n-data-table
    :data="reidsCache"
    :columns="columns"
    style="margin-top: 10px"
    :pagination="pagination"
    remote
    max-height="calc(100vh - 320px)"
  />
  <dialog-form
    ref="updateForm"
    title="更新缓存"
    :field="fields"
    :rules="rules"
    @submit="formSubmit"
  />
</template>
