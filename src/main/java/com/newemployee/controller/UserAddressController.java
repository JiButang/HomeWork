package com.newemployee.controller;

import com.newemployee.common.ServiceResultEnum;
import com.newemployee.config.annotation.TokenToUserDO;
import com.newemployee.controller.param.SaveMallUserAddressParam;
import com.newemployee.controller.param.UpdateUserAddressParam;
import com.newemployee.dataobject.UserAddressDO;
import com.newemployee.dataobject.UserDO;
import com.newemployee.service.UserAddressService;
import com.newemployee.util.BeanUtil;
import com.newemployee.util.ResultGeneratorUtil;
import com.newemployee.util.ResultUtil;
import com.newemployee.vo.UserAddressVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserAddressController {

    @Resource
    private UserAddressService userAddressService;

    @GetMapping("/address")
    @ApiOperation(value = "我的收货地址列表", notes = "")
    public ResultUtil<List<UserAddressVO>> addressList(@TokenToUserDO UserDO loginMallUser) {
        return ResultGeneratorUtil.genSuccessResult(userAddressService.getMyAddresses(loginMallUser.getUserId()));
    }

    @PostMapping("/address")
    @ApiOperation(value = "添加地址", notes = "")
    public ResultUtil<Boolean> saveUserAddress(@RequestBody SaveMallUserAddressParam saveMallUserAddressParam,
                                           @TokenToUserDO UserDO loginMallUser) {
        UserAddressDO userAddress = new UserAddressDO();
        BeanUtil.copyProperties(saveMallUserAddressParam, userAddress);
        userAddress.setUserId(loginMallUser.getUserId());
        Boolean saveResult = userAddressService.saveUserAddress(userAddress);
        //添加成功
        if (saveResult) {
            return ResultGeneratorUtil.genSuccessResult();
        }
        //添加失败
        return ResultGeneratorUtil.genFailResult("添加失败");
    }

    @PutMapping("/address")
    @ApiOperation(value = "修改地址", notes = "")
    public ResultUtil<Boolean> updateMallUserAddress(@RequestBody UpdateUserAddressParam updateMallUserAddressParam,
                                                 @TokenToUserDO UserDO loginMallUser) {
        UserAddressDO mallUserAddressById = userAddressService.getMallUserAddressById(updateMallUserAddressParam.getAddressId());
        if (!loginMallUser.getUserId().equals(mallUserAddressById.getUserId())) {
            return ResultGeneratorUtil.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        UserAddressDO userAddress = new UserAddressDO();
        BeanUtil.copyProperties(updateMallUserAddressParam, userAddress);
        userAddress.setUserId(loginMallUser.getUserId());
        Boolean updateResult = userAddressService.updateMallUserAddress(userAddress);
        //修改成功
        if (updateResult) {
            return ResultGeneratorUtil.genSuccessResult();
        }
        //修改失败
        return ResultGeneratorUtil.genFailResult("修改失败");
    }

    @GetMapping("/address/{addressId}")
    @ApiOperation(value = "获取收货地址详情", notes = "传参为地址id")
    public ResultUtil<UserAddressVO> getMallUserAddress(@PathVariable("addressId") Long addressId,
                                                              @TokenToUserDO UserDO loginMallUser) {
        UserAddressDO mallUserAddressById = userAddressService.getMallUserAddressById(addressId);
        UserAddressVO newBeeMallUserAddressVO = new UserAddressVO();
        BeanUtil.copyProperties(mallUserAddressById, newBeeMallUserAddressVO);
        if (!loginMallUser.getUserId().equals(mallUserAddressById.getUserId())) {
            return ResultGeneratorUtil.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        return ResultGeneratorUtil.genSuccessResult(newBeeMallUserAddressVO);
    }

    @GetMapping("/address/default")
    @ApiOperation(value = "获取默认收货地址", notes = "无传参")
    public ResultUtil getDefaultMallUserAddress(@TokenToUserDO UserDO loginMallUser) {
        UserAddressDO mallUserAddressById = userAddressService.getMyDefaultAddressByUserId(loginMallUser.getUserId());
        return ResultGeneratorUtil.genSuccessResult(mallUserAddressById);
    }

    @DeleteMapping("/address/{addressId}")
    @ApiOperation(value = "删除收货地址", notes = "传参为地址id")
    public ResultUtil deleteAddress(@PathVariable("addressId") Long addressId,
                                @TokenToUserDO UserDO loginMallUser) {
        UserAddressDO mallUserAddressById = userAddressService.getMallUserAddressById(addressId);
        if (!loginMallUser.getUserId().equals(mallUserAddressById.getUserId())) {
            return ResultGeneratorUtil.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        Boolean deleteResult = userAddressService.deleteById(addressId);
        //删除成功
        if (deleteResult) {
            return ResultGeneratorUtil.genSuccessResult();
        }
        //删除失败
        return ResultGeneratorUtil.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }
}
