package com.xzj.wiki.resp;

import java.util.List;

/**
 * @author zixi
 * @version 1.0
 * @date 21/11/16 下午 8:55
 */


public class PageResp<T> {

    private long total;

    private List<T> list;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "PageResp{" +
                "total=" + total +
                ", list=" + list +
                '}';
    }
}
