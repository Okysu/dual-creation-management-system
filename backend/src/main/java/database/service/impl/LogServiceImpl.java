package database.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import database.domain.Log;
import database.service.LogService;
import database.mapper.LogMapper;
import org.springframework.stereotype.Service;

/**
 * @author okysu
 * @description 针对表【log】的数据库操作Service实现
 * @createDate 2023-10-01 23:34:05
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log>
        implements LogService {

}




