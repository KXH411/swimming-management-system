package com.swimming.management.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.swimming.management.entity.*;
import com.swimming.management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIChatService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private NewsRepository newsRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    // DeepSeek API 配置（注册获取：https://platform.deepseek.com/）
    private final String API_KEY = "sk-4b061330d2cb4ddbb6867376eb7aaf5b";  // 替换为你的 API Key
    private final String API_URL = "https://api.deepseek.com/v1/chat/completions";

    // 对话历史
    private final Map<Long, List<Map<String, String>>> conversationHistory = new HashMap<>();

    public String chat(Long userId, String userMessage) {
        try {
            // 获取对话历史
            List<Map<String, String>> history = getHistory(userId);

            // 分析是否需要查询数据库
            String intent = analyzeIntent(userMessage);
            String contextData = "";

            if (!intent.equals("CHAT")) {
                contextData = executeQuery(intent, userId);
                System.out.println("📊 查询到数据: " + contextData);
            }

            // 构建消息
            List<Map<String, String>> messages = buildMessages(history, userMessage, contextData);

            // 调用AI
            String reply = callDeepSeekAPI(messages);

            // 保存历史
            saveHistory(userId, userMessage, reply);

            return reply;

        } catch (Exception e) {
            e.printStackTrace();
            return getLocalReply(userMessage);
        }
    }

    private String analyzeIntent(String message) {
        // 闲聊关键词
        String[] chatKeywords = {"你好", "您好", "嗨", "早上好", "下午好", "晚上好", "谢谢", "感谢",
                "辛苦了", "再见", "拜拜", "真棒", "厉害", "名字", "你是谁", "做什么", "天气", "心情", "无聊", "开心"};

        for (String keyword : chatKeywords) {
            if (message.contains(keyword)) {
                return "CHAT";
            }
        }

        if (message.contains("课程") || message.contains("游泳课")) {
            return "COURSE";
        }
        if (message.contains("教练")) {
            return "COACH";
        }
        if (message.contains("预约") && (message.contains("我的") || message.contains("查看"))) {
            return "MY_RESERVATION";
        }
        if (message.contains("新闻") || message.contains("公告")) {
            return "NEWS";
        }
        if (message.contains("营业") || message.contains("地址") || message.contains("电话")) {
            return "INFO";
        }
        return "CHAT";
    }

    private String executeQuery(String intent, Long userId) {
        switch (intent) {
            case "COURSE":
                return queryCourses();
            case "COACH":
                return queryCoaches();
            case "MY_RESERVATION":
                return queryMyReservations(userId);
            case "NEWS":
                return queryNews();
            case "INFO":
                return getFacilityInfo();
            default:
                return "";
        }
    }

    private String queryCourses() {
        List<Course> courses = courseRepository.findAll();
        if (courses.isEmpty()) return "暂无课程";

        JSONArray result = new JSONArray();
        for (Course c : courses) {
            JSONObject obj = new JSONObject();
            obj.put("name", c.getXiangmumingcheng());
            obj.put("type", c.getLeixing());
            obj.put("coach", c.getJiaolianmingcheng());
            obj.put("price", c.getJiage());
            obj.put("duration", c.getShichang());
            result.add(obj);
        }
        return result.toJSONString();
    }

    private String queryCoaches() {
        List<Coach> coaches = coachRepository.findAll();
        if (coaches.isEmpty()) return "暂无教练";

        JSONArray result = new JSONArray();
        for (Coach c : coaches) {
            JSONObject obj = new JSONObject();
            obj.put("name", c.getJiaolianxingming());
            obj.put("gender", c.getXingbie());
            obj.put("specialty", c.getZhuanye());
            obj.put("intro", c.getJianjie());
            result.add(obj);
        }
        return result.toJSONString();
    }

    private String queryMyReservations(Long userId) {
        List<Reservation> reservations = reservationRepository.findByCrossuserid(userId.intValue());
        if (reservations.isEmpty()) return "暂无预约";

        JSONArray result = new JSONArray();
        for (Reservation r : reservations) {
            JSONObject obj = new JSONObject();
            obj.put("course", r.getXiangmumingcheng());
            obj.put("coach", r.getJiaolianmingcheng());
            obj.put("time", r.getYuyueshijian() != null ? r.getYuyueshijian().toString() : "");
            obj.put("status", r.getZhuangtai());
            result.add(obj);
        }
        return result.toJSONString();
    }

    private String queryNews() {
        List<News> newsList = newsRepository.findAll();
        if (newsList.isEmpty()) return "暂无新闻";

        JSONArray result = new JSONArray();
        for (News n : newsList.stream().limit(3).collect(Collectors.toList())) {
            JSONObject obj = new JSONObject();
            obj.put("title", n.getTitle());
            obj.put("intro", n.getIntroduction());
            result.add(obj);
        }
        return result.toJSONString();
    }

    private String getFacilityInfo() {
        JSONObject info = new JSONObject();
        info.put("hours", "周一至周五 09:00-22:00，周末及节假日 08:00-23:00");
        info.put("address", "XX市XX区XX路123号游泳馆");
        info.put("phone", "400-123-4567");
        info.put("parking", "有地下停车场，凭游泳券免费2小时");
        return info.toJSONString();
    }

    private List<Map<String, String>> getHistory(Long userId) {
        return conversationHistory.getOrDefault(userId, new ArrayList<>());
    }

    private void saveHistory(Long userId, String userMsg, String aiMsg) {
        List<Map<String, String>> history = getHistory(userId);

        Map<String, String> userMap = new HashMap<>();
        userMap.put("role", "user");
        userMap.put("content", userMsg);
        history.add(userMap);

        Map<String, String> aiMap = new HashMap<>();
        aiMap.put("role", "assistant");
        aiMap.put("content", aiMsg);
        history.add(aiMap);

        // 只保留最近10条
        while (history.size() > 20) {
            history.remove(0);
        }

        conversationHistory.put(userId, history);
    }

    private List<Map<String, String>> buildMessages(List<Map<String, String>> history,
                                                    String userMessage,
                                                    String contextData) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 系统提示
        Map<String, String> systemMsg = new HashMap<>();
        String systemContent = "你是游泳馆的AI助手'小泳'，热情友好，能自然对话。";
        if (!contextData.isEmpty() && !contextData.equals("暂无课程") && !contextData.equals("暂无教练")) {
            systemContent += "\n\n当前查询到的数据：\n" + contextData;
        }
        systemMsg.put("role", "system");
        systemMsg.put("content", systemContent);
        messages.add(systemMsg);

        // 添加历史对话
        int start = Math.max(0, history.size() - 8);
        for (int i = start; i < history.size(); i++) {
            messages.add(history.get(i));
        }

        // 添加当前消息
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        return messages;
    }

    private String callDeepSeekAPI(List<Map<String, String>> messages) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "deepseek-chat");

            JSONArray msgArray = new JSONArray();
            for (Map<String, String> msg : messages) {
                JSONObject obj = new JSONObject();
                obj.put("role", msg.get("role"));
                obj.put("content", msg.get("content"));
                msgArray.add(obj);
            }
            requestBody.put("messages", msgArray);
            requestBody.put("temperature", 0.8);
            requestBody.put("max_tokens", 500);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + API_KEY);

            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(
                    requestBody.toJSONString(), headers
            );

            org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity(
                    API_URL, entity, String.class
            );

            JSONObject responseBody = JSON.parseObject(response.getBody());
            return responseBody.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getLocalReply(String message) {
        if (message.contains("课程")) {
            return "我们这里有成人蛙泳、儿童自由泳、健身游泳等课程，价格在150-400元之间。你想了解哪个课程呢？🏊";
        }
        if (message.contains("教练")) {
            return "我们的教练都很专业！有国家级教练张教练，还有儿童游泳专家李教练。你想找哪位教练？👨‍🏫";
        }
        if (message.contains("你好") || message.contains("您好")) {
            return "你好呀！我是小泳，很高兴认识你！今天想了解什么？😊";
        }
        if (message.contains("谢谢")) {
            return "不客气！能帮到你我超开心的！还有什么需要帮忙的吗？❤️";
        }
        if (message.contains("再见")) {
            return "再见啦～祝你生活愉快，有空常来游泳哦！👋";
        }
        return "你好！我是游泳馆AI助手小泳，可以帮你查询课程、教练、预约等信息。请问有什么可以帮你的？🤖";
    }
}