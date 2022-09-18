package com.newemployee.controller;

import com.newemployee.common.BaseException;
import com.newemployee.common.Constants;
import com.newemployee.common.ServiceResultEnum;
import com.newemployee.config.annotation.TokenToUserDO;
import com.newemployee.controller.param.SaveCartItemParam;
import com.newemployee.controller.param.UpdateCartItemParam;
import com.newemployee.dataobject.ShoppingCartItemDO;
import com.newemployee.dataobject.UserDO;
import com.newemployee.service.ShoppingCartService;
import com.newemployee.util.PageQueryUtil;
import com.newemployee.util.PageResultUtil;
import com.newemployee.util.ResultGeneratorUtil;
import com.newemployee.util.ResultUtil;
import com.newemployee.vo.ShoppingCartItemVO;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService newBeeMallShoppingCartService;

    @GetMapping("/shop-cart/page")
    //购物车列表(每页默认5条), 传参为页码
    public ResultUtil<PageResultUtil<List<ShoppingCartItemVO>>> cartItemPageList(Integer pageNumber, @TokenToUserDO UserDO loginMallUser) {
        Map params = new HashMap(8);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        params.put("userId", loginMallUser.getUserId());
        params.put("page", pageNumber);
        params.put("limit", Constants.SHOPPING_CART_PAGE_LIMIT);
        //封装分页请求参数
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGeneratorUtil.genSuccessResult(newBeeMallShoppingCartService.getMyShoppingCartItems(pageUtil));
    }

    @GetMapping("/shop-cart")
    //购物车列表(网页移动端不分页)
    public ResultUtil<List<ShoppingCartItemVO>> cartItemList(@TokenToUserDO UserDO loginMallUser) {
        return ResultGeneratorUtil.genSuccessResult(newBeeMallShoppingCartService.getMyShoppingCartItems(loginMallUser.getUserId()));
    }

    @PostMapping("/shop-cart")
    //添加商品到购物车接口, 传参为商品id、数量
    public ResultUtil saveNewBeeMallShoppingCartItem(@RequestBody SaveCartItemParam saveCartItemParam,
                                                     @TokenToUserDO UserDO loginMallUser) {
        String saveResult = newBeeMallShoppingCartService.saveNewBeeMallCartItem(saveCartItemParam, loginMallUser.getUserId());
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGeneratorUtil.genSuccessResult();
        }
        //添加失败
        return ResultGeneratorUtil.genFailResult(saveResult);
    }

    @PutMapping("/shop-cart")
    //修改购物项数据, 传参为购物项id、数量
    public ResultUtil updateNewBeeMallShoppingCartItem(@RequestBody UpdateCartItemParam updateCartItemParam,
                                                   @TokenToUserDO UserDO loginMallUser) {
        String updateResult = newBeeMallShoppingCartService.updateNewBeeMallCartItem(updateCartItemParam, loginMallUser.getUserId());
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGeneratorUtil.genSuccessResult();
        }
        //修改失败
        return ResultGeneratorUtil.genFailResult(updateResult);
    }

    @DeleteMapping("/shop-cart/{newBeeMallShoppingCartItemId}")
    //删除购物项, 传参为购物项id
    public ResultUtil updateNewBeeMallShoppingCartItem(@PathVariable("newBeeMallShoppingCartItemId") Long newBeeMallShoppingCartItemId,
                                                   @TokenToUserDO UserDO loginMallUser) {
        ShoppingCartItemDO newBeeMallCartItemById = newBeeMallShoppingCartService.getNewBeeMallCartItemById(newBeeMallShoppingCartItemId);
        if (!loginMallUser.getUserId().equals(newBeeMallCartItemById.getUserId())) {
            return ResultGeneratorUtil.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        Boolean deleteResult = newBeeMallShoppingCartService.deleteById(newBeeMallShoppingCartItemId,loginMallUser.getUserId());
        //删除成功
        if (deleteResult) {
            return ResultGeneratorUtil.genSuccessResult();
        }
        //删除失败
        return ResultGeneratorUtil.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @GetMapping("/shop-cart/settle")
    //根据购物项id数组查询购物项明细, 确认订单页面使用
    public ResultUtil<List<ShoppingCartItemVO>> toSettle(Long[] cartItemIds, @TokenToUserDO UserDO loginMallUser) {
        if (cartItemIds.length < 1) {
            BaseException.toss("参数异常");
        }
        int priceTotal = 0;
        List<ShoppingCartItemVO> itemsForSettle = newBeeMallShoppingCartService.getCartItemsForSettle(Arrays.asList(cartItemIds), loginMallUser.getUserId());
        if (CollectionUtils.isEmpty(itemsForSettle)) {
            //无数据则抛出异常
            BaseException.toss("参数异常");
        } else {
            //总价
            for (ShoppingCartItemVO newBeeMallShoppingCartItemVO : itemsForSettle) {
                priceTotal += newBeeMallShoppingCartItemVO.getGoodsCount() * newBeeMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                BaseException.toss("价格异常");
            }
        }
        return ResultGeneratorUtil.genSuccessResult(itemsForSettle);
    }
}
