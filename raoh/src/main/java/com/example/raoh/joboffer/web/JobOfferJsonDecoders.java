package com.example.raoh.joboffer.web;

import com.example.raoh.joboffer.data.*;
import net.unit8.raoh.Decoder;
import net.unit8.raoh.Result;
import tools.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.util.Map;

import static net.unit8.raoh.json.JsonDecoders.*;

public class JobOfferJsonDecoders {

    // --- Budget: discriminate on "type" ---
    static final Decoder<JsonNode, Budget> BUDGET_DECODER = discriminate("type", Map.of(
            "range", combine(
                    field("lowerBound", long_().min(1)),
                    field("upperBound", long_().min(1).max(99_999_999))
            ).map(RangeBudget::new),
            "limit", field("limit", long_().min(1).max(99_999_999))
                    .map(LimitBudget::new),
            "undecided", (Decoder<JsonNode, Budget>) (in, path) -> Result.ok(new UndecidedBudget())
    ));

    // --- Settlement: discriminate on "type" ---
    static final Decoder<JsonNode, Settlement> SETTLEMENT_DECODER = discriminate("type", Map.of(
            "fixed", combine(
                    field("numberOfWorkers", long_().min(2).max(1023)),
                    field("budget", BUDGET_DECODER)
            ).map(FixedSettlement::new),
            "per_hour", combine(
                    field("numberOfWorkers", long_().min(2).max(1023)),
                    field("hourlyRate", enumOf(HourlyRate.class)),
                    field("workHoursPerWeek", long_().min(2).max(159)),
                    field("offerDuration", enumOf(OfferDuration.class))
            ).map(PerHourSettlement::new)
    ));

    // --- LimitTaskUnitsPerWorker: discriminate on "type" ---
    static final Decoder<JsonNode, LimitTaskUnitsPerWorker> LIMIT_TASK_UNITS_DECODER = discriminate("type", Map.of(
            "unlimited", (Decoder<JsonNode, LimitTaskUnitsPerWorker>) (in, path) ->
                    Result.ok(new LimitTaskUnitsPerWorker.Unlimited()),
            "limited", field("limit", long_().min(2).max(2999))
                    .map(LimitTaskUnitsPerWorker.Limited::new)
    ));

    // --- ContractPrice: discriminate on "type" ---
    static final Decoder<JsonNode, ContractPrice> CONTRACT_PRICE_DECODER = discriminate("type", Map.of(
            "economy", (Decoder<JsonNode, ContractPrice>) (in, path) -> Result.ok(new ContractPrice.Economy()),
            "basic", (Decoder<JsonNode, ContractPrice>) (in, path) -> Result.ok(new ContractPrice.Basic()),
            "standard", (Decoder<JsonNode, ContractPrice>) (in, path) -> Result.ok(new ContractPrice.Standard()),
            "premium", (Decoder<JsonNode, ContractPrice>) (in, path) -> Result.ok(new ContractPrice.Premium()),
            "custom", field("value", long_().min(1))
                    .map(ContractPrice.Custom::new)
    ));

    // --- Common field decoders ---
    private static final Decoder<JsonNode, String> TITLE = field("title", string().minLength(1).maxLength(99));
    private static final Decoder<JsonNode, String> DESCRIPTION = field("description", string().minLength(1).maxLength(999));
    private static final Decoder<JsonNode, LocalDate> OFFER_EXPIRE_DATE = field("offerExpireDate", string().date());

    // --- JobOffer: discriminate on "type" ---
    public static final Decoder<JsonNode, JobOffer> JOB_OFFER_DECODER = discriminate("type", Map.of(
            "project", combine(TITLE, DESCRIPTION, OFFER_EXPIRE_DATE,
                    field("settlement", SETTLEMENT_DECODER)
            ).map(ProjectJobOffer::new),
            "task", combine(TITLE, DESCRIPTION, OFFER_EXPIRE_DATE,
                    field("ratePerTaskUnit", long_().min(6).max(999_999)),
                    field("numberOfTaskUnits", long_().min(2).max(999_999)),
                    field("limitTaskUnitsPerWorker", LIMIT_TASK_UNITS_DECODER)
            ).map(TaskJobOffer::new),
            "competition", combine(TITLE, DESCRIPTION, OFFER_EXPIRE_DATE,
                    field("contractPrice", CONTRACT_PRICE_DECODER)
            ).map(CompetitionJobOffer::new)
    ));
}
