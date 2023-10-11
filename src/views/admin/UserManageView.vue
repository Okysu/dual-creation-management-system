<script setup lang="ts">
import { computed, h, onMounted, reactive, ref } from "vue";
import {
  getUserCount,
  getUserList,
  deleteUser,
  updateUser,
  addUser,
} from "@/api/user";
import { describeTime } from "@/utils/strings";
import { NButton, NTag, NSpace, type FormItemRule } from "naive-ui";
import dialogForm, { type Field } from "@/components/dialogForm.vue";
import { deepClone, isWidthLessThan } from "@/utils/function";
import * as echarts from "echarts/core";
import { PieChart } from "echarts/charts";
import { LabelLayout } from "echarts/features";
import { CanvasRenderer } from "echarts/renderers";

echarts.use([PieChart, CanvasRenderer, LabelLayout]);

const userForm = ref<InstanceType<typeof dialogForm>>();
const addMode = ref(false);

const roleCount = ref<Role[]>([]);
const users = ref<User[]>([]);
const total = ref(0);
const paginationReactive = reactive({
  page: 1,
  pageSize: 10,
  showSizePicker: true,
  pageSizes: [5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 100],
  pageCount: 1,
  onChange: (page: number) => {
    paginationReactive.page = page;
    getUsers();
  },
  onUpdatePageSize: (pageSize: number) => {
    paginationReactive.pageSize = pageSize;
    paginationReactive.page = 1;
    getUsers();
  },
});

const getUsers = async () => {
  const res = await getUserList({
    page: paginationReactive.page,
    size: paginationReactive.pageSize,
  });
  const { data } = res.data;
  total.value = data.total;
  users.value = data.users;
  paginationReactive.pageCount = Math.ceil(
    total.value / paginationReactive.pageSize
  );
};

const getUserCountInfo = async () => {
  const res = await getUserCount();
  const { data } = res.data;
  roleCount.value = data;
  const roleCountDom = document.getElementById("roleCount") as HTMLDivElement;
  const chart = echarts.init(roleCountDom);
  const option = {
    series: [
      {
        name: "Access From",
        type: "pie",
        radius: "50%",
        data: roleCount.value.map((r) => ({
          name: r.name,
          value: r.count,
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
  };
  chart.setOption(option);
  isWidthLessThan(768, (_val: boolean) => {
    chart.resize()
  });
};

onMounted(() => {
  getUsers();
  getUserCountInfo();
});

const columns = [
  {
    title: "学工号",
    key: "uid",
  },
  {
    title: "姓名",
    key: "name",
  },
  {
    title: "角色",
    key: "role",
    render(row: User) {
      if (!row.role) return h("span", null, "未知");
      else {
        const find = roleCount.value.find((r) => r.id === row.role);
        return h("span", null, find ? find.name : "未知");
      }
    },
  },
  {
    title: "邮箱",
    key: "mail",
  },
  {
    title: "过期时间",
    key: "expire",
    render(row: User) {
      if (!row.expire || new Date(row.expire).getTime() === 0)
        return h("span", null, "永久");
      else return h("span", null, describeTime(new Date(row.expire)));
    },
  },
  {
    title: "状态",
    key: "status",
    render(row: User) {
      return h(NTag, { type: row.disabled ? "error" : "success" }, () =>
        row.disabled ? "已禁用" : "启用中"
      );
    },
  },
  {
    title: "操作",
    key: "action",
    render(row: User) {
      const disabled = h(
        NButton,
        {
          type: row.disabled ? "success" : "error",
          strong: true,
          tertiary: true,
          onClick: () => {
            if (row.role && row.role === 1) {
              window.$message.error("无法禁用管理员账户");
              return;
            }
            deleteUser(row.id).then(() => {
              getUsers();
            });
          },
        },
        () => (row.disabled ? "启用" : "禁用")
      );
      const edit = h(
        NButton,
        {
          type: "info",
          strong: true,
          tertiary: true,
          onClick: () => {
            if (row.role && row.role === 1) {
              window.$message.error("无法编辑管理员账户");
              return;
            }
            addMode.value = false;
            const data = {
              ...deepClone(row),
              expire: row.expire ? new Date(row.expire).getTime() : 0,
            };
            userForm.value?.openModal(data);
          },
        },
        () => "编辑"
      );
      const space = h(NSpace, null, {
        default: () => [disabled, edit],
      });
      return space;
    },
  },
];

const fields = computed<Field[]>(() => [
  {
    label: "学工号",
    field: "uid",
    type: "input-number",
    disabled: !addMode.value,
  },
  {
    label: "姓名",
    field: "name",
    type: "input",
  },
  {
    label: "邮箱",
    field: "mail",
    type: "input",
  },
  {
    label: "角色",
    field: "role",
    type: "select",
    options: roleCount.value.map((r) => ({
      label: r.name,
      value: r.id,
    })),
  },
  {
    label: "过期时间",
    field: "expire",
    type: "date-picker",
  },
  {
    label: "密码",
    field: "password",
    type: "input-password",
    placeholder: "留空则不修改密码/默认密码为12345678",
  },
]);

const rules = {
  uid: [
    {
      required: true,
      message: "学工号不能为空",
    },
  ],
  name: [
    {
      required: true,
      message: "姓名不能为空",
    },
  ],
  mail: [
    {
      required: true,
      message: "邮箱不能为空",
      validator: (_rule: FormItemRule, value: string) => {
        if (!value) {
          return new Error("邮箱不能为空");
        }
        if (!value.match(/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/))
          return new Error("邮箱格式不正确");
        return true;
      },
    },
  ],
  role: [
    {
      required: true,
      message: "角色不能为空",
    },
  ],
  expire: [
    {
      required: true,
      message: "过期时间不能为空",
      validator: (_rule: FormItemRule, value: number) => {
        if (value === 0) return true;
        if (value < new Date().getTime()) {
          return new Error("过期时间不能小于当前时间");
        }
        if (value > new Date().getTime() + 1000 * 60 * 60 * 24 * 365 * 10) {
          return new Error("过期时间不能大于10年");
        }
        return true;
      },
    },
  ],
  password: [
    {
      validator: (_rule: FormItemRule, value: string) => {
        if (!value) return true;
        if (value.length < 8) {
          return new Error("密码长度不能小于8位");
        }
        return true;
      },
    },
  ],
};

const formSubmit = (data: User, valid: boolean) => {
  if (!valid) {
    window.$message.error("请检查表单");
    return;
  }
  if (addMode.value) {
    addUser(data).then(() => {
      window.$message.success("添加成功");
      getUsers();
    });
  } else {
    updateUser(data).then(() => {
      window.$message.success("修改成功");
      getUsers();
    });
  }
  userForm.value?.closeModal();
};
</script>

<template>
  <n-page-header subtitle="用户信息管理">
    <n-grid :cols="2">
      <n-gi>
        <n-grid :cols="3">
          <n-gi v-for="r in roleCount" :key="r.id">
            <n-statistic :label="r.name" :value="r.count" />
          </n-gi>
          <n-gi>
            <n-statistic label="用户总数" :value="total" />
          </n-gi>
        </n-grid>
      </n-gi>
      <n-gi>
        <div id="roleCount" style="height: 400px"></div>
      </n-gi>
    </n-grid>
    <template #title>
      <a href="#" style="text-decoration: none; color: inherit">用户管理</a>
    </template>
    <template #extra>
      <n-button
        type="primary"
        secondary
        strong
        @click="(addMode = true), userForm?.openModal({ role: 2 })"
        >添加用户</n-button
      >
    </template>
  </n-page-header>
  <n-data-table
    :data="users"
    :columns="columns"
    style="margin-top: 10px"
    :pagination="paginationReactive"
    remote
    max-height="calc(100vh - 320px)"
  />
  <dialog-form
    ref="userForm"
    :title="addMode ? '添加用户' : '编辑用户'"
    :field="fields"
    :rules="rules"
    @submit="formSubmit"
  />
</template>
