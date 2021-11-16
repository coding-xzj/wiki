package com.xzj.wiki.service;

import com.xzj.wiki.domain.Test;
import com.xzj.wiki.mapper.TestMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zixi
 * @version 1.0
 * @date 21/11/15 下午 7:58
 */

@Service
public class TestService {

//    JDK自带的，注入
    @Resource
//    Spring，注入
//    @Autowired
    private TestMapper testMapper;

    public List<Test> list() {
        return testMapper.list();
    }
}
