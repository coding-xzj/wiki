package com.xzj.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzj.wiki.domain.Doc;
import com.xzj.wiki.domain.DocExample;
import com.xzj.wiki.mapper.DocMapper;
import com.xzj.wiki.req.DocQueryReq;
import com.xzj.wiki.req.DocSaveReq;
import com.xzj.wiki.resp.DocQueryResp;
import com.xzj.wiki.resp.PageResp;
import com.xzj.wiki.util.CopyUtil;
import com.xzj.wiki.util.SnowFlake;
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
public class DocService {

    @Resource
    private DocMapper docMapper;

    @Resource
    private SnowFlake snowFlake;

    public List<DocQueryResp> all() {
        DocExample docExample = new DocExample();
        // 条件 按sort排序
        docExample.setOrderByClause("sort asc");
        // 查询数据库
        List<Doc> docList = docMapper.selectByExample(docExample);
        // 优化Resp数据
        List<DocQueryResp> respList = CopyUtil.copyList(docList, DocQueryResp.class);
        return respList;
    }

    public PageResp<DocQueryResp> list(DocQueryReq req) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        // 查询条件
        docExample.setOrderByClause("sort asc");
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        // 查询数据库
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);
        // 优化Resp数据
        List<DocQueryResp> respList = CopyUtil.copyList(docList, DocQueryResp.class);
        PageResp<DocQueryResp> pageResp = new PageResp<>();
        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }

    /*
    保存功能
    req的id有值就是更新，没值就是新增.
     */
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        if (ObjectUtils.isEmpty(req.getId())){
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);
        } else {
            docMapper.updateByPrimaryKey(doc);
        }
    }

    /*
    删除功能
     */
    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }
}
