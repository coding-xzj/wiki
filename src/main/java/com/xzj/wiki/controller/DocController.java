package com.xzj.wiki.controller;

import com.xzj.wiki.req.DocQueryReq;
import com.xzj.wiki.req.DocSaveReq;
import com.xzj.wiki.resp.DocQueryResp;
import com.xzj.wiki.resp.CommonResp;
import com.xzj.wiki.resp.PageResp;
import com.xzj.wiki.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * @author zixi
 * @version 1.0
 * @date 21/11/12 下午 8:35
 */

@RestController
@RequestMapping("/doc")
public class DocController {

    @Resource
    private DocService docService;

    @GetMapping("/all")
    public CommonResp all() {
        CommonResp<List<DocQueryResp>> response = new CommonResp<>();
        List<DocQueryResp> list = docService.all();
        response.setContent(list);
        return response;
    }

    @GetMapping("/list")
    public CommonResp list(@Valid DocQueryReq req) {
        CommonResp<PageResp<DocQueryResp>> response = new CommonResp<>();
        PageResp<DocQueryResp> list = docService.list(req);
        response.setContent(list);
        return response;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody DocSaveReq req) {
        CommonResp response = new CommonResp();
        docService.save(req);
        return response;
    }

    @DeleteMapping("/delete/{idsStr}")
    public CommonResp delete(@PathVariable String idsStr) {
        CommonResp response = new CommonResp();
        List<String> list = Arrays.asList(idsStr.split(","));
        docService.delete(list);
        return response;
    }

    @GetMapping("/find-content/{id}")
    public CommonResp findContent(@PathVariable Long id) {
        CommonResp<String> response = new CommonResp<>();
        response.setContent(docService.findContent(id));
        return response;
    }

    @GetMapping("/vote/{id}")
    public CommonResp vote(@PathVariable Long id) {
        CommonResp<String> response = new CommonResp<>();
        docService.vote(id);
        return response;
    }
}
