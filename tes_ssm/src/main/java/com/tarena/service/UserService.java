package com.tarena.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.multipart.MultipartFile;

import com.tarena.entity.User;
import com.tarena.vo.Page;
import com.tarena.vo.Result;

public interface UserService {
	//用户的登录
	public Result login(String loginName,String password,HttpSession session);
    //用户的分页业务(不带角色类型)
	public Result findUsersByPage(Page page);
	//添加用户信息
	public void addUser(User user, String roleId, HttpServletRequest request, HttpServletResponse response,
			MultipartFile head);
	//根据id查询用户信息(用户更新时使用)
	public Result findUserById(String userId);
	//更新用户信息
	public void updateUser(User user, String[] roleIds, MultipartFile updatePicture, HttpServletRequest request,
			HttpServletResponse response);
	//删除用户信息
	public Result deleteUser(String userId);
}
