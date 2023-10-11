package database.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import database.domain.Menu;
import database.mapper.MenuMapper;
import database.service.MenuService;
import org.springframework.stereotype.Service;

/**
 * @author okysu
 * @description 针对表【menu】的数据库操作Service实现
 * @createDate 2023-10-02 00:37:50
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu>
        implements MenuService {

}




