package com.taotao.content.service;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;

public interface ContentService {
    EasyUIDataGridResult getContentList(long categoryId,int page,int rows);
    TaotaoResult addConeten(TbContent tbContent);
}
