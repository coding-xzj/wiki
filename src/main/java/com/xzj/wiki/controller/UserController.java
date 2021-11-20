package com.xzj.wiki.controller;

import com.xzj.wiki.req.UserQueryReq;
import com.xzj.wiki.req.UserSaveReq;
import com.xzj.wiki.resp.CommonResp;
import com.xzj.wiki.resp.UserQueryResp;
import com.xzj.wiki.resp.PageResp;
import com.xzj.wiki.service.UserService;
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
    public CommonResp all() {
        CommonResp<List<UserQueryResp>> response = new CommonResp<>();
        List<UserQueryResp> list = userService.all();
        response.setContent(list);
        return response;
    }

    @GetMapping("/list")
    public CommonResp list(@Valid UserQueryReq req) {
        CommonResp<PageResp<UserQueryResp>> response = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        response.setContent(list);
        return response;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody UserSaveReq req) {
        CommonResp response = new CommonResp();
        userService.save(req);
        return response;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp response = new CommonResp();
        userService.delete(id);
        return response;
    }
}
