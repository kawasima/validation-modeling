package com.example.web.joboffer;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class JobOfferFormValidator implements ConstraintValidator<ValidJobOfferForm, JobOfferForm> {
    @Override
    public void initialize(ValidJobOfferForm constraintAnnotation) {
    }

    @Override
    public boolean isValid(JobOfferForm form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }

        String jobOfferType = form.getJobOfferType();
        if (jobOfferType == null) {
            return true;
        }

        switch (jobOfferType) {
            case "PROJECT":
                return validateProjectOffer(form);
            case "TASK":
                return validateTaskOffer(form);
            case "CONTEST":
                return validateContestOffer(form);
            default:
                return true;
        }
    }

    private boolean validateProjectOffer(JobOfferForm form) {
        return form.getPaymentType() != null && form.getDeliveryDate() != null;
    }

    private boolean validateTaskOffer(JobOfferForm form) {
        return form.getTaskPrice() != null && form.getTaskCount() != null && form.getMaxTasksPerPerson() != null;
    }

    private boolean validateContestOffer(JobOfferForm form) {
        return form.getContractAmount() != null;
    }
} 