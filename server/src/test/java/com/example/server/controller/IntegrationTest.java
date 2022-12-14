package com.example.server.controller;

import com.example.server.dao.UserDao;
import com.example.server.exception.ProjectException;
import com.example.server.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.LinkedBlockingDeque;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class IntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserDao userDao;

    @Test
    void testOneThread() throws ProjectException {
        User user = new User();
        userDao.save(user);

        Long id = user.getId();
        ResponseEntity<Long> response = restTemplate.getForEntity("/api/user/{id}/balance", Long.class, id);
        Assertions.assertEquals(0, response.getBody());

        for (int i = 0; i < 10; i++) {
            Long amount = 10L;
            restTemplate.postForEntity("/api/user/{id}/balance", amount, Long.class, id);
        }
        response = restTemplate.getForEntity("/api/user/{id}/balance", Long.class, id);
        Assertions.assertEquals(100, response.getBody());
    }

    @Test
    void testMultiThread() throws InterruptedException, ProjectException {
        User user = new User();
        userDao.save(user);

        Long id = user.getId();
        ResponseEntity<Long> response = restTemplate.getForEntity("/api/user/{id}/balance", Long.class, id);
        Assertions.assertEquals(0, response.getBody());
        LinkedBlockingDeque<Boolean> blockingQueue = new LinkedBlockingDeque<>();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                Long amount = 10L;
                restTemplate.postForEntity("/api/user/{id}/balance", amount, Object.class, id);
                blockingQueue.addLast(true);
            });
            thread.start();
        }
        for (int i = 0; i < 10; i++) {
            blockingQueue.takeFirst();
        }

        response = restTemplate.getForEntity("/api/user/{id}/balance", Long.class, id);
        Assertions.assertEquals(100, response.getBody());
    }

}
