import request from "@/utils/request";

/**
 * 获取实验室列表
 * @permission contest
 */
export const getLabList = () => {
  return request.get("/contest/lab");
};

/**
 * 获取详细实验室信息
 * @permission contest
 */
export const getLabDetail = (id: number) => {
  return request.get(`/contest/lab/${id}`);
};

/**
 * 新增实验室
 * @permission contest
 */
export const addLab = (data: Lab) => {
  return request.post("/contest/lab", data);
};

/**
 * 删除实验室
 * @permission contest
 */
export const deleteLab = (id: number) => {
  return request.delete("/contest/lab", { data: { id } });
};

/**
 * 更新实验室信息
 * @permission contest
 */
export const updateLab = (data: Lab) => {
  return request.put("/contest/lab", data);
};
