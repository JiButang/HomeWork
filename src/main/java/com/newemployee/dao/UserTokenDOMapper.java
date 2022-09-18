package com.newemployee.dao;

import com.newemployee.dataobject.UserTokenDO;

public interface UserTokenDOMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(UserTokenDO record);

    int insertSelective(UserTokenDO record);

    UserTokenDO selectByPrimaryKey(Long userId);

    UserTokenDO selectByToken(String token);

    int updateByPrimaryKeySelective(UserTokenDO record);

    int updateByPrimaryKey(UserTokenDO record);
}
