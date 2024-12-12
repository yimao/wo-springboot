package com.mudcode.springboot.common.util;

import com.mudcode.springboot.bean.IdNameItem;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class ReflectionUtilTest {

    @Test
    public void getFieldValue() throws Exception {
        IdNameItem item = new IdNameItem();
        item.setId(123);
        item.setName("abc");

        Field field = item.getClass().getDeclaredField("id");
        field.setAccessible(true);
        Object oid = field.get(item);
        System.out.println(oid);
    }

}
