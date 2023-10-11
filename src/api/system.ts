import request from "@/utils/request";

export const getSystemInfo = () => {
  return request.get("/public/system");
};

export const getLoginCount = () => {
  return request.get("/public/count/login");
};

export const getNewUserCount = () => {
  return request.get("/public/count/new");
};

export const getLogInfo = (
  args: { page: number; size: number } = { page: 1, size: 10 }
) => {
  return request.get("/admin/log", { params: args });
};

export const getRedisCache = () => {
  return request.get("/admin/cache");
};

export const updateRedisCache = (args: {
  key: string;
  value: string;
  ttl: number;
}) => {
  return request.put("/admin/cache", args);
};

export const deleteRedisCache = (key: string) => {
  return request.delete("/admin/cache", { data: { key } });
};
