package com.taotao.service.impl;


import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.mapper.TbItemCatMapper;
import com.taotao.pojo.TbItemCat;
import com.taotao.pojo.TbItemCatExample;
import com.taotao.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.taotao.pojo.TbItemCatExample.Criteria;
import java.util.ArrayList;
import java.util.List;
@Service
public class ItemCatServiceImpl implements ItemCatService {
    @Autowired
    private TbItemCatMapper tbItemCatMapper;
    @Override
    public List<EasyUITreeNode> getItemCatList(long parentId) {
        //1.根据父id查询，创建一个对应的Excample
        TbItemCatExample example = new TbItemCatExample();
        //2.设置查询条件
        Criteria criteria = example.createCriteria();
        //设置parentid
        criteria.andParentIdEqualTo(parentId);
        //3.执行查询,返回数据库的数据
        List<TbItemCat> list = tbItemCatMapper.selectByExample(example);
        //4.封装数据
        List<EasyUITreeNode> result = new ArrayList<>();
        //转换成EasyUITreeNode列表
        for (TbItemCat tbItemCat:
             list) {
            EasyUITreeNode node = new EasyUITreeNode();
            node.setId(tbItemCat.getId());
            node.setText(tbItemCat.getName());
            //getIsParent是否父节点，是的话closed不是的话open
            node.setState(tbItemCat.getIsParent()? "closed":"open");
            result.add(node);
        }
        return result;
    }
}
