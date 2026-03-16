package com.example.jsr380.joboffer;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class JobOfferFormValidator implements ConstraintValidator<ValidJobOfferForm, JobOfferForm> {
    @Override
    public void initialize(ValidJobOfferForm constraintAnnotation) {
    }

    @Override
    public boolean isValid(JobOfferForm form, ConstraintValidatorContext context) {
        if (form == null || form.getJobOfferType() == null) {
            return true;
        }

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        switch (form.getJobOfferType()) {
            case "PROJECT" -> valid = validateProject(form, context);
            case "TASK" -> valid = validateTask(form, context);
            case "COMPETITION" -> valid = validateCompetition(form, context);
        }

        return valid;
    }

    private boolean validateProject(JobOfferForm form, ConstraintValidatorContext context) {
        boolean valid = true;

        if (form.getSettlementMethod() == null) {
            addViolation(context, "settlementMethod", "精算方式は必須です");
            return false;
        }

        if (form.getNumberOfWorkers() == null) {
            addViolation(context, "numberOfWorkers", "募集人数は必須です");
            valid = false;
        }

        switch (form.getSettlementMethod()) {
            case "FIXED" -> {
                if (form.getBudgetType() == null) {
                    addViolation(context, "budgetType", "予算タイプは必須です");
                    valid = false;
                } else {
                    switch (form.getBudgetType()) {
                        case "RANGE" -> {
                            if (form.getBudgetLowerBound() == null) {
                                addViolation(context, "budgetLowerBound", "予算下限は必須です");
                                valid = false;
                            }
                            if (form.getBudgetUpperBound() == null) {
                                addViolation(context, "budgetUpperBound", "予算上限は必須です");
                                valid = false;
                            }
                        }
                        case "LIMIT" -> {
                            if (form.getBudgetLimit() == null) {
                                addViolation(context, "budgetLimit", "予算上限額は必須です");
                                valid = false;
                            }
                        }
                    }
                }
            }
            case "PER_HOUR" -> {
                if (form.getHourlyRate() == null) {
                    addViolation(context, "hourlyRate", "時間単価は必須です");
                    valid = false;
                }
                if (form.getWorkHoursPerWeek() == null) {
                    addViolation(context, "workHoursPerWeek", "週あたり稼働時間は必須です");
                    valid = false;
                }
                if (form.getOfferDuration() == null) {
                    addViolation(context, "offerDuration", "募集期間は必須です");
                    valid = false;
                }
            }
        }

        return valid;
    }

    private boolean validateTask(JobOfferForm form, ConstraintValidatorContext context) {
        boolean valid = true;

        if (form.getRatePerTaskUnit() == null) {
            addViolation(context, "ratePerTaskUnit", "作業単価は必須です");
            valid = false;
        }
        if (form.getNumberOfTaskUnits() == null) {
            addViolation(context, "numberOfTaskUnits", "作業件数は必須です");
            valid = false;
        }
        if (form.getLimitTaskUnitsType() == null) {
            addViolation(context, "limitTaskUnitsType", "1人あたり件数制限は必須です");
            valid = false;
        } else if ("LIMITED".equals(form.getLimitTaskUnitsType()) && form.getLimitTaskUnitsValue() == null) {
            addViolation(context, "limitTaskUnitsValue", "1人あたり件数上限は必須です");
            valid = false;
        }

        return valid;
    }

    private boolean validateCompetition(JobOfferForm form, ConstraintValidatorContext context) {
        boolean valid = true;

        if (form.getContractPriceType() == null) {
            addViolation(context, "contractPriceType", "契約金額タイプは必須です");
            valid = false;
        } else if ("CUSTOM".equals(form.getContractPriceType()) && form.getContractPriceValue() == null) {
            addViolation(context, "contractPriceValue", "カスタム契約金額は必須です");
            valid = false;
        }

        return valid;
    }

    private void addViolation(ConstraintValidatorContext context, String property, String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(property)
                .addConstraintViolation();
    }
}
