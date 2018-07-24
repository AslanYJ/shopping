package com.taotao.search.service;

import com.taotao.common.pojo.TaotaoResult;

import java.io.IOException;

public interface SearchItemService {
    TaotaoResult addSerchItem() throws Exception;
    TaotaoResult addDocument(Long itemId) throws Exception;
}
