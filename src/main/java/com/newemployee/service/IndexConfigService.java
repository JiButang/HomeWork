package com.newemployee.service;

import com.newemployee.dataobject.IndexConfigDO;
import com.newemployee.util.PageQueryUtil;
import com.newemployee.util.PageResultUtil;
import com.newemployee.vo.IndexConfigItemsVO;

import java.util.List;

public interface IndexConfigService {
    /**
     * 返回固定数量的首页配置商品对象(首页调用)
     *
     * @param number
     * @return
     */
    List<IndexConfigItemsVO> getConfigGoodsesForIndex(int configType, int number);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResultUtil getConfigsPage(PageQueryUtil pageUtil);

    String saveIndexConfig(IndexConfigDO indexConfig);

    String updateIndexConfig(IndexConfigDO indexConfig);

    IndexConfigDO getIndexConfigById(Long id);

    Boolean deleteBatch(Long[] ids);
}
