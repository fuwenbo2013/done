package com.tarena.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.tarena.entity.User;
import com.tarena.service.UserService;
import com.tarena.vo.Page;
import com.tarena.vo.Result;

@Controller//用spring实例化此类对象,用spring_mvc.xml中的<context:componentScan  basePath="com.tarena.controller"
@RequestMapping("user/")
public class UserController {
	@Resource(name="userService")
	private UserService userService;
	
	@RequestMapping(value="login/name/{lName}/password/{pwd}",method=RequestMethod.GET)
	@ResponseBody//{"status":1,"message":"登录成功","data":{"id":1,"loginName":"wt_zss@126.com"}}
	public Result login(
			@PathVariable(value="lName") String loginName,
			@PathVariable(value="pwd") String password,
			HttpSession session){
		System.out.println(loginName+"   "+password);
		Result result=null;
		
		//调用登录业务
		result=this.userService.login(loginName,password,session);
		
		return result;
	}
	@RequestMapping(value="logout",method=RequestMethod.DELETE)
	@ResponseBody
	public Result logout(HttpSession session){
		Result result=new Result();
		//清除session
		session.invalidate();
		result.setStatus(1);
		result.setMessage("清除成功");
		return result;
	}
	@RequestMapping(value="findUsersByPage",method=RequestMethod.GET)
	@ResponseBody
	public Result findUsersByPage(Page page){
		Result result=null;
		result=this.userService.findUsersByPage(page);		
		return result;
	}
	@RequestMapping(value="addUserBukesiyi",method=RequestMethod.POST)
	//@ResponseBody//回json数据
	public void addUser(User user,
            String roleId,
			MultipartFile addPicture,
			HttpServletRequest request,
			HttpServletResponse response){
		
		this.userService.addUser(user,roleId,request,response,addPicture);
	}
	@RequestMapping(value="findUserById/{userId}",method=RequestMethod.GET)
	@ResponseBody//回json数据
	public Result findUserById(@PathVariable(value="userId") String userId){
		Result result=null;
		result=this.userService.findUserById(userId);
		return result;
	}
	@RequestMapping(value="updateUser",method=RequestMethod.POST)
	//@ResponseBody//回json数据
	public void updateUser(User user,
            String[] roleIds,
			MultipartFile updatePicture,
			HttpServletRequest request,
			HttpServletResponse response){
		
		this.userService.updateUser(user,roleIds,updatePicture,request,response);
		
	}
	@RequestMapping(value="deleteUser/{userId}",method=RequestMethod.DELETE)
	@ResponseBody//回json数据
	public Result deleteUser(@PathVariable(value="userId") String userId){
		System.out.println("deleteUser-->"+userId);
		Result result=null;
		result=this.userService.deleteUser(userId);
		return result;
	}
}
