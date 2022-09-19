package com.newemployee.controller;

import com.newemployee.common.Constants;
import com.newemployee.common.IndexConfigTypeEnum;
import com.newemployee.service.CarouselService;
import com.newemployee.service.IndexConfigService;
import com.newemployee.util.ResultGeneratorUtil;
import com.newemployee.util.ResultUtil;
import com.newemployee.vo.IndexCarouselVO;
import com.newemployee.vo.IndexConfigItemsVO;
import com.newemployee.vo.IndexInfoVO;
import java.util.Date;

import com.newemployee.vo.SeckillVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api")
public class IndexController {

    @Resource
    private CarouselService carouselService;

    @Resource
    private IndexConfigService indexConfigService;

    @GetMapping("/index-infos")
    //获取首页数据, 轮播图、新品、推荐等
    public ResultUtil<IndexInfoVO> indexInfo() {
        IndexInfoVO indexInfoVO = new IndexInfoVO();
        List<IndexCarouselVO> carousels = carouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<IndexConfigItemsVO> hotGoodses = indexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_ITEMS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<IndexConfigItemsVO> newGoodses = indexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_ITEMS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<IndexConfigItemsVO> recommendGoodses = indexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_ITEMS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        indexInfoVO.setCarousels(carousels);
        indexInfoVO.setHotGoodses(hotGoodses);
        indexInfoVO.setNewGoodses(newGoodses);
        indexInfoVO.setRecommendGoodses(recommendGoodses);
        return ResultGeneratorUtil.genSuccessResult(indexInfoVO);
    }

    @GetMapping("/seckill")
    //获取秒杀时间
    public ResultUtil<SeckillVO> getSeckillDate() {
        Date date = new Date();
        SeckillVO seckillVO = new SeckillVO();
        seckillVO.setHours(24-date.getHours());
        seckillVO.setMinutes(60-date.getMinutes());
        seckillVO.setSeconds(60-date.getSeconds());
        seckillVO.setId(10893);
        System.out.println(seckillVO);
        return ResultGeneratorUtil.genSuccessResult(seckillVO);
    }
}
