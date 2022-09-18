package com.newemployee.controller;

import com.newemployee.common.BaseException;
import com.newemployee.common.Constants;
import com.newemployee.common.ServiceResultEnum;
import com.newemployee.config.annotation.TokenToUserDO;
import com.newemployee.controller.param.SaveOrderParam;
import com.newemployee.dataobject.UserAddressDO;
import com.newemployee.dataobject.UserDO;
import com.newemployee.service.OrderService;
import com.newemployee.service.ShoppingCartService;
import com.newemployee.service.UserAddressService;
import com.newemployee.util.PageQueryUtil;
import com.newemployee.util.PageResultUtil;
import com.newemployee.util.ResultGeneratorUtil;
import com.newemployee.util.ResultUtil;
import com.newemployee.vo.OrderDetailVO;
import com.newemployee.vo.OrderListVO;
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
public class OrderController {

    @Resource
    private ShoppingCartService shoppingCartService;
    @Resource
    private OrderService orderService;
    @Resource
    private UserAddressService userAddressService;

    @PostMapping("/saveOrder")
    //生成订单接口, 传参为地址id和待结算的购物项id数组
    public ResultUtil<String> saveOrder(@RequestBody SaveOrderParam saveOrderParam, @TokenToUserDO UserDO loginMallUser) {
        int priceTotal = 0;
        if (saveOrderParam == null || saveOrderParam.getCartItemIds() == null || saveOrderParam.getAddressId() == null) {
            BaseException.toss(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        if (saveOrderParam.getCartItemIds().length < 1) {
            BaseException.toss(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        List<ShoppingCartItemVO> itemsForSave = shoppingCartService.getCartItemsForSettle(Arrays.asList(saveOrderParam.getCartItemIds()), loginMallUser.getUserId());
        if (CollectionUtils.isEmpty(itemsForSave)) {
            //无数据
            BaseException.toss("参数异常");
        } else {
            //总价
            for (ShoppingCartItemVO newBeeMallShoppingCartItemVO : itemsForSave) {
                priceTotal += newBeeMallShoppingCartItemVO.getGoodsCount() * newBeeMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                BaseException.toss("价格异常");
            }
            UserAddressDO address = userAddressService.getMallUserAddressById(saveOrderParam.getAddressId());
            if (!loginMallUser.getUserId().equals(address.getUserId())) {
                return ResultGeneratorUtil.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
            }
            //保存订单并返回订单号
            String saveOrderResult = orderService.saveOrder(loginMallUser, address, itemsForSave);
            ResultUtil result = ResultGeneratorUtil.genSuccessResult();
            result.setData(saveOrderResult);
            return result;
        }
        return ResultGeneratorUtil.genFailResult("生成订单失败");
    }

    @GetMapping("/order/{orderNo}")
    //订单详情接口,传参为订单号
    public ResultUtil<OrderDetailVO> orderDetailPage(@PathVariable("orderNo") String orderNo, @TokenToUserDO UserDO loginMallUser) {
        return ResultGeneratorUtil.genSuccessResult(orderService.getOrderDetailByOrderNo(orderNo, loginMallUser.getUserId()));
    }

    @GetMapping("/order")
    //订单列表接口, 传参为页码
    public ResultUtil<PageResultUtil<List<OrderListVO>>> orderList(@RequestParam(required = false) Integer pageNumber,
                                                                   @RequestParam(required = false) Integer status,
                                                                   @TokenToUserDO UserDO loginMallUser) {
        Map params = new HashMap(8);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        params.put("userId", loginMallUser.getUserId());
        params.put("orderStatus", status);
        params.put("page", pageNumber);
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        //封装分页请求参数
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGeneratorUtil.genSuccessResult(orderService.getMyOrders(pageUtil));
    }

    @PutMapping("/order/{orderNo}/cancel")
    //订单取消接口, 传参为订单号
    public ResultUtil cancelOrder(@PathVariable("orderNo") String orderNo, @TokenToUserDO UserDO loginMallUser) {
        String cancelOrderResult = orderService.cancelOrder(orderNo, loginMallUser.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(cancelOrderResult)) {
            return ResultGeneratorUtil.genSuccessResult();
        } else {
            return ResultGeneratorUtil.genFailResult(cancelOrderResult);
        }
    }

    @PutMapping("/order/{orderNo}/finish")
    //确认收货接口, 传参为订单号
    public ResultUtil finishOrder(@PathVariable("orderNo") String orderNo, @TokenToUserDO UserDO loginMallUser) {
        String finishOrderResult = orderService.finishOrder(orderNo, loginMallUser.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)) {
            return ResultGeneratorUtil.genSuccessResult();
        } else {
            return ResultGeneratorUtil.genFailResult(finishOrderResult);
        }
    }

    @GetMapping("/paySuccess")
    //模拟支付成功回调的接口, 传参为订单号和支付方式
    public ResultUtil paySuccess(@RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType) {
        String payResult = orderService.paySuccess(orderNo, payType);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            return ResultGeneratorUtil.genSuccessResult();
        } else {
            return ResultGeneratorUtil.genFailResult(payResult);
        }
    }

}
