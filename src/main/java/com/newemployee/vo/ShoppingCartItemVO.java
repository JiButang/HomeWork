package com.newemployee.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShoppingCartItemVO implements Serializable {
    //购物项id
    private Long cartItemId;

    //商品id
    private Long itemsId;

    //商品数量
    private Integer itemsCount;

    //商品名称
    private String itemsName;

    //商品图片
    private String itemsCoverImg;

    //商品价格
    private Integer sellingPrice;
}
