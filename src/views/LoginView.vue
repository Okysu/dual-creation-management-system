<script lang="ts" setup>
import { storeToRefs } from "pinia";
import { useAppConfig } from "@/stores/appConfig";
import { useUserConfig } from "@/stores/userConfig";
import { login as userlogin, logout as userlogout } from "@/api/user";
import { onMounted, reactive, ref, nextTick } from "vue";
import type { FormInst, FormItemRule } from "naive-ui";
import Captcha from "@/utils/captcha";
import { describeTime } from "@/utils/strings";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();

const redirect_uri = route.query.redirect_uri as string;

const { theme, menu } = storeToRefs(useAppConfig());
const userConfig = useUserConfig();
const { token, user, signAt } = storeToRefs(userConfig);

const themeOptions = [
  { label: "系统", value: "auto" },
  { label: "亮色", value: "light" },
  { label: "暗色", value: "dark" },
];

const loginForm = ref<FormInst>();
const loginValue = reactive({
  username: "",
  password: "",
  captcha: "",
  loading: false,
});

const emptyLoginValue = () => {
  loginValue.username = "";
  loginValue.password = "";
  loginValue.captcha = "";
  loginValue.loading = false;
};

const captcha = ref<HTMLCanvasElement>();

const captchaInstance = new Captcha();

const loginRules = {
  username: [
    {
      required: true,
      message: "请输入你的账号",
      trigger: "blur",
    },
    {
      min: 3,
      max: 64,
      message: "长度在 3 到 64 个字符",
      trigger: "blur",
    },
  ],
  password: [
    {
      required: true,
      message: "请输入你的密码",
      trigger: "blur",
    },
    {
      min: 6,
      max: 64,
      message: "长度在 6 到 64 个字符",
      trigger: "blur",
    },
  ],
  captcha: {
    required: true,
    trigger: "blur",
    validator(rule: FormItemRule, value: string) {
      if (!value) {
        return new Error("请输入验证码");
      }
      if (!captchaInstance.verify(Number(value))) {
        return new Error("验证码错误");
      }
      return true;
    },
  },
};

type loginResponse = {
  code: number;
  data: {
    token: string;
    user: User;
  };
  msg: string;
};

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
      nextTick(() => {
        if (captcha.value) captchaInstance.setRef(captcha.value);
        captchaInstance.generateCaptcha();
      });
    });
};

const login = (e: Event) => {
  e.preventDefault();
  loginForm.value?.validate((errors) => {
    if (errors) {
      captchaInstance.generateCaptcha();
      return;
    }
    loginValue.loading = true;
    let data = {
      uid: loginValue.username,
      password: loginValue.password,
    };
    userlogin(data)
      .then((res) => {
        const { data } = res.data as loginResponse;
        window.$message.success("登录成功");
        token.value = data.token;
        user.value = data.user;
        menu.value = data.user.menu ?? "[]";
        signAt.value = Date.now();
        emptyLoginValue();
        if (redirect_uri) {
          window.location.href = redirect_uri;
        } else {
          const getFirstView = (menu: Menu[]): string => {
            for (const item of menu) {
              if (item.children) {
                return getFirstView(item.children);
              } else {
                return item.key;
              }
            }
            return "console";
          };
          const firstRoute = getFirstView(JSON.parse(menu.value));
          try {
            router.replace({ name: firstRoute });
          } catch (err) {
            router.replace({ name: "console" });
          }
        }
      })
      .catch(() => {
        window.$message.error("登录失败");
      })
      .finally(() => {
        captchaInstance.generateCaptcha();
        loginValue.loading = false;
      });
  });
};

onMounted(() => {
  if (captcha.value) captchaInstance.setRef(captcha.value);
  captchaInstance.generateCaptcha();
});
</script>

<template>
  <div class="login__box">
    <n-card title="双创实验室管理系统">
      <div v-if="userConfig.login">
        <n-h4>您已登录，当前登录账号为：</n-h4>
        <n-thing>
          <template #header>
            {{ user.name }}
          </template>
          <template #description>
            登录时间：{{ describeTime(new Date(signAt)) }}
          </template>
        </n-thing>
        <n-button type="error" block secondary strong @click="logout">
          退出登录
        </n-button>
      </div>
      <n-tabs
        v-else
        class="card-tabs"
        default-value="signin"
        size="large"
        animated
        pane-wrapper-style="margin: 0 -4px"
        pane-style="padding-left: 4px; padding-right: 4px; box-sizing: border-box;"
      >
        <n-tab-pane name="signin" tab="登录" display-directive="show">
          <n-form ref="loginForm" :model="loginValue" :rules="loginRules">
            <n-form-item path="username" label="用户名">
              <n-input
                v-model:value="loginValue.username"
                type="text"
                placeholder="请输入登录账号..."
              />
            </n-form-item>
            <n-form-item path="password" label="密码">
              <n-input
                v-model:value="loginValue.password"
                type="password"
                placeholder="请输入登录密码..."
              />
            </n-form-item>
            <n-form-item path="captcha" label="验证码">
              <n-input
                v-model:value="loginValue.captcha"
                type="text"
                placeholder="请输入计算结果..."
              />
              <canvas
                ref="captcha"
                @click="captchaInstance.generateCaptcha()"
                height="34"
                width="120"
              ></canvas>
            </n-form-item>
          </n-form>
          <n-button
            type="primary"
            block
            secondary
            strong
            :loading="loginValue.loading"
            :disabled="loginValue.loading"
            @click="login"
          >
            登录
          </n-button>
        </n-tab-pane>
      </n-tabs>
      <template #footer>
        <span class="license"
          >使用本网站的服务则默认你已知晓并同意
          <a href="/license.html" target="_blank">《服务条款》</a>
        </span>
      </template>
      <template #action>
        <div class="footer-tools">
          <n-form-item
            label="主题"
            label-placement="left"
            :show-feedback="false"
          >
            <n-select
              style="width: 80px"
              v-model:value="theme"
              size="small"
              :options="themeOptions"
            />
          </n-form-item>
        </div>
      </template>
    </n-card>
  </div>
</template>

<style scoped>
.login__box {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.n-card {
  width: 460px;
}

.card-tabs .n-tabs-nav--bar-type {
  padding-left: 4px;
}

.license {
  margin-top: 2px;
  font-size: 10px;
  color: grey;
}

.license a {
  color: cornflowerblue;
  text-decoration: none;
}

.footer-tools {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

@media screen and (max-width: 768px) {
  .n-card {
    width: 100%;
    height: 100%;
  }
}
</style>
