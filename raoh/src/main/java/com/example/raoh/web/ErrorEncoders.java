package com.example.raoh.web;

import net.unit8.raoh.Issue;
import net.unit8.raoh.Issues;
import net.unit8.raoh.encode.Encoder;
import org.jspecify.annotations.Nullable;

import java.util.Map;

import static net.unit8.raoh.encode.MapEncoders.*;
import static net.unit8.raoh.encode.ObjectEncoders.*;

/**
 * デコード失敗時のレスポンスボディを組み立てるエンコーダ。
 *
 * <p>{@link Issues} は raoh のコア型なので、機能ごとではなくここに置いて全コントローラで共有する。
 */
public final class ErrorEncoders {

    private ErrorEncoders() {}

    /**
     * 1件の {@link Issue} をエンコードする。
     *
     * <p>{@code code} は機械可読なエラーコードだが、既存のレスポンス形式に合わせて path と message
     * だけを出力する。
     */
    static final Encoder<Issue, Map<String, @Nullable Object>> ISSUE = object(
            property("path",    (Issue i) -> i.path().toString(), string()),
            property("message", Issue::message,                   string()));

    /** {@link Issues} 全体を {@code {"errors": [...]}} の形にエンコードする。 */
    public static final Encoder<Issues, Map<String, @Nullable Object>> ERRORS = object(
            property("errors", Issues::asList, list(nested(ISSUE))));
}
