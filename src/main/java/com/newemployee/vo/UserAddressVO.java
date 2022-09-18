package com.newemployee.vo;

import lombok.Data;

@Data
public class UserAddressVO {
    //地址id
    private Long addressId;

    //用户id
    private Long userId;

    //收件人名称
    private String userName;

    //收件人联系方式
    private String userPhone;

    //是否默认地址 0-不是 1-是
    private Byte defaultFlag;

    //省
    private String provinceName;

    //市
    private String cityName;

    //区/县
    private String regionName;

    //详细地址
    private String detailAddress;
}
