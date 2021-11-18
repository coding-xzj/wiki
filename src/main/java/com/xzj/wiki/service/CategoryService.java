package com.xzj.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzj.wiki.domain.Category;
import com.xzj.wiki.domain.CategoryExample;
import com.xzj.wiki.mapper.CategoryMapper;
import com.xzj.wiki.req.CategoryQueryReq;
import com.xzj.wiki.req.CategorySaveReq;
import com.xzj.wiki.resp.CategoryQueryResp;
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
public class CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SnowFlake snowFlake;

    public List<CategoryQueryResp> all() {
        CategoryExample categoryExample = new CategoryExample();
        // 条件 按sort排序
        categoryExample.setOrderByClause("sort asc");
        // 查询数据库
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);
        // 优化Resp数据
        List<CategoryQueryResp> respList = CopyUtil.copyList(categoryList, CategoryQueryResp.class);
        return respList;
    }

    public PageResp<CategoryQueryResp> list(CategoryQueryReq req) {
        CategoryExample categoryExample = new CategoryExample();
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
        // 查询条件
        categoryExample.setOrderByClause("sort asc");
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        // 查询数据库
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);
        // 优化Resp数据
        List<CategoryQueryResp> respList = CopyUtil.copyList(categoryList, CategoryQueryResp.class);
        PageResp<CategoryQueryResp> pageResp = new PageResp<>();
        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }

    /*
    保存功能
    req的id有值就是更新，没值就是新增.
     */
    public void save(CategorySaveReq req) {
        Category category = CopyUtil.copy(req, Category.class);
        if (ObjectUtils.isEmpty(req.getId())){
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        } else {
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    /*
    删除功能
     */
    public void delete(Long id) {
        categoryMapper.deleteByPrimaryKey(id);
    }
}
