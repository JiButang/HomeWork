package com.newemployee.service.impl;

import com.newemployee.common.BaseException;
import com.newemployee.common.Constants;
import com.newemployee.common.ServiceResultEnum;
import com.newemployee.controller.param.SaveCartItemParam;
import com.newemployee.controller.param.UpdateCartItemParam;
import com.newemployee.dao.ItemsDOMapper;
import com.newemployee.dao.ShoppingCartItemDOMapper;
import com.newemployee.dataobject.ItemsDO;
import com.newemployee.dataobject.ShoppingCartItemDO;
import com.newemployee.service.ShoppingCartService;
import com.newemployee.util.BeanUtil;
import com.newemployee.util.PageQueryUtil;
import com.newemployee.util.PageResultUtil;
import com.newemployee.vo.ShoppingCartItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartItemDOMapper shoppingCartItemDOMapper;

    @Autowired
    private ItemsDOMapper itemsDOMapper;

    @Override
    public String saveNewBeeMallCartItem(SaveCartItemParam saveCartItemParam, Long userId) {
        ShoppingCartItemDO temp = shoppingCartItemDOMapper.selectByUserIdAndGoodsId(userId, saveCartItemParam.getGoodsId());
        if (temp != null) {
            //已存在则修改该记录
            BaseException.toss(ServiceResultEnum.SHOPPING_CART_ITEM_EXIST_ERROR.getResult());
        }
        ItemsDO newBeeMallGoods = itemsDOMapper.selectByPrimaryKey(saveCartItemParam.getGoodsId());
        //商品为空
        if (newBeeMallGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = shoppingCartItemDOMapper.selectCountByUserId(userId);
        //超出单个商品的最大数量
        if (saveCartItemParam.getGoodsCount() < 1) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_NUMBER_ERROR.getResult();
        }
        //超出单个商品的最大数量
        if (saveCartItemParam.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //超出最大数量
        if (totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        ShoppingCartItemDO newBeeMallShoppingCartItem = new ShoppingCartItemDO();
        BeanUtil.copyProperties(saveCartItemParam, newBeeMallShoppingCartItem);
        newBeeMallShoppingCartItem.setUserId(userId);
        //保存记录
        if (shoppingCartItemDOMapper.insertSelective(newBeeMallShoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateNewBeeMallCartItem(UpdateCartItemParam updateCartItemParam, Long userId) {
        ShoppingCartItemDO newBeeMallShoppingCartItemUpdate = shoppingCartItemDOMapper.selectByPrimaryKey(updateCartItemParam.getCartItemId());
        if (newBeeMallShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (!newBeeMallShoppingCartItemUpdate.getUserId().equals(userId)) {
            BaseException.toss(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        //超出单个商品的最大数量
        if (updateCartItemParam.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //当前登录账号的userId与待修改的cartItem中userId不同，返回错误
        if (!newBeeMallShoppingCartItemUpdate.getUserId().equals(userId)) {
            return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
        }
        //数值相同，则不执行数据操作
        if (updateCartItemParam.getGoodsCount().equals(newBeeMallShoppingCartItemUpdate.getGoodsCount())) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        newBeeMallShoppingCartItemUpdate.setGoodsCount(updateCartItemParam.getGoodsCount());
        newBeeMallShoppingCartItemUpdate.setUpdateTime(new Date());
        //修改记录
        if (shoppingCartItemDOMapper.updateByPrimaryKeySelective(newBeeMallShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public ShoppingCartItemDO getNewBeeMallCartItemById(Long newBeeMallShoppingCartItemId) {
        ShoppingCartItemDO newBeeMallShoppingCartItem = shoppingCartItemDOMapper.selectByPrimaryKey(newBeeMallShoppingCartItemId);
        if (newBeeMallShoppingCartItem == null) {
            BaseException.toss(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return newBeeMallShoppingCartItem;
    }

    @Override
    public Boolean deleteById(Long shoppingCartItemId, Long userId) {
        ShoppingCartItemDO newBeeMallShoppingCartItem = shoppingCartItemDOMapper.selectByPrimaryKey(shoppingCartItemId);
        if (newBeeMallShoppingCartItem == null) {
            return false;
        }
        //userId不同不能删除
        if (!userId.equals(newBeeMallShoppingCartItem.getUserId())) {
            return false;
        }
        return shoppingCartItemDOMapper.deleteByPrimaryKey(shoppingCartItemId) > 0;
    }

    @Override
    public List<ShoppingCartItemVO> getMyShoppingCartItems(Long newBeeMallUserId) {
        List<ShoppingCartItemVO> newBeeMallShoppingCartItemVOS = new ArrayList<>();
        List<ShoppingCartItemDO> newBeeMallShoppingCartItems = shoppingCartItemDOMapper.selectByUserId(newBeeMallUserId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        return getNewBeeMallShoppingCartItemVOS(newBeeMallShoppingCartItemVOS, newBeeMallShoppingCartItems);
    }

    @Override
    public List<ShoppingCartItemVO> getCartItemsForSettle(List<Long> cartItemIds, Long newBeeMallUserId) {
        List<ShoppingCartItemVO> newBeeMallShoppingCartItemVOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(cartItemIds)) {
            BaseException.toss("购物项不能为空");
        }
        List<ShoppingCartItemDO> newBeeMallShoppingCartItems = shoppingCartItemDOMapper.selectByUserIdAndCartItemIds(newBeeMallUserId, cartItemIds);
        if (CollectionUtils.isEmpty(newBeeMallShoppingCartItems)) {
            BaseException.toss("购物项不能为空");
        }
        if (newBeeMallShoppingCartItems.size() != cartItemIds.size()) {
            BaseException.toss("参数异常");
        }
        return getNewBeeMallShoppingCartItemVOS(newBeeMallShoppingCartItemVOS, newBeeMallShoppingCartItems);
    }

    /**
     * 数据转换
     *
     * @param newBeeMallShoppingCartItemVOS
     * @param newBeeMallShoppingCartItems
     * @return
     */
    private List<ShoppingCartItemVO> getNewBeeMallShoppingCartItemVOS(List<ShoppingCartItemVO> newBeeMallShoppingCartItemVOS, List<ShoppingCartItemDO> newBeeMallShoppingCartItems) {
        if (!CollectionUtils.isEmpty(newBeeMallShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Long> newBeeMallGoodsIds = newBeeMallShoppingCartItems.stream().map(ShoppingCartItemDO::getGoodsId).collect(Collectors.toList());
            List<ItemsDO> newBeeMallGoods = itemsDOMapper.selectByPrimaryKeys(newBeeMallGoodsIds);
            Map<Long, ItemsDO> newBeeMallGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(newBeeMallGoods)) {
                newBeeMallGoodsMap = newBeeMallGoods.stream().collect(Collectors.toMap(ItemsDO::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            }
            for (ShoppingCartItemDO newBeeMallShoppingCartItem : newBeeMallShoppingCartItems) {
                ShoppingCartItemVO newBeeMallShoppingCartItemVO = new ShoppingCartItemVO();
                BeanUtil.copyProperties(newBeeMallShoppingCartItem, newBeeMallShoppingCartItemVO);
                if (newBeeMallGoodsMap.containsKey(newBeeMallShoppingCartItem.getGoodsId())) {
                    ItemsDO newBeeMallGoodsTemp = newBeeMallGoodsMap.get(newBeeMallShoppingCartItem.getGoodsId());
                        newBeeMallShoppingCartItemVO.setItemsCoverImg(newBeeMallGoodsTemp.getGoodsCoverImg());
                    String goodsName = newBeeMallGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    newBeeMallShoppingCartItemVO.setItemsName(goodsName);
                    newBeeMallShoppingCartItemVO.setSellingPrice(newBeeMallGoodsTemp.getSellingPrice());
                    newBeeMallShoppingCartItemVOS.add(newBeeMallShoppingCartItemVO);
                }
            }
        }
        return newBeeMallShoppingCartItemVOS;
    }

    @Override
    public PageResultUtil getMyShoppingCartItems(PageQueryUtil pageUtil) {
        List<ShoppingCartItemVO> newBeeMallShoppingCartItemVOS = new ArrayList<>();
        List<ShoppingCartItemDO> newBeeMallShoppingCartItems = shoppingCartItemDOMapper.findMyNewBeeMallCartItems(pageUtil);
        int total = shoppingCartItemDOMapper.getTotalMyNewBeeMallCartItems(pageUtil);
        PageResultUtil pageResult = new PageResultUtil(getNewBeeMallShoppingCartItemVOS(newBeeMallShoppingCartItemVOS, newBeeMallShoppingCartItems), total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
