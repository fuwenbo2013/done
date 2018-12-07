package com.tarena.dao;

import java.util.List;

import com.tarena.entity.User;
import com.tarena.entity.UserRole;
import com.tarena.vo.Page;

public interface UserMapper {
	//登录的数据库操作
	public String login(User user);
    //用户的分页(不带角色类型)
	public int getCount(Page page);
	public List<User> getUserByPage(Page page);
	//添加用户信息
	public void addUser(User user);
	//添加用户和角色的中间表
	public void addUserRole(UserRole ur);
	//根据id查询用户信息
	public User findUserById(String userId);
	//给用户更新信息
	public void updateUser(User user);
	//根据用户的id,在t_user_role表中删除指定用户id的角色id
	public void deleteUserRoleByUserId(String id);
	
	
	
	
	//删除用户和角色中间表中的指定用户id的所有的角色
	public void deleteRolesByUserId(String userId);
	//删除用户和模块的中间表,根据指定userid
	public void deleteModuleByUserId(String userId);
	//删除好友列表中有指定用户id的数据项
	public void deleteFriendListByUserId(String userId);
	//根据用户id删除此用户历史和缓存中的数据
	public void deleteHistoryCacheByUserId(String userId);
	//根据用id删除用户的信息
	public void deleteUserByUserId(String userId);
	
	
	
	
	
}
