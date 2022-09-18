package com.newemployee.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class OrderDetailVO implements Serializable {

    //订单号
    private String orderNo;

    //订单价格
    private Integer totalPrice;

    //订单支付状态码
    private Byte payStatus;

    //订单支付方式
    private Byte payType;

    //订单支付方式
    private String payTypeString;

    //订单支付时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;

    //订单状态码
    private Byte orderStatus;

    //订单状态
    private String orderStatusString;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    //订单项列表
    private List<OrderItemVO> OrderItemVOs;
}
