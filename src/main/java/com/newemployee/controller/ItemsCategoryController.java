package com.newemployee.controller;

import com.newemployee.common.BaseException;
import com.newemployee.common.ServiceResultEnum;
import com.newemployee.service.CategoryService;
import com.newemployee.util.ResultGeneratorUtil;
import com.newemployee.util.ResultUtil;
import com.newemployee.vo.IndexCategoryVO;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ItemsCategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/categories")
    //获取分类数据, 分类页面使用
    public ResultUtil<List<IndexCategoryVO>> getCategories() {
        List<IndexCategoryVO> categories = categoryService.getCategoriesForIndex();
        if (CollectionUtils.isEmpty(categories)) {
            BaseException.toss(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGeneratorUtil.genSuccessResult(categories);
    }
}
