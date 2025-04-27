package cn.gybyt.controller;

import cn.gybyt.util.BaseException;
import cn.gybyt.util.BaseResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 拦截器请求页面
 * @program: utils
 * @classname: FilterErrorController
 * @author: codetiger
 * @create: 2022/8/7 15:30
 **/
@RestController
@ConditionalOnClass(ModelAndView.class)
public class GybytAuthError {

    /**
     * 用于处理用户鉴权异常
     * @param request request对象
     * @return 返回体
     */
    @RequestMapping("/error/authError")
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public BaseResponse<String> error(HttpServletRequest request) {
        BaseException baseException = (BaseException) request.getAttribute("authError");
        return BaseResponse.failure(baseException.getCode(), baseException.getMsg());
    }

}
