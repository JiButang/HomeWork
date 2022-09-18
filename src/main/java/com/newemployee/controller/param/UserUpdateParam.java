package com.newemployee.controller.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户修改param
 */
@Data
public class UserUpdateParam implements Serializable {

    //用户昵称
    private String nickName;

    //用户密码(需要MD5加密)
    private String passwordMd5;

    //个性签名
    private String introduceSign;

}
