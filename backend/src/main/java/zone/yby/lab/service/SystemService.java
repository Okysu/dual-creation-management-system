package zone.yby.lab.service;

import zone.yby.lab.domain.Pagination;
import zone.yby.lab.domain.RedisRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SystemService {
    /**
     * 获取最近七天登录人数
     *
     * @return 登录人数列表
     */
    Integer[] getLoginCount();

    /**
     * 获取过去5周的新增用户数量
     *
     * @return 新增用户数量列表
     */
    Map<String, Integer> getNewUsersCount();

    /**
     * 获取日志信息
     *
     * @param pagination 分页信息
     * @return 日志信息列表
     */
    HashMap<String, Object> getLogList(Pagination pagination);

    /**
     * 获取所有Redis缓存的键值
     *
     * @return 键值列表
     */
    List<RedisRecord> getRedisKeys();

    /**
     * 设置Redis缓存
     *
     * @param redisRecord Redis缓存信息
     */
    void setRedisKey(RedisRecord redisRecord);

    /**
     * 删除Redis缓存
     *
     * @param redisRecord Redis缓存信息
     */
    void deleteRedisKey(RedisRecord redisRecord);
}
