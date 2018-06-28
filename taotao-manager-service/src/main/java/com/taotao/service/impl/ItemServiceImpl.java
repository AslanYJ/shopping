package com.taotao.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemExample;
import com.taotao.service.ItemService;
import com.taotao.utils.IDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 商品管理
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private TbItemDescMapper itemDescMapper;
    @Override
    public TbItem getItemById(long itemId) {
        TbItem item = tbItemMapper.selectByPrimaryKey(itemId);
        return item;
    }

    @Override
    public EasyUIDataGridResult getItemList(int page, int rows) {
        //1.在执行查询之前配置分页条件。使用PageHelper的静态方法
        PageHelper.startPage(page,rows);
        //2.执行查询
        TbItemExample tbItemExample = new TbItemExample();
        List<TbItem> list = tbItemMapper.selectByExample(tbItemExample);
        //3.创建PageInfo对象
        PageInfo<TbItem> pageInfo = new PageInfo<>(list);
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        //设置数目
        result.setTotal(pageInfo.getTotal());
        //设置返回的数据
        result.setRows(list);
        return result;
    }

    @Override
    public TaotaoResult addItem(TbItem item, String desc) {
        long id = IDUtils.genItemId();
        item.setId(id);
        item.setCreated(new Date());
        item.setUpdated(new Date());
        item.setStatus((byte) 1);
        tbItemMapper.insert(item);
        TbItemDesc itemDesc = new TbItemDesc();
        itemDesc.setItemDesc(desc);
        itemDesc.setCreated(new Date());
        itemDesc.setUpdated(new Date());
        itemDescMapper.insert(itemDesc);
        return TaotaoResult.ok();
    }


    @Override
    public TbItem updateItem(long itemId) {

        return null;
    }
}
