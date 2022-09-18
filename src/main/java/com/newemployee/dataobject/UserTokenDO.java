package com.newemployee.dataobject;

import lombok.Data;

import java.util.Date;

@Data
public class UserTokenDO {
    private Long userId;

    private String token;

    private Date updateTime;

    private Date expireTime;
}
