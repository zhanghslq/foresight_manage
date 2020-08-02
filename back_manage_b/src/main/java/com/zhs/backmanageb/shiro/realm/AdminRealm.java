package com.zhs.backmanageb.shiro.realm;


import cn.hutool.crypto.SecureUtil;
import com.zhs.backmanageb.entity.Admin;
import com.zhs.backmanageb.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * 认证与授权Realm，类比于DAO层
 * @author: stonelu
 * @create: 2020-03-12 10:57
 **/
@Slf4j
public class AdminRealm extends AuthorizingRealm  {

    @Autowired
    private AdminService adminService;

    /**
     * 授权
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Subject subject = SecurityUtils.getSubject();
        // 以防万一，需要进行登录验证
        if (!subject.isAuthenticated()) {
            throw new AuthorizationException("未登录");
        }
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        return simpleAuthorizationInfo;
    }

    /**
     * 认证
     *
     * 用户账号信息
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        return new SimpleAuthenticationInfo(token.getPrincipal().toString(),token.getCredentials().toString(),
                this.getName());

    }
}
