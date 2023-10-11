package zone.yby.lab.service;

import database.domain.Lab;
import database.domain.User;

import java.util.List;

public interface LabService {
    /**
     * 获取实验室列表
     *
     * @param filter 是否过滤已删除的实验室
     * @return 实验室列表
     */
    List<Lab> getLabList(boolean filter);

    /**
     * 添加实验室
     *
     * @param lab 实验室信息
     * @return 重复添加的成员
     */
    List<User> addLab(Lab lab);

    /**
     * 根据实验室id获取实验室信息
     *
     * @param id 实验室id
     * @return 实验室信息
     */
    Lab getLabById(Integer id);

    /**
     * 更新实验室信息
     *
     * @param lab 实验室信息（不更新成员内容）
     */
    void updateLab(Lab lab);

    /**
     * 禁用/启用实验室
     *
     * @param id 实验室id
     */
    void disableLab(Integer id);

    /**
     * 添加成员
     *
     * @param lab  实验室信息
     * @param user 成员
     */
    void addMember(Lab lab, User user);

    /**
     * 删除成员
     *
     * @param lab  实验室信息
     * @param user 成员
     */
    void deleteMember(Lab lab, User user);
}
