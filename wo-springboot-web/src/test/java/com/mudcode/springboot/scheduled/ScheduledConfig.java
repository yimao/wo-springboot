package com.mudcode.springboot.scheduled;

import com.mudcode.springboot.LogUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@EnableScheduling
@Configuration
public class ScheduledConfig {

    private static final Logger logger = LogUtil.logger(ScheduledConfig.class);

    @Scheduled(cron = "0/10 * * * * ?")
    public void scheduleSeconds() {
        logger.info("schedule seconds run: {}", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void scheduleMinutes() {
        logger.info("schedule minutes run: {}", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }

}
