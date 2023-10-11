package zone.yby.lab.service;

import zone.yby.lab.utils.ResponseResult;
import database.domain.User;

import java.util.HashMap;

public interface LoginService {
    ResponseResult<HashMap<String, Object>> login(User user);

    ResponseResult<String> logout(String token);
}
