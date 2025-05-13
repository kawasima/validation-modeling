package com.example.yavi.reserve;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("tours")
public class ReserveTourController {
    @Autowired
    private ReserveService reserveService;

    @PostMapping("{tourCode}/reserve")
    public String confirm(@PathVariable("tourCode") String tourCode,
                          @RequestParam Map<String, String> params,
                          Model model) {
        return ReserveTourInput.parse(tourCode, params.get("adultCount"), params.get("childCount"), params.get("remarks"))
                .fold(errors -> {
                            // バリデーションエラー時の処理
                            model.addAttribute("errors", errors);
                            return "reservetour/reserveForm";
                        },
                        input -> {
                            // パースOK時の処理
                            ReserveTourOutput output = reserveService.reserve(input);
                            return "reservetour/reserveConfirm";
                        });
    }
}
