package com.newemployee.vo;

import lombok.Data;

@Data
public class ItemsDetailVO {

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
}
