package cn.gybyt.controller;

import cn.gybyt.util.BaseException;
import cn.gybyt.util.BaseResponse;
import cn.gybyt.util.HttpStatusEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器请求页面
 * @program: utils
 * @classname: FilterErrorController
 * @author: codetiger
 * @create: 2022/8/7 15:30
 **/
@RestController
public class GybytAuthError {

    /**
     * 用于处理用户鉴权异常
     * @param request
     * @param httpServletResponse
     * @return
     */
    @RequestMapping("/error/authError")
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public BaseResponse error(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        BaseException baseException = (BaseException) request.getAttribute("authError");
        return BaseResponse.failure(baseException.getCode(), baseException.getMsg());
    }

}
