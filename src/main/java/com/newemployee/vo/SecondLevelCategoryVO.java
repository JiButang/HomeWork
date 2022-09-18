package com.newemployee.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

//首页分类数据VO(第二级)
@Data
public class SecondLevelCategoryVO implements Serializable {

    //当前二级分类id
    private Long categoryId;

    //父级分类id
    private Long parentId;

    //当前分类级别
    private Byte categoryLevel;

    //当前二级分类名称
    private String categoryName;

    //三级分类列表
    private List<ThirdLevelCategoryVO> thirdLevelCategoryVOS;
}