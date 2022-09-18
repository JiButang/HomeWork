package com.newemployee.controller.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 用户注册param
 */
@Data
public class UserRegisterParam implements Serializable {

    //登录名
    @NotEmpty(message = "登录名不能为空")
    private String loginName;

    //用户密码
    @NotEmpty(message = "密码不能为空")
    private String password;
}
