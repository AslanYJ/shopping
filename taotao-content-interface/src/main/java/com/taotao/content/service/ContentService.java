package com.taotao.content.service;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;

import java.util.List;

public interface ContentService {
    EasyUIDataGridResult getContentList(long categoryId,int page,int rows);
    TaotaoResult addContent(TbContent tbContent);
    TaotaoResult updateContent(TbContent tbContent);
    TaotaoResult deleteContents(String ids);
    List<TbContent> getContentListByCid(long cid);
}
