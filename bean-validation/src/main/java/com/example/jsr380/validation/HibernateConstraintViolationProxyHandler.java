package com.example.jsr380.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import org.hibernate.validator.internal.engine.path.PathImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HibernateConstraintViolationProxyHandler implements InvocationHandler {
    private final ConstraintViolation<?> delegate;
    private final String prefixPathSegment;
    private Path newPathCache;


    public HibernateConstraintViolationProxyHandler(ConstraintViolation<?> delegate, String prefixPathSegment) {
        if (delegate == null) {
            throw new IllegalArgumentException("Delegate ConstraintViolation cannot be null.");
        }
        if (prefixPathSegment == null || prefixPathSegment.isEmpty()) {
            throw new IllegalArgumentException("Prefix path segment cannot be null or empty.");
        }
        this.delegate = delegate;
        this.prefixPathSegment = prefixPathSegment;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();

        if ("getPropertyPath".equals(methodName)) {
            if (newPathCache == null) {
                Path originalPath = delegate.getPropertyPath();
                String originalPathString = (originalPath == null) ? "" : originalPath.toString();
                String newPathString;

                if (originalPathString.isEmpty()) {
                    newPathString = prefixPathSegment;
                } else if (originalPathString.startsWith("[")) {
                    // 例: originalPathString が "[index]" や "[key]" の場合
                    // prefixPathSegment が "myList" なら "myList[index]" となる
                    newPathString = prefixPathSegment + originalPathString;
                } else {
                    // 例: originalPathString が "fieldName" の場合
                    // prefixPathSegment が "parentBean" なら "parentBean.fieldName" となる
                    newPathString = prefixPathSegment + "." + originalPathString;
                }
                // Hibernate Validator の PathImpl を使って新しい Path オブジェクトを生成
                // PathImpl.createPathFromString はドット区切りやインデックス/キー形式のパスを解釈できる
                newPathCache = PathImpl.createPathFromString(newPathString);
            }
            return newPathCache;
        }

        // toString() の結果も新しい propertyPath を反映させる例
        if ("toString".equals(methodName) && (args == null || args.length == 0)) {
            // 再計算を避けるために getPropertyPath() を経由
            Path currentPath = (Path) invoke(proxy, delegate.getClass().getMethod("getPropertyPath"), null);
            return String.format(
                    "ProxiedConstraintViolation{message='%s', propertyPath='%s', invalidValue='%s', rootBeanClass='%s', leafBean='%s'}",
                    delegate.getMessage(),
                    currentPath.toString(), // ここで newPathCache (または計算結果) が使われる
                    delegate.getInvalidValue(),
                    delegate.getRootBeanClass().getSimpleName(),
                    delegate.getLeafBean() != null ? delegate.getLeafBean().getClass().getSimpleName() : "null"
            );
        }

        // equals() と hashCode() はプロキシオブジェクト間で一貫性を保つために注意が必要
        // ここでは単純に委譲するが、より厳密な実装が必要な場合がある
        // 例えば、プロキシされたオブジェクトと元のオブジェクト、または他のプロキシされたオブジェクトとの比較
        if ("equals".equals(methodName) && args != null && args.length == 1 && args[0] != null) {
            if (Proxy.isProxyClass(args[0].getClass())) {
                InvocationHandler otherHandler = Proxy.getInvocationHandler(args[0]);
                if (otherHandler instanceof HibernateConstraintViolationProxyHandler) {
                    HibernateConstraintViolationProxyHandler otherProxyHandler = (HibernateConstraintViolationProxyHandler) otherHandler;
                    // 元のデリゲートと、計算されるパス文字列で比較する例 (より堅牢な比較が必要な場合あり)
                    Path thisPath = (Path) invoke(proxy, delegate.getClass().getMethod("getPropertyPath"), null);
                    Path otherPath = (Path) otherHandler.invoke(args[0], delegate.getClass().getMethod("getPropertyPath"), null);
                    return delegate.equals(otherProxyHandler.delegate) && thisPath.toString().equals(otherPath.toString());
                }
            }
            // プロキシでないオブジェクトとの比較は、通常 false になるか、デリゲートの equals に依存
        }

        // 他のすべてのメソッドは元のオブジェクトに委譲
        return method.invoke(delegate, args);
    }
}
