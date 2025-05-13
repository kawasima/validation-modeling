package com.example.jsr380.gameconsole;

public interface AccountRepository {
    Account findByUsername(String username);
}
