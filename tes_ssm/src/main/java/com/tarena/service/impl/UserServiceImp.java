package com.tarena.service.impl;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tarena.dao.ActivityMapper;
import com.tarena.dao.CommentMapper;
import com.tarena.dao.ParticipationMapper;
import com.tarena.dao.UserMapper;
import com.tarena.dao.VideoMapper;
import com.tarena.entity.User;
import com.tarena.entity.UserRole;
import com.tarena.service.UserService;
import com.tarena.util.CommonValue;
import com.tarena.util.PageUtil;
import com.tarena.util.PrintWriterUtil;
import com.tarena.util.UUIDUtil;
import com.tarena.util.UploadUtil;
import com.tarena.vo.Page;
import com.tarena.vo.Result;
@Service("userService")
public class UserServiceImp implements UserService {
	@Resource(name="pageUtil")
	private PageUtil pageUtil;
	
	@Resource(name="userMapper")
	private UserMapper userMapper;
	
	@Resource(name="participationMapper")
	private ParticipationMapper participationMapper;
	@Resource(name="activityMapper")
	private ActivityMapper activityMapper;
	@Resource(name="commentMapper")
	private CommentMapper commentMapper;
	@Resource(name="videoMapper")
	private VideoMapper videoMapper;
	
	
	@Override
	public Result login(String loginName, String password,HttpSession session) {
		Result result=new Result();
		User user=new User();
		user.setLoginName(loginName);
		user.setPassword(password);
		
		String id=this.userMapper.login(user);
		if(id!=null){
			session.setAttribute("loginName", loginName);
			result.setStatus(1);
			result.setMessage("登录成功");
		}else{
			result.setStatus(0);
			result.setMessage("登录失败");
		}
		
		return result;
	}
	@Override
	public Result findUsersByPage(Page page) {
		Result result=new Result();
		//处理模糊关键字
		String ukw=page.getUserKeyword();
		ukw="undefined".equals(ukw)? "%%" : "%"+ukw+"%";
		page.setUserKeyword(ukw);
	
		//处理pageSize
		page.setPageSize(pageUtil.getPageSize());
		//处理totalCount
		int totalCount=this.userMapper.getCount(page);
		page.setTotalCount(totalCount);
		//处理tatalPage
		int totalPage=(totalCount%pageUtil.getPageSize()==0)? (totalCount/pageUtil.getPageSize()) : (totalCount/pageUtil.getPageSize())+1;
		page.setTotalPage(totalPage);
		//处理前一页(待定)
		
		//处理下一页(待定)
		
		//获取当前页的数据
		List<User> users=this.userMapper.getUserByPage(page);
		page.setData(users);		
		//处理页面中分页组件中的超链接的个数(待定)
		
		result.setStatus(1);
		result.setData(page);
		return result;
	}
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void addUser(User user, String roleId, HttpServletRequest request, HttpServletResponse response,
			MultipartFile picture) {
		//定位服务端的存储图片的路径
		String realPath=request.getServletContext().getRealPath("/head");
		File realFile=new File(realPath);
		if(!realFile.exists()) realFile.mkdir();
		//预备必要的数据
		String uuid=UUIDUtil.getUUID();
		System.out.println("userId="+uuid);
		String oriFileName=null;
		String imageFileName=null;
		//判断上传的文件是否存在
		if(picture==null || picture.isEmpty()){
			//给用户存储默认头像
			user.setHead("default.png");
		}else{
			//说明头像正常上传成功
			//获取图片的相关信息
			oriFileName=picture.getOriginalFilename();
			String contentType=picture.getContentType();
			long size=picture.getSize();
			//判断头像类型是否符合
			if(!CommonValue.contentTypes.contains(contentType)){
				//给客户端提示消息
				PrintWriterUtil.printMessageToClient(response, "文件类型不合格");
				return;
			}else if(size>4194304){
				//判断头像图片文件大小是否符合
				//给客户端提示消息
				PrintWriterUtil.printMessageToClient(response, "文件太大,请重新上传");
				return;
			}			
			//如果都符合就需要把图片做缩放并添加水印
			//File serverPath=new File(realPath,oriFileName);
			boolean flag=UploadUtil.uploadImage(picture, uuid, true, 64, realPath);
		    if(flag){
		    	//说明图片缩放后上传成功
				String originalExtendName=oriFileName.substring(oriFileName.lastIndexOf(".")+1);
				imageFileName=uuid+"."+originalExtendName;
		    	user.setHead(imageFileName);
		    }else{
		    	return;
		    }
		}
		try{
			user.setId(uuid);
			//给user对象存储必要的数据
			//给数据库的t_user表添加用户信息
			this.userMapper.addUser(user);
			//给数据库的t_user_role添加用户和角色关联关系
			UserRole ur=new UserRole();
			ur.setRoleId(roleId);
			ur.setUserId(uuid);
			this.userMapper.addUserRole(ur);
			PrintWriterUtil.printMessageToClient(response, "用户添加成功!!");
		}catch(Exception e){
			//删除服务端的已经上传完的文件
			File deleteFile=new File(realPath+File.separator+imageFileName);
			if(deleteFile.exists()){
				deleteFile.delete();
			}
			e.printStackTrace();
			//能进catch,说明两次数据库操作,至少有一次不成功
			throw new RuntimeException(e);//用来使用spring的事物管理
		}		
	}
	@Override
	public Result findUserById(String userId) {
		Result result=new Result();
		User user=this.userMapper.findUserById(userId);
		result.setStatus(1);
		result.setData(user);
	
		return result;
	}
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public void updateUser(User user, String[] roleIds, MultipartFile updatePicture, HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		//定位上传文件的服务器路径
		String realPath=request.getServletContext().getRealPath("/head");
		File realFile=new File(realPath);
		if(!realFile.exists()){
			realFile.mkdir();
		}
		String oriFileName="";
		//上传文件
		if(updatePicture!=null  && !updatePicture.isEmpty()){
			//开始处理上传文件
			String contentType=updatePicture.getContentType();
			long size=updatePicture.getSize();
			if(!CommonValue.contentTypes.contains(contentType)){
				PrintWriterUtil.printMessageToClient(response, "图片格式不正确");
				return;
			}
			if(size>4194304){
				PrintWriterUtil.printMessageToClient(response, "文件太大");
				return;
			}
			boolean flag=UploadUtil.uploadImage(updatePicture, user.getId(), true, 64, realPath);
			if(!flag){
				PrintWriterUtil.printMessageToClient(response, "文件上传失败!!!");
				return;
			}else{
				oriFileName=updatePicture.getOriginalFilename();
				String extendName=oriFileName.substring(oriFileName.lastIndexOf(".")+1);
				user.setHead(user.getId()+"."+extendName);
			}
		}else{
			PrintWriterUtil.printMessageToClient(response, "必须选择文件上传");
			return;
		}		
		//处理数据库相关
		//更新用户信息
		this.userMapper.updateUser(user);
		//根据用户的id在中间表t_user_role中,删除此用户id的所有的角色id
		this.userMapper.deleteUserRoleByUserId(user.getId());
		//循环给中间表添加用户和角色对应关系
		for(String roleId : roleIds){
			UserRole ur=new UserRole();
			ur.setUserId(user.getId());
			ur.setRoleId(roleId);
			this.userMapper.addUserRole(ur);
		}
		PrintWriterUtil.printMessageToClient(response, "更新用户成功!!!");
		return;
	}
	@Override
	public Result deleteUser(String userId) {
		Result result=new Result();
		
		//删除用户和角色的中间表---------------------------
		//this.userMapper.deleteRolesByUserId(userId);
		this.userMapper.deleteUserRoleByUserId(userId);
		//删除用户和模块的中间表---------------------------
		this.userMapper.deleteModuleByUserId(userId);
		//删除好友列表中有指定用户id---------------------
		this.userMapper.deleteFriendListByUserId(userId);
		//删除活动相关的内容----------------------------
		//先删除用户参与的活动,就是删除活动参与表中的数据
		this.participationMapper.deleteParticipationByUserId(userId);
		//根据用户的id查询出所有的这个用户所发起的活动id
		List<String> activityIds=this.activityMapper.findActivityIds(userId);
		//循环所有的活动id去参与表删除对应的活动参与信息,这个用户发起的活动被别的用户参与
		for(String activityId : activityIds){
			this.participationMapper.deleteParticipationByActivityId(activityId);
		}
		//删除指定用户发起所有的活动信息
		this.activityMapper.deleteActivityByUserId(userId);
		//删除评论---------------------------------
		this.commentMapper.deleteCommentByUserId(userId);
		//删除评论和视频相关-------------------------
		//根据指定的用户id查询出此用户发布的所有视频id
		List<String> videoIds=this.videoMapper.findVideoIdsByUserId(userId);
		//循环所有的视频id删除此视频id对应评论信息,其实处理当期用户发的视频有人评论过
		for(String videoId : videoIds){
			this.commentMapper.deleteCommentByVideoId(videoId);
		}
		//删除历史,缓存表中的信息
		this.userMapper.deleteHistoryCacheByUserId(userId);
		//删除指定视频id的对应的历史缓存信息,其实处理当前用户发布的视频有缓存和历史
		for(String videoId : videoIds){
			this.videoMapper.deleteHistroyCacheByVideoId(videoId);
		}
		//根据用户删除指定用户发布的视频信息
		this.videoMapper.deleteVideoByUserId(userId);
		//删除用户信息-----------------------------
		this.userMapper.deleteUserByUserId(userId);
		
		result.setStatus(1);
		result.setMessage("删除用户成功!!!");
		return result;
	}

	
}
