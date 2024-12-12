package com.mudcode.springboot.common;

import com.mudcode.springboot.bean.IdNameItem;
import com.mudcode.springboot.common.util.JsonUtil;
import com.mudcode.springboot.message.ServiceMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class ServiceMessageTest {

    private IdNameItem beanTest;

    @BeforeEach
    public void before() {
        beanTest = new IdNameItem();
        beanTest.setId(101);
        beanTest.setName("Hello, World!");
        beanTest.setDateTime(new Date());
    }

    @Test
    public void testServiceEntity() {
        System.out.println(JsonUtil.toJson(ServiceMessage.success()));

        System.out.println(JsonUtil.toJson(ServiceMessage.success("Hello, World!")));

        ServiceMessage<?> success0 = ServiceMessage.success(beanTest);
        System.out.println(JsonUtil.toJson(success0));

        ServiceMessage<Object[]> success1 = ServiceMessage.success(new Object[]{beanTest});
        System.out.println(JsonUtil.toJson(success1));

        ServiceMessage<?> error0 = ServiceMessage.error("something was wrong");
        System.out.println(JsonUtil.toJson(error0));

        ServiceMessage<?> error1 = ServiceMessage.error(500, "system error");
        System.out.println(JsonUtil.toJson(error1));
    }

}
