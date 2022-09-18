package com.newemployee.controller;

import com.newemployee.common.BaseException;
import com.newemployee.common.Constants;
import com.newemployee.common.ServiceResultEnum;
import com.newemployee.config.annotation.TokenToUserDO;
import com.newemployee.dataobject.ItemsDO;
import com.newemployee.dataobject.UserDO;
import com.newemployee.service.ItemsService;
import com.newemployee.util.*;
import com.newemployee.vo.ItemsDetailVO;
import com.newemployee.vo.SearchItemsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ItemsController {

    private static final Logger logger = LoggerFactory.getLogger(ItemsController.class);

    @Resource
    private ItemsService newBeeMallGoodsService;

    @GetMapping("/search")
    //商品搜索接口, 根据关键字和分类id进行搜索
    public ResultUtil<PageResultUtil<List<SearchItemsVO>>> search(@RequestParam(required = false) String keyword,
                                                                  @RequestParam(required = false) Long goodsCategoryId,
                                                                  @RequestParam(required = false) String orderBy,
                                                                  @RequestParam(required = false) Integer pageNumber,
                                                                  @TokenToUserDO UserDO loginMallUser) {
        
        logger.info("goods search api,keyword={},goodsCategoryId={},orderBy={},pageNumber={},userId={}", keyword, goodsCategoryId, orderBy, pageNumber, loginMallUser.getUserId());

        Map params = new HashMap(8);
        //两个搜索参数都为空，直接返回异常
        if (goodsCategoryId == null && StringUtils.isEmpty(keyword)) {
            BaseException.toss("非法的搜索参数");
        }
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        params.put("goodsCategoryId", goodsCategoryId);
        params.put("page", pageNumber);
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);
        //对keyword做过滤 去掉空格
        if (!StringUtils.isEmpty(keyword)) {
            params.put("keyword", keyword);
        }
        if (!StringUtils.isEmpty(orderBy)) {
            params.put("orderBy", orderBy);
        }
        //搜索上架状态下的商品
        params.put("goodsSellStatus", Constants.SELL_STATUS_UP);
        //封装商品数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGeneratorUtil.genSuccessResult(newBeeMallGoodsService.searchItems(pageUtil));
    }

    @GetMapping("/goods/detail/{goodsId}")
    //商品详情接口, 传参为商品id
    public ResultUtil<ItemsDetailVO> goodsDetail(@PathVariable("goodsId") Long goodsId, @TokenToUserDO UserDO loginMallUser) {
        logger.info("goods detail api,goodsId={},userId={}", goodsId, loginMallUser.getUserId());
        if (goodsId < 1) {
            return ResultGeneratorUtil.genFailResult("参数异常");
        }
        ItemsDO goods = newBeeMallGoodsService.getItemsById(goodsId);
        if (Constants.SELL_STATUS_UP != goods.getGoodsSellStatus()) {
            BaseException.toss(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
        }
        ItemsDetailVO goodsDetailVO = new ItemsDetailVO();
        BeanUtil.copyProperties(goods, goodsDetailVO);
        goodsDetailVO.setGoodsCarouselList(goods.getGoodsCarousel().split(","));
        return ResultGeneratorUtil.genSuccessResult(goodsDetailVO);
    }

}
