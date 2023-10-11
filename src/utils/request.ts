import axios from "axios";
import sha1 from "sha1";
import { deepClone } from "./function";
import { useUserConfig } from "@/stores/userConfig";
import router from "@/router";

const PASSWORD_SALT = "";

let userConfig: ReturnType<typeof useUserConfig>;

const request = axios.create({
  baseURL: import.meta.env.VITE_BASE_URL,
  timeout: 5000,
});

request.interceptors.request.use(
  function (config) {
    if (window.$loadingbar) window.$loadingbar.start();
    // init user config
    if (!userConfig) userConfig = useUserConfig();
    // deep copying to avoid side effects caused by directly referencing reactive data.
    config.params = config.params ? deepClone(config.params) : config.params;
    // if using FormData, do not deepclone
    if (config.data instanceof FormData) {
      config.data = config.data;
    } else {
      config.data = config.data ? deepClone(config.data) : config.data;
    }
    // encrypt password
    if (config.data && config.data.password)
      config.data.password = sha1(config.data.password + PASSWORD_SALT);
    if (config.data && config.data.old_password)
      config.data.old_password = sha1(config.data.old_password + PASSWORD_SALT);
    if (config.data && config.data.new_password)
      config.data.new_password = sha1(config.data.new_password + PASSWORD_SALT);
    // add timestamp to request to avoid caching when using GET method
    if (config.method?.toLocaleLowerCase() === "get") {
      config.params = {
        ...config.params,
        t: Date.now(),
      };
    }
    // add token to request
    if (userConfig.login) {
      config.headers.Authorization = "Bearer " + userConfig.token;
    }
    return config;
  },
  function (error) {
    if (window.$loadingbar) window.$loadingbar.error();
    return Promise.reject(error);
  }
);

interface ResponseData<T = any> {
  code: number;
  data: T;
  msg: string;
}

request.interceptors.response.use(
  function (response) {
    if (window.$loadingbar) window.$loadingbar.finish();
    // response interceptor
    const result = response.data;
    const { code, msg } = result as ResponseData;
    if (code === 401) {
      window.$message.error("登录已过期，请重新登录");
      userConfig.logout();
      router.replace({ name: "login" });
      return Promise.reject("登录已过期，请重新登录");
    } else if (code === 403) {
      window.$message.error("您没有权限访问该页面");
      router.replace({ name: "console" });
      return Promise.reject("您没有权限访问该页面");
    } else if (code !== 200) {
      window.$message.error(msg);
      return Promise.reject(msg);
    }
    return response;
  },
  function (error) {
    if (window.$loadingbar) window.$loadingbar.error();
    return Promise.reject(error);
  }
);

export default request;
