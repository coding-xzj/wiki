package com.xzj.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzj.wiki.domain.Content;
import com.xzj.wiki.domain.Doc;
import com.xzj.wiki.domain.DocExample;
import com.xzj.wiki.exception.BusinessException;
import com.xzj.wiki.exception.BusinessExceptionCode;
import com.xzj.wiki.mapper.ContentMapper;
import com.xzj.wiki.mapper.DocMapper;
import com.xzj.wiki.mapper.MyDocMapper;
import com.xzj.wiki.req.DocQueryReq;
import com.xzj.wiki.req.DocSaveReq;
import com.xzj.wiki.resp.DocQueryResp;
import com.xzj.wiki.resp.PageResp;
import com.xzj.wiki.util.CopyUtil;
import com.xzj.wiki.util.RedisUtil;
import com.xzj.wiki.util.SnowFlake;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private ContentMapper contentMapper;

    @Resource
    private MyDocMapper myDocMapper;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private SnowFlake snowFlake;

    public List<DocQueryResp> all(Long ebookId) {
        DocExample docExample = new DocExample();
        // 条件 按sort排序
        docExample.setOrderByClause("sort asc");
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        // 查询数据库
        List<Doc> docList = docMapper.selectByExample(docExample);
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);
        return list;
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
    @Transactional 事务注解
     */
    @Transactional
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        if (ObjectUtils.isEmpty(req.getId())){
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            content.setId(doc.getId());
            contentMapper.insert(content);
        } else {
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if(count == 0) {
                contentMapper.insert(content);
            }
        }
    }

    /*
    删除功能
     */
    public void delete(Long id) {
        myDocMapper.increaseViewCount(id);
    }

    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        myDocMapper.increaseViewCount(id);
        if (ObjectUtils.isEmpty(content)) {
            return "";
        } else {
            return content.getContent();
        }
    }

    /*
    点赞功能
     */
    public void vote(Long id) {
        if (redisUtil.validateRepeat("DOC_VOTE_" + id, 2)) {
            myDocMapper.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }
        // ws推送消息
        Doc docDB = docMapper.selectByPrimaryKey(id);
        String logId = MDC.get("LOG_ID");
    }

    /*
    电子书信息定时更新
     */
    public void updateEbookInfo() {
        myDocMapper.updateEbookInfo();
    }
}
