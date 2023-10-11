import request from "@/utils/request";

export const login = (agrs: { uid: string; password: string }) => {
  return request.post("/user/login", agrs);
};

export const logout = () => {
  return request.delete("/user/logout");
};

// type UserPasswordForm = {
//   old_password: string;
//   new_password: string;
// };

// export const resetPassword = (data: UserPasswordForm) => {
//   return request.put("/user/reset-password", data);
// };

/**
 * 获取用户列表
 * @permission admin
 */
export const getUserList = (pagination: Pagination) => {
  return request.get("/admin/user", { params: pagination });
};

/**
 * 新增用户
 * @permission admin
 */
export const addUser = (data: User) => {
  return request.post("/admin/user", data);
}

/**
 * 删除用户(其实是禁用用户)
 * @permission admin
 */
export const deleteUser = (id: number) => {
  return request.delete("/admin/user", { data: { id } });
}

/**
 * 更新用户信息
 * @permission admin
 */
export const updateUser = (data: User) => {
  return request.put("/admin/user", data);
}

/**
 * 统计用户信息
 * @permission admin
 */
export const getUserCount = () => {
  return request.get("/admin/user/count");
}