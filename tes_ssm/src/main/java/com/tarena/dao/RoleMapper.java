package com.tarena.dao;

import java.util.List;

import com.tarena.entity.Role;
import com.tarena.vo.Page;

public interface RoleMapper {
	//角色的分页
	public int getCount(Page page);
	public List<Role> getRolesByPage(Page page);
	//添加角色
	public int addRole(Role role);
	//更新角色
	public int updateRole(Role role);
	//删除角色
	public int deleteRole(String roleId);
	//查询所有角色信息
	public List<Role> findAllRoles();
}
