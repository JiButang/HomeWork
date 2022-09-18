package com.newemployee.dao;

import com.newemployee.dataobject.OrderItemDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemDOMapper {
    int deleteByPrimaryKey(Long orderItemId);

    int insert(OrderItemDO record);

    int insertSelective(OrderItemDO record);

    OrderItemDO selectByPrimaryKey(Long orderItemId);

    /**
     * 根据订单id获取订单项列表
     *
     * @param orderId
     * @return
     */
    List<OrderItemDO> selectByOrderId(Long orderId);

    /**
     * 根据订单ids获取订单项列表
     *
     * @param orderIds
     * @return
     */
    List<OrderItemDO> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 批量insert订单项数据
     *
     * @param orderItems
     * @return
     */
    int insertBatch(@Param("orderItems") List<OrderItemDO> orderItems);

    int updateByPrimaryKeySelective(OrderItemDO record);

    int updateByPrimaryKey(OrderItemDO record);
}
