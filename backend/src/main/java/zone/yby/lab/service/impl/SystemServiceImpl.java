package zone.yby.lab.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import database.domain.Log;
import database.mapper.LogMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import zone.yby.lab.domain.Pagination;
import zone.yby.lab.domain.RedisRecord;
import zone.yby.lab.service.SystemService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class SystemServiceImpl implements SystemService {

    private final LogMapper logMapper;

    private final RedisTemplate<String, String> redisTemplate;

    public SystemServiceImpl(LogMapper logMapper, RedisTemplate<String, String> redisTemplate) {
        this.logMapper = logMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Integer[] getLoginCount() {
        // 获取从今天起连续七天的登录人数
        Integer[] loginCount = new Integer[7];
        LocalDate now = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate date = now.minusDays(i);
            LambdaQueryWrapper<Log> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Log::getUrl, "/user/login")
                    .eq(Log::getMethod, "POST")
                    .eq(Log::getStatus, "请求完成:200")
                    .ge(Log::getTime, date.atStartOfDay()) // 开始时间为当天的0点
                    .lt(Log::getTime, date.plusDays(1).atStartOfDay()); // 结束时间为次日的0点
            loginCount[i] = Math.toIntExact(logMapper.selectCount(queryWrapper));
        }
        return loginCount;
    }

    @Override
    public Map<String, Integer> getNewUsersCount() {
        Map<String, Integer> result = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        DateTimeFormatter day = DateTimeFormatter.ofPattern("dd");
        LocalDate now = LocalDate.now();
        for (int i = 0; i < 5; i++) {
            LocalDate startOfWeek = now.minusWeeks(i).with(DayOfWeek.MONDAY); // 获取周一的日期
            LocalDate endOfWeek = startOfWeek.plusDays(6); // 获取周日的日期
            LambdaQueryWrapper<Log> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Log::getUrl, "/admin/user")
                    .eq(Log::getMethod, "POST")
                    .eq(Log::getStatus, "请求完成:200")
                    .ge(Log::getTime, startOfWeek.atStartOfDay()) // 开始时间为周一的0点
                    .lt(Log::getTime, endOfWeek.plusDays(1).atStartOfDay()); // 结束时间为下周一的0点
            int count = Math.toIntExact(logMapper.selectCount(queryWrapper));
            result.put(startOfWeek.format(formatter) + "~" + endOfWeek.format(day), count);
        }
        return result;
    }

    @Override
    public HashMap<String, Object> getLogList(Pagination pagination) {
        int page = pagination.getPage();
        int size = pagination.getSize();
        Page<Log> logPage = new Page<>(page, size);
        LambdaQueryWrapper<Log> queryWrapper = new LambdaQueryWrapper<Log>().orderByDesc(Log::getTime);
        List<Log> logs = logMapper.selectPage(logPage, queryWrapper).getRecords();
        HashMap<String, Object> data = new HashMap<>();
        data.put("total", logPage.getTotal());
        data.put("logs", logs);
        return data;
    }

    @Override
    public List<RedisRecord> getRedisKeys() {
        Set<String> keys = redisTemplate.keys("*");
        List<RedisRecord> keyMap = new ArrayList<>();
        assert keys != null;
        for (String key : keys) {
            RedisRecord data = new RedisRecord();
            data.setKey(key);
            data.setValue(redisTemplate.opsForValue().get(key));
            // 查询过期时间
            Long expire = redisTemplate.getExpire(key);
            if (expire != null) {
                data.setTtl(Math.toIntExact(expire));
            }
            keyMap.add(data);
        }
        return keyMap;
    }

    @Override
    public void setRedisKey(RedisRecord redisRecord) {
        String key = redisRecord.getKey();
        String value = redisRecord.getValue();
        // 如果过期时间是0或者-1，则不设置过期时间
        Integer expire = redisRecord.getTtl();
        if (expire != null && expire > 0) {
            redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    @Override
    public void deleteRedisKey(RedisRecord redisRecord) {
        String key = redisRecord.getKey();
        redisTemplate.delete(key);
    }
}
