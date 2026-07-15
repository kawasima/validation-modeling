package com.example.raoh.joboffer.web;

import com.example.raoh.joboffer.data.*;
import net.unit8.raoh.Ok;
import net.unit8.raoh.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Map;

import static com.example.raoh.joboffer.web.JobOfferEncoders.JOB_OFFER_ENCODER;
import static com.example.raoh.joboffer.web.JobOfferJsonDecoders.JOB_OFFER_DECODER;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * JOB_OFFER_ENCODER が JOB_OFFER_DECODER の対になっていることを、往復させて確かめる。
 *
 * <p>エンコーダは Map を返すので、{@code valueToTree} で JsonNode に橋渡ししてからデコーダに戻す。
 */
class JobOfferEncodersTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /** encode -> JSON -> decode して、元の値に戻ることを確かめる。 */
    private static void assertRoundTrips(JobOffer original) {
        Map<String, Object> encoded = JOB_OFFER_ENCODER.encode(original);
        JsonNode json = objectMapper.valueToTree(encoded);
        Result<JobOffer> decoded = JOB_OFFER_DECODER.decode(json);

        assertThat(decoded).isInstanceOf(Ok.class);
        assertThat(((Ok<JobOffer>) decoded).value()).isEqualTo(original);
    }

    @Test
    @DisplayName("プロジェクト案件(固定精算/範囲予算)が往復する")
    void projectWithFixedSettlement() {
        assertRoundTrips(new ProjectJobOffer("Webサイト構築", "コーポレートサイトの構築",
                LocalDate.of(2026, 12, 31),
                new FixedSettlement(3, new RangeBudget(100_000, 500_000))));
    }

    @Test
    @DisplayName("プロジェクト案件(時間精算)が往復する")
    void projectWithPerHourSettlement() {
        assertRoundTrips(new ProjectJobOffer("運用支援", "サーバ運用の支援",
                LocalDate.of(2026, 12, 31),
                new PerHourSettlement(2, HourlyRate.FROM_2000_TO_3000, 20, OfferDuration.ONE_WEEK_TO_ONE_MONTH)));
    }

    @Test
    @DisplayName("上限予算・未定予算が往復する")
    void budgetVariants() {
        assertRoundTrips(new ProjectJobOffer("A", "B", LocalDate.of(2026, 1, 1),
                new FixedSettlement(2, new LimitBudget(300_000))));
        assertRoundTrips(new ProjectJobOffer("A", "B", LocalDate.of(2026, 1, 1),
                new FixedSettlement(2, new UndecidedBudget())));
    }

    @Test
    @DisplayName("タスク案件(件数制限あり/なし)が往復する")
    void taskVariants() {
        assertRoundTrips(new TaskJobOffer("データ入力", "住所録の入力", LocalDate.of(2026, 6, 30),
                100, 500, new LimitTaskUnitsPerWorker.Limited(50)));
        assertRoundTrips(new TaskJobOffer("データ入力", "住所録の入力", LocalDate.of(2026, 6, 30),
                100, 500, new LimitTaskUnitsPerWorker.Unlimited()));
    }

    @Test
    @DisplayName("コンペ案件の契約金額が全種類往復する")
    void competitionContractPrices() {
        for (ContractPrice price : java.util.List.of(
                new ContractPrice.Economy(),
                new ContractPrice.Basic(),
                new ContractPrice.Standard(),
                new ContractPrice.Premium(),
                new ContractPrice.Custom(250_000))) {
            assertRoundTrips(new CompetitionJobOffer("ロゴ作成", "新サービスのロゴ",
                    LocalDate.of(2026, 3, 1), price));
        }
    }

    @Test
    @DisplayName("判別タグはデコーダと同じ名前で書き出される")
    void writesDecoderTags() {
        Map<String, Object> encoded = JOB_OFFER_ENCODER.encode(
                new ProjectJobOffer("A", "B", LocalDate.of(2026, 1, 1),
                        new FixedSettlement(2, new UndecidedBudget())));

        assertThat(encoded.get("type")).isEqualTo("project");
        assertThat(encoded).containsKeys("title", "description", "offerExpireDate", "settlement");

        @SuppressWarnings("unchecked")
        Map<String, Object> settlement = (Map<String, Object>) encoded.get("settlement");
        assertThat(settlement.get("type")).isEqualTo("fixed");

        @SuppressWarnings("unchecked")
        Map<String, Object> budget = (Map<String, Object>) settlement.get("budget");
        assertThat(budget).containsExactly(Map.entry("type", "undecided"));
    }

    @Test
    @DisplayName("案件IDが未採番でも jobOfferId は null のまま出力される")
    void nullJobOfferId() {
        Map<String, Object> encoded = JobOfferEncoders.JOB_OFFER_CREATED.encode(
                new JobOfferEncoders.JobOfferCreated(null,
                        new CompetitionJobOffer("A", "B", LocalDate.of(2026, 1, 1),
                                new ContractPrice.Economy())));

        assertThat(encoded).containsKey("jobOfferId");
        assertThat(encoded.get("jobOfferId")).isNull();
    }
}
