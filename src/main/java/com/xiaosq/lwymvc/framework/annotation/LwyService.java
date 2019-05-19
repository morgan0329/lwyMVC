package com.xiaosq.lwymvc.framework.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LwyService {
    String value() default "";
}
