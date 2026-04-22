package com.swimming.management.service;

import com.swimming.management.entity.News;
import com.swimming.management.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    public List<News> findAll() {
        return newsRepository.findAll();
    }

    public News findById(Integer id) {
        News news = newsRepository.findById(id).orElse(null);
        if (news != null) {
            // 增加浏览数
            news.setLiulancount(news.getLiulancount() + 1);
            newsRepository.save(news);
        }
        return news;
    }

    public News save(News news) {
        if (news.getAddtime() == null) {
            news.setAddtime(LocalDateTime.now());
        }
        if (news.getLiulancount() == null) {
            news.setLiulancount(0);
        }
        return newsRepository.save(news);
    }

    public void delete(Integer id) {
        newsRepository.deleteById(id);
    }

    public List<News> search(String keyword) {
        return newsRepository.findByTitleContaining(keyword);
    }
}