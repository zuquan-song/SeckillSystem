package org.seckill.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.seckill.result.Result;
import org.seckill.service.SeckillUserService;
import org.seckill.vo.LoginVo;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author Zuquan Song
 *
 * @description LoginController
 */
@Controller
@Slf4j
@RequestMapping("login")
public class LoginController {

    private final SeckillUserService userService;

    @Autowired
    public LoginController(SeckillUserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/login"})
    public String login() {
        return "login";
    }

    @PostMapping("/do_login")
    @ResponseBody
    public Result login(HttpServletResponse response, @Valid LoginVo loginVo) {
        boolean login = userService.login(response, loginVo);
        return Result.success(true);
    }


}
