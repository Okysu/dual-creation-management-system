package database.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import database.domain.Role;
import database.mapper.RoleMapper;
import database.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * @author okysu
 * @description 针对表【role】的数据库操作Service实现
 * @createDate 2023-10-01 23:34:01
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
        implements RoleService {

}




