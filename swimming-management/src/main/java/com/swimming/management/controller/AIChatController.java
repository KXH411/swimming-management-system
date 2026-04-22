package com.swimming.management.controller;

import com.swimming.management.service.AIChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIChatController {

    @Autowired
    private AIChatService aiChatService;

    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "AI控制器工作正常");
        return result;
    }

    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String userIdStr = request.get("userId");

        Long userId = 1L;
        if (userIdStr != null) {
            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                // 忽略
            }
        }

        String reply = aiChatService.chat(userId, message);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("reply", reply);
        return result;
    }
}