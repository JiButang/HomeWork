package com.newemployee.dataobject;

import lombok.Data;

@Data
public class OrderAddressDO {
    private Long orderId;

    private String userName;

    private String userPhone;

    private String provinceName;

    private String cityName;

    private String regionName;

    private String detailAddress;
}
