package com.newemployee.controller.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 保存订单param
 */
@Data
public class SaveOrderParam implements Serializable {

    //订单项id数组
    private Long[] cartItemIds;

    //地址id
    private Long addressId;
}
