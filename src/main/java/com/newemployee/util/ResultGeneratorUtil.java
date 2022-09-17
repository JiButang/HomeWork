package com.newemployee.util;

import org.springframework.util.StringUtils;

public class ResultGeneratorUtil {
    private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";
    private static final String DEFAULT_FAIL_MESSAGE = "FAIL";
    private static final int RESULT_CODE_SUCCESS = 200;
    private static final int RESULT_CODE_SERVER_ERROR = 500;

    public static ResultUtil genSuccessResult() {
        ResultUtil result = new ResultUtil();
        result.setResultCode(RESULT_CODE_SUCCESS);
        result.setMessage(DEFAULT_SUCCESS_MESSAGE);
        return result;
    }

    public static ResultUtil genSuccessResult(String message) {
        ResultUtil result = new ResultUtil();
        result.setResultCode(RESULT_CODE_SUCCESS);
        result.setMessage(message);
        return result;
    }

    public static ResultUtil genSuccessResult(Object data) {
        ResultUtil result = new ResultUtil();
        result.setResultCode(RESULT_CODE_SUCCESS);
        result.setMessage(DEFAULT_SUCCESS_MESSAGE);
        result.setData(data);
        return result;
    }

    public static ResultUtil genFailResult(String message) {
        ResultUtil result = new ResultUtil();
        result.setResultCode(RESULT_CODE_SERVER_ERROR);
        if (StringUtils.isEmpty(message)) {
            result.setMessage(DEFAULT_FAIL_MESSAGE);
        } else {
            result.setMessage(message);
        }
        return result;
    }

    public static ResultUtil genErrorResult(int code, String message) {
        ResultUtil result = new ResultUtil();
        result.setResultCode(code);
        result.setMessage(message);
        return result;
    }
}
