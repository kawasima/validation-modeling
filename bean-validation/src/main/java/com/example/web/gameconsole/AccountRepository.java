package com.example.web.gameconsole;

public interface AccountRepository {
    Account findByUsername(String username);
}
