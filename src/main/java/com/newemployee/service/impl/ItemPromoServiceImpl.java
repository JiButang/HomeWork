package com.newemployee.service.impl;

import com.newemployee.dao.ItemsDOMapper;
import com.newemployee.dataobject.ItemsDO;
import com.newemployee.service.ItemPromoService;
import com.newemployee.service.PromoService;
import com.newemployee.service.model.ItemModel;
import com.newemployee.service.model.PromoModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class ItemPromoServiceImpl implements ItemPromoService {

    @Autowired
    private ItemsDOMapper itemsDOMapper;

    @Autowired
    private PromoService promoService;

    @Override
    public ItemModel getItemById(Long id) {
        ItemsDO itemsDO = itemsDOMapper.selectByPrimaryKey(id);
        if (itemsDO == null) {
            return null;
        }
        //操作获得库存数量
        Integer itemStockDO = itemsDO.getStockNum();

        //将dataobject-> Model
        ItemModel itemModel = convertModelFromDataObject(itemsDO);

        //获取活动商品信息
        return itemModel;
    }

    private ItemModel convertModelFromDataObject(ItemsDO itemsDO) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemsDO, itemModel);
        //获取活动商品信息
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getGoodsId());
        if (promoModel != null && promoModel.getStatus().intValue() != 3) {
            itemModel.setPromoModel(promoModel);
        }
        return itemModel;
    }
}
