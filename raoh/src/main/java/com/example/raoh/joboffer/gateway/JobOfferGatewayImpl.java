package com.example.raoh.joboffer.gateway;

import com.example.raoh.joboffer.data.*;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Component
public class JobOfferGatewayImpl implements JobOfferGateway {
    private final DSLContext dslContext;

    public JobOfferGatewayImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public String save(JobOffer jobOffer) {
        var insert = dslContext.insertInto(table("job_offers"))
                .set(field("title"), jobOffer.title())
                .set(field("description"), jobOffer.description())
                .set(field("offer_expire_date"), jobOffer.offerExpireDate())
                .set(field("offer_type"), offerType(jobOffer));

        switch (jobOffer) {
            case ProjectJobOffer p -> setProjectFields(insert, p);
            case TaskJobOffer t -> setTaskFields(insert, t);
            case CompetitionJobOffer c -> setCompetitionFields(insert, c);
        }

        var rec = insert.returning(field("id")).fetchOne();
        return rec != null ? rec.get("id", String.class) : null;
    }

    private String offerType(JobOffer jobOffer) {
        return switch (jobOffer) {
            case ProjectJobOffer _ -> "PROJECT";
            case TaskJobOffer _ -> "TASK";
            case CompetitionJobOffer _ -> "COMPETITION";
        };
    }

    @SuppressWarnings("unchecked")
    private void setProjectFields(org.jooq.InsertSetMoreStep<?> insert, ProjectJobOffer p) {
        switch (p.settlement()) {
            case FixedSettlement fs -> {
                insert.set(field("settlement_method"), "FIXED");
                insert.set(field("number_of_workers"), fs.numberOfWorkers());
                switch (fs.budget()) {
                    case RangeBudget rb -> {
                        insert.set(field("budget_type"), "RANGE");
                        insert.set(field("budget_lower_bound"), rb.lowerBound());
                        insert.set(field("budget_upper_bound"), rb.upperBound());
                    }
                    case LimitBudget lb -> {
                        insert.set(field("budget_type"), "LIMIT");
                        insert.set(field("budget_limit"), lb.limit());
                    }
                    case UndecidedBudget _ -> insert.set(field("budget_type"), "UNDECIDED");
                }
            }
            case PerHourSettlement phs -> {
                insert.set(field("settlement_method"), "PER_HOUR");
                insert.set(field("number_of_workers"), phs.numberOfWorkers());
                insert.set(field("hourly_rate"), phs.hourlyRate().name());
                insert.set(field("work_hours_per_week"), phs.workHoursPerWeek());
                insert.set(field("offer_duration"), phs.offerDuration().name());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void setTaskFields(org.jooq.InsertSetMoreStep<?> insert, TaskJobOffer t) {
        insert.set(field("rate_per_task_unit"), t.ratePerTaskUnit());
        insert.set(field("number_of_task_units"), t.numberOfTaskUnits());
        switch (t.limitTaskUnitsPerWorker()) {
            case LimitTaskUnitsPerWorker.Limited l -> {
                insert.set(field("limit_task_units_type"), "LIMITED");
                insert.set(field("limit_task_units_value"), l.limit());
            }
            case LimitTaskUnitsPerWorker.Unlimited _ ->
                    insert.set(field("limit_task_units_type"), "UNLIMITED");
        }
    }

    @SuppressWarnings("unchecked")
    private void setCompetitionFields(org.jooq.InsertSetMoreStep<?> insert, CompetitionJobOffer c) {
        switch (c.contractPrice()) {
            case ContractPrice.Economy _ -> insert.set(field("contract_price_type"), "ECONOMY");
            case ContractPrice.Basic _ -> insert.set(field("contract_price_type"), "BASIC");
            case ContractPrice.Standard _ -> insert.set(field("contract_price_type"), "STANDARD");
            case ContractPrice.Premium _ -> insert.set(field("contract_price_type"), "PREMIUM");
            case ContractPrice.Custom cu -> {
                insert.set(field("contract_price_type"), "CUSTOM");
                insert.set(field("contract_price_value"), cu.value());
            }
        }
    }
}
