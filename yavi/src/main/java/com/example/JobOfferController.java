package com.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class JobOfferController {
    @PostMapping("/job-offer")
    public void register(@RequestParam Map<String, Object> params) {

    }
}
