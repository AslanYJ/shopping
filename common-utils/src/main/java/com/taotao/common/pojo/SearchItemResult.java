package com.taotao.common.pojo;

import java.io.Serializable;
import java.util.List;

public class SearchItemResult implements Serializable{
    private long pageTotal;
    private long recordCount;
    private List<SearchItem> searchList;

    public long getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(long pageTotal) {
        this.pageTotal = pageTotal;
    }

    public long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(long recordCount) {
        this.recordCount = recordCount;
    }

    public List<SearchItem> getSearchList() {
        return searchList;
    }

    public void setSearchList(List<SearchItem> searchList) {
        this.searchList = searchList;
    }
}
