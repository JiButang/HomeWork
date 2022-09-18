package com.newemployee.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThirdLevelCategoryVO implements Serializable {

    //当前三级分类id
    private Long categoryId;

    //当前分类级别
    private Byte categoryLevel;

    //当前三级分类名称
    private String categoryName;
}
