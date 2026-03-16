package com.example.yavi.joboffer.adapter;

import com.example.yavi.joboffer.domain.*;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.field;

@Component
public class JobOfferMapper {
    private static final List<Field<?>> COMMON_FIELDS = List.of(
            field("title"),
            field("description"),
            field("offer_expire_date"),
            field("offer_type")
    );

    private final DSLContext dslContext;

    public JobOfferMapper(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public JobOffer toDomain(Record rec) {
        assert rec != null;
        String offerType = rec.get("offer_type", String.class);

        Map<String, Object> map = new HashMap<>();
        map.put("title", rec.get("title", String.class));
        map.put("description", rec.get("description", String.class));
        map.put("offerExpireDate", rec.get("offer_expire_date", LocalDate.class).toString());
        map.put("type", offerType.toLowerCase());

        switch (offerType) {
            case "PROJECT" -> buildProjectMap(rec, map);
            case "TASK" -> buildTaskMap(rec, map);
            case "COMPETITION" -> buildCompetitionMap(rec, map);
        }

        return JobOffer.mapValidator.validated(map);
    }

    private void buildProjectMap(Record rec, Map<String, Object> map) {
        String settlementMethod = rec.get("settlement_method", String.class);
        Map<String, Object> settlement = new HashMap<>();
        settlement.put("type", settlementMethod.toLowerCase());
        settlement.put("numberOfWorkers", rec.get("number_of_workers", Long.class));

        if ("FIXED".equals(settlementMethod)) {
            String budgetType = rec.get("budget_type", String.class);
            Map<String, Object> budget = new HashMap<>();
            budget.put("type", budgetType.toLowerCase());
            switch (budgetType) {
                case "RANGE" -> {
                    budget.put("lowerBound", rec.get("budget_lower_bound", Long.class));
                    budget.put("upperBound", rec.get("budget_upper_bound", Long.class));
                }
                case "LIMIT" -> budget.put("limit", rec.get("budget_limit", Long.class));
            }
            settlement.put("budget", budget);
        } else if ("PER_HOUR".equals(settlementMethod)) {
            settlement.put("hourlyRate", rec.get("hourly_rate", String.class));
            settlement.put("workHoursPerWeek", rec.get("work_hours_per_week", Long.class));
            settlement.put("offerDuration", rec.get("offer_duration", String.class));
        }

        map.put("settlement", settlement);
    }

    private void buildTaskMap(Record rec, Map<String, Object> map) {
        map.put("ratePerTaskUnit", rec.get("rate_per_task_unit", Long.class));
        map.put("numberOfTaskUnits", rec.get("number_of_task_units", Long.class));

        String limitType = rec.get("limit_task_units_type", String.class);
        Map<String, Object> limit = new HashMap<>();
        limit.put("type", limitType.toLowerCase());
        if ("LIMITED".equals(limitType)) {
            limit.put("limit", rec.get("limit_task_units_value", Long.class));
        }
        map.put("limitTaskUnitsPerWorker", limit);
    }

    private void buildCompetitionMap(Record rec, Map<String, Object> map) {
        String priceType = rec.get("contract_price_type", String.class);
        Map<String, Object> contractPrice = new HashMap<>();
        contractPrice.put("type", priceType.toLowerCase());
        if ("CUSTOM".equals(priceType)) {
            contractPrice.put("value", rec.get("contract_price_value", Long.class));
        }
        map.put("contractPrice", contractPrice);
    }

    public Record toRecord(JobOffer jobOffer) {
        assert jobOffer != null;
        List<Field<?>> fields = new ArrayList<>(COMMON_FIELDS);
        addTypeSpecificFields(jobOffer, fields);

        Record rec = dslContext.newRecord(fields);
        rec.set(field("title"), jobOffer.getTitle());
        rec.set(field("description"), jobOffer.getDescription());
        rec.set(field("offer_expire_date"), jobOffer.getOfferExpireDate());
        rec.set(field("offer_type"), jobOffer.getJobOfferType().name());
        setTypeSpecificValues(jobOffer, rec);
        return rec;
    }

    private void addTypeSpecificFields(JobOffer jobOffer, List<Field<?>> fields) {
        switch (jobOffer) {
            case ProjectJobOffer p -> {
                fields.add(field("settlement_method"));
                fields.add(field("number_of_workers"));
                Settlement settlement = p.getSettlement();
                if (settlement instanceof FixedSettlement) {
                    fields.add(field("budget_type"));
                    Budget budget = ((FixedSettlement) settlement).getBudget();
                    if (budget instanceof RangeBudget) {
                        fields.add(field("budget_lower_bound"));
                        fields.add(field("budget_upper_bound"));
                    } else if (budget instanceof LimitBudget) {
                        fields.add(field("budget_limit"));
                    }
                } else if (settlement instanceof PerHourSettlement) {
                    fields.add(field("hourly_rate"));
                    fields.add(field("work_hours_per_week"));
                    fields.add(field("offer_duration"));
                }
            }
            case TaskJobOffer t -> {
                fields.add(field("rate_per_task_unit"));
                fields.add(field("number_of_task_units"));
                fields.add(field("limit_task_units_type"));
                if (t.getLimitTaskUnitsPerWorker().getLimit().isPresent()) {
                    fields.add(field("limit_task_units_value"));
                }
            }
            case CompetitionJobOffer c -> {
                fields.add(field("contract_price_type"));
                if (c.getContractPrice() instanceof ContractPrice.Custom) {
                    fields.add(field("contract_price_value"));
                }
            }
            default -> {}
        }
    }

    private void setTypeSpecificValues(JobOffer jobOffer, Record rec) {
        switch (jobOffer) {
            case ProjectJobOffer p -> {
                Settlement settlement = p.getSettlement();
                if (settlement instanceof FixedSettlement fs) {
                    rec.set(field("settlement_method"), "FIXED");
                    rec.set(field("number_of_workers"), fs.getNumberOfWorkers());
                    Budget budget = fs.getBudget();
                    if (budget instanceof RangeBudget rb) {
                        rec.set(field("budget_type"), "RANGE");
                        rec.set(field("budget_lower_bound"), rb.getLowerBound());
                        rec.set(field("budget_upper_bound"), rb.getUpperBound());
                    } else if (budget instanceof LimitBudget lb) {
                        rec.set(field("budget_type"), "LIMIT");
                        rec.set(field("budget_limit"), lb.getLimit());
                    } else if (budget instanceof UndecidedBudget) {
                        rec.set(field("budget_type"), "UNDECIDED");
                    }
                } else if (settlement instanceof PerHourSettlement phs) {
                    rec.set(field("settlement_method"), "PER_HOUR");
                    rec.set(field("number_of_workers"), phs.getNumberOfWorkers());
                    rec.set(field("hourly_rate"), phs.getHourlyRate().name());
                    rec.set(field("work_hours_per_week"), phs.getWorkHoursPerWeek());
                    rec.set(field("offer_duration"), phs.getOfferDuration().name());
                }
            }
            case TaskJobOffer t -> {
                rec.set(field("rate_per_task_unit"), t.getRatePerTaskUnit());
                rec.set(field("number_of_task_units"), t.getNumberOfTaskUnits());
                t.getLimitTaskUnitsPerWorker().getLimit().ifPresentOrElse(
                        limit -> {
                            rec.set(field("limit_task_units_type"), "LIMITED");
                            rec.set(field("limit_task_units_value"), limit);
                        },
                        () -> rec.set(field("limit_task_units_type"), "UNLIMITED")
                );
            }
            case CompetitionJobOffer c -> {
                ContractPrice cp = c.getContractPrice();
                if (cp instanceof ContractPrice.Custom custom) {
                    rec.set(field("contract_price_type"), "CUSTOM");
                    rec.set(field("contract_price_value"), custom.getValue());
                } else if (cp instanceof ContractPrice.Economy) {
                    rec.set(field("contract_price_type"), "ECONOMY");
                } else if (cp instanceof ContractPrice.Basic) {
                    rec.set(field("contract_price_type"), "BASIC");
                } else if (cp instanceof ContractPrice.Standard) {
                    rec.set(field("contract_price_type"), "STANDARD");
                } else if (cp instanceof ContractPrice.Premium) {
                    rec.set(field("contract_price_type"), "PREMIUM");
                }
            }
            default -> {}
        }
    }
}
