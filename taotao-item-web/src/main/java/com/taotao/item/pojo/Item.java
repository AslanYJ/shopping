package com.taotao.item.pojo;

import com.taotao.pojo.TbItem;
import org.apache.commons.lang3.StringUtils;

public class Item extends TbItem {
    public Item(TbItem tbItem) {
        //设置item的属性
        this.setBarcode(tbItem.getBarcode());
        this.setCid(tbItem.getCid());
        this.setCreated(tbItem.getCreated());
        this.setId(tbItem.getId());
        this.setImage(tbItem.getImage());
        this.setNum(tbItem.getNum());
        this.setPrice(tbItem.getPrice());
        this.setSellPoint(tbItem.getSellPoint());
        this.setStatus(tbItem.getStatus());
        this.setTitle(tbItem.getTitle());
        this.setUpdated(tbItem.getUpdated());
    }

    public String[] getImages() {
        if(StringUtils.isNotBlank(this.getImage())) {
            String [] images = this.getImage().split(",");
            return  images;
        }
        return null;
    }
}
