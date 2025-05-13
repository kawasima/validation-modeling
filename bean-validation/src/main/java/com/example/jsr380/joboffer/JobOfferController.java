package com.example.jsr380.joboffer;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class JobOfferController {
    @PostMapping("/job-offers")
    public String createJobOffer(@Validated JobOfferForm jobOfferForm, BindingResult bindingResult) {
        return "redirect:/job-offers"; // Redirect to the job offers list page
    }
}
