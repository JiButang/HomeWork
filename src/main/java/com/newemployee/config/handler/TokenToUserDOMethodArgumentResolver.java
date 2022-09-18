package com.newemployee.config.handler;

import com.newemployee.common.BaseException;
import com.newemployee.common.Constants;
import com.newemployee.common.ServiceResultEnum;
import com.newemployee.config.annotation.TokenToUserDO;
import com.newemployee.dao.UserDOMapper;
import com.newemployee.dao.UserTokenDOMapper;
import com.newemployee.dataobject.UserDO;
import com.newemployee.dataobject.UserTokenDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class TokenToUserDOMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private UserDOMapper userDOMapper;
    @Autowired
    private UserTokenDOMapper userTokenDOMapper;

    public TokenToUserDOMethodArgumentResolver() {
    }

    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(TokenToUserDO.class)) {
            return true;
        }
        return false;
    }

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        if (parameter.getParameterAnnotation(TokenToUserDO.class) instanceof TokenToUserDO) {
            UserDO mallUser = null;
            String token = webRequest.getHeader("token");
            if (null != token && !"".equals(token) && token.length() == Constants.TOKEN_LENGTH) {
                UserTokenDO mallUserToken = userTokenDOMapper.selectByToken(token);
                if (mallUserToken == null || mallUserToken.getExpireTime().getTime() <= System.currentTimeMillis()) {
                    BaseException.toss(ServiceResultEnum.TOKEN_EXPIRE_ERROR.getResult());
                }
                mallUser = userDOMapper.selectByPrimaryKey(mallUserToken.getUserId());
                if (mallUser == null) {
                    BaseException.toss(ServiceResultEnum.USER_NULL_ERROR.getResult());
                }
                if (mallUser.getLockedFlag().intValue() == 1) {
                    BaseException.toss(ServiceResultEnum.LOGIN_USER_LOCKED_ERROR.getResult());
                }
                return mallUser;
            } else {
                BaseException.toss(ServiceResultEnum.NOT_LOGIN_ERROR.getResult());
            }
        }
        return null;
    }

    public static byte[] getRequestPostBytes(HttpServletRequest request)
            throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {
            int readlen = request.getInputStream().read(buffer, i,
                    contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

}
