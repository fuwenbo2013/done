package com.tarena.service;

import com.tarena.entity.Role;
import com.tarena.vo.Page;
import com.tarena.vo.Result;

public interface RoleService {
	//角色分页业务
	public Result findRolesByPage(Page page);
    //添加角色业务
	public Result addRole(String roleName);
	//添加角色信息
	public Result updateRole(Role role);
	//删除角色
	public Result deleteRole(String roleId);
	//查询所有的角色的信息(给新增用户使用)
	public Result findAllRoles();
}
