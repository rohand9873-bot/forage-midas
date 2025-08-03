package com.jpmc.midascore.controller;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.foundation.Users;
import com.jpmc.midascore.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/balance")
public class BalanceController {

    @Autowired
    private UsersRepository usersRepository;

    @GetMapping
    public Balance getBalance(@RequestParam Long userId) {
        Users user = usersRepository.findById(userId).orElse(null);
        float balanceAmount = (user != null) ? user.getBalance() : 0f;
        return new Balance(userId, balanceAmount);
    }
}
