package com.mudcode.springboot.test;

import org.junit.jupiter.api.Test;

public class ResourceTest {

    @Test
    public void testClassResource() {
        System.out.println("this.getClass().getResource(\"/\") = " + this.getClass().getResource("/"));
        System.out.println("this.getClass().getResource(\"\") = " + this.getClass().getResource(""));
        System.out.println("this.getClass().getClassLoader().getResource(\"/\") = "
                + this.getClass().getClassLoader().getResource("/"));
        System.out.println("this.getClass().getClassLoader().getResource(\"\") = "
                + this.getClass().getClassLoader().getResource(""));
    }

}
