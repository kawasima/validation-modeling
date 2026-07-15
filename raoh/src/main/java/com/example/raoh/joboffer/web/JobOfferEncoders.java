package com.example.raoh.joboffer.web;

import com.example.raoh.joboffer.data.*;
import net.unit8.raoh.encode.Encoder;
import net.unit8.raoh.encode.PropertyEncoder;
import org.jspecify.annotations.Nullable;

import java.util.Map;

import static net.unit8.raoh.encode.MapEncoders.*;
import static net.unit8.raoh.encode.ObjectEncoders.*;

/**
 * 案件レスポンスのエンコーダ。{@link JobOfferJsonDecoders} と対になるように、同じ判別キー "type" と
 * 同じタグ名（"project" / "task" / ...）で定義する。デコーダ側の {@code discriminate} がタグを読んで
 * 型を選ぶのに対し、こちらは値の実行時クラスから型を選んでタグを書き出す。
 */
public final class JobOfferEncoders {

    private JobOfferEncoders() {}

    // --- Budget: discriminate on "type" ---
    static final Encoder<Budget, Map<String, @Nullable Object>> BUDGET_ENCODER = discriminate("type",
            variant(RangeBudget.class, "range", object(
                    property("lowerBound", RangeBudget::lowerBound, long_()),
                    property("upperBound", RangeBudget::upperBound, long_()))),
            variant(LimitBudget.class, "limit", object(
                    property("limit", LimitBudget::limit, long_()))),
            variant(UndecidedBudget.class, "undecided", object()));

    // --- Settlement: discriminate on "type" ---
    static final Encoder<Settlement, Map<String, @Nullable Object>> SETTLEMENT_ENCODER = discriminate("type",
            variant(FixedSettlement.class, "fixed", object(
                    property("numberOfWorkers", FixedSettlement::numberOfWorkers, long_()),
                    property("budget",          FixedSettlement::budget,          nested(BUDGET_ENCODER)))),
            variant(PerHourSettlement.class, "per_hour", object(
                    property("numberOfWorkers",  PerHourSettlement::numberOfWorkers,  long_()),
                    property("hourlyRate",       PerHourSettlement::hourlyRate,       enumOf()),
                    property("workHoursPerWeek", PerHourSettlement::workHoursPerWeek, long_()),
                    property("offerDuration",    PerHourSettlement::offerDuration,    enumOf()))));

    // --- LimitTaskUnitsPerWorker: discriminate on "type" ---
    static final Encoder<LimitTaskUnitsPerWorker, Map<String, @Nullable Object>> LIMIT_TASK_UNITS_ENCODER =
            discriminate("type",
                    variant(LimitTaskUnitsPerWorker.Unlimited.class, "unlimited", object()),
                    variant(LimitTaskUnitsPerWorker.Limited.class, "limited", object(
                            property("limit", LimitTaskUnitsPerWorker.Limited::limit, long_()))));

    // --- ContractPrice: discriminate on "type" ---
    static final Encoder<ContractPrice, Map<String, @Nullable Object>> CONTRACT_PRICE_ENCODER = discriminate("type",
            variant(ContractPrice.Economy.class,  "economy",  object()),
            variant(ContractPrice.Basic.class,    "basic",    object()),
            variant(ContractPrice.Standard.class, "standard", object()),
            variant(ContractPrice.Premium.class,  "premium",  object()),
            variant(ContractPrice.Custom.class, "custom", object(
                    property("value", ContractPrice.Custom::value, long_()))));

    // --- Common property encoders ---
    // JobOffer が宣言するアクセサなので、どの派生型でもそのまま使える。
    private static <T extends JobOffer> PropertyEncoder<T> title() {
        return property("title", JobOffer::title, string());
    }

    private static <T extends JobOffer> PropertyEncoder<T> description() {
        return property("description", JobOffer::description, string());
    }

    private static <T extends JobOffer> PropertyEncoder<T> offerExpireDate() {
        return property("offerExpireDate", JobOffer::offerExpireDate, date());
    }

    // --- JobOffer: discriminate on "type" ---
    public static final Encoder<JobOffer, Map<String, @Nullable Object>> JOB_OFFER_ENCODER = discriminate("type",
            variant(ProjectJobOffer.class, "project", object(
                    title(), description(), offerExpireDate(),
                    property("settlement", ProjectJobOffer::settlement, nested(SETTLEMENT_ENCODER)))),
            variant(TaskJobOffer.class, "task", object(
                    title(), description(), offerExpireDate(),
                    property("ratePerTaskUnit",        TaskJobOffer::ratePerTaskUnit,        long_()),
                    property("numberOfTaskUnits",      TaskJobOffer::numberOfTaskUnits,      long_()),
                    property("limitTaskUnitsPerWorker", TaskJobOffer::limitTaskUnitsPerWorker,
                            nested(LIMIT_TASK_UNITS_ENCODER)))),
            variant(CompetitionJobOffer.class, "competition", object(
                    title(), description(), offerExpireDate(),
                    property("contractPrice", CompetitionJobOffer::contractPrice, nested(CONTRACT_PRICE_ENCODER)))));

    /**
     * data 案件登録済 = 案件ID AND 案件
     */
    public record JobOfferCreated(@Nullable String jobOfferId, JobOffer jobOffer) {}

    /**
     * 登録された案件のレスポンス。案件IDは gateway が採番するので案件そのものには含まれない。
     */
    public static final Encoder<JobOfferCreated, Map<String, @Nullable Object>> JOB_OFFER_CREATED = object(
            nullableProperty("jobOfferId", JobOfferCreated::jobOfferId, string()),
            property("jobOffer", JobOfferCreated::jobOffer, nested(JOB_OFFER_ENCODER)));
}
