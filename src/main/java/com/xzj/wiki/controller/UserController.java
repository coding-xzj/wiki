package com.xzj.wiki.controller;

import com.alibaba.fastjson.JSONObject;
import com.xzj.wiki.req.UserLoginReq;
import com.xzj.wiki.req.UserQueryReq;
import com.xzj.wiki.req.UserSaveReq;
import com.xzj.wiki.resp.CommonResp;
import com.xzj.wiki.resp.PageResp;
import com.xzj.wiki.resp.UserLoginResp;
import com.xzj.wiki.resp.UserQueryResp;
import com.xzj.wiki.service.UserService;
import com.xzj.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zixi
 * @version 1.0
 * @date 21/11/12 下午 8:35
 */

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private SnowFlake snowFlake;

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

        Long token = snowFlake.nextId();
        LOG.info("生成单点登录token：{}，并放入redis中", token);
        userLoginResp.setToken(token.toString());
        redisTemplate.opsForValue().set(token.toString(), JSONObject.toJSONString(userLoginResp), 3600 * 24, TimeUnit.SECONDS);

        response.setContent(userLoginResp);
        return response;
    }

    @GetMapping("/logout/{token}")
    public CommonResp<String> logout(@PathVariable String token) {
        CommonResp<String> response = new CommonResp<>();
        redisTemplate.delete(token);
        LOG.info("从redis中删除token：{}", token);
        return response;
    }
}
