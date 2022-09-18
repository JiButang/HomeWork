package com.newemployee.dao;

import com.newemployee.dataobject.OrderAddressDO;

public interface OrderAddressDOMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(OrderAddressDO record);

    int insertSelective(OrderAddressDO record);

    OrderAddressDO selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(OrderAddressDO record);

    int updateByPrimaryKey(OrderAddressDO record);
}
