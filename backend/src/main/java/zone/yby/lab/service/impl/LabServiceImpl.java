package zone.yby.lab.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import database.domain.Lab;
import database.domain.LabMemberMap;
import database.domain.User;
import database.mapper.LabMapper;
import database.mapper.LabMemberMapMapper;
import org.springframework.stereotype.Service;
import zone.yby.lab.service.LabService;
import zone.yby.lab.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class LabServiceImpl implements LabService {

    private final LabMapper labMapper;

    private final UserService userService;

    private final LabMemberMapMapper labMemberMapMapper;

    public LabServiceImpl(LabMapper labMapper, UserService userService, LabMemberMapMapper labMemberMapMapper) {
        this.labMapper = labMapper;
        this.userService = userService;
        this.labMemberMapMapper = labMemberMapMapper;
    }

    @Override
    public List<Lab> getLabList(boolean filter) {
        if (!filter) return labMapper.selectList(null);
        LambdaQueryWrapper<Lab> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Lab::getDelFlag, 0);
        return labMapper.selectList(queryWrapper);
    }

    @Override
    public List<User> addLab(Lab lab) {
        labMapper.insert(lab);
        List<User> teachers = lab.getTeachers();
        List<User> repeat = new ArrayList<>();
        if (teachers != null) {
            repeat.addAll(RepeatUser(lab, teachers));
        }
        return repeat;
    }

    @Override
    public Lab getLabById(Integer id) {
        Lab lab = labMapper.selectById(id);
        LambdaQueryWrapper<LabMemberMap> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LabMemberMap::getLid, id);
        List<LabMemberMap> labMemberMaps = labMemberMapMapper.selectList(queryWrapper);
        List<User> users = new ArrayList<>();
        labMemberMaps.forEach(labMemberMap -> {
            User user = userService.getUserById(labMemberMap.getUid());
            users.add(user);
        });
        // 筛选出老师和学生 role为2的是老师，role为5的是学生
        List<User> teachers = new ArrayList<>();
        List<User> students = new ArrayList<>();
        users.forEach(user -> {
            if (user.getRole() == 2) {
                teachers.add(user);
            } else if (user.getRole() == 5) {
                students.add(user);
            }
        });
        lab.setTeachers(teachers);
        lab.setStudents(students);
        return lab;
    }

    @Override
    public void updateLab(Lab lab) {
        labMapper.updateById(lab);
    }

    @Override
    public void disableLab(Integer id) {
        Lab lab = labMapper.selectById(id);
        lab.setDelFlag(lab.getDelFlag() == 0 ? 1 : 0);
        labMapper.updateById(lab);
    }

    @Override
    public void addMember(Lab lab, User user) {
        // 首先判断这个学生或老师是否已经成为了实验室成员
        LambdaQueryWrapper<LabMemberMap> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LabMemberMap::getUid, user.getId());
        queryWrapper.eq(LabMemberMap::getLid, lab.getId());
        if (labMemberMapMapper.selectOne(queryWrapper) == null) {
            LabMemberMap labMemberMap = new LabMemberMap();
            labMemberMap.setUid(user.getId());
            labMemberMap.setLid(lab.getId());
            labMemberMapMapper.insert(labMemberMap);
        } else {
            throw new RuntimeException("该成员已经存在");
        }
    }

    @Override
    public void deleteMember(Lab lab, User user) {
        LambdaQueryWrapper<LabMemberMap> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LabMemberMap::getUid, user.getId());
        queryWrapper.eq(LabMemberMap::getLid, lab.getId());
        labMemberMapMapper.delete(queryWrapper);
    }

    private List<User> RepeatUser(Lab lab, List<User> users) {
        List<User> repeat = new ArrayList<>();
        for (User user : users) {
            // 首先判断这个学生或老师是否已经成为了实验室成员
            LambdaQueryWrapper<LabMemberMap> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(LabMemberMap::getUid, user.getId());
            queryWrapper.eq(LabMemberMap::getLid, lab.getId());
            if (labMemberMapMapper.selectOne(queryWrapper) == null) {
                LabMemberMap labMemberMap = new LabMemberMap();
                labMemberMap.setUid(user.getId());
                labMemberMap.setLid(lab.getId());
                labMemberMapMapper.insert(labMemberMap);
            } else {
                repeat.add(user);
            }
        }
        return repeat;
    }
}




