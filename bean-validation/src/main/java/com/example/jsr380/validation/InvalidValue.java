package com.example.jsr380.validation;

import java.lang.reflect.Proxy;

public class InvalidValue {
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<? extends T> clazz) {
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("Class must be an interface");
        }
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                (proxy, method, args) -> {
                    if (method.getName().equals("toString")) {
                        return "INVALID";
                    }
                    throw new UnsupportedOperationException("This is an invalid value");
                }
        );
    }
}
