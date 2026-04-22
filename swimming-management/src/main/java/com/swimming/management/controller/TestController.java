package com.swimming.management.controller;



import org.springframework.web.bind.annotation.*;
        import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Hello! 后端运行正常");
        return result;
    }
}