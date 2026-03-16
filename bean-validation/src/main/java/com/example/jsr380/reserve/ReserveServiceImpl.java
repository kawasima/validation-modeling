package com.example.jsr380.reserve;

import org.springframework.stereotype.Service;

@Service
public class ReserveServiceImpl implements ReserveService {

    @Override
    public ReserveTourOutput reserve(ReserveTourInput input) {
        // ビジネスルール: 残席チェック
        int availableCapacity = findAvailableCapacity(input.getTourCode());
        int totalParticipants = input.getAdultCount() + input.getChildCount();
        if (availableCapacity < totalParticipants) {
            throw new BusinessException("ツアーに空きがありません");
        }

        // 予約処理（本来はDBに保存）
        String reservationId = generateReservationId();
        return new ReserveTourOutput(reservationId, input.getTourCode(),
                input.getAdultCount(), input.getChildCount());
    }

    private int findAvailableCapacity(String tourCode) {
        // 本来はDBから残席数を取得
        return 20;
    }

    private String generateReservationId() {
        // 本来はシーケンスから採番
        return "R" + System.currentTimeMillis();
    }
}
