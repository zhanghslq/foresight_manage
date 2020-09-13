package com.zhs.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhs.entity.*;
import com.zhs.exception.MyException;
import com.zhs.mapper.AdminMapper;
import com.zhs.model.vo.AdminAddDataVO;
import com.zhs.service.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 一些公用数据（下拉框选择的） 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private AdminRoleService adminRoleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PageService pageService;

    @Autowired
    private RolePageService rolePageService;

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private ContactsService contactsService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ExpertService expertService;

    @Autowired
    private AdminMapper adminMapper;



    @Override
    public List<Role> listRoleByAdminId(Long adminId) {
        ArrayList<Role> result = new ArrayList<>();
        QueryWrapper<AdminRole> adminRoleQueryWrapper = new QueryWrapper<>();
        adminRoleQueryWrapper.eq("admin_id",adminId);
        List<AdminRole> list = adminRoleService.list(adminRoleQueryWrapper);
        if(list.size()==0){
            return result;
        }
        List<Long> roleIds = list.stream().map(AdminRole::getRoleId).collect(Collectors.toList());
        return roleService.listByIds(roleIds);
    }

    @Override
    public List<Page> listPageByAdminId(Long adminId) {
        ArrayList<Page> result = new ArrayList<>();
        QueryWrapper<AdminRole> adminRoleQueryWrapper = new QueryWrapper<>();
        adminRoleQueryWrapper.eq("admin_id",adminId);
        List<AdminRole> list = adminRoleService.list(adminRoleQueryWrapper);
        if(list.size()==0){
            return result;
        }
        List<Long> roleIds = list.stream().map(AdminRole::getRoleId).collect(Collectors.toList());
        QueryWrapper<RolePage> rolePageQueryWrapper = new QueryWrapper<>();
        rolePageQueryWrapper.in("role_id",roleIds);
        List<RolePage> rolePages = rolePageService.list(rolePageQueryWrapper);
        if(rolePages.size()==0){
            return result;
        }
        List<Long> pageIds = rolePages.stream().map(RolePage::getPageId).collect(Collectors.toList());

        return pageService.listByIds(pageIds);
    }

    @Override
    public List<Page> listPageByRoleId(Long roleId) {
        ArrayList<Page> result = new ArrayList<>();
        QueryWrapper<RolePage> rolePageQueryWrapper = new QueryWrapper<>();
        rolePageQueryWrapper.eq("role_id",roleId);
        List<RolePage> rolePages = rolePageService.list(rolePageQueryWrapper);
        if(rolePages.size()==0){
            return result;
        }
        List<Long> pageIds = rolePages.stream().map(RolePage::getPageId).collect(Collectors.toList());
        return pageService.listByIds(pageIds);
    }

    @Override
    public Admin login(String username, String password) {
        Admin admin = queryByUserName(username);

        if(Objects.isNull(admin)){
            throw new  MyException("用户不存在");
        }
        if(Objects.equals(admin.getStatus(), 1)){
            throw new MyException("账号已被冻结，联系管理员恢复");
        }
        String salt = admin.getSalt();
        String pass = SecureUtil.md5(password + salt);
        if(admin.getPassword().equals(pass)){
            Subject subject = SecurityUtils.getSubject();
            subject.login(new UsernamePasswordToken(String.valueOf(admin.getId()),pass));
        }else {
            throw new MyException("密码不正确");
        }
        adminMapper.increaseLoginCount(admin.getId());
        return admin;
    }

    @Override
    public Admin queryByUserName(String username) {
        QueryWrapper<Admin> adminQueryWrapper = new QueryWrapper<>();
        adminQueryWrapper.eq("username",username);
        return getOne(adminQueryWrapper);
    }

    @Override
    public Admin queryByMobile(String mobile) {
        QueryWrapper<Admin> adminQueryWrapper = new QueryWrapper<>();
        adminQueryWrapper.eq("mobile",mobile);
        return getOne(adminQueryWrapper);
    }

    @Override
    public void register(String username, String password, String realName, String mobile, Long roleId) {
        Admin admin = queryByUserName(username);
        if(!Objects.isNull(admin)){
            throw new MyException("用户名已存在");
        }
        if(!Objects.isNull(mobile)){
            Admin byMobile = queryByMobile(mobile);
            if(!Objects.isNull(byMobile)){
                throw new MyException("手机号已存在");
            }
        }
        String salt = RandomUtil.randomString(6);
        admin = new Admin();
        admin.setUsername(username);
        admin.setSalt(salt);
        admin.setRealName(realName);
        String newPassword = SecureUtil.md5(password + salt);
        admin.setPassword(newPassword);
        save(admin);
        // 注册完，添加用户角色
        if(!Objects.isNull(roleId)){
            AdminRole adminRole = new AdminRole();
            adminRole.setAdminId(admin.getId());
            adminRole.setRoleId(roleId);
            adminRoleService.save(adminRole);
        }
    }

    @Override
    public void updateUserAndRole(Long adminId, String username, String realName, String mobile, Long roleId) {
        Admin admin = getById(adminId);
        // 修改的时候不能修改密码
        Admin admin1 = queryByUserName(username);
        if(!Objects.isNull(admin1)&&!admin1.getId().equals(admin.getId())){
            throw new MyException("用户名已存在");
        }
        if(!Objects.isNull(mobile)){
            Admin admin2 = queryByMobile(mobile);
            if(!Objects.isNull(admin2)&&!admin.getId().equals(admin2.getId())){
                throw new MyException("手机号已存在");
            }
        }
        admin.setUsername(username);
        admin.setRealName(realName);
        admin.setMobile(mobile);
        updateById(admin);
        if(!Objects.isNull(roleId)){
            QueryWrapper<AdminRole> adminRoleQueryWrapper = new QueryWrapper<>();
            adminRoleQueryWrapper.eq("admin_id",adminId);
            List<AdminRole> list = adminRoleService.list(adminRoleQueryWrapper);
            AdminRole adminRole = new AdminRole();
            adminRole.setRoleId(roleId);
            adminRole.setAdminId(adminId);
            if(list.size()==0){
                adminRoleService.save(adminRole);
            }else if(list.size()==1){
                AdminRole adminRole1 = list.get(0);
                adminRole.setId(adminRole1.getId());
                adminRoleService.updateById(adminRole);
            }else {
                List<Long> adminRoleIds = list.stream().map(AdminRole::getId).collect(Collectors.toList());
                Long adminRoleId = adminRoleIds.get(0);
                adminRole.setId(adminRoleId);
                adminRoleService.updateById(adminRole);
                adminRoleIds.remove(0);
                adminRoleService.removeByIds(adminRoleIds);
            }
        }

    }

    @Override
    public void updatePassword(Long adminId,  String password) {
        Admin byId = getById(adminId);
        if(Objects.isNull(byId)){
            throw new MyException("用户不存在");
        }
        String salt = byId.getSalt();

        String newPassword = SecureUtil.md5(password + salt);
        byId.setPassword(newPassword);
        updateById(byId);
    }

    @Override
    public AdminAddDataVO queryAddData(Long adminId) {
        QueryWrapper<Company> companyQueryWrapper = new QueryWrapper<>();
        companyQueryWrapper.eq("admin_id",adminId);
        int companyCount = companyService.count(companyQueryWrapper);

        QueryWrapper<Organization> organizationQueryWrapper = new QueryWrapper<>();
        organizationQueryWrapper.eq("admin_id",adminId);
        int organizationCount = organizationService.count(organizationQueryWrapper);

        QueryWrapper<Resume> resumeQueryWrapper = new QueryWrapper<>();
        resumeQueryWrapper.eq("admin_id",adminId);
        int resumeCount = resumeService.count(resumeQueryWrapper);

        QueryWrapper<Expert> expertQueryWrapper = new QueryWrapper<>();
        expertQueryWrapper.eq("admin_id",adminId);
        int expertCount = expertService.count(expertQueryWrapper);

        QueryWrapper<Contacts> contactsQueryWrapper = new QueryWrapper<>();
        contactsQueryWrapper.eq("admin_id",adminId);
        int contactCount = contactsService.count(contactsQueryWrapper);
        AdminAddDataVO adminAddDataVO = new AdminAddDataVO();
        adminAddDataVO.setOrganizationCount(organizationCount+companyCount);
        adminAddDataVO.setResumeCount(resumeCount);
        adminAddDataVO.setImportCount(expertCount+resumeCount+contactCount);
        return adminAddDataVO;
    }
}
