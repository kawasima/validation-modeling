package com.example.jsr380.joboffer;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.lang.annotation.*;
import java.time.LocalDate;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = JobOfferFormValidator.class)
@Documented
@interface ValidJobOfferForm {
    String message() default "依頼タイプに応じた必須項目が入力されていません";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Data
@ValidJobOfferForm
public class JobOfferForm {
    // 共通フィールド
    @NotBlank(message = "タイトルは必須です")
    @Size(max = 100, message = "タイトルは100文字以内で入力してください")
    private String title;

    @NotBlank(message = "詳細は必須です")
    @Size(max = 1000, message = "詳細は1000文字以内で入力してください")
    private String description;

    @NotBlank(message = "依頼タイプは必須です")
    @Pattern(regexp = "PROJECT|TASK|COMPETITION", message = "依頼タイプはPROJECT、TASK、COMPETITIONのいずれかである必要があります")
    private String jobOfferType;

    @NotNull(message = "募集期限は必須です")
    @Future(message = "募集期限は未来の日付である必要があります")
    private LocalDate offerExpireDate;

    // PROJECT: settlement
    @Pattern(regexp = "FIXED|PER_HOUR", message = "精算方式はFIXEDまたはPER_HOURである必要があります")
    private String settlementMethod;

    @Min(value = 2, message = "募集人数は2人以上である必要があります")
    @Max(value = 1023, message = "募集人数は1023人以下である必要があります")
    private Long numberOfWorkers;

    // PROJECT/FIXED: budget
    @Pattern(regexp = "RANGE|LIMIT|UNDECIDED", message = "予算タイプはRANGE、LIMIT、UNDECIDEDのいずれかである必要があります")
    private String budgetType;

    @Min(value = 1, message = "予算下限は1以上である必要があります")
    private Long budgetLowerBound;

    @Min(value = 1, message = "予算上限は1以上である必要があります")
    @Max(value = 99_999_999, message = "予算上限は99999999以下である必要があります")
    private Long budgetUpperBound;

    @Min(value = 1, message = "予算上限額は1以上である必要があります")
    @Max(value = 99_999_999, message = "予算上限額は99999999以下である必要があります")
    private Long budgetLimit;

    // PROJECT/PER_HOUR
    @Pattern(regexp = "FROM_700_TO_1000|FROM_1000_TO_1500|FROM_1500_TO_2000|FROM_2000_TO_3000|FROM_3000_TO_4000|FROM_4000_TO_5000|OVER_5000",
            message = "時間単価の範囲が不正です")
    private String hourlyRate;

    @Min(value = 2, message = "週あたり稼働時間は2以上である必要があります")
    @Max(value = 159, message = "週あたり稼働時間は159以下である必要があります")
    private Long workHoursPerWeek;

    @Pattern(regexp = "UNDER_ONE_WEEK|ONE_WEEK_TO_ONE_MONTH|ONE_MONTH_TO_THREE_MONTHS|THREE_MONTHS_TO_SIX_MONTHS|OVER_SIX_MONTHS",
            message = "募集期間が不正です")
    private String offerDuration;

    // TASK
    @Min(value = 6, message = "作業単価は6円以上である必要があります")
    @Max(value = 999_999, message = "作業単価は999999円以下である必要があります")
    private Long ratePerTaskUnit;

    @Min(value = 2, message = "作業件数は2件以上である必要があります")
    @Max(value = 999_999, message = "作業件数は999999件以下である必要があります")
    private Long numberOfTaskUnits;

    @Pattern(regexp = "UNLIMITED|LIMITED", message = "1人あたり件数制限はUNLIMITEDまたはLIMITEDである必要があります")
    private String limitTaskUnitsType;

    @Min(value = 2, message = "1人あたり件数上限は2以上である必要があります")
    @Max(value = 2999, message = "1人あたり件数上限は2999以下である必要があります")
    private Long limitTaskUnitsValue;

    // COMPETITION
    @Pattern(regexp = "ECONOMY|BASIC|STANDARD|PREMIUM|CUSTOM", message = "契約金額タイプが不正です")
    private String contractPriceType;

    @Min(value = 1, message = "カスタム契約金額は1以上である必要があります")
    private Long contractPriceValue;
}
