package com.taotao.search.service;

import com.taotao.common.pojo.SearchItemResult;

public interface SearchService {
    SearchItemResult search(String queryString,Integer page,Integer rows)  throws Exception;
}
