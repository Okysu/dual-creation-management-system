package zone.yby.lab.controller;

import database.domain.User;
import org.springframework.web.bind.annotation.*;
import zone.yby.lab.service.LoginService;
import zone.yby.lab.utils.ResponseResult;

import java.util.HashMap;

@RestController
public class AccountController {
    private final LoginService LoginService;

    public AccountController(LoginService LoginService) {
        this.LoginService = LoginService;
    }

    @PostMapping("/user/login")
    public ResponseResult<HashMap<String, Object>> login(@RequestBody User user) {
        return LoginService.login(user);
    }

    @DeleteMapping("/user/logout")
    public ResponseResult<String> logout(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return LoginService.logout(token);
    }
}
