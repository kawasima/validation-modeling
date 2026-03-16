package com.example.jsr380.joboffer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JobOfferControllerTest {

    private LocalValidatorFactoryBean createValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        return validator;
    }

    @Test
    @DisplayName("有効なPROJECTフォームの場合、登録成功しリダイレクト")
    void createProjectSuccess() {
        JobOfferService service = mock(JobOfferService.class);
        when(service.register(any(JobOfferInput.class)))
                .thenReturn(new JobOfferOutput("JO001", "Web Dev", "PROJECT"));

        JobOfferController controller = new JobOfferController(service);

        JobOfferForm form = new JobOfferForm();
        form.setTitle("Web Development");
        form.setDescription("Build a website");
        form.setJobOfferType("PROJECT");
        form.setOfferExpireDate(LocalDate.now().plusDays(30));
        form.setSettlementMethod("FIXED");
        form.setNumberOfWorkers(3L);
        form.setBudgetType("RANGE");
        form.setBudgetLowerBound(100000L);
        form.setBudgetUpperBound(500000L);

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "jobOfferForm");
        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String result = controller.create(form, bindingResult, model, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/job-offers?complete");
        assertThat(redirectAttributes.getFlashAttributes()).containsKey("output");
    }

    @Test
    @DisplayName("有効なTASKフォームの場合、登録成功しリダイレクト")
    void createTaskSuccess() {
        JobOfferService service = mock(JobOfferService.class);
        when(service.register(any(JobOfferInput.class)))
                .thenReturn(new JobOfferOutput("JO002", "Data Entry", "TASK"));

        JobOfferController controller = new JobOfferController(service);

        JobOfferForm form = new JobOfferForm();
        form.setTitle("Data Entry");
        form.setDescription("Process survey responses");
        form.setJobOfferType("TASK");
        form.setOfferExpireDate(LocalDate.now().plusDays(30));
        form.setRatePerTaskUnit(100L);
        form.setNumberOfTaskUnits(1000L);
        form.setLimitTaskUnitsType("LIMITED");
        form.setLimitTaskUnitsValue(50L);

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "jobOfferForm");
        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String result = controller.create(form, bindingResult, model, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/job-offers?complete");
    }

    @Test
    @DisplayName("有効なCOMPETITIONフォームの場合、登録成功しリダイレクト")
    void createCompetitionSuccess() {
        JobOfferService service = mock(JobOfferService.class);
        when(service.register(any(JobOfferInput.class)))
                .thenReturn(new JobOfferOutput("JO003", "Logo Design", "COMPETITION"));

        JobOfferController controller = new JobOfferController(service);

        JobOfferForm form = new JobOfferForm();
        form.setTitle("Logo Design");
        form.setDescription("Design a company logo");
        form.setJobOfferType("COMPETITION");
        form.setOfferExpireDate(LocalDate.now().plusDays(30));
        form.setContractPriceType("STANDARD");

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "jobOfferForm");
        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String result = controller.create(form, bindingResult, model, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/job-offers?complete");
    }

    @Test
    @DisplayName("バリデーションエラーの場合、フォームに戻る")
    void createValidationError() {
        JobOfferService service = mock(JobOfferService.class);
        JobOfferController controller = new JobOfferController(service);

        JobOfferForm form = new JobOfferForm();
        form.setTitle(""); // blank
        form.setDescription("Valid");
        form.setJobOfferType("PROJECT");
        form.setOfferExpireDate(LocalDate.now().plusDays(30));

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "jobOfferForm");
        LocalValidatorFactoryBean validator = createValidator();
        validator.validate(form, bindingResult);

        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String result = controller.create(form, bindingResult, model, redirectAttributes);

        assertThat(result).isEqualTo("joboffer/jobOfferForm");
        assertThat(bindingResult.hasErrors()).isTrue();
    }
}
