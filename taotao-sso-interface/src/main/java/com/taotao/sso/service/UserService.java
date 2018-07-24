package com.taotao.sso.service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;

public interface UserService {
    public TaotaoResult check(String param, Integer type);
    public TaotaoResult register(TbUser user);
    public TaotaoResult login(String username, String password);
    public TaotaoResult getUserMessageByToken(String token);
    public TaotaoResult logout(String token);
}
