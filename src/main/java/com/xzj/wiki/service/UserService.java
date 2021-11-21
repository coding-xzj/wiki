package com.xzj.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xzj.wiki.domain.User;
import com.xzj.wiki.domain.UserExample;
import com.xzj.wiki.exception.BusinessException;
import com.xzj.wiki.exception.BusinessExceptionCode;
import com.xzj.wiki.mapper.UserMapper;
import com.xzj.wiki.req.UserLoginReq;
import com.xzj.wiki.req.UserQueryReq;
import com.xzj.wiki.req.UserSaveReq;
import com.xzj.wiki.resp.PageResp;
import com.xzj.wiki.resp.UserLoginResp;
import com.xzj.wiki.resp.UserQueryResp;
import com.xzj.wiki.util.CopyUtil;
import com.xzj.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zixi
 * @version 1.0
 * @date 21/11/15 下午 7:58
 */

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    public List<UserQueryResp> all() {
        UserExample userExample = new UserExample();
        // 条件 按name排序
        userExample.setOrderByClause("name asc");
        // 查询数据库
        List<User> userList = userMapper.selectByExample(userExample);
        // 优化Resp数据
        List<UserQueryResp> respList = CopyUtil.copyList(userList, UserQueryResp.class);
        return respList;
    }

    public PageResp<UserQueryResp> list(UserQueryReq req) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        // 查询条件
        if (!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }
        // 查询数据库
        PageHelper.startPage(req.getPage(), req.getSize());
        List<User> userList = userMapper.selectByExample(userExample);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        // 优化Resp数据
        List<UserQueryResp> respList = CopyUtil.copyList(userList, UserQueryResp.class);
        PageResp<UserQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(respList);
        return pageResp;
    }

    /*
    保存功能
    req的id有值就是更新，没值就是新增.
     */
    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            User userDB = selectByLoginName(req.getLoginName());
            if (ObjectUtils.isEmpty(userDB)) {
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            } else {
                // 用户名已存在
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
        } else {
            // 更新
            userMapper.deleteByPrimaryKey(user.getId());
            userMapper.insert(user);
            userMapper.updateByPrimaryKey(user);
        }
    }

    /*
    删除功能
     */
    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }

    public User selectByLoginName(String loginName) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(loginName);
        List<User> userList = userMapper.selectByExample(userExample);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        } else {
            return userList.get(0);
        }
    }

    /*
    登录功能
     */
    public UserLoginResp login(UserLoginReq req){
        User userDB = selectByLoginName(req.getLoginName());
        if (ObjectUtils.isEmpty(userDB)){
            // 用户名不存在
            LOG.info("用户名不存在，{}", req.getLoginName());
            throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
        } else {
            if (userDB.getPassword().equals(req.getPassword())) {
                // 登录成功
                UserLoginResp userLoginResp = CopyUtil.copy(req, UserLoginResp.class);
                return userLoginResp;
            } else {
                // 密码不正确
                LOG.info("密码不正确，输入密码{}，正确密码{}", req.getPassword(), userDB.getPassword());
                throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
            }
        }
    }

}
