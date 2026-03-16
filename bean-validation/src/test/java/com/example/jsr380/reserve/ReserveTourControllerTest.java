package com.example.jsr380.reserve;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReserveTourControllerTest {

    private LocalValidatorFactoryBean createValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        return validator;
    }

    @Test
    @DisplayName("有効なフォームの場合、予約が成功しリダイレクト")
    void reserveSuccess() {
        ReserveService reserveService = mock(ReserveService.class);
        when(reserveService.reserve(any(ReserveTourInput.class)))
                .thenReturn(new ReserveTourOutput("R001", "T001", 2, 1));

        ReserveTourController controller = new ReserveTourController(reserveService);

        ReserveTourForm form = new ReserveTourForm();
        form.setAdultCount(2);
        form.setChildCount(1);
        form.setRemarks("窓側希望");

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "reserveTourForm");
        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String result = controller.reserve("T001", "C001", form, bindingResult, model, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/tours/{tourCode}/reserve?complete");
        assertThat(redirectAttributes.getFlashAttributes()).containsKey("output");
    }

    @Test
    @DisplayName("バリデーションエラーの場合、フォームに戻る")
    void reserveValidationError() {
        ReserveService reserveService = mock(ReserveService.class);
        ReserveTourController controller = new ReserveTourController(reserveService);

        ReserveTourForm form = new ReserveTourForm();
        form.setAdultCount(6); // 5以下でなければならない
        form.setChildCount(0);

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "reserveTourForm");

        // Springと同様にバリデーション実行
        LocalValidatorFactoryBean validator = createValidator();
        validator.validate(form, bindingResult);

        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String result = controller.reserve("T001", "C001", form, bindingResult, model, redirectAttributes);

        assertThat(result).isEqualTo("reservetour/reserveForm");
        assertThat(bindingResult.hasErrors()).isTrue();
    }

    @Test
    @DisplayName("ビジネスエラーの場合、フォームに戻りエラーメッセージ表示")
    void reserveBusinessError() {
        ReserveService reserveService = mock(ReserveService.class);
        when(reserveService.reserve(any(ReserveTourInput.class)))
                .thenThrow(new BusinessException("ツアーに空きがありません"));

        ReserveTourController controller = new ReserveTourController(reserveService);

        ReserveTourForm form = new ReserveTourForm();
        form.setAdultCount(2);
        form.setChildCount(1);

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "reserveTourForm");
        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String result = controller.reserve("T001", "C001", form, bindingResult, model, redirectAttributes);

        assertThat(result).isEqualTo("reservetour/reserveForm");
        assertThat(model.getAttribute("errorMessage")).isEqualTo("ツアーに空きがありません");
    }
}
