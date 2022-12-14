package com.example.server.service;

import com.example.server.dao.UserDao;
import com.example.server.exception.ProjectException;
import com.example.server.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.LinkedBlockingDeque;

@SpringBootTest
@ActiveProfiles("test")
@EnableAsync
class BalanceServiceTest {
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private UserDao userDao;

    @Test
    void getBalance() throws ProjectException {
        User user = new User();
        userDao.save(user);

        Assertions.assertEquals(0, balanceService.getBalance(user.getId()).get());
    }

    @Test
    void changeBalanceOneThread() throws ProjectException {
        User user = new User();
        userDao.save(user);

        long sum = 0;
        for (int i = 0; i < 10; i++) {
            balanceService.changeBalance(user.getId(), 10L);
            sum += 10L;
            Assertions.assertEquals(sum, balanceService.getBalance(user.getId()).get());
        }
    }

    @Test
    void changeBalanceMultiThread() throws ProjectException, InterruptedException {
        User user = new User();
        userDao.save(user);
        Long id = user.getId();
        int countThreads = 10;


        LinkedBlockingDeque<Boolean> blockingQueue = new LinkedBlockingDeque<>();

        for (int i = 0; i < countThreads; i++) {
            Thread thread = new Thread(() -> {
                changeBalance(id, 10L);
                blockingQueue.addLast(true);
            });
            thread.start();
        }

        for (int i = 0; i < countThreads; i++) {
            blockingQueue.takeFirst();
        }

        Assertions.assertEquals(100, balanceService.getBalance(id).get());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void changeBalance(Long id, Long amount) {
        try {
            balanceService.changeBalance(id, amount);
        } catch (ProjectException e) {
            System.out.println(Thread.currentThread().getName());
        }
    }



}