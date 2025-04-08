package com.example.web.gameconsole;

import lombok.Getter;

import java.util.Locale;

@Getter
public class Account {
    private final String username;
    private final String password;
    private final String email;
    private final Locale locale;

    public Account(String username, String password, String email, Locale locale) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.locale = locale;
    }
}
