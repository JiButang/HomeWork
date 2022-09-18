package com.newemployee.dao;

import com.newemployee.dataobject.ShoppingCartItemDO;
import com.newemployee.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShoppingCartItemDOMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(ShoppingCartItemDO record);

    int insertSelective(ShoppingCartItemDO record);

    ShoppingCartItemDO selectByPrimaryKey(Long cartItemId);

    ShoppingCartItemDO selectByUserIdAndGoodsId(@Param("newBeeMallUserId") Long newBeeMallUserId, @Param("goodsId") Long goodsId);

    List<ShoppingCartItemDO> selectByUserId(@Param("newBeeMallUserId") Long newBeeMallUserId, @Param("number") int number);

    List<ShoppingCartItemDO> selectByUserIdAndCartItemIds(@Param("newBeeMallUserId") Long newBeeMallUserId, @Param("cartItemIds") List<Long> cartItemIds);

    int selectCountByUserId(Long newBeeMallUserId);

    int updateByPrimaryKeySelective(ShoppingCartItemDO record);

    int updateByPrimaryKey(ShoppingCartItemDO record);

    int deleteBatch(List<Long> ids);

    List<ShoppingCartItemDO> findMyNewBeeMallCartItems(PageQueryUtil pageUtil);

    int getTotalMyNewBeeMallCartItems(PageQueryUtil pageUtil);
}
