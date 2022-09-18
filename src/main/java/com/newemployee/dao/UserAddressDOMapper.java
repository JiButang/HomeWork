package com.newemployee.dao;

import com.newemployee.dataobject.UserAddressDO;

import java.util.List;

public interface UserAddressDOMapper {
    int deleteByPrimaryKey(Long addressId);

    int insert(UserAddressDO record);

    int insertSelective(UserAddressDO record);

    UserAddressDO selectByPrimaryKey(Long addressId);

    UserAddressDO getMyDefaultAddress(Long userId);

    List<UserAddressDO> findMyAddressList(Long userId);

    int updateByPrimaryKeySelective(UserAddressDO record);

    int updateByPrimaryKey(UserAddressDO record);
}
