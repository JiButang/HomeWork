package com.newemployee.service.impl;

import com.newemployee.common.BaseException;
import com.newemployee.common.ServiceResultEnum;
import com.newemployee.dao.UserAddressDOMapper;
import com.newemployee.dataobject.UserAddressDO;
import com.newemployee.service.UserAddressService;
import com.newemployee.util.BeanUtil;
import com.newemployee.vo.UserAddressVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private UserAddressDOMapper userAddressDOMapper;

    @Override
    public List<UserAddressVO> getMyAddresses(Long userId) {
        List<UserAddressDO> myAddressList = userAddressDOMapper.findMyAddressList(userId);
        List<UserAddressVO> newBeeMallUserAddressVOS = BeanUtil.copyList(myAddressList, UserAddressVO.class);
        return newBeeMallUserAddressVOS;
    }

    @Override
    @Transactional
    public Boolean saveUserAddress(UserAddressDO mallUserAddress) {
        Date now = new Date();
        if (mallUserAddress.getDefaultFlag().intValue() == 1) {
            //添加默认地址，需要将原有的默认地址修改掉
            UserAddressDO defaultAddress = userAddressDOMapper.getMyDefaultAddress(mallUserAddress.getUserId());
            if (defaultAddress != null) {
                defaultAddress.setDefaultFlag((byte) 0);
                defaultAddress.setUpdateTime(now);
                int updateResult = userAddressDOMapper.updateByPrimaryKeySelective(defaultAddress);
                if (updateResult < 1) {
                    //未更新成功
                    BaseException.toss(ServiceResultEnum.DB_ERROR.getResult());
                }
            }
        }
        return userAddressDOMapper.insertSelective(mallUserAddress) > 0;
    }

    @Override
    public Boolean updateMallUserAddress(UserAddressDO mallUserAddress) {
        UserAddressDO tempAddress = getMallUserAddressById(mallUserAddress.getAddressId());
        Date now = new Date();
        if (mallUserAddress.getDefaultFlag().intValue() == 1) {
            //修改为默认地址，需要将原有的默认地址修改掉
            UserAddressDO defaultAddress = userAddressDOMapper.getMyDefaultAddress(mallUserAddress.getUserId());
            if (defaultAddress != null && !defaultAddress.getAddressId().equals(tempAddress)) {
                //存在默认地址且默认地址并不是当前修改的地址
                defaultAddress.setDefaultFlag((byte) 0);
                defaultAddress.setUpdateTime(now);
                int updateResult = userAddressDOMapper.updateByPrimaryKeySelective(defaultAddress);
                if (updateResult < 1) {
                    //未更新成功
                    BaseException.toss(ServiceResultEnum.DB_ERROR.getResult());
                }
            }
        }
        mallUserAddress.setUpdateTime(now);
        return userAddressDOMapper.updateByPrimaryKeySelective(mallUserAddress) > 0;
    }

    @Override
    public UserAddressDO getMallUserAddressById(Long addressId) {
        UserAddressDO mallUserAddress = userAddressDOMapper.selectByPrimaryKey(addressId);
        if (mallUserAddress == null) {
            BaseException.toss(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return mallUserAddress;
    }

    @Override
    public UserAddressDO getMyDefaultAddressByUserId(Long userId) {
        return userAddressDOMapper.getMyDefaultAddress(userId);
    }

    @Override
    public Boolean deleteById(Long addressId) {
        return userAddressDOMapper.deleteByPrimaryKey(addressId) > 0;
    }
}
