package database.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import database.domain.User;
import database.mapper.UserMapper;
import database.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author okysu
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2023-10-01 23:33:54
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

}




