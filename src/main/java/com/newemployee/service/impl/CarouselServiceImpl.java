package com.newemployee.service.impl;

import com.newemployee.common.ServiceResultEnum;
import com.newemployee.dao.CarouselDOMapper;
import com.newemployee.dataobject.CarouselDO;
import com.newemployee.service.CarouselService;
import com.newemployee.util.BeanUtil;
import com.newemployee.util.PageQueryUtil;
import com.newemployee.util.PageResultUtil;
import com.newemployee.vo.IndexCarouselVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CarouselServiceImpl implements CarouselService {

    @Autowired
    private CarouselDOMapper carouselDOMapper;


    @Override
    public PageResultUtil getCarouselPage(PageQueryUtil pageQueryUtil) {
        List<CarouselDO> carouselDOs = carouselDOMapper.findCarouselList(pageQueryUtil);
        int total = carouselDOMapper.getTotalCarousels(pageQueryUtil);
        PageResultUtil pageResult = new PageResultUtil(carouselDOs, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveCarousel(CarouselDO carouselDO) {
        if (carouselDOMapper.insertSelective(carouselDO) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateCarousel(CarouselDO carouselDO) {
        CarouselDO temp = carouselDOMapper.selectByPrimaryKey(carouselDO.getCarouselId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        temp.setCarouselRank(carouselDO.getCarouselRank());
        temp.setRedirectUrl(carouselDO.getRedirectUrl());
        temp.setCarouselUrl(carouselDO.getCarouselUrl());
        temp.setUpdateTime(new Date());
        if (carouselDOMapper.updateByPrimaryKeySelective(temp) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public CarouselDO getCarouselById(Integer id) {
        return carouselDOMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return carouselDOMapper.deleteBatch(ids) > 0;
    }

    @Override
    public List<IndexCarouselVO> getCarouselsForIndex(int number) {
        List<IndexCarouselVO> indexCarouselVOS = new ArrayList<>(number);
        List<CarouselDO> carouselDOs = carouselDOMapper.findCarouselsByNum(number);
        if (!CollectionUtils.isEmpty(carouselDOs)) {
            indexCarouselVOS = BeanUtil.copyList(carouselDOs, IndexCarouselVO.class);
        }
        return indexCarouselVOS;
    }
}
