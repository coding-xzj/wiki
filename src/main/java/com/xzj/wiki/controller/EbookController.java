package com.xzj.wiki.controller;

import com.xzj.wiki.req.EbookQueryReq;
import com.xzj.wiki.req.EbookSaveReq;
import com.xzj.wiki.resp.CommonResp;
import com.xzj.wiki.resp.EbookQueryResp;
import com.xzj.wiki.resp.PageResp;
import com.xzj.wiki.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author zixi
 * @version 1.0
 * @date 21/11/12 下午 8:35
 */

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(@Valid EbookQueryReq req) {
        CommonResp<PageResp<EbookQueryResp>> response = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(req);
        response.setContent(list);
        return response;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody EbookSaveReq req) {
        CommonResp response = new CommonResp();
        ebookService.save(req);
        return response;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp response = new CommonResp();
        ebookService.delete(id);
        return response;
    }
}
