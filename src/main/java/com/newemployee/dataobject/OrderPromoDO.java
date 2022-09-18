package com.newemployee.dataobject;

import java.math.BigDecimal;
import java.util.Date;

public class OrderPromoDO {

    private Long orderItemId;

    private Long orderId;

    private Long goodsId;

    private String goodsName;

    private String goodsCoverImg;

    private Integer sellingPrice;

    private Integer goodsCount;

    private Date createTime;

    //若非空，则表示是以秒杀商品方式下单
    private Integer promoId;

    //购买时商品的单价,若promoId非空，则表示是以秒杀商品方式下单
    private BigDecimal promoItemPrice;
}
