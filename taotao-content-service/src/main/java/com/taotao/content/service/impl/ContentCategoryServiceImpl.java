package com.taotao.content.service.impl;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.taotao.pojo.TbContentCategoryExample.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
    @Autowired
    private TbContentCategoryMapper tbContentCategoryMapper;
    @Override
    public List<EasyUITreeNode> getContentCategoryList(Long parentId) {
        //创建example
        TbContentCategoryExample example = new TbContentCategoryExample();
        //设置查询条件
        Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        //执行查询
        List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
        List<EasyUITreeNode> result = new ArrayList<>();
        for (TbContentCategory tbContentCategory : list ) {
            EasyUITreeNode node = new EasyUITreeNode();
            node.setId(tbContentCategory.getId());
            node.setText(tbContentCategory.getName());
            node.setState(tbContentCategory.getIsParent()? "closed":"open");
            //添加到结果集中
            result.add(node);
        }
        return result;
    }

    @Override
    public TaotaoResult addContentCategory(Long parentId, String name) {
        //创建一个pojo对象
        TbContentCategory contentCategory = new TbContentCategory();
        //补全对象的属性
        contentCategory.setParentId(parentId);
        contentCategory.setName(name);
        //状态。可选值:1(正常),2(删除)
        contentCategory.setStatus(1);
        //排序，默认为1
        contentCategory.setSortOrder(1);
        contentCategory.setIsParent(false);
        contentCategory.setCreated(new Date());
        contentCategory.setUpdated(new Date());
        //插入到数据库
        tbContentCategoryMapper.insert(contentCategory);
        //判断父节点的状态
        TbContentCategory parent = tbContentCategoryMapper.selectByPrimaryKey(parentId);
        if (!parent.getIsParent()) {
            //如果父节点为叶子节点应该改为父节点
            parent.setIsParent(true);
            //更新父节点
            tbContentCategoryMapper.updateByPrimaryKey(parent);
        }

        //返回结果
        return TaotaoResult.ok(contentCategory);
    }

    @Override
    public TaotaoResult updateContentCategory(Long id, String name) {
        TbContentCategory tbContentCategory =  tbContentCategoryMapper.selectByPrimaryKey(id);
        if(name != null && name.equals(tbContentCategory.getName())){
            return TaotaoResult.ok();
        }
        tbContentCategory.setName(name);
        tbContentCategory.setUpdated(new Date());
        tbContentCategoryMapper.updateByPrimaryKey(tbContentCategory);
        return TaotaoResult.ok();
    }

    @Override
    public TaotaoResult deleteContentCategory(Long id) {
        //从数据库中获取数据
        TbContentCategory tbContentCategory = tbContentCategoryMapper.selectByPrimaryKey(id);
        //删除节点
        this.resuresiveDelete(id);
        ////判断是否更新父节点
        this.updataNode(tbContentCategory.getParentId());
        return TaotaoResult.ok();
    }

    private void resuresiveDelete(Long id) {
        //先获得所有此节点下的所有子节点
        List<TbContentCategory> list = this.getListByParentId(id);
        //如果是空的话，就证明没有子节点可以直接删除
        if(list.size() == 0) {
            //先获得该子节点的父节点
            TbContentCategory deleteNode = tbContentCategoryMapper.selectByPrimaryKey(id);
            //获得父节点的id，便于更新父节点的状态
            Long parentId = deleteNode.getParentId();
            //删除子节点
            tbContentCategoryMapper.deleteByPrimaryKey(id);
            //删除此节点后，判断此节点的父节点是否为子节点，若是，则更新其父节点为子节点
            this.updataNode(parentId);
        }else {
            //有子节点的话就实现递归操作，把其他子节点删除
            //先删除当前节点
            tbContentCategoryMapper.deleteByPrimaryKey(id);
            for (TbContentCategory node:
                 list) {
                //如果该节点下面还有子节点则递归
                if(node.getIsParent()) {
                    //递归删除
                    this.resuresiveDelete(node.getId());
                }else {
                    //到达最后的节点，即最后的子节点下面没有节点了，就直接删除
                    tbContentCategoryMapper.deleteByPrimaryKey(node.getId());
                }
            }
        }
    }
    private void updataNode(Long parentId) {
        //获得其他兄弟节点
        List<TbContentCategory> list = this.getListByParentId(parentId);
        //如果没有兄弟节点则直接更新
        if(list.size() == 0) {
            TbContentCategory node = tbContentCategoryMapper.selectByPrimaryKey(parentId);
            node.setIsParent(false);
            tbContentCategoryMapper.updateByPrimaryKey(node);
        }
    }
    /**
     * 根据父节点获取该节点下的所有兄弟节点
     * @return
     */
    private List<TbContentCategory> getListByParentId(Long parentId) {
        TbContentCategoryExample example = new TbContentCategoryExample();
        //设置查询条件
        Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        criteria.andStatusEqualTo(1);
        List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
        return list;
    }
}
