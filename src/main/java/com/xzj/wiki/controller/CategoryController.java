package com.xzj.wiki.controller;

import com.xzj.wiki.req.CategoryQueryReq;
import com.xzj.wiki.req.CategorySaveReq;
import com.xzj.wiki.resp.CategoryQueryResp;
import com.xzj.wiki.resp.CommonResp;
import com.xzj.wiki.resp.PageResp;
import com.xzj.wiki.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * TODO 可以增加搜索分类的功能
 * @author zixi
 * @version 1.0
 * @date 21/11/12 下午 8:35
 */

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/all")
    public CommonResp all() {
        CommonResp<List<CategoryQueryResp>> response = new CommonResp<>();
        List<CategoryQueryResp> list = categoryService.all();
        response.setContent(list);
        return response;
    }

    @GetMapping("/list")
    public CommonResp list(@Valid CategoryQueryReq req) {
        CommonResp<PageResp<CategoryQueryResp>> response = new CommonResp<>();
        PageResp<CategoryQueryResp> list = categoryService.list(req);
        response.setContent(list);
        return response;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody CategorySaveReq req) {
        CommonResp response = new CommonResp();
        categoryService.save(req);
        return response;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp response = new CommonResp();
        categoryService.delete(id);
        return response;
    }
}
