package com.example.jsr380.gameconsole;

import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public class PlayingRecord {
    private final String accountId;
    private final String gameTitle;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public PlayingRecord(String accountId, String gameTitle, LocalDateTime startTime, LocalDateTime endTime) {
        this.accountId = accountId;
        this.gameTitle = gameTitle;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getPlayingMinutes() {
        return Duration.between(startTime, endTime).toMinutes();
    }

}
