package zone.yby.lab.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import database.domain.Role;
import database.domain.RoleMap;
import database.domain.User;
import database.mapper.RoleMapMapper;
import database.mapper.RoleMapper;
import database.mapper.UserMapper;
import org.springframework.stereotype.Service;
import zone.yby.lab.domain.Pagination;
import zone.yby.lab.service.UserService;
import zone.yby.lab.utils.SelfPasswordEncoder;

import java.util.HashMap;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final RoleMapMapper roleMapMapper;

    private final RoleMapper roleMapper;


    public UserServiceImpl(UserMapper userMapper, RoleMapMapper roleMapMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.roleMapMapper = roleMapMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public HashMap<String, Object> getUserList(Pagination pagination, Wrapper<User> wrapper) {
        HashMap<String, Object> map = new HashMap<>();
        if (pagination.getPage() == null || pagination.getSize() == null) {
            List<User> users = userMapper.selectList(wrapper);
            users.forEach(user -> {
                Integer rid = roleMapMapper.selectOne(new LambdaQueryWrapper<RoleMap>().eq(RoleMap::getUid, user.getId())).getRid();
                user.setRole(rid);
            });
            map.put("total", users.size());
            map.put("users", users);
            return map;
        }
        int page = pagination.getPage();
        int size = pagination.getSize();
        Page<User> userPage = new Page<>(page, size);
        List<User> users = userMapper.selectPage(userPage, wrapper).getRecords();
        // 查询用户角色
        users.forEach(user -> {
            Integer rid = roleMapMapper.selectOne(new LambdaQueryWrapper<RoleMap>().eq(RoleMap::getUid, user.getId())).getRid();
            user.setRole(rid);
        });
        map.put("total", userPage.getTotal());
        map.put("users", users);
        return map;
    }

    @Override
    public User getUserById(Integer id) {
        User user = userMapper.selectById(id);
        // 查询用户角色
        Integer rid = roleMapMapper.selectOne(new LambdaQueryWrapper<RoleMap>().eq(RoleMap::getUid, user.getId())).getRid();
        user.setRole(rid);
        return user;
    }

    @Override
    public User getUserByUid(Integer uid) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUid, uid));
        // 查询用户角色
        Integer rid = roleMapMapper.selectOne(new LambdaQueryWrapper<RoleMap>().eq(RoleMap::getUid, user.getId())).getRid();
        user.setRole(rid);
        return user;
    }

    @Override
    public void disableUser(Integer id) {
        User user = userMapper.selectById(id);
        user.setDisabled(user.getDisabled() == 0 ? 1 : 0);
        userMapper.updateById(user);
    }

    @Override
    public void updateUser(User user) {
        // 给uid置空，防止修改学工号
        user.setUid(null);
        if (!user.getPassword().isEmpty()) {
            user.setPassword(new SelfPasswordEncoder().encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        userMapper.updateById(user);
        // 更新角色
        RoleMap roleMap = roleMapMapper.selectOne(new LambdaQueryWrapper<RoleMap>().eq(RoleMap::getUid, user.getId()));
        roleMap.setRid(user.getRole());
        roleMapMapper.updateById(roleMap);
    }

    @Override
    public void addUser(User user, Integer role) {
        user.setId(null); // 自增用
        if (user.getPassword().isEmpty()) {
            // 12345678
            user.setPassword("991740fb5d93abd2446a531f0e90bee9ccf6b59c1414e9aa6519d7a931edb7ef");
        } else {
            user.setPassword(new SelfPasswordEncoder().encode(user.getPassword()));
        }
        // 判断是否有相同的学工号
        User equal = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUid, user.getUid()));
        if (equal != null) {
            throw new RuntimeException("学工号已存在");
        }
        RoleMap roleMap = new RoleMap();
        roleMap.setUid(user.getId());
        roleMap.setRid(role);
        roleMapMapper.insert(roleMap);
    }

    @Override
    public List<Role> getRole() {
        List<Role> roles = roleMapper.selectList(null);
        roles.forEach(role -> {
            Integer count = Math.toIntExact(roleMapMapper.selectCount(new LambdaQueryWrapper<RoleMap>().eq(RoleMap::getRid, role.getId())));
            role.setCount(count);
        });
        return roles;
    }
}
