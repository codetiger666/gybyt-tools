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
public class FilterErrorController{

    /**
     * 用于spring security处理用户未登录或令牌失效异常
     * @param request
     * @param httpServletResponse
     * @return
     */
    @RequestMapping("/error/userNotLogin")
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public BaseResponse error(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        BaseException baseException = (BaseException) request.getAttribute("filterError");
        return BaseResponse.failure(HttpStatusEnum.UNAUTHORIZED.value(), baseException.getMsg());
    }

}
