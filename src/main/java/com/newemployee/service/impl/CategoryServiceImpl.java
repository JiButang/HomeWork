package com.newemployee.service.impl;

import com.newemployee.common.CategoryLevelEnum;
import com.newemployee.common.Constants;
import com.newemployee.common.ServiceResultEnum;
import com.newemployee.dao.ItemsCategoryDOMapper;
import com.newemployee.dataobject.ItemsCategoryDO;
import com.newemployee.service.CategoryService;
import com.newemployee.util.BeanUtil;
import com.newemployee.util.PageQueryUtil;
import com.newemployee.util.PageResultUtil;
import com.newemployee.vo.IndexCategoryVO;
import com.newemployee.vo.SecondLevelCategoryVO;
import com.newemployee.vo.ThirdLevelCategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private ItemsCategoryDOMapper itemsCategoryDOMapper;

    @Override
    public String saveCategory(ItemsCategoryDO goodsCategory) {
        ItemsCategoryDO temp = itemsCategoryDOMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        if (temp != null) {
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        if (itemsCategoryDOMapper.insertSelective(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateGoodsCategory(ItemsCategoryDO goodsCategory) {
        ItemsCategoryDO temp = itemsCategoryDOMapper.selectByPrimaryKey(goodsCategory.getCategoryId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        ItemsCategoryDO temp2 = itemsCategoryDOMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        if (temp2 != null && !temp2.getCategoryId().equals(goodsCategory.getCategoryId())) {
            //???????????????id ??????????????????
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        goodsCategory.setUpdateTime(new Date());
        if (itemsCategoryDOMapper.updateByPrimaryKeySelective(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public ItemsCategoryDO getGoodsCategoryById(Long id) {
        return itemsCategoryDOMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //??????????????????
        return itemsCategoryDOMapper.deleteBatch(ids) > 0;
    }

    @Override
    public List<IndexCategoryVO> getCategoriesForIndex() {
        List<IndexCategoryVO> indexCategoryVOS = new ArrayList<>();
        //??????????????????????????????????????????
        List<ItemsCategoryDO> firstLevelCategories = itemsCategoryDOMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), CategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            List<Long> firstLevelCategoryIds = firstLevelCategories.stream().map(ItemsCategoryDO::getCategoryId).collect(Collectors.toList());
            //???????????????????????????
            List<ItemsCategoryDO> secondLevelCategories = itemsCategoryDOMapper.selectByLevelAndParentIdsAndNumber(firstLevelCategoryIds, CategoryLevelEnum.LEVEL_TWO.getLevel(), 0);
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                List<Long> secondLevelCategoryIds = secondLevelCategories.stream().map(ItemsCategoryDO::getCategoryId).collect(Collectors.toList());
                //???????????????????????????
                List<ItemsCategoryDO> thirdLevelCategories = itemsCategoryDOMapper.selectByLevelAndParentIdsAndNumber(secondLevelCategoryIds, CategoryLevelEnum.LEVEL_THREE.getLevel(), 0);
                if (!CollectionUtils.isEmpty(thirdLevelCategories)) {
                    //?????? parentId ??? thirdLevelCategories ??????
                    Map<Long, List<ItemsCategoryDO>> thirdLevelCategoryMap = thirdLevelCategories.stream().collect(groupingBy(ItemsCategoryDO::getParentId));
                    List<SecondLevelCategoryVO> secondLevelCategoryVOS = new ArrayList<>();
                    //??????????????????
                    for (ItemsCategoryDO secondLevelCategory : secondLevelCategories) {
                        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                        BeanUtil.copyProperties(secondLevelCategory, secondLevelCategoryVO);
                        //?????????????????????????????????????????? secondLevelCategoryVOS ?????????
                        if (thirdLevelCategoryMap.containsKey(secondLevelCategory.getCategoryId())) {
                            //?????????????????????id??????thirdLevelCategoryMap????????????????????????list
                            List<ItemsCategoryDO> tempGoodsCategories = thirdLevelCategoryMap.get(secondLevelCategory.getCategoryId());
                            secondLevelCategoryVO.setThirdLevelCategoryVOS((BeanUtil.copyList(tempGoodsCategories, ThirdLevelCategoryVO.class)));
                            secondLevelCategoryVOS.add(secondLevelCategoryVO);
                        }
                    }
                    //??????????????????
                    if (!CollectionUtils.isEmpty(secondLevelCategoryVOS)) {
                        //?????? parentId ??? thirdLevelCategories ??????
                        Map<Long, List<SecondLevelCategoryVO>> secondLevelCategoryVOMap = secondLevelCategoryVOS.stream().collect(groupingBy(SecondLevelCategoryVO::getParentId));
                        for (ItemsCategoryDO firstCategory : firstLevelCategories) {
                            IndexCategoryVO indexCategoryVO = new IndexCategoryVO();
                            BeanUtil.copyProperties(firstCategory, indexCategoryVO);
                            //?????????????????????????????????????????? newBeeMallIndexCategoryVOS ?????????
                            if (secondLevelCategoryVOMap.containsKey(firstCategory.getCategoryId())) {
                                //?????????????????????id??????secondLevelCategoryVOMap???????????????????????????list
                                List<SecondLevelCategoryVO> tempGoodsCategories = secondLevelCategoryVOMap.get(firstCategory.getCategoryId());
                                indexCategoryVO.setSecondLevelCategoryVOS(tempGoodsCategories);
                                indexCategoryVOS.add(indexCategoryVO);
                            }
                        }
                    }
                }
            }
            return indexCategoryVOS;
        } else {
            return null;
        }
    }

    @Override
    public PageResultUtil getCategorisPage(PageQueryUtil pageUtil) {
        List<ItemsCategoryDO> goodsCategories = itemsCategoryDOMapper.findGoodsCategoryList(pageUtil);
        int total = itemsCategoryDOMapper.getTotalGoodsCategories(pageUtil);
        PageResultUtil pageResult = new PageResultUtil(goodsCategories, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public List<ItemsCategoryDO> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel) {
        return itemsCategoryDOMapper.selectByLevelAndParentIdsAndNumber(parentIds, categoryLevel, 0);//0??????????????????
    }
}
