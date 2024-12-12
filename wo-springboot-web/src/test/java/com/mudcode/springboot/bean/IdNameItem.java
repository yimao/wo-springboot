package com.mudcode.springboot.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Date;

@JsonInclude(value = JsonInclude.Include.ALWAYS)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
public class IdNameItem implements IdName {

    private long id;

    private String name;

    private Date dateTime;

    private boolean enabled;

    private String[] tags;

}
