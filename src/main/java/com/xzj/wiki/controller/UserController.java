package com.xzj.wiki.controller;

import com.xzj.wiki.req.UserLoginReq;
import com.xzj.wiki.req.UserQueryReq;
import com.xzj.wiki.req.UserSaveReq;
import com.xzj.wiki.resp.CommonResp;
import com.xzj.wiki.resp.PageResp;
import com.xzj.wiki.resp.UserLoginResp;
import com.xzj.wiki.resp.UserQueryResp;
import com.xzj.wiki.service.UserService;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author zixi
 * @version 1.0
 * @date 21/11/12 下午 8:35
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/all")
    public CommonResp<List<UserQueryResp>> all() {
        CommonResp<List<UserQueryResp>> response = new CommonResp<>();
        List<UserQueryResp> list = userService.all();
        response.setContent(list);
        return response;
    }

    @GetMapping("/list")
    public CommonResp<PageResp<UserQueryResp>> list(@Valid UserQueryReq req) {
        CommonResp<PageResp<UserQueryResp>> response = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        response.setContent(list);
        return response;
    }

    @PostMapping("/save")
    public CommonResp<String> save(@RequestBody UserSaveReq req) {
        // 密码加密
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<String> response = new CommonResp<>();
        userService.save(req);
        return response;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<String> delete(@PathVariable Long id) {
        CommonResp<String> response = new CommonResp<>();
        userService.delete(id);
        return response;
    }

    @PostMapping("/login")
    public CommonResp<UserLoginResp> login(@RequestBody UserLoginReq req) {
        // 密码加密
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> response = new CommonResp<>();
        UserLoginResp userLoginResp = userService.login(req);
        response.setContent(userLoginResp);
        return response;
    }
}
