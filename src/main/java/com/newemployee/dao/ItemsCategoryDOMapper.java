package com.newemployee.dao;

import com.newemployee.dataobject.ItemsCategoryDO;
import com.newemployee.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemsCategoryDOMapper {
    int deleteByPrimaryKey(Long categoryId);

    int insert(ItemsCategoryDO record);

    int insertSelective(ItemsCategoryDO record);

    ItemsCategoryDO selectByPrimaryKey(Long categoryId);

    ItemsCategoryDO selectByLevelAndName(@Param("categoryLevel") Byte categoryLevel, @Param("categoryName") String categoryName);

    int updateByPrimaryKeySelective(ItemsCategoryDO record);

    int updateByPrimaryKey(ItemsCategoryDO record);

    List<ItemsCategoryDO> findGoodsCategoryList(PageQueryUtil pageUtil);

    int getTotalGoodsCategories(PageQueryUtil pageUtil);

    int deleteBatch(Long[] ids);

    List<ItemsCategoryDO> selectByLevelAndParentIdsAndNumber(@Param("parentIds") List<Long> parentIds, @Param("categoryLevel") int categoryLevel, @Param("number") int number);
}
