package com.newemployee.service.impl;

import com.newemployee.common.*;
import com.newemployee.dao.*;
import com.newemployee.dataobject.*;
import com.newemployee.service.OrderService;
import com.newemployee.util.BeanUtil;
import com.newemployee.util.NumberUtil;
import com.newemployee.util.PageQueryUtil;
import com.newemployee.util.PageResultUtil;
import com.newemployee.vo.OrderDetailVO;
import com.newemployee.vo.OrderItemVO;
import com.newemployee.vo.OrderListVO;
import com.newemployee.vo.ShoppingCartItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDOMapper orderDOMapper;
    @Autowired
    private OrderItemDOMapper orderItemDOMapper;
    @Autowired
    private ShoppingCartItemDOMapper shoppingCartItemDOMapper;
    @Autowired
    private ItemsDOMapper itemsDOMapper;
    @Autowired
    private OrderAddressDOMapper orderAddressDOMapper;

    @Override
    public OrderDetailVO getOrderDetailByOrderId(Long orderId) {
        OrderDO newBeeMallOrder = orderDOMapper.selectByPrimaryKey(orderId);
        if (newBeeMallOrder == null) {
            BaseException.toss(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        List<OrderItemDO> orderItems = orderItemDOMapper.selectByOrderId(newBeeMallOrder.getOrderId());
        //获取订单项数据
        if (!CollectionUtils.isEmpty(orderItems)) {
            List<OrderItemVO> newBeeMallOrderItemVOS = BeanUtil.copyList(orderItems, OrderItemVO.class);
            OrderDetailVO newBeeMallOrderDetailVO = new OrderDetailVO();
            BeanUtil.copyProperties(newBeeMallOrder, newBeeMallOrderDetailVO);
            newBeeMallOrderDetailVO.setOrderStatusString(OrderStatusEnum.getOrderStatusEnumByStatus(newBeeMallOrderDetailVO.getOrderStatus()).getMsg());
            newBeeMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(newBeeMallOrderDetailVO.getPayType()).getMsg());
            newBeeMallOrderDetailVO.setOrderItemVOs(newBeeMallOrderItemVOS);
            return newBeeMallOrderDetailVO;
        } else {
            BaseException.toss(ServiceResultEnum.ORDER_ITEM_NULL_ERROR.getResult());
            return null;
        }
    }

    @Override
    public OrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        OrderDO newBeeMallOrder = orderDOMapper.selectByOrderNo(orderNo);
        if (newBeeMallOrder == null) {
            BaseException.toss(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        if (!userId.equals(newBeeMallOrder.getUserId())) {
            BaseException.toss(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        List<OrderItemDO> orderItems = orderItemDOMapper.selectByOrderId(newBeeMallOrder.getOrderId());
        //获取订单项数据
        if (CollectionUtils.isEmpty(orderItems)) {
            BaseException.toss(ServiceResultEnum.ORDER_ITEM_NOT_EXIST_ERROR.getResult());
        }
        List<OrderItemVO> newBeeMallOrderItemVOS = BeanUtil.copyList(orderItems, OrderItemVO.class);
        OrderDetailVO newBeeMallOrderDetailVO = new OrderDetailVO();
        BeanUtil.copyProperties(newBeeMallOrder, newBeeMallOrderDetailVO);
        newBeeMallOrderDetailVO.setOrderStatusString(OrderStatusEnum.getOrderStatusEnumByStatus(newBeeMallOrderDetailVO.getOrderStatus()).getMsg());
        newBeeMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(newBeeMallOrderDetailVO.getPayType()).getMsg());
        newBeeMallOrderDetailVO.setOrderItemVOs(newBeeMallOrderItemVOS);
        return newBeeMallOrderDetailVO;
    }


    @Override
    public PageResultUtil getMyOrders(PageQueryUtil pageUtil) {
        int total = orderDOMapper.getTotalNewBeeMallOrders(pageUtil);
        List<OrderDO> newBeeMallOrders = orderDOMapper.findNewBeeMallOrderList(pageUtil);
        List<OrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = BeanUtil.copyList(newBeeMallOrders, OrderListVO.class);
            //设置订单状态中文显示值
            for (OrderListVO newBeeMallOrderListVO : orderListVOS) {
                newBeeMallOrderListVO.setOrderStatusString(OrderStatusEnum.getOrderStatusEnumByStatus(newBeeMallOrderListVO.getOrderStatus()).getMsg());
            }
            List<Long> orderIds = newBeeMallOrders.stream().map(OrderDO::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<OrderItemDO> orderItems = orderItemDOMapper.selectByOrderIds(orderIds);
                Map<Long, List<OrderItemDO>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(OrderItemDO::getOrderId));
                for (OrderListVO newBeeMallOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(newBeeMallOrderListVO.getOrderId())) {
                        List<OrderItemDO> orderItemListTemp = itemByOrderIdMap.get(newBeeMallOrderListVO.getOrderId());
                        //将NewBeeMallOrderItem对象列表转换成NewBeeMallOrderItemVO对象列表
                        List<OrderItemVO> newBeeMallOrderItemVOS = BeanUtil.copyList(orderItemListTemp, OrderItemVO.class);
                        newBeeMallOrderListVO.setNewBeeMallOrderItemVOS(newBeeMallOrderItemVOS);
                    }
                }
            }
        }
        PageResultUtil pageResult = new PageResultUtil(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        OrderDO newBeeMallOrder = orderDOMapper.selectByOrderNo(orderNo);
        if (newBeeMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(newBeeMallOrder.getUserId())) {
                BaseException.toss(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
            }
            //订单状态判断
            if (newBeeMallOrder.getOrderStatus().intValue() == OrderStatusEnum.ORDER_SUCCESS.getOrderStatus()
                    || newBeeMallOrder.getOrderStatus().intValue() == OrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()
                    || newBeeMallOrder.getOrderStatus().intValue() == OrderStatusEnum.ORDER_CLOSED_BY_EXPIRED.getOrderStatus()
                    || newBeeMallOrder.getOrderStatus().intValue() == OrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            if (orderDOMapper.closeOrder(Collections.singletonList(newBeeMallOrder.getOrderId()), OrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        OrderDO newBeeMallOrder = orderDOMapper.selectByOrderNo(orderNo);
        if (newBeeMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(newBeeMallOrder.getUserId())) {
                return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
            }
            //订单状态判断 非出库状态下不进行修改操作
            if (newBeeMallOrder.getOrderStatus().intValue() != OrderStatusEnum.ORDER_EXPRESS.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            newBeeMallOrder.setOrderStatus((byte) OrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            newBeeMallOrder.setUpdateTime(new Date());
            if (orderDOMapper.updateByPrimaryKeySelective(newBeeMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        OrderDO newBeeMallOrder = orderDOMapper.selectByOrderNo(orderNo);
        if (newBeeMallOrder != null) {
            //订单状态判断 非待支付状态下不进行修改操作
            if (newBeeMallOrder.getOrderStatus().intValue() != OrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            newBeeMallOrder.setOrderStatus((byte) OrderStatusEnum.ORDER_PAID.getOrderStatus());
            newBeeMallOrder.setPayType((byte) payType);
            newBeeMallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            newBeeMallOrder.setPayTime(new Date());
            newBeeMallOrder.setUpdateTime(new Date());
            if (orderDOMapper.updateByPrimaryKeySelective(newBeeMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    @Transactional
    public String saveOrder(UserDO loginMallUser, UserAddressDO address, List<ShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(ShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(ShoppingCartItemVO::getItemsId).collect(Collectors.toList());
        List<ItemsDO> newBeeMallGoods = itemsDOMapper.selectByPrimaryKeys(goodsIds);
        //检查是否包含已下架商品
        List<ItemsDO> goodsListNotSelling = newBeeMallGoods.stream()
                .filter(newBeeMallGoodsTemp -> newBeeMallGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            //goodsListNotSelling 对象非空则表示有下架商品
            BaseException.toss(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }
        Map<Long, ItemsDO> newBeeMallGoodsMap = newBeeMallGoods.stream().collect(Collectors.toMap(ItemsDO::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (ShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!newBeeMallGoodsMap.containsKey(shoppingCartItemVO.getItemsId())) {
                BaseException.toss(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getItemsCount() > newBeeMallGoodsMap.get(shoppingCartItemVO.getItemsId()).getStockNum()) {
                BaseException.toss(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(newBeeMallGoods)) {
            if (shoppingCartItemDOMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = itemsDOMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    BaseException.toss(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                OrderDO newBeeMallOrder = new OrderDO();
                newBeeMallOrder.setOrderNo(orderNo);
                newBeeMallOrder.setUserId(loginMallUser.getUserId());
                //总价
                for (ShoppingCartItemVO newBeeMallShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += newBeeMallShoppingCartItemVO.getItemsCount() * newBeeMallShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    BaseException.toss(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                newBeeMallOrder.setTotalPrice(priceTotal);
                String extraInfo = "";
                newBeeMallOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (orderDOMapper.insertSelective(newBeeMallOrder) > 0) {
                    //生成订单收货地址快照，并保存至数据库
                    OrderAddressDO newBeeMallOrderAddress = new OrderAddressDO();
                    BeanUtil.copyProperties(address, newBeeMallOrderAddress);
                    newBeeMallOrderAddress.setOrderId(newBeeMallOrder.getOrderId());
                    //生成所有的订单项快照，并保存至数据库
                    List<OrderItemDO> newBeeMallOrderItems = new ArrayList<>();
                    for (ShoppingCartItemVO newBeeMallShoppingCartItemVO : myShoppingCartItems) {
                        OrderItemDO newBeeMallOrderItem = new OrderItemDO();
                        //使用BeanUtil工具类将newBeeMallShoppingCartItemVO中的属性复制到newBeeMallOrderItem对象中
                        BeanUtil.copyProperties(newBeeMallShoppingCartItemVO, newBeeMallOrderItem);
                        //NewBeeMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        newBeeMallOrderItem.setOrderId(newBeeMallOrder.getOrderId());
                        newBeeMallOrderItems.add(newBeeMallOrderItem);
                    }
                    //保存至数据库
                    if (orderItemDOMapper.insertBatch(newBeeMallOrderItems) > 0 && orderAddressDOMapper.insertSelective(newBeeMallOrderAddress) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    BaseException.toss(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                BaseException.toss(ServiceResultEnum.DB_ERROR.getResult());
            }
            BaseException.toss(ServiceResultEnum.DB_ERROR.getResult());
        }
        BaseException.toss(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }


    @Override
    public PageResultUtil getNewBeeMallOrdersPage(PageQueryUtil pageUtil) {
        List<OrderDO> newBeeMallOrders = orderDOMapper.findNewBeeMallOrderList(pageUtil);
        int total = orderDOMapper.getTotalNewBeeMallOrders(pageUtil);
        PageResultUtil pageResult = new PageResultUtil(newBeeMallOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String updateOrderInfo(OrderDO newBeeMallOrder) {
        OrderDO temp = orderDOMapper.selectByPrimaryKey(newBeeMallOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(newBeeMallOrder.getTotalPrice());
            temp.setUpdateTime(new Date());
            if (orderDOMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<OrderDO> orders = orderDOMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (OrderDO newBeeMallOrder : orders) {
                if (newBeeMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (newBeeMallOrder.getOrderStatus() != 1) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (orderDOMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<OrderDO> orders = orderDOMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (OrderDO newBeeMallOrder : orders) {
                if (newBeeMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (newBeeMallOrder.getOrderStatus() != 1 && newBeeMallOrder.getOrderStatus() != 2) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (orderDOMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<OrderDO> orders = orderDOMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (OrderDO newBeeMallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (newBeeMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (newBeeMallOrder.getOrderStatus() == 4 || newBeeMallOrder.getOrderStatus() < 0) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (orderDOMapper.closeOrder(Arrays.asList(ids), OrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public List<OrderItemVO> getOrderItems(Long orderId) {
        OrderDO newBeeMallOrder = orderDOMapper.selectByPrimaryKey(orderId);
        if (newBeeMallOrder != null) {
            List<OrderItemDO> orderItems = orderItemDOMapper.selectByOrderId(newBeeMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<OrderItemVO> newBeeMallOrderItemVOS = BeanUtil.copyList(orderItems, OrderItemVO.class);
                return newBeeMallOrderItemVOS;
            }
        }
        return null;
    }
}
