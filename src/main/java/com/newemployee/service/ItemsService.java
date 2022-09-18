package com.newemployee.service;

import com.newemployee.dataobject.ItemsDO;
import com.newemployee.util.PageQueryUtil;
import com.newemployee.util.PageResultUtil;

import java.util.List;

public interface ItemsService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResultUtil getItemsPage(PageQueryUtil pageUtil);

    /**
     * 添加商品
     *
     * @param items
     * @return
     */
    String saveItems(ItemsDO items);

    /**
     * 批量新增商品数据
     *
     * @param itemsList
     * @return
     */
    void batchSaveItems(List<ItemsDO> itemsList);

    /**
     * 修改商品信息
     *
     * @param items
     * @return
     */
    String updateItems(ItemsDO items);

    /**
     * 批量修改销售状态(上架下架)
     *
     * @param ids
     * @return
     */
    Boolean batchUpdateSellStatus(Long[] ids, int sellStatus);

    /**
     * 获取商品详情
     *
     * @param id
     * @return
     */
    ItemsDO getItemsById(Long id);

    /**
     * 商品搜索
     *
     * @param pageUtil
     * @return
     */
    PageResultUtil searchItems(PageQueryUtil pageUtil);
}
