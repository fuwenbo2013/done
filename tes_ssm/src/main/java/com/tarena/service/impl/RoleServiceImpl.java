package com.tarena.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tarena.dao.RoleMapper;
import com.tarena.entity.Role;
import com.tarena.service.RoleService;
import com.tarena.util.PageUtil;
import com.tarena.util.UUIDUtil;
import com.tarena.vo.Page;
import com.tarena.vo.Result;

/**
 * @author Administrator
 *
 */
@Service("roleService")
public class RoleServiceImpl implements RoleService {
	@Resource(name="pageUtil")
	private PageUtil pageUtil;
	@Resource(name="roleMapper")
	private RoleMapper roleMapper;
	@Override
	public Result findRolesByPage(Page page) {
		Result result=new Result();
		//currentPage和roleKeyword由用户提供
		String roleKW=page.getRoleKeyword();
		roleKW="undefined".equals(roleKW)? "%%" : "%"+roleKW+"%";
		page.setRoleKeyword(roleKW);
		//获取pageSize,从属性文件取
		page.setPageSize(this.pageUtil.getPageSize());
		//System.out.println(this.pageUtil.getPageSize()+"   "+this.pageUtil.getShowNum_a());
		//查询数据库获取总记录数
		int totalCount=this.roleMapper.getCount(page);
		//System.out.println(totalCount);
		page.setTotalCount(totalCount);
		//计算总页数
		int totalPage=(totalCount%pageUtil.getPageSize()==0)? (totalCount/pageUtil.getPageSize()) : (totalCount/pageUtil.getPageSize())+1;
		page.setTotalPage(totalPage);
		//计算前一页
		if(page.getCurrentPage()==1){
			page.setPreviousPage(1);
		}else{
			page.setPreviousPage(page.getCurrentPage()-1);
		}		
		//计算后一页
		if(page.getCurrentPage()==totalPage){
			page.setNextPage(totalPage);
		}else{
			page.setNextPage(page.getCurrentPage()+1);
		}
		//查询数据库获取当前页的那页数据(集合)
		List<Role> roles=this.roleMapper.getRolesByPage(page); 
		page.setData(roles);
		//计算html页面上分页组件的超链接个数			
		
		page.setNums(pageUtil.getFenYe_a_Num(page.getCurrentPage(), page.getPageSize(), totalCount, totalPage));
		
		System.out.println(page);
		result.setStatus(1);
		result.setData(page);
		return result;
	}
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public Result addRole(String roleName) {
		Result result=new Result();
		Role role=new Role();
		role.setId(UUIDUtil.getUUID());
		role.setName(roleName);
		int rowAffect=this.roleMapper.addRole(role);
		result.setStatus(1);
		result.setMessage("添加角色成功!");
		
		return result;
	}
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public Result updateRole(Role role) {
		Result result=new Result();
		int rowAffect=this.roleMapper.updateRole(role);
		result.setStatus(1);
		result.setMessage("更新角色成功!!");
		
		return result;
	}
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public Result deleteRole(String roleId) {
		Result result=new Result();
		int rowAffect=this.roleMapper.deleteRole(roleId);
		result.setStatus(1);
		result.setMessage("删除角色成功!!");
		return result;
	}
	@Override
	public Result findAllRoles() {
		Result result=new Result();
		List<Role> roles=this.roleMapper.findAllRoles();
		result.setStatus(1);
		result.setData(roles);
		return result;
	}

}
