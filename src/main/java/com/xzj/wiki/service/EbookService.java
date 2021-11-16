package com.xzj.wiki.service;

import com.xzj.wiki.domain.Ebook;
import com.xzj.wiki.domain.EbookExample;
import com.xzj.wiki.mapper.EbookMapper;
import com.xzj.wiki.req.EbookReq;
import com.xzj.wiki.resp.EbookResp;
import com.xzj.wiki.util.CopyUtil;
import org.springframework.stereotype.Service;

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

    public List<EbookResp> list(EbookReq req) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        criteria.andNameLike("%" + req.getName() + "%");
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
        List<EbookResp> respList = CopyUtil.copyList(ebookList,EbookResp.class);
        return respList;
    }
}
