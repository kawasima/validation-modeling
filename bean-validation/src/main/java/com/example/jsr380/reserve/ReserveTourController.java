package com.example.jsr380.reserve;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("tours")
public class ReserveTourController {
    private final ReserveService reserveService;

    public ReserveTourController(ReserveService reserveService) {
        this.reserveService = reserveService;
    }

    @ModelAttribute
    public ReserveTourForm setUpForm() {
        return new ReserveTourForm();
    }

    @PostMapping("{tourCode}/reserve")
    public String reserve(@PathVariable("tourCode") String tourCode,
                          @RequestParam("customerId") String customerId,
                          @Validated ReserveTourForm form,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tourCode", tourCode);
            return "reservetour/reserveForm";
        }

        ReserveTourInput input = new ReserveTourInput();
        input.setTourCode(tourCode);
        input.setCustomerId(customerId);
        input.setAdultCount(form.getAdultCount());
        input.setChildCount(form.getChildCount());
        input.setRemarks(form.getRemarks());

        try {
            ReserveTourOutput output = reserveService.reserve(input);
            redirectAttributes.addFlashAttribute("output", output);
        } catch (BusinessException e) {
            model.addAttribute("tourCode", tourCode);
            model.addAttribute("errorMessage", e.getMessage());
            return "reservetour/reserveForm";
        }

        return "redirect:/tours/{tourCode}/reserve?complete";
    }
}
