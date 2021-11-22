package com.xzj.wiki.job;

import com.xzj.wiki.service.DocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zixi
 * @version 1.0
 * @date 21/11/22 上午 8:49
 */

@Component
public class DocJob {
    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);

    @Resource
    private DocService docService;

    /*
    每30秒更新一次电子书信息
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void cron() {
        docService.updateEbookInfo();
    }
}
