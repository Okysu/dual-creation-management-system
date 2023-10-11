package zone.yby.lab.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import database.domain.Role;
import database.domain.User;
import zone.yby.lab.domain.Pagination;

import java.util.HashMap;
import java.util.List;

public interface UserService {
    /**
     * 获取用户列表
     *
     * @param pagination 分页信息
     * @return 用户列表
     */
    HashMap<String, Object> getUserList(Pagination pagination, Wrapper<User> wrapper);

    /**
     * 根据用户主键获取用户信息
     *
     * @param id 用户主键
     * @return 用户信息
     */
    User getUserById(Integer id);

    /**
     * 根据用户学工号获取用户信息
     *
     * @param uid 用户学工号
     * @return 用户信息
     */
    User getUserByUid(Integer uid);

    /**
     * 禁用/启用用户
     *
     * @param id 用户主键
     */
    void disableUser(Integer id);

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     */
    void updateUser(User user);

    /**
     * 新增用户
     *
     * @param user 用户信息
     * @param role 用户角色
     */
    void addUser(User user, Integer role);

    /**
     * 获取当前的角色信息及其数量
     *
     * @return 角色信息列表
     */
    List<Role> getRole();
}
