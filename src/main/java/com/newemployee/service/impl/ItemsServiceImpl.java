package com.newemployee.service.impl;

import com.newemployee.common.BaseException;
import com.newemployee.common.CategoryLevelEnum;
import com.newemployee.common.ServiceResultEnum;
import com.newemployee.dao.ItemsCategoryDOMapper;
import com.newemployee.dao.ItemsDOMapper;
import com.newemployee.dataobject.ItemsCategoryDO;
import com.newemployee.dataobject.ItemsDO;
import com.newemployee.service.ItemsService;
import com.newemployee.util.BeanUtil;
import com.newemployee.util.PageQueryUtil;
import com.newemployee.util.PageResultUtil;
import com.newemployee.vo.SearchItemsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ItemsServiceImpl implements ItemsService {

    @Autowired
    private ItemsDOMapper itemsDOMapper;

    @Autowired
    private ItemsCategoryDOMapper itemsCategoryDOMapper;

    @Override
    public PageResultUtil getItemsPage(PageQueryUtil pageUtil) {
        List<ItemsDO> goodsList = itemsDOMapper.findNewBeeMallGoodsList(pageUtil);
        int total = itemsDOMapper.getTotalNewBeeMallGoods(pageUtil);
        PageResultUtil pageResult = new PageResultUtil(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveItems(ItemsDO items) {
        ItemsCategoryDO goodsCategory = itemsCategoryDOMapper.selectByPrimaryKey(items.getGoodsCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != CategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        if (itemsDOMapper.selectByCategoryIdAndName(items.getGoodsName(), items.getGoodsCategoryId()) != null) {
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        if (itemsDOMapper.insertSelective(items) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSaveItems(List<ItemsDO> itemsList) {
        if (!CollectionUtils.isEmpty(itemsList)) {
            itemsDOMapper.batchInsert(itemsList);
        }
    }

    @Override
    public String updateItems(ItemsDO items) {
        ItemsCategoryDO goodsCategory = itemsCategoryDOMapper.selectByPrimaryKey(items.getGoodsCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != CategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        ItemsDO temp = itemsDOMapper.selectByPrimaryKey(items.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        ItemsDO temp2 = itemsDOMapper.selectByCategoryIdAndName(items.getGoodsName(), items.getGoodsCategoryId());
        if (temp2 != null && !temp2.getGoodsId().equals(items.getGoodsId())) {
            //name和分类id相同且不同id 不能继续修改
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        items.setUpdateTime(new Date());
        if (itemsDOMapper.updateByPrimaryKeySelective(items) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return itemsDOMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public ItemsDO getItemsById(Long id) {
        ItemsDO Items = itemsDOMapper.selectByPrimaryKey(id);
        if (Items == null) {
            BaseException.toss(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        return Items;
    }

    @Override
    public PageResultUtil searchItems(PageQueryUtil pageUtil) {
        List<ItemsDO> goodsList = itemsDOMapper.findNewBeeMallGoodsListBySearch(pageUtil);
        int total = itemsDOMapper.getTotalNewBeeMallGoodsBySearch(pageUtil);
        List<SearchItemsVO> newBeeMallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            newBeeMallSearchGoodsVOS = BeanUtil.copyList(goodsList, SearchItemsVO.class);
            for (SearchItemsVO newBeeMallSearchGoodsVO : newBeeMallSearchGoodsVOS) {
                String goodsName = newBeeMallSearchGoodsVO.getGoodsName();
                String goodsIntro = newBeeMallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    newBeeMallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    newBeeMallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResultUtil pageResult = new PageResultUtil(newBeeMallSearchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
