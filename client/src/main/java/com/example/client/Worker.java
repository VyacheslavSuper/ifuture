package com.example.client;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Log4j2
public class Worker implements CommandLineRunner {
    private final TaskExecutor taskExecutor;
    @Value("${url}")
    private String url;
    @Value("${threadCount}")
    private Integer threadCount;
    @Value("${readQuota}")
    private Integer readQuota;
    @Value("${writeQuota}")
    private Integer writeQuota;
    @Value("${readIdList}")
    private List<Long> readIdList;
    @Value("${writeIdList}")
    private List<Long> writeIdList;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public Worker(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void run(String... args) {
        for (int i = 0; i < threadCount; i++) {
            taskExecutor.execute(this::work);
        }
    }


    public void work() {
        log.info("Create worker");
        while (!Thread.interrupted()) {
            double readProbability = (double) readQuota / (double) (readQuota + writeQuota);

            if (ThreadLocalRandom.current().nextDouble() < readProbability) {
                getBalance(randomFromList(readIdList));
            } else {
                changeBalance(randomFromList(writeIdList), 1L);
            }
        }
    }

    private Long randomFromList(List<Long> list) {
        int index = ThreadLocalRandom.current().nextInt(list.size());
        return list.get(index);
    }

    private void getBalance(Long id) {
        try {
            ResponseEntity<Long> response = restTemplate.getForEntity(url, Long.class, id);
            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("Get balance error {}, id = {}", response, id);
            }
        } catch (RuntimeException e) {
            log.warn("Cant get balance, id = {}", id, e);
        }
    }

    private void changeBalance(Long id, Long amount) {
        try {
            ResponseEntity<Long> response = restTemplate.postForEntity(url, amount, Long.class, id);
            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("Change balance error {}, id = {}, amount = {}", response, id, amount);
            }
        } catch (RuntimeException e) {
            log.warn("Cant change balance, id = {}, amount = {}", id, amount, e);
        }
    }
}
