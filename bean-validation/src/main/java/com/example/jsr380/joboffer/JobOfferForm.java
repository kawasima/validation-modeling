package com.example.jsr380.joboffer;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.lang.annotation.*;
import java.time.LocalDate;
import java.util.List;

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
    @NotBlank(message = "タイトルは必須です")
    @Size(max = 100, message = "タイトルは100文字以内で入力してください")
    private String title;

    @NotBlank(message = "詳細は必須です")
    @Size(max = 1000, message = "詳細は1000文字以内で入力してください")
    private String description;

    @NotBlank(message = "依頼タイプは必須です")
    @Pattern(regexp = "PROJECT|TASK|CONTEST", message = "依頼タイプはPROJECT、TASK、CONTESTのいずれかである必要があります")
    private String jobOfferType;

    @Size(max = 500, message = "求めるスキルは500文字以内で入力してください")
    private String requiredSkills;

    @Size(max = 10, message = "添付ファイルは10個以内にしてください")
    private List<String> attachments;

    @Size(max = 5, message = "特記事項は5個以内にしてください")
    private List<String> specialNotes;

    @NotNull(message = "応募期限は必須です")
    @Future(message = "応募期限は未来の日付である必要があります")
    private LocalDate applicationDeadline;

    @NotNull(message = "支払い方式は必須です")
    @Pattern(regexp = "FIXED|HOURLY", message = "支払い方式はFIXEDまたはHOURLYである必要があります")
    private String paymentType;

    @NotNull(message = "納品希望日は必須です")
    @Future(message = "納品希望日は未来の日付である必要があります")
    private LocalDate deliveryDate;

    @Min(value = 1, message = "募集人数は1人以上である必要があります")
    @Max(value = 1024, message = "募集人数は1024人以下である必要があります")
    private Integer numberOfRecruits;

    @Min(value = 5, message = "作業単価は5円以上である必要があります")
    private Integer taskPrice;

    @Min(value = 1, message = "作業件数は1件以上である必要があります")
    private Integer taskCount;

    @Min(value = 0, message = "1人あたりの件数上限は0以上である必要があります")
    private Integer maxTasksPerPerson;

    @Pattern(regexp = "ECONOMY|BASIC|STANDARD|PREMIUM|CUSTOM", message = "契約金額はECONOMY、BASIC、STANDARD、PREMIUM、CUSTOMのいずれかである必要があります")
    private String contractAmount;

    @Min(value = 0, message = "カスタム契約金額は0以上である必要があります")
    private Integer customContractAmount;
}
