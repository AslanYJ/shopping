package com.taotao.content.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentService;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class ContentServiceImpl implements ContentService {
    private final static String INDEX_CONTENT = "INDEX_CONTENT";
    @Autowired
    private TbContentMapper tbContentMapper;
    @Autowired
    private JedisClient jedisClient;
    @Override
    public EasyUIDataGridResult getContentList(long categoryId, int page, int rows) {
        //设置分页查询信息
        PageHelper.startPage(page,rows);
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<TbContent> list = tbContentMapper.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo(list);
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setTotal(pageInfo.getTotal());
        result.setRows(list);
        return result;
    }

    @Override
    public TaotaoResult addContent(TbContent tbContent) {
        tbContent.setCreated(new Date());
        tbContent.setUpdated(new Date());
        tbContentMapper.insert(tbContent);
        //同步缓存
        //删除对应的缓存信息
        //下次同步的时候是最新的数据
        jedisClient.hdel(INDEX_CONTENT, tbContent.getCategoryId().toString());
        return TaotaoResult.ok();

    }

    @Override
    public TaotaoResult updateContent(TbContent tbContent) {

        tbContent.setUpdated(new Date());
        tbContentMapper.updateByPrimaryKeyWithBLOBs(tbContent);
        return TaotaoResult.ok();
    }

    @Override
    public TaotaoResult deleteContents(String ids) {
        String[] idList = ids.split(",");
        for (String id:
            idList ) {
            tbContentMapper.deleteByPrimaryKey(Long.valueOf(id));
        }
        return TaotaoResult.ok();
    }
    //获取首页大广告
    @Override
    public List<TbContent> getContentListByCid(long cid) {
        try{
            //在缓存中查看有没有已经缓存的数据
            String json = jedisClient.hget(INDEX_CONTENT,cid + "");
            //查询到结果，把json转换成List返回
            if(StringUtils.isNotBlank(json)) {
                List<TbContent> list = JsonUtils.jsonToList(json,TbContent.class);
                return list;
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        //没有设置缓存的时候正常查询
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(cid);
        List<TbContent> list = tbContentMapper.selectByExample(example);
        //查询以后将结果放进缓存中
        try{
            jedisClient.hset(INDEX_CONTENT,cid + "",JsonUtils.objectToJson(list));

        }catch(Exception e) {
            e.printStackTrace();
        }
        //返回结果
        return list;
    }
}
