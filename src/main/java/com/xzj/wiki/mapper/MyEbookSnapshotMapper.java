package com.xzj.wiki.mapper;

import com.xzj.wiki.resp.StatisticResp;

import java.util.List;

public interface MyEbookSnapshotMapper {

    public void genSnapshot();

    List<StatisticResp> getStatistic();

    List<StatisticResp> get30Statistic();
}
