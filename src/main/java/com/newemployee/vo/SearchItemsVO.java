package com.newemployee.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SearchItemsVO implements Serializable {

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
}
