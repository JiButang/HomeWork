package com.newemployee.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IndexCategoryVO implements Serializable {
    //当前一级分类id
    private Long categoryId;

    //当前分类级别
    private Byte categoryLevel;

    //当前一级分类名称
    private String categoryName;

    //二级分类列表
    private List<SecondLevelCategoryVO> secondLevelCategoryVOS;
}
