package com.zhs.backmanageb.shiro.realm;


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
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
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

        /*List<RolePermissionUriBO> rolePermissionUris;
        if (ShiroThreadLocalUtil.isCurrOperationIsWithoutLogin()) {
            // 当前为免密/免登录访问
            rolePermissionUris = getRolePermissionsWhenWithoutLogin();
        } else {
            // 持有正常登录凭证进行访问
            Long userId = (Long)principals.getPrimaryPrincipal();
            if (DefaultUtil.isDefaultId(userId)) {
                throw new AuthorizationException("用户id非法：" + userId);
            }
            rolePermissionUris = getRolePermissionsOfUser(userId);
        }

        return getAuthInfo(rolePermissionUris);*/
        return null;
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

        Object credentials = token.getCredentials();
        Object principal = token.getPrincipal();
        Admin admin = adminService.queryByUserName(principal.toString());
        if(Objects.isNull(admin)){
            return null;
        }
        // 判断密码是否正确

        // 此步骤前，当前用户已经认证成功，故不再重复认证，直接返回即可
        log.info("用户认证");
        return new SimpleAuthenticationInfo(admin.getUsername(), admin.getPassword(), this.getName());
    }
}
