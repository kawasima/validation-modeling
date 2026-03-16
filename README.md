# バリデーションモデリング

同じドメイン仕様に対して、異なるバリデーション手法で実装し比較するプロジェクト。

## サンプルアプリケーション

| モジュール | YAVI (`yavi/`) | Bean Validation (`bean-validation/`) | Raoh (`raoh/`) |
|---|---|---|---|
| **enrollment** | domain + adapter(jOOQ) + controller + テスト | domain model（アダプタ層なし） | record + JsonDecoder + behavior + controller + テスト |
| **joboffer** | sealed record + mapValidator + STI永続化 + controller + テスト | フラットForm + カスタムValidator + controller + テスト | sealed record + discriminateデコーダ + controller + テスト |
| **reserve** | domain + adapter(jOOQ) + controller + テスト | Form + Service + controller + テスト | record + JsonDecoder + behavior + controller + テスト |

## 比較分析

### バリデーション定義の所在

**YAVI** — バリデーションルールがドメインオブジェクト内にコードとして存在する。`ProjectJobOffer`のバリデータは`ProjectJobOffer`クラス内に書かれ、`Settlement`のバリデータは`Settlement`に書かれる。型の階層とバリデーションの階層が一致するが、ドメインオブジェクトがバリデーションの知識を持つ。

**Bean Validation** — フィールドレベルの制約はアノテーションでFormクラスに書けるが、タイプ別の条件分岐（PROJECTならsettlementMethodが必須、FIXEDならbudgetTypeが必須…）は`ConstraintValidator`実装クラスに手続き的に書くしかない。結果として、フラットなFormに全タイプのフィールドが同居し、どのフィールドがどのタイプに属するかはValidatorのswitch文を読まないとわからない。

**Raoh** — バリデーションは`JsonDecoder`の合成としてweb層に定義される。ドメインモデル（record/sealed interface）はバリデーション知識を一切持たない。デコーダの構造が入力の構造を宣言的に表現し、バリデーションルール（`min`/`max`/`maxLength`）がデコーダにチェーンされる。

### 型の表現力

3つの手法すべてが`ProjectJobOffer`, `TaskJobOffer`, `CompetitionJobOffer`を別の型として表現できるが、**バリデーション通過後に型が確定するかどうか**が異なる。

**YAVI** — `mapValidator`通過後は具体型が手に入る。sealed interface + recordで型の階層を表現し、共通バリデータはインターフェースに定義。ただしバリデータがドメインモデル内に同居する点はRaohと異なる。

**Bean Validation** — バリデーション後も`JobOfferForm`のまま。Controller内で再度switch分岐してDTOに詰め替える必要がある。

**Raoh** — `discriminate`通過後はsealed interfaceの具体型（record）が直接手に入る。ドメインモデルはただのrecordなので最も軽い。

```java
// Raoh: discriminateの構造 = sealed interfaceの階層
JOB_OFFER_DECODER = discriminate("type", Map.of(
    "project", combine(TITLE, DESCRIPTION, OFFER_EXPIRE_DATE,
        field("settlement", SETTLEMENT_DECODER)       // ← さらにdiscriminate
    ).map(ProjectJobOffer::new),
    "task", combine(TITLE, DESCRIPTION, OFFER_EXPIRE_DATE,
        field("ratePerTaskUnit", long_().min(6).max(999_999)),
        ...
    ).map(TaskJobOffer::new),
    "competition", combine(TITLE, DESCRIPTION, OFFER_EXPIRE_DATE,
        field("contractPrice", CONTRACT_PRICE_DECODER) // ← さらにdiscriminate
    ).map(CompetitionJobOffer::new)
));
```

```java
// YAVI: sealed interface + recordにバリデータが同居
public sealed interface JobOffer permits ProjectJobOffer, TaskJobOffer, CompetitionJobOffer {
    // 共通バリデータをインターフェースに1箇所定義
    Arguments1Validator<Map<String, Object>, String> mapTitle = titleValidator.compose(m -> ...);
    Arguments1Validator<Map<String, Object>, JobOffer> mapValidator = ...
}
// サブタイプはrecordだが、static mapValidatorを持つ
public record ProjectJobOffer(...) implements JobOffer {
    static final Arguments1Validator<...> mapValidator = combine(
        mapTitle, mapDescription, mapOfferExpireDate, ...
    ).apply(ProjectJobOffer::new);
}
```

```java
// Bean Validation: フラットなFormに全フィールドが同居。手続き的なValidatorで条件分岐
@ValidJobOfferForm
public class JobOfferForm {
    private String title;
    private String jobOfferType;
    private String settlementMethod;  // PROJECTの場合のみ
    private Long ratePerTaskUnit;     // TASKの場合のみ
    private String contractPriceType; // COMPETITIONの場合のみ
    // ... 全タイプのフィールドがフラットに並ぶ
}
```

### 合成性と多態バリデーション

**Raoh** — `discriminate`の入れ子がsealed interfaceの階層と1対1で対応する。`BUDGET_DECODER`を`SETTLEMENT_DECODER`に組み込み、それを`JOB_OFFER_DECODER`に組み込む。各デコーダは独立してテスト可能で、合成の構造がドメインの構造を直接反映する。

**YAVI** — `mapValidator`の`compose`で同様の合成が可能。Raohと構造的には似ているが、バリデータがドメインクラス内のstatic fieldとして散在するため、全体像を把握するには各クラスを横断して読む必要がある。

**Bean Validation** — アノテーションは合成できない。カスタムValidatorの中でswitch/ifの手続きコードが100行超になる。`JobOfferFormValidator`がPROJECT→FIXED→RANGE/LIMIT/UNDECIDEDの分岐を全て手動で書く必要があり、構造の深さが増すほどコードが肥大化する。

### エラーパスの扱い

**Raoh** — `field("settlement", SETTLEMENT_DECODER)`と書くだけで、ネストした`settlement.budget.lowerBound`のようなエラーパスが自動構成される。

**YAVI** — 同様にネストしたバリデータの合成でエラーパスが自動構成される。

**Bean Validation** — `ConstraintValidatorContext.buildConstraintViolationWithTemplate().addPropertyNode()`で手動指定が必要。

### 単純なフォームバリデーション

フラットで条件分岐のないフォーム（`ReserveTourForm`のようなケース）での比較。

```java
// Bean Validation: 宣言的で読みやすい
@NotNull @Min(0) @Max(5)
private Integer adultCount;
@Size(max = 80)
private String remarks;
```

```java
// Raoh: Bean Validationに近い簡潔さ
field("adultCount", int_().min(0).max(5)),
field("childCount", int_().min(0).max(5)),
field("remarks", allowBlankString().maxLength(80))
```

```java
// YAVI: やや冗長
static Arguments1Validator<String, Integer> adultCountValidator =
    (s, locale, context) -> integerStringValidatorBuilder.apply("adultCount")
        .validate(s)
        .flatMap(i -> countValidatorBuilder.apply("adultCount").validate(i));
```

単純なケースではBean Validationが最も簡潔だが、Raohも十分に宣言的。YAVIは文字列→整数の変換を自前で書く必要があり冗長になる。

### ドメインモデルの重さ

jobofferモジュールのドメインモデル（data層）のコード量比較:

**Raoh** — 15ファイルすべてがrecord/sealed interface。バリデーション知識ゼロ。1ファイル平均5行。

**YAVI** — sealed interface + recordだが、共通バリデータ（`mapTitle`等）はインターフェースに集約。各サブタイプrecordは`mapValidator`のみ持つ。Raohと同じ型の軽さだが、バリデータが同居する分だけコードが増える。

**Bean Validation** — ドメインモデルではなくフラットなFormクラス。型の階層を表現できないため、比較対象外。

### 比較表

| 観点 | Raoh | YAVI | Bean Validation |
|---|---|---|---|
| フラットなフォーム | 簡潔 | やや冗長 | アノテーションで最も簡潔 |
| 多態バリデーション | `discriminate`で宣言的に合成 | `mapValidator`で合成可能 | 手続き的Validatorに集約 |
| バリデーション後の型安全性 | sealed recordで保証 | sealed recordで保証 | 文字列のまま |
| ドメインモデルの軽さ | record/sealedのみ（最軽量） | sealed record + バリデータ同居 | Form DTO（型安全性なし） |
| 合成・再利用 | デコーダ合成が自然 | 関数合成で可能 | 別Validator切り出しが必要 |
| エラーパス | 自動構成 | 自動構成 | 手動構築 |
| I/O統合 | 入力(JSON)も出力(DB)もデコーダで統一 | controller側で別ステップ | service側で別ステップ |
| コントローラ | switch式(Ok/Err)パターンマッチ | fold/flatMapチェーン | if/try-catch |
| 学習コスト | Decoder合成の理解が必要 | 関数型バリデータの理解が必要 | アノテーションは直感的 |
| Springとの統合 | `@RequestBody JsonNode`で自前処理 | 自前でfold処理 | `@Validated`+`BindingResult`が標準 |

### 結論

ドメインの複雑さが増すほど、宣言的なデコーダ/バリデータ合成の価値が高まる。

**Bean Validation**は単純なフォームでは最も簡潔だが、JobOfferのような多態的なバリデーションではフラットなForm＋手続き的Validatorの構造的ミスマッチが顕著になる。

**YAVI**は型の階層とバリデーションの階層を一致させることで多態バリデーションを自然に表現できるが、ドメインオブジェクトにバリデーションの知識が入り込む。

**Raoh**は`discriminate`の入れ子でsealed interfaceの階層をそのままデコーダの構造として表現し、ドメインモデルをただのrecordに保つ。バリデーションとパースと型構築が一体化しているため、「バリデーション済みデータ」と「ドメインオブジェクト」の間にギャップが生じない。

## 技術スタック

| | YAVI | Bean Validation | Raoh |
|---|---|---|---|
| バリデーション | YAVI 0.16.0 | Jakarta Bean Validation (Hibernate Validator) | Raoh 0.4.1 |
| フレームワーク | Spring Boot 4.0.3 | Spring Boot 4.0.3 | Spring Boot 4.0.3 |
| 永続化 | jOOQ + H2 + Flyway | なし（サンプルのため省略） | jOOQ + H2 + Flyway |
| Java | 25 | 21 | 25 |
| その他 | — | Lombok | — |

