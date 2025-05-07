package com.example.enrollment.domain;

import am.ik.yavi.arguments.LongValidator;

import java.util.concurrent.atomic.AtomicLong;

public class Identifier {
    private final AtomicLong value;

    private Identifier(long value) {
        this.value = new AtomicLong(value);
    }

    private Identifier() {
        this.value = new AtomicLong(-1);
    }

    public static Identifier of(long value) {
        return new Identifier(value);
    }
    public static Identifier undecided() {
        return new Identifier();
    }

    public long getValue() {
        long v = value.get();
        if (v < 0) {
            throw new IllegalStateException("Identifier is not set");
        }
        return value.get();
    }

    public void decide(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Identifier value must be non-negative");
        }
        if (this.value.get() >= 0) {
            throw new IllegalStateException("Identifier is already set");
        }
        this.value.set(value);
    }

    public boolean isDecided() {
        return value.get() >= 0;
    }

    @Override
    public String toString() {
        return String.valueOf(value.get());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Identifier that = (Identifier) obj;
        return value.get() == that.value.get();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(value.get());
    }
}
