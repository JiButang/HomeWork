package com.newemployee.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderItemVO implements Serializable {
    //商品id
    private Long goodsId;

    //商品数量
    private Integer goodsCount;

    //商品名称
    private String goodsName;

    //商品图片
    private String goodsCoverImg;

    //商品价格
    private Integer sellingPrice;
}
