package com.tarena.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tarena.entity.Role;
import com.tarena.service.RoleService;
import com.tarena.vo.Page;
import com.tarena.vo.Result;

@Controller
@RequestMapping("role/")
public class RoleController {
	@Resource(name="roleService")
	private RoleService roleService;
	
	@RequestMapping(value="findRolesByPage",method=RequestMethod.GET)
	@ResponseBody
	public Result findRolesByPage(Page page){
		Result result=null;
		
		result=this.roleService.findRolesByPage(page);
		
		return result;
	}
	@RequestMapping(value="addRole/{roleName}",method=RequestMethod.POST)
	@ResponseBody
	public Result addRole(@PathVariable(value="roleName") String roleName){
		Result result=null;
		System.out.println(roleName);
		
		result=this.roleService.addRole(roleName);
		return result;
	}
	@RequestMapping(value="updateRole",method=RequestMethod.POST)
	@ResponseBody
	public Result updateRole(Role role){
		Result result=null;
		System.out.println(role.getId()+"   "+role.getName());
		
		result=this.roleService.updateRole(role);
		return result;
	}
	@RequestMapping(value="deleteRole/{roleId}",method=RequestMethod.DELETE)
	@ResponseBody
	public Result deleteRole(@PathVariable(value="roleId") String roleId){
		Result result=new Result();
		System.out.println(roleId);
		
		result=this.roleService.deleteRole(roleId);
		
		return result;
	}
	@RequestMapping(value="findAllRoles",method=RequestMethod.GET)
	@ResponseBody
	public Result findAllRoles(){
		Result result=null;
		
		result=this.roleService.findAllRoles();
		
		return result;
	}
}
