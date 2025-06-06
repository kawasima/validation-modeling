package com.example.yavi.enrollment.adapter;

import com.example.yavi.enrollment.domain.Identifier;

public class RecordNotFoundException extends RuntimeException {
    private final Identifier id;

    public RecordNotFoundException(Identifier id) {
        super("Record not found for ID: " + id);
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }
}
