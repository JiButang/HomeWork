package com.newemployee.service.impl;

import com.newemployee.common.ServiceResultEnum;
import com.newemployee.dao.IndexConfigDOMapper;
import com.newemployee.dao.ItemsDOMapper;
import com.newemployee.dataobject.IndexConfigDO;
import com.newemployee.dataobject.ItemsDO;
import com.newemployee.service.IndexConfigService;
import com.newemployee.util.BeanUtil;
import com.newemployee.util.PageQueryUtil;
import com.newemployee.util.PageResultUtil;
import com.newemployee.vo.IndexConfigItemsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndexConfigServiceImpl implements IndexConfigService {
    @Autowired
    private IndexConfigDOMapper indexConfigDOMapper;

    @Autowired
    private ItemsDOMapper itemsDOMapper;

    @Override
    public List<IndexConfigItemsVO> getConfigGoodsesForIndex(int configType, int number) {
        List<IndexConfigItemsVO> indexConfigItemsVOS = new ArrayList<>(number);
        List<IndexConfigDO> indexConfigs = indexConfigDOMapper.findIndexConfigsByTypeAndNum(configType, number);
        if (!CollectionUtils.isEmpty(indexConfigs)) {
            //取出所有的goodsId
            List<Long> itemsIds = indexConfigs.stream().map(IndexConfigDO::getGoodsId).collect(Collectors.toList());
            List<ItemsDO> items = itemsDOMapper.selectByPrimaryKeys(itemsIds);
            indexConfigItemsVOS = BeanUtil.copyList(items, IndexConfigItemsVO.class);
            for (IndexConfigItemsVO indexConfigItemsVO : indexConfigItemsVOS) {
                String goodsName = indexConfigItemsVO.getGoodsName();
                String goodsIntro = indexConfigItemsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 30) {
                    goodsName = goodsName.substring(0, 30) + "...";
                    indexConfigItemsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 22) {
                    goodsIntro = goodsIntro.substring(0, 22) + "...";
                    indexConfigItemsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        return indexConfigItemsVOS;
    }

    @Override
    public PageResultUtil getConfigsPage(PageQueryUtil pageUtil) {
        List<IndexConfigDO> indexConfigs = indexConfigDOMapper.findIndexConfigList(pageUtil);
        int total = indexConfigDOMapper.getTotalIndexConfigs(pageUtil);
        PageResultUtil pageResult = new PageResultUtil(indexConfigs, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveIndexConfig(IndexConfigDO indexConfig) {
        if (itemsDOMapper.selectByPrimaryKey(indexConfig.getGoodsId()) == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        if (indexConfigDOMapper.selectByTypeAndGoodsId(indexConfig.getConfigType(), indexConfig.getGoodsId()) != null) {
            return ServiceResultEnum.SAME_INDEX_CONFIG_EXIST.getResult();
        }
        if (indexConfigDOMapper.insertSelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateIndexConfig(IndexConfigDO indexConfig) {
        if (itemsDOMapper.selectByPrimaryKey(indexConfig.getGoodsId()) == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        IndexConfigDO temp = indexConfigDOMapper.selectByPrimaryKey(indexConfig.getConfigId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        IndexConfigDO temp2 = indexConfigDOMapper.selectByTypeAndGoodsId(indexConfig.getConfigType(), indexConfig.getGoodsId());
        if (temp2 != null && !temp2.getConfigId().equals(indexConfig.getConfigId())) {
            //goodsId相同且不同id 不能继续修改
            return ServiceResultEnum.SAME_INDEX_CONFIG_EXIST.getResult();
        }
        indexConfig.setUpdateTime(new Date());
        if (indexConfigDOMapper.updateByPrimaryKeySelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public IndexConfigDO getIndexConfigById(Long id) {
        return indexConfigDOMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return indexConfigDOMapper.deleteBatch(ids) > 0;
    }
}
