package com.xzj.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzj.wiki.domain.Ebook;
import com.xzj.wiki.domain.EbookExample;
import com.xzj.wiki.mapper.EbookMapper;
import com.xzj.wiki.req.EbookQueryReq;
import com.xzj.wiki.req.EbookSaveReq;
import com.xzj.wiki.resp.EbookQueryResp;
import com.xzj.wiki.resp.PageResp;
import com.xzj.wiki.util.CopyUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zixi
 * @version 1.0
 * @date 21/11/15 下午 7:58
 */

@Service
public class EbookService {

    @Resource
    private EbookMapper ebookMapper;

    public PageResp<EbookQueryResp> list(EbookQueryReq req) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }

        PageHelper.startPage(req.getPage(), req.getSize());
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
//        System.out.printf("总行数：%d", pageInfo.getTotal());
//        System.out.println();
//        System.out.printf("总页数：%d", pageInfo.getPages());
//        System.out.println();

        List<EbookQueryResp> respList = CopyUtil.copyList(ebookList, EbookQueryResp.class);
        PageResp<EbookQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }

    /*
    保存功能
    req的id有值就是更新，没值就是新增.
     */
    public void save(EbookSaveReq req) {
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        if (ObjectUtils.isEmpty(req.getId())){
            ebookMapper.insert(ebook);
        } else {
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }

}
