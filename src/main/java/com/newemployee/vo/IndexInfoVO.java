package com.newemployee.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IndexInfoVO implements Serializable {

    //轮播图(列表)
    private List<IndexCarouselVO> carousels;

    //首页热销商品(列表)
    private List<IndexConfigItemsVO> hotGoodses;

    //首页新品推荐(列表)
    private List<IndexConfigItemsVO> newGoodses;

    //首页推荐商品(列表)
    private List<IndexConfigItemsVO> recommendGoodses;
}
