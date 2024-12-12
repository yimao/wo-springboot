package com.mudcode.springboot.controller.advice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface URLDecodeBody {

}
