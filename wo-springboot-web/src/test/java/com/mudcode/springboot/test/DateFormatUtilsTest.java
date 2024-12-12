package com.mudcode.springboot.test;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

class DateFormatUtilsTest {

    @Test
    void test() {
        Date now = new Date();
        System.out.println(now.getTime());
        System.out.println(DateFormatUtils.format(now, "yyyyMMddHHmmss"));
        // yyyy-MM-dd
        System.out.println(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(now));
        // HH:mm:ss
        System.out.println(DateFormatUtils.ISO_8601_EXTENDED_TIME_FORMAT.format(now));
        // yyyy-MM-dd'T'HH:mm:ss
        System.out.println(DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(now));
        // yyyy-MM-dd'T'HH:mm:ssZZ
        System.out.println(DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(now));
    }

    @Test
    void test2() throws ParseException {
        // 2019-09-20T09:21:18.776373989+08:00
        // long TIMEORIGIN = 1609430400000L;
        String str = "2021-01-01T00:00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = sdf.parse(str);
        System.out.println(date.getTime());
        System.out.println(date.getTime() / 1000);
    }

    @Test
    void testCalender() {
        Calendar c = Calendar.getInstance();
        System.out.println(c.getTime());
        System.out.println(c.get(Calendar.DAY_OF_YEAR));
        System.out.println(c.get(Calendar.HOUR_OF_DAY));
        System.out.println(c.get(Calendar.MINUTE));
        // c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.DAY_OF_MONTH, -40);
        System.out.println(c.getTime());
        System.out.println(c.get(Calendar.DAY_OF_YEAR));
    }

    @Test
    void testLocalDate() {
        Date oldDate = new Date();
        LocalDate date = oldDate.toInstant().atZone(ZoneId.systemDefault()).withDayOfMonth(1).toLocalDate();
        System.out.println(date);
        System.out.println(date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));

        LocalDate l0 = LocalDate.of(2024, 10, 11); // 2024-10-11 08:17:30
        LocalDate l1 = LocalDate.of(2024, 11, 15); // 2024-11-15 08:17:30
        long daysBetween = ChronoUnit.DAYS.between(l0, l1);
        System.out.println(daysBetween);

        System.out.println(l0);
        System.out.println(l1);
        System.out.println(l0.withDayOfMonth(1));
        System.out.println(l1.withDayOfMonth(1));
        System.out.println(l0.withDayOfMonth(1).equals(l1.withDayOfMonth(1)));

        l0 = l0.plusMonths(1);
        System.out.println(l0);
    }

    @Test
    void testLocalTime() {
        LocalDateTime time = LocalDateTime.now();

        time = LocalDateTime.of(2023, 10, 31, 17, 04, 6);

        System.out.println(time);
        System.out.println(DateTimeFormatter.ISO_DATE_TIME.format(time));
        System.out.println(time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")));
        System.out.println(time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00.000")));
        System.out.println(time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00.000'Z'")));

        System.out.println(time.format(DateTimeFormatter.ofPattern("yyyyMMddHH")));

        System.out.println(time.getMinute());

        int truncMinutes = time.getMinute() / 10;
        System.out.println(truncMinutes);
        if (truncMinutes == 0) {
            time = time.minusMinutes(10);
            System.out.println(time);
        }
        truncMinutes = time.getMinute() / 10;
        System.out.println(truncMinutes);

        System.out.println(LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
    }

    @Test
    void testDuration() {
        Date now = new Date();
        System.out.println(now);
        Duration duration = Duration.ofMillis(now.getTime());
        System.out.println(duration.toString());
        System.out.println(duration.toSeconds());
        System.out.println(duration.toMinutes());
        System.out.println(duration.toHours());
        System.out.println(duration.toDays());
        System.out.println(duration.truncatedTo(ChronoUnit.MINUTES).toMinutes());
        System.out.println(now.getTime() / 1000 / 60);
    }

    @Test
    void testParse() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String str = "2024-03-06T07:58:48.000Z";
        Date date = sdf.parse(str);
        System.out.println(date);
    }

}
