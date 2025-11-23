package chaye.com.ordermanage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {
    
    @GetMapping("/")
    public Map<String, Object> hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "订单管理系统已启动");
        result.put("time", LocalDateTime.now());
        result.put("status", "running");
        result.put("version", "1.0.0");
        return result;
    }
}