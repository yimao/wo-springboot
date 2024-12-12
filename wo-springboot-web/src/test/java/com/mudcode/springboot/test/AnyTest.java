package com.mudcode.springboot.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AnyTest {

    @Test
    void testAny() {
        String className = this.getClass().getName();
        Assertions.assertNotNull(className);
        System.out.println(className);
    }

}
