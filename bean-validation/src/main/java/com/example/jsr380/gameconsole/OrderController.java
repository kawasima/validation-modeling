package com.example.jsr380.gameconsole;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class OrderController {
    @PostMapping
    public String processOrder(Principal principal, @Validated OrderForm orderForm, BindingResult bindingResult) {
        principal.getName();
        return "orderConfirmation"; // Return the name of the view to be rendered
    }
}
