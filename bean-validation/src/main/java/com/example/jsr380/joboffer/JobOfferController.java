package com.example.jsr380.joboffer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("job-offers")
public class JobOfferController {
    private final JobOfferService jobOfferService;

    public JobOfferController(JobOfferService jobOfferService) {
        this.jobOfferService = jobOfferService;
    }

    @ModelAttribute
    public JobOfferForm setUpForm() {
        return new JobOfferForm();
    }

    @PostMapping
    public String create(@Validated JobOfferForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "joboffer/jobOfferForm";
        }

        JobOfferInput input = toInput(form);

        JobOfferOutput output = jobOfferService.register(input);
        redirectAttributes.addFlashAttribute("output", output);

        return "redirect:/job-offers?complete";
    }

    private JobOfferInput toInput(JobOfferForm form) {
        JobOfferInput input = new JobOfferInput();
        input.setTitle(form.getTitle());
        input.setDescription(form.getDescription());
        input.setJobOfferType(form.getJobOfferType());
        input.setOfferExpireDate(form.getOfferExpireDate());

        switch (form.getJobOfferType()) {
            case "PROJECT" -> {
                input.setSettlementMethod(form.getSettlementMethod());
                input.setNumberOfWorkers(form.getNumberOfWorkers());
                switch (form.getSettlementMethod()) {
                    case "FIXED" -> {
                        input.setBudgetType(form.getBudgetType());
                        input.setBudgetLowerBound(form.getBudgetLowerBound());
                        input.setBudgetUpperBound(form.getBudgetUpperBound());
                        input.setBudgetLimit(form.getBudgetLimit());
                    }
                    case "PER_HOUR" -> {
                        input.setHourlyRate(form.getHourlyRate());
                        input.setWorkHoursPerWeek(form.getWorkHoursPerWeek());
                        input.setOfferDuration(form.getOfferDuration());
                    }
                }
            }
            case "TASK" -> {
                input.setRatePerTaskUnit(form.getRatePerTaskUnit());
                input.setNumberOfTaskUnits(form.getNumberOfTaskUnits());
                input.setLimitTaskUnitsType(form.getLimitTaskUnitsType());
                input.setLimitTaskUnitsValue(form.getLimitTaskUnitsValue());
            }
            case "COMPETITION" -> {
                input.setContractPriceType(form.getContractPriceType());
                input.setContractPriceValue(form.getContractPriceValue());
            }
        }

        return input;
    }
}
