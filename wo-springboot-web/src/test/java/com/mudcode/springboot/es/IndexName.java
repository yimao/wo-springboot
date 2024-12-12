package com.mudcode.springboot.es;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class IndexName {

    /**
     * 按照 月 维度创建索引
     */
    public static String get(String indexNamePrefix, Date date, String datePattern) {
        return get(indexNamePrefix, date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), datePattern);
    }

    /**
     * 按照 月 维度创建索引
     */
    public static String get(String indexNamePrefix, LocalDate date, String datePattern) {
        if (datePattern == null || datePattern.isEmpty()) {
            datePattern = "yyyyMMdd";
        }

        // 基于业务逻辑场景，index 不需要按照每天创建索引
        // 如果 date pattern 配置为 yyyyMMdd 方式，则直接重定向到每个月1号
        // 确保最终效果还是一个月一个索引
        LocalDate localDate = date.withDayOfMonth(1);
        if (indexNamePrefix.endsWith("_")) {
            return indexNamePrefix.toLowerCase() + localDate.format(DateTimeFormatter.ofPattern(datePattern));
        }
        return indexNamePrefix.toLowerCase() + "_" + localDate.format(DateTimeFormatter.ofPattern(datePattern));
    }

    /**
     * 按照 月 维度创建索引
     */
    public static String[] get(String indexNamePrefix, LocalDate begin, LocalDate end, String datePattern) {
        if (begin.getMonthValue() == end.getMonthValue()) {
            return new String[]{IndexName.get(indexNamePrefix, begin, datePattern)};
        }
        Set<String> indices = new HashSet<>();
        int endMonthValue = end.getMonthValue();
        while (begin.getMonthValue() <= endMonthValue) {
            indices.add(IndexName.get(indexNamePrefix, begin, datePattern));
            begin = begin.plusMonths(1);
        }
        return indices.toArray(new String[0]);
    }

}
