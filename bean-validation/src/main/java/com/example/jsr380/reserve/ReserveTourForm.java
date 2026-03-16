package com.example.jsr380.reserve;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReserveTourForm {
    @NotNull(message = "大人の人数は必須です")
    @Min(value = 0, message = "大人の人数は0以上である必要があります")
    @Max(value = 5, message = "大人の人数は5以下である必要があります")
    private Integer adultCount;

    @NotNull(message = "子供の人数は必須です")
    @Min(value = 0, message = "子供の人数は0以上である必要があります")
    @Max(value = 5, message = "子供の人数は5以下である必要があります")
    private Integer childCount;

    @Size(max = 80, message = "備考は80文字以内で入力してください")
    private String remarks;
}
