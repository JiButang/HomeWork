package com.newemployee.dao;

import com.newemployee.dataobject.IndexConfigDO;
import com.newemployee.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IndexConfigDOMapper {
    int deleteByPrimaryKey(Long configId);

    int insert(IndexConfigDO record);

    int insertSelective(IndexConfigDO record);

    IndexConfigDO selectByPrimaryKey(Long configId);

    IndexConfigDO selectByTypeAndGoodsId(@Param("configType") int configType, @Param("goodsId") Long goodsId);

    int updateByPrimaryKeySelective(IndexConfigDO record);

    int updateByPrimaryKey(IndexConfigDO record);

    List<IndexConfigDO> findIndexConfigList(PageQueryUtil pageUtil);

    int getTotalIndexConfigs(PageQueryUtil pageUtil);

    int deleteBatch(Long[] ids);

    List<IndexConfigDO> findIndexConfigsByTypeAndNum(@Param("configType") int configType, @Param("number") int number);
}
