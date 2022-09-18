package com.newemployee.service;

import com.newemployee.dataobject.ItemsCategoryDO;
import com.newemployee.util.PageQueryUtil;
import com.newemployee.util.PageResultUtil;
import com.newemployee.vo.IndexCategoryVO;

import java.util.List;

public interface CategoryService {
    String saveCategory(ItemsCategoryDO goodsCategory);

    String updateGoodsCategory(ItemsCategoryDO goodsCategory);

    ItemsCategoryDO getGoodsCategoryById(Long id);

    Boolean deleteBatch(Long[] ids);

    /**
     * 返回分类数据(首页调用)
     *
     * @return
     */
    List<IndexCategoryVO> getCategoriesForIndex();

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResultUtil getCategorisPage(PageQueryUtil pageUtil);

    /**
     * 根据parentId和level获取分类列表
     *
     * @param parentIds
     * @param categoryLevel
     * @return
     */
    List<ItemsCategoryDO> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel);
}
