package com.xzj.wiki.service;

import com.xzj.wiki.domain.Ebook;
import com.xzj.wiki.mapper.EbookMapper;
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

    public List<Ebook> list() {
        return ebookMapper.selectByExample(null);
    }
}
