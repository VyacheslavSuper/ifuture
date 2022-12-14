package com.example.server.controller;

import com.example.server.exception.ProjectException;
import com.example.server.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;


@RestController
@RequestMapping("/api/")
public class BalanceController {
    private final BalanceService balanceService;

    @Autowired
    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping(value = "/user/{id}/balance", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long getBalance(@Min(0) @PathVariable Long id) throws ProjectException {
        return balanceService.getBalance(id).orElseThrow(() -> new ProjectException("Not found"));
    }

    @PostMapping(value = "/user/{id}/balance", produces = MediaType.APPLICATION_JSON_VALUE)
    public void changeBalance(@Min(0) @PathVariable Long id,
                              @Min(0) @RequestBody Long amount) throws ProjectException {
        balanceService.changeBalance(id, amount);
    }
}
