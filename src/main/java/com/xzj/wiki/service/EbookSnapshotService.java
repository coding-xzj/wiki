package com.xzj.wiki.service;

import com.xzj.wiki.mapper.MyEbookSnapshotMapper;
import com.xzj.wiki.resp.StatisticResp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookSnapshotService {

    @Resource
    private MyEbookSnapshotMapper myBookSnapshotMapper;

    public void genSnapshot() {
        myBookSnapshotMapper.genSnapshot();
    }

    /**
     * 获取首页数值数据：总阅读数、总点赞数、今日阅读数、今日点赞数、今日预计阅读数、今日预计阅读增长
     */
    public List<StatisticResp> getStatistic() {
        return myBookSnapshotMapper.getStatistic();
    }

    /**
     * 30天数值统计
     */
    public List<StatisticResp> get30Statistic() {
        return myBookSnapshotMapper.get30Statistic();
    }

}
