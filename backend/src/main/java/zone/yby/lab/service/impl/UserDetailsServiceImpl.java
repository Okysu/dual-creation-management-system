package zone.yby.lab.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import database.domain.RoleMap;
import database.domain.User;
import database.mapper.MenuMapper;
import database.mapper.RoleMapMapper;
import database.mapper.RoleMapper;
import database.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import zone.yby.lab.domain.LoginUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    private final RoleMapMapper roleMapMapper;

    private final RoleMapper roleMapper;

    private final MenuMapper menuMapper;

    public UserDetailsServiceImpl(UserMapper userMapper, RoleMapMapper roleMapMapper, RoleMapper roleMapper, MenuMapper menuMapper) {
        this.userMapper = userMapper;
        this.roleMapMapper = roleMapMapper;
        this.roleMapper = roleMapper;
        this.menuMapper = menuMapper;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户信息
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUid, Integer.parseInt(username));
        User user = userMapper.selectOne(queryWrapper);
        // 没有用户就抛出异常
        if (Objects.isNull(user)) {
            throw new RuntimeException("不存在当前账号");
        }
        // 是个正常的用户都应该有个权限 除非有人不给你权限
        LambdaQueryWrapper<RoleMap> roleMapQueryWrapper = new LambdaQueryWrapper<RoleMap>()
                .eq(RoleMap::getUid, user.getId());
        RoleMap roleMap = roleMapMapper.selectOne(roleMapQueryWrapper);
        // 没有权限就抛出异常，但很显然是不可能的
        if (Objects.isNull(roleMap)) {
            throw new RuntimeException("当前账号没有权限");
        }
        // 查询权限
        String role = roleMapper.selectById(roleMap.getRid()).getName();
        // 赋予他本身的一个权限
        List<String> permissions = new ArrayList<>();
        permissions.add(role);
        // 根据权限查询菜单
        String menu = menuMapper.selectById(roleMap.getRid()).getMenu();
        // 将菜单信息存入用户信息
        user.setMenu(menu);
        // 返回用户信息
        return new LoginUser(user, permissions);
    }
}
