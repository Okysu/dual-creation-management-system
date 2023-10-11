package zone.yby.lab.controller;

import database.domain.Role;
import database.domain.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zone.yby.lab.domain.Pagination;
import zone.yby.lab.service.UserService;
import zone.yby.lab.utils.ResponseResult;

import java.util.HashMap;
import java.util.List;

@RestController
public class UserForAdminController {
    private final UserService userService;

    public UserForAdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/admin/user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseResult<?> addUser(@RequestBody User user) {
        userService.addUser(user, user.getRole());
        return new ResponseResult<>(200, "添加成功");
    }

    @PutMapping("/admin/user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseResult<?> updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return new ResponseResult<>(200, "更新成功");
    }

    @DeleteMapping("/admin/user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseResult<?> deleteUser(@RequestBody User user) {
        userService.disableUser(user.getId());
        return new ResponseResult<>(200, "删除成功");
    }

    @GetMapping("/admin/user/count")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseResult<List<Role>> countRole() {
        return new ResponseResult<>(200, "获取成功", userService.getRole());
    }

    @GetMapping("/admin/user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseResult<HashMap<String, Object>> getUserList(@RequestParam(value = "page", required = false) Integer page,
                                                               @RequestParam(value = "size", required = false) Integer size) {
        Pagination pagination = new Pagination(page, size);
        return new ResponseResult<>(200, "获取成功", userService.getUserList(pagination, null));
    }
}
