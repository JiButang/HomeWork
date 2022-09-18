package com.newemployee.dao;

import com.newemployee.dataobject.ItemsDO;
import com.newemployee.dataobject.StockNumDTO;
import com.newemployee.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemsDOMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(ItemsDO record);

    int insertSelective(ItemsDO record);

    ItemsDO selectByPrimaryKey(Long goodsId);

    ItemsDO selectByCategoryIdAndName(@Param("goodsName") String goodsName, @Param("goodsCategoryId") Long goodsCategoryId);

    int updateByPrimaryKeySelective(ItemsDO record);

    int updateByPrimaryKeyWithBLOBs(ItemsDO record);

    int updateByPrimaryKey(ItemsDO record);

    List<ItemsDO> findNewBeeMallGoodsList(PageQueryUtil pageUtil);

    int getTotalNewBeeMallGoods(PageQueryUtil pageUtil);

    List<ItemsDO> selectByPrimaryKeys(List<Long> goodsIds);

    List<ItemsDO> findNewBeeMallGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalNewBeeMallGoodsBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("newBeeMallGoodsList") List<ItemsDO> newBeeMallGoodsList);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int batchUpdateSellStatus(@Param("orderIds")Long[] orderIds,@Param("sellStatus") int sellStatus);
}
