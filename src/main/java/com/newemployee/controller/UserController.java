package com.newemployee.controller;

import com.newemployee.common.Constants;
import com.newemployee.common.ServiceResultEnum;
import com.newemployee.config.annotation.TokenToUserDO;
import com.newemployee.controller.param.UserLoginParam;
import com.newemployee.controller.param.UserRegisterParam;
import com.newemployee.controller.param.UserUpdateParam;
import com.newemployee.dataobject.UserDO;
import com.newemployee.service.UserService;
import com.newemployee.util.BeanUtil;
import com.newemployee.util.NumberUtil;
import com.newemployee.util.ResultGeneratorUtil;
import com.newemployee.util.ResultUtil;
import com.newemployee.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {

    @Resource
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/user/login")
    //登录接口, 返回token
    public ResultUtil<String> login(@RequestBody @Valid UserLoginParam mallUserLoginParam) {
        if (!NumberUtil.isPhone(mallUserLoginParam.getLoginName())){
            return ResultGeneratorUtil.genFailResult(ServiceResultEnum.LOGIN_NAME_IS_NOT_PHONE.getResult());
        }
        String loginResult = userService.login(mallUserLoginParam.getLoginName(), mallUserLoginParam.getPasswordMd5());

        logger.info("login api,loginName={},loginResult={}", mallUserLoginParam.getLoginName(), loginResult);

        //登录成功
        if (!StringUtils.isEmpty(loginResult) && loginResult.length() == Constants.TOKEN_LENGTH) {
            ResultUtil result = ResultGeneratorUtil.genSuccessResult();
            result.setData(loginResult);
            return result;
        }
        //登录失败
        return ResultGeneratorUtil.genFailResult(loginResult);
    }


    @PostMapping("/user/logout")
    //登出接口, 清除token
    public ResultUtil<String> logout(@TokenToUserDO UserDO loginMallUser) {
        Boolean logoutResult = userService.logout(loginMallUser.getUserId());

        logger.info("logout api,loginMallUser={}", loginMallUser.getUserId());

        //登出成功
        if (logoutResult) {
            return ResultGeneratorUtil.genSuccessResult();
        }
        //登出失败
        return ResultGeneratorUtil.genFailResult("logout error");
    }


    @PostMapping("/user/register")
    //用户注册
    public ResultUtil register(@RequestBody @Valid UserRegisterParam mallUserRegisterParam) {
        if (!NumberUtil.isPhone(mallUserRegisterParam.getLoginName())){
            return ResultGeneratorUtil.genFailResult(ServiceResultEnum.LOGIN_NAME_IS_NOT_PHONE.getResult());
        }
        String registerResult = userService.register(mallUserRegisterParam.getLoginName(), mallUserRegisterParam.getPassword());

        logger.info("register api,loginName={},loginResult={}", mallUserRegisterParam.getLoginName(), registerResult);

        //注册成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(registerResult)) {
            return ResultGeneratorUtil.genSuccessResult();
        }
        //注册失败
        return ResultGeneratorUtil.genFailResult(registerResult);
    }

    @PutMapping("/user/info")
    //修改用户信息
    public ResultUtil updateInfo(@RequestBody UserUpdateParam mallUserUpdateParam, @TokenToUserDO UserDO loginMallUser) {
        Boolean flag = userService.updateUserInfo(mallUserUpdateParam, loginMallUser.getUserId());
        if (flag) {
            //返回成功
            ResultUtil result = ResultGeneratorUtil.genSuccessResult();
            return result;
        } else {
            //返回失败
            ResultUtil result = ResultGeneratorUtil.genFailResult("修改失败");
            return result;
        }
    }

    @GetMapping("/user/info")
    //获取用户信息
    public ResultUtil<UserVO> getUserDetail(@TokenToUserDO UserDO loginMallUser) {
        //已登录则直接返回
        UserVO mallUserVO = new UserVO();
        BeanUtil.copyProperties(loginMallUser, mallUserVO);
        return ResultGeneratorUtil.genSuccessResult(mallUserVO);
    }
}
