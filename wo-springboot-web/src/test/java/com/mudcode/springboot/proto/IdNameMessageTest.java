package com.mudcode.springboot.proto;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.mudcode.message.IdNameMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.UUID;

class IdNameMessageTest {

    @Test
    void testMessage() throws InvalidProtocolBufferException {
        IdNameMessage idNameMessage = IdNameMessage.newBuilder()
                .setId(10001)
                .setName(UUID.randomUUID().toString())
                .setEnabled(true)
                .addTags("dev")
                .addTags("java")
                .setApiVersion("1.0")
                .build();

        String toString = idNameMessage.toString();
        System.out.println("toString: \n" + toString);
        String base64Encoded = Base64.getEncoder().encodeToString(idNameMessage.toByteArray());
        System.out.println("base64Encoded: \n" + base64Encoded);
        String jsonString = JsonFormat.printer().print(idNameMessage);
        System.out.println("JsonFormat: " + jsonString);

        IdNameMessage check = IdNameMessage.parseFrom(idNameMessage.toByteArray());
        Assertions.assertEquals(idNameMessage, check);
    }

}
