package com.newemployee.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemsPromoVO {

    //商品id
    private Long goodsId;

    //商品名称
    private String goodsName;

    //商品简介
    private String goodsIntro;

    //商品图片地址
    private String goodsCoverImg;

    //商品价格
    private Integer sellingPrice;

    //商品标签
    private String tag;

    //商品图片
    private String[] goodsCarouselList;

    //商品原价
    private Integer originalPrice;

    //商品详情字段
    private String goodsDetailContent;

    //商品是否在秒杀活动中，以及对应的状态：0表示没有秒杀活动，1表示秒杀活动等待开始，2表示进行中
    private Integer promoStatus;

    //秒杀活动价格
    private BigDecimal promoPrice;

    //秒杀活动id
    private Integer promoId;

    //秒杀活动开始时间
    private String startDate;
}
