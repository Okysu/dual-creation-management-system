package zone.yby.lab;

import org.springframework.web.bind.annotation.GetMapping;
import zone.yby.lab.utils.ResponseResult;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

@RestController
@SpringBootApplication
@MapperScan("mapper")
public class DualCreationManagementJavaApplication {
    private static final Date runTime = new Date();

    private static final String version = "1.0.0";


    @GetMapping("/public/system")
    public static ResponseResult<HashMap<String, Serializable>> getRunTime() {
        HashMap<String, java.io.Serializable> hashMap = new HashMap<String, java.io.Serializable>();
        hashMap.put("version", version);
        hashMap.put("time", runTime.getTime());
        long l = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        hashMap.put("memory", l);
        return new ResponseResult<>(200, "获取系统信息成功", hashMap);
    }

    public static void main(String[] args) {
        SpringApplication.run(DualCreationManagementJavaApplication.class, args);
    }

}
