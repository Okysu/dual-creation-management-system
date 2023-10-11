package zone.yby.lab.service.impl;

import zone.yby.lab.domain.LoginUser;
import zone.yby.lab.service.LoginService;
import zone.yby.lab.utils.HashCode;
import zone.yby.lab.utils.JwtUtil;
import zone.yby.lab.utils.RedisCache;
import zone.yby.lab.utils.ResponseResult;
import database.domain.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {
    private final AuthenticationManager authenticationManager;
    private final RedisCache redisCache;

    public LoginServiceImpl(AuthenticationManager authenticationManager, RedisCache redisCache) {
        this.authenticationManager = authenticationManager;
        this.redisCache = redisCache;
    }

    @Override
    public ResponseResult<HashMap<String, Object>> login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUid(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            return new ResponseResult<>(200, "用户或密码错误，或用户不存在。", null);
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String token = JwtUtil.createJWT(loginUser.getUsername());
        // 将认证后的用户数据的密码字段置空
        loginUser.getUser().setPassword(null);
        // 使用murmurhash将token签名缩短
        String sign = String.valueOf(HashCode.murmurhash(token + "cslg-lab-manager"));
        // 将token存入redis
        redisCache.setCacheObject("token:" + sign, loginUser.getUsername(), 7, TimeUnit.DAYS);
        // 将用户信息存入redis
        redisCache.setCacheObject("login:" + loginUser.getUsername(), loginUser, 7, TimeUnit.DAYS);
        HashMap<String, Object> map = new HashMap<>();
        map.put("token", token);
        User safeUser = loginUser.getUser();
        safeUser.setPassword(null);
        map.put("user", safeUser);
        return new ResponseResult<>(200, "登录成功", map);
    }

    @Override
    public ResponseResult<String> logout(String token) {
        String sign = String.valueOf(HashCode.murmurhash(token + "cslg-lab-manager"));
        redisCache.deleteObject("token:" + sign);
        return new ResponseResult<>(200, "退出成功");
    }
}
