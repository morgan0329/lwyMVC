package com.xiaosq.lwymvc.framework.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LwyAutowired {
    String value() default "";
}
