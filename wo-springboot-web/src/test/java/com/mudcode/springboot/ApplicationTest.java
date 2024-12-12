package com.mudcode.springboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"test", "debug"})
@SpringBootTest
public class ApplicationTest {

    protected Logger logger = LoggerFactory.getLogger(getClass());

}
