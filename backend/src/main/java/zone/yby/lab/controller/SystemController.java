package zone.yby.lab.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zone.yby.lab.domain.Pagination;
import zone.yby.lab.domain.RedisRecord;
import zone.yby.lab.service.SystemService;
import zone.yby.lab.utils.ResponseResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SystemController {

    private final SystemService systemService;

    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }

    @GetMapping("/public/count/login")
    public ResponseResult<Integer[]> getLoginCount() {
        return new ResponseResult<>(200, "获取成功", systemService.getLoginCount());
    }

    @GetMapping("/public/count/new")
    public ResponseResult<Map<String, Integer>> getNewUsersCount() {
        return new ResponseResult<>(200, "获取成功", systemService.getNewUsersCount());
    }

    @GetMapping("/admin/log")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseResult<HashMap<String, Object>> getLogCount(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        Pagination pagination = new Pagination(page, size);
        return new ResponseResult<>(200, "获取成功", systemService.getLogList(pagination));
    }

    @GetMapping("/admin/cache")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseResult<List<RedisRecord>> getRedisKeys() {
        return new ResponseResult<>(200, "获取成功", systemService.getRedisKeys());
    }

    @PutMapping("/admin/cache")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseResult<String> setRedisKey(@RequestBody RedisRecord redisRecord) {
        systemService.setRedisKey(redisRecord);
        return new ResponseResult<>(200, "设置成功");
    }

    @DeleteMapping("/admin/cache")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseResult<String> deleteRedisKey(@RequestBody RedisRecord redisRecord) {
        systemService.deleteRedisKey(redisRecord);
        return new ResponseResult<>(200, "删除成功");
    }
}
