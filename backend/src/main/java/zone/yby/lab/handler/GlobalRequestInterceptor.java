package zone.yby.lab.handler;

import database.domain.Log;
import database.mapper.LogMapper;
import zone.yby.lab.domain.LoginUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

@Component
@Service
public class GlobalRequestInterceptor extends HandlerInterceptorAdapter {

    private final LogMapper logMapper;


    public GlobalRequestInterceptor(LogMapper logMapper) {
        this.logMapper = logMapper;
    }


    /**
     * 获取ip
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    try {
                        ipAddress = InetAddress.getLocalHost().getHostAddress();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
            }
            // 通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null) {
                if (ipAddress.contains(",")) {
                    return ipAddress.split(",")[0];
                } else {
                    return ipAddress;
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 请求之前被调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        String uri = request.getRequestURI();
        // 如果是请求的日志查询，则不记录日志
        if (uri.contains("log") && !uri.contains("login")) {
            return super.preHandle(request, response, handler);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username; // 请求用户的uid
        if (authentication.getPrincipal() != null && authentication.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            username = loginUser.getUsername();
        } else {
            username = "-1";
        }
        // 构建日志
        Log log = new Log();
        // 获取请求ip
        String ip = getIpAddr(request);
        // 获取请求方法
        String method = request.getMethod();
        // 设置请求状态
        log.setStatus("发起请求");
        // 设置请求用户
        log.setUid(Integer.parseInt(username));
        // 设置请求ip
        log.setIp(ip);
        // 设置请求url
        log.setUrl(uri);
        // 设置请求方法
        log.setMethod(method);
        // 设置请求时间
        Date date = new Date(System.currentTimeMillis());
        log.setTime(date);
        // 添加数据库
        logMapper.insert(log);
        return super.preHandle(request, response, handler);
    }

    /**
     * 请求完成之后，如果需要渲染视图，则此操作在渲染视图之前
     */
    @Override
    public void postHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, ModelAndView modelAndView) throws Exception {
        String uri = request.getRequestURI();
        // 如果是请求的日志查询，则不记录日志，
        if (uri.contains("log") && !uri.contains("login")) {
            return;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username; // 请求用户的uid
        if (authentication.getPrincipal() != null && authentication.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            username = loginUser.getUsername();
        } else {
            username = "-1";
        }
        // 构建日志
        Log log = new Log();
        // 获取请求ip
        String ip = getIpAddr(request);
        // 获取请求方法
        String method = request.getMethod();
        // 设置请求状态
        log.setStatus("请求完成:" + response.getStatus());
        // 设置请求用户
        log.setUid(Integer.parseInt(username));
        // 设置请求ip
        log.setIp(ip);
        // 设置请求url
        log.setUrl(uri);
        // 设置请求方法
        log.setMethod(method);
        // 设置请求时间
        Date date = new Date(System.currentTimeMillis());
        log.setTime(date);
        // 添加数据库
        logMapper.insert(log);
        super.postHandle(request, response, handler, modelAndView);
    }
}
