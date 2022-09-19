package com.newemployee.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeckillVO implements Serializable {
    private Integer id;
    private Integer hours;
    private Integer minutes;
    private Integer seconds;
}
