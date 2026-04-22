package com.swimming.management.controller;

import com.swimming.management.entity.News;
import com.swimming.management.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/list")
    public ResponseEntity<List<News>> getAllNews() {
        return ResponseEntity.ok(newsService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable Integer id) {
        News news = newsService.findById(id);
        return news != null ? ResponseEntity.ok(news) : ResponseEntity.notFound().build();
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addNews(@RequestBody News news) {
        Map<String, Object> result = new HashMap<>();
        try {
            News savedNews = newsService.save(news);
            result.put("success", true);
            result.put("message", "新闻添加成功");
            result.put("data", savedNews);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<News>> searchNews(@RequestParam String keyword) {
        return ResponseEntity.ok(newsService.search(keyword));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNews(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            newsService.delete(id);
            result.put("success", true);
            result.put("message", "新闻删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateNews(
            @PathVariable Integer id,
            @RequestBody News news) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 确保ID一致
            news.setId(id);
            News updatedNews = newsService.save(news);
            result.put("success", true);
            result.put("message", "新闻更新成功");
            result.put("data", updatedNews);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}