package com.newemployee.dao;

import com.newemployee.dataobject.CarouselDO;
import com.newemployee.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarouselDOMapper {
    int deleteByPrimaryKey(Integer carouselId);

    int insert(CarouselDO record);

    int insertSelective(CarouselDO record);

    CarouselDO selectByPrimaryKey(Integer carouselId);

    int updateByPrimaryKeySelective(CarouselDO record);

    int updateByPrimaryKey(CarouselDO record);

    List<CarouselDO> findCarouselList(PageQueryUtil pageUtil);

    int getTotalCarousels(PageQueryUtil pageUtil);

    int deleteBatch(Long[] ids);

    List<CarouselDO> findCarouselsByNum(@Param("number") int number);
}
