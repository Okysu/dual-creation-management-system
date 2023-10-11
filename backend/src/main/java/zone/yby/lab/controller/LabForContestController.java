package zone.yby.lab.controller;

import database.domain.Lab;
import database.domain.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zone.yby.lab.service.LabService;
import zone.yby.lab.utils.ResponseResult;

import java.util.List;

@RestController
public class LabForContestController {
    private final LabService labService;


    public LabForContestController(LabService labService) {
        this.labService = labService;
    }

    /**
     * 获取实验室列表
     */
    @GetMapping("/contest/lab")
    @PreAuthorize("hasAuthority('CONTEST')")
    public ResponseResult<List<Lab>> getLabList() {
        return new ResponseResult<>(200, "获取成功", labService.getLabList(false));
    }

    /**
     * 获取实验室信息
     *
     * @param id 实验室id
     */
    @GetMapping("/contest/lab/{id}")
    @PreAuthorize("hasAuthority('CONTEST')")
    public ResponseResult<Lab> getLabById(@PathVariable Integer id) {
        return new ResponseResult<>(200, "获取成功", labService.getLabById(id));
    }

    /**
     * 添加实验室
     *
     * @param lab 实验室信息
     */
    @PostMapping("/contest/lab")
    @PreAuthorize("hasAuthority('CONTEST')")
    public ResponseResult<List<User>> addLab(@RequestBody Lab lab) {
        List<User> users = labService.addLab(lab);
        return new ResponseResult<>(200, "添加成功", users);
    }

    /**
     * 更新实验室信息
     *
     * @param lab 实验室信息
     */
    @PutMapping("/contest/lab")
    @PreAuthorize("hasAuthority('CONTEST')")
    public ResponseResult<?> updateLab(@RequestBody Lab lab) {
        labService.updateLab(lab);
        return new ResponseResult<>(200, "更新成功");
    }

    /**
     * 禁用/启用实验室
     *
     * @param lab 实验室信息
     */
    @DeleteMapping("/contest/lab")
    @PreAuthorize("hasAuthority('CONTEST')")
    public ResponseResult<?> disableLab(@RequestBody Lab lab) {
        labService.disableLab(lab.getId());
        return new ResponseResult<>(200, "操作成功");
    }

    /**
     * 添加成员
     *
     * @param lab 实验室信息
     */
    @PostMapping("/contest/lab/member")
    @PreAuthorize("hasAuthority('CONTEST')")
    public ResponseResult<?> addMember(@RequestBody Lab lab) {
        labService.addMember(lab, lab.getUser());
        return new ResponseResult<>(200, "添加成功");
    }

    /**
     * 删除成员
     *
     * @param lab 实验室信息
     */
    @DeleteMapping("/contest/lab/member")
    @PreAuthorize("hasAuthority('CONTEST')")
    public ResponseResult<?> deleteMember(@RequestBody Lab lab) {
        labService.deleteMember(lab, lab.getUser());
        return new ResponseResult<>(200, "删除成功");
    }

}
