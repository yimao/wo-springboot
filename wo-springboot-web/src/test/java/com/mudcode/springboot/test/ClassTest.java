package com.mudcode.springboot.test;

import com.mudcode.springboot.bean.IdNameItem;
import org.junit.jupiter.api.Test;

public class ClassTest {

    @Test
    public void test() {
        Class<?> cls = String.class;

        System.out.println(cls.getTypeName());
        System.out.println(cls instanceof Class);
        System.out.println(cls.getClass().equals(Class.class));

        Object obj = "";
        System.out.println(obj instanceof Class);
        System.out.println(obj.getClass().equals(Class.class));
        System.out.println(obj.getClass().getTypeName());

        System.out.println(null instanceof Class);
        System.out.println(null instanceof Object);
    }

    @Test
    public void test1() {
        IdNameItem student = new IdNameItem();
        System.out.println("getName():" + student.getClass().getName());
        System.out.println("getSimpleName():" + student.getClass().getSimpleName());
        System.out.println("getCanonicalName():" + student.getClass().getCanonicalName());
    }

    @Test
    public void test2() {
        IdNameItem[] student = new IdNameItem[0];
        System.out.println("getName():" + student.getClass().getName());
        System.out.println("getSimpleName():" + student.getClass().getSimpleName());
        System.out.println("getCanonicalName():" + student.getClass().getCanonicalName());
    }

}
