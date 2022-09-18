package com.newemployee.service;

import com.newemployee.dataobject.CarouselDO;
import com.newemployee.util.PageQueryUtil;
import com.newemployee.util.PageResultUtil;
import com.newemployee.vo.IndexCarouselVO;

import java.util.List;

public interface CarouselService {
    /**
     * 返回固定数量的轮播图对象(首页调用)
     *
     * @param number
     * @return
     */
    List<IndexCarouselVO> getCarouselsForIndex(int number);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResultUtil getCarouselPage(PageQueryUtil pageUtil);

    String saveCarousel(CarouselDO carousel);

    String updateCarousel(CarouselDO carousel);

    CarouselDO getCarouselById(Integer id);

    Boolean deleteBatch(Long[] ids);
}
