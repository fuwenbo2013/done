//@ sourceURL=user.js
var userId;

$(function(){
	//当第一次点击用户管理时,带有模糊条件的分页查询,查询时第一页
	findUsersByPage(1)
	//给用户的模糊搜索添加click事件
	$("#userPanel form .input-group button").click(function(){
		geshu=findUsersByPage(1);
		alert(geshu);
	});
	//给"详情"标签页添加click事件
	$('#user_tab a:eq(1)').click(function (e) {
		  e.preventDefault();
		  $(this).tab('show');
		  $("#detailPanel .media").html('请从"列表"标签中选择一个具体用户信息');
    });
	//给"新增"标签页添加click事件
	$('#user_tab a:eq(2)').click(function(e){
		  e.preventDefault();
		  $(this).tab('show');
		  //查询所有数据库真实角色信息
		  findAllRoles();
	});
	//给新增的表单添加submit事件
	$("#addUserPanel form").submit(function(){
		
		return addUser();
	});
	//给"编辑"的表单添加submit的事件
	$("#editUser form").submit(function(){
		return updateUser();
	});
	//给"删除"modal的确定按钮添加click事件
	$(".bs-example-modal-sm button:eq(1)").click(function(){
		alert("deleteUser");
		deleteUser();
		
	});
});
//删除用户信息
function deleteUser(){
	alert("deleteUser111-->"+userId);
	$.ajax({
		url:basePath+"user/deleteUser/"+userId,
		type:"delete",
		dataType:"json",
		success:function(result){
			
			alert(result.message);
			
		},
		error:function(){
			alert("请求失败!!!");
		}
	});
}
function deleteUserClick(uid){
	userId=uid;
}
//修改用户信息
function updateUser(){
	//获取所有的新的用户信息
	var loginName=$("#editUser form #inputEmail").val();
	var password=$("#editUser form #inputPassword").val();
	var password2=$("#editUser form #inputPassword2").val();
	var nickName=$("#editUser form #nickName").val();
	var age=$("#editUser form #age").val();
	var sex=$("#editUser form input[name=user-type]:checked").val();
	//获取所有对勾的checkbox对象(在html中如果有相同对象,默认为数组)
	var checkboxs=$("#editUser form input[type=checkbox]:checked");
	var roleIds=[];//js的数组就是用[]来初始化
	var roleString="";//构建角色名称,名称之间用逗号间隔
	//循环遍历checkbox对象
	$(checkboxs).each(function(index,checkboxObj){
		var rid=$(checkboxObj).val();		
		var rname=$(checkboxObj).next().text();
		roleString+=rname+",";
		roleIds.push(rid);
	});
	//alert(roleIds+"   "+roleString);
	if(password!=password2){
		alert("两个密码要相同");
		$("#editUser form #inputPassword").focus();
		return false;
	}
	if(age<1 || age>200){
		alert("年龄不符合");
		$("#editUser form #age").focus();
		return false;
	}
	//异步请求更新用户数据(借助ajaxfileupload.js框架)
	$.ajaxFileUpload({
		url:"/tes_ssm/user/updateUser",
		secureuri:false,
		fileElementId:"updateHeadpicture",
		type:"post",
		data:{
			"id":userId,
			"loginName":loginName,
			"password":password,
			"nickName":nickName,
			"age":age,
			"sex":sex,
			"roleIds":roleIds
		},
		dataType:"text",
		success:function(data,status){
			//alert(data);
			data=data.replace(/<PRE.*?>/g,'');
			data=data.replace("<PRE>",'');
			data=data.replace("</PRE>",'');
			data=data.replace(/<pre.*?>/g,'');
			data=data.replace("<pre>",'');
			data=data.replace("</pre>",'');
			alert(data);
			//关掉modal模态框
			$("#editUser").modal("toggle");
			$("#tr_"+userId).find("td:eq(1)").html(loginName);
			$("#tr_"+userId).find("td:eq(2)").html(nickName);
			$("#tr_"+userId).find("td:eq(7)").html(roleString);
			
		},
		error:function(){
			alert("请求失败!");
		}
	});
	return false;
}
//点击指定用户项的"编辑"
function updateUserClick(uid){
	userId=uid
	//给编辑的表单添加用户的旧数据
	//从数据据库查询用户信息
	$.ajax({
		url:basePath+"user/findUserById/"+uid,
		type:"get",
	    dataType:"json",
	    success:function(result){
	    	if(result.status==1){
	    		var user=result.data;
	    		var rolesOfUser=user.roles;
	    		$("#editUser form #inputEmail").val(user.loginName);
	    		$("#editUser form #nickName").val(user.nickName);
	    		$("#editUser form #age").val(user.age);
	    		if(user.sex=='男'){
	    			$("#editUser form input[value=男]").attr("checked","checked");
	    		}else{
	    			$("#editUser form input[value=女]").attr("checked","checked");
	    		}
	    		
	    		//发送异步请求查询所有的角色信息
	    		$.ajax({
		    		url:basePath+"role/findAllRoles",
		    		type:"get",
		    		dataType:"json",
		    		success:function(result){
		    			//给新增标签页中的拉列表添加角色选项
		    			if(result.status==1){
		    				var allroles=result.data;
		    				//allroles.indexOf(role);
		    				$("#editUser form #allRoleName").html('');
		    				$(allroles).each(function(index,role){
		    					var flag=false;
		    					var checkBox1=`<input type="checkbox" name="roleName" value="${role.id}" /><span>${role.name}</span>&nbsp;&nbsp;`;
		    					var checkBox2=`<input type="checkbox" name="roleName" value="${role.id}" checked="checked" /><span>${role.name}</span>&nbsp;&nbsp;`;
		    					$(rolesOfUser).each(function(n,value){
		    						if(value.name==role.name){
		    							$("#editUser form #allRoleName").append(checkBox2);
		    							flag=true;
		    						}						
		    					});
		    					if(!flag){
		    						//都没匹配上加添加没有checked
		    						$("#editUser form #allRoleName").append(checkBox1);
		    					}					
		    					if((index+1)%3==0){
		    						$("#editUser form #allRoleName").append("<br />");
		    					}
		    				});
		    				
		    								
		    			}else if(result.status==0){
		    			}
		    		},
		    		error:function(){
		    			alert("请求失败");
		    		}
		    	});
	    	}
	    },
	    error:function(){
	    	alert("请求失败!!");
	    }
	});	
}
//添加用户
function addUser(){
	//获取页面的数据
	var loginName=$("#addUserPanel #add_loginName").val();
	var password=$("#addUserPanel #add_inputPassword").val();
	var password2=$("#addUserPanel #add_inputPassword2").val();
	var nickName=$("#addUserPanel #add_nickName").val();
	var roleId=$("#addUserPanel #roleCategory").val();
	var age=$("#addUserPanel #add_age").val();
	var sex=$("#addUserPanel input[name=user-type]:checked").val();

	if(password!=password2){
		return false;
	}
	if(age<1 || age>200){
		return false;
	}
	//alert(loginName+"  "+password+"   "+nickName+"   "+roleId+"   "+age+"   "+sex);
	//异步请求添加用户数据(借助ajaxfileupload.js框架)
	$.ajaxFileUpload({
		url:"/tes_ssm/user/addUserBukesiyi",
		secureuri:false,
		fileElementId:"addHeadPicture",
		type:"post",
		data:{
			"loginName":loginName,
			"password":password,
			"nickName":nickName,
			"age":age,
			"sex":sex,
			"roleId":roleId
		},
		dataType:"text",
		success:function(data,status){
			//alert(data);
			data=data.replace(/<PRE.*?>/g,'');
			data=data.replace("<PRE>",'');
			data=data.replace("</PRE>",'');
			data=data.replace(/<pre.*?>/g,'');
			data=data.replace("<pre>",'');
			data=data.replace("</pre>",'');
			alert(data);
		},
		error:function(){
			alert("请求失败!");
		}
	});

	return false;
}
//查询所有数据库真实角色信息
function findAllRoles(){
	$.ajax({
		url:basePath+"role/findAllRoles",
		type:"get",
		dataType:"json",
		success:function(result){
			//给新增标签页中的拉列表添加角色选项
			if(result.status==1){
				var roles=result.data;
				$("#addUserPanel #roleCategory").html('');
				$(roles).each(function(index,role){
					var select_option=`<option value="${role.id}">${role.name}</option>`;
					$("#addUserPanel #roleCategory").append(select_option);
				});
								
			}else if(result.status==0){
				//给提示信息
				$("#addUserPanel #roleCategory").append('<option value="${role.id}">没有角色数据,不能添加用户</option>')
			}
		},
		error:function(){
			alert("请求失败");
		}
	});
}
//获取到指定的用户信息,并给页面中的详情添加数据
function findUserById(rid,loginName,nickName,loginType,sex,age,score,regDate,isLock,roleString,head){
	//alert('test');
	var details=`<div class="media-left">
				    <a href="#">
				    <img width="64" height="64" class="media-object img-circle" src="head/${head}" alt="头像">
				  </a>
				</div>
				<div class="media-body">
				  <h1 class="media-heading">${loginName}</h1>
				  <br/>
				  <p>账号类型：<span>${loginType}</span></p>
				  <p>昵称：<span>${nickName}</span></p>
				  <p>性别：<span>${sex}</span></p>
				  <p>年龄：<span>${age}</span></p>
				  <p>积分：<span>${score}</span></p>
				  <p>注册日期：<span>${new Date(regDate).toLocaleDateString().replace("/","-").replace("/","-")}</span></p>
				  <p>锁定：<span>${isLock}</span></p>
				  <p>角色：<span>${roleString}</span></p>
				</div>`;
	$("#detailPanel .media").html(details);
	$('#user_tab li:eq(1) a').tab('show') // Select third tab (0-indexed)
	
}
function findUsersByPage(currentPage){
	var geshu;
	//处理用户模糊关键字
	var userKeyword=$("#userPanel form input[type=text]").val();
	if(userKeyword==''){
		userKeyword="undefined";
	}
	//发送异步请求,分页查询
	$.ajax({
		url:basePath+"user/findUsersByPage",
		type:"get",
		data:{
			"currentPage":currentPage,
			"userKeyword":userKeyword
		},
		dataType:"json",
		success:function(result){
			//分页数据查询成功
			if(result.status==1){
				//给表格赋值tr
				var page=result.data;
				var users=page.data;
				//清空表格的tbody
				$("#user_table tbody").html("");
				geshu=page.totalCount;
			    //循环集合数据并追加到表格中
				$(users).each(function(index,user){
					//没进一次循环获得一个user对象,查看user对象中的roles是否有数据
					var roles=user.roles;
					var roleString='';
					$(roles).each(function(n,role){
						roleString+=role.name+',';
					});
					if(roleString.length==0){
						roleString="无角色"
					}else{
						roleString=roleString.substring(0,roleString.length-1);
						//roleString=roleString.substr(0,roleString.lenth-1);
					}
					//var dateString=new Date(user.regDate).toLocaleDateString().replace("/","-").replace("/","-");
					//alert("dateString-->"+dateString);
					var tr=`<tr id="tr_${user.id}">
				                <td>${index+1}</td>
				                <td><a href="javascript:findUserById('${user.id}','${user.loginName}','${user.nickName}','${user.loginType}','${user.sex}',${user.age},${user.score},${user.regDate},'${user.isLock}','${roleString}','${user.head}');">${user.loginName}</a></td>
				                <td>${user.nickName}</td>
				                <td>${user.loginType}</td>
				                <td>${user.score}</td>
				                <td>${new Date(user.regDate).toLocaleDateString().replace("/","-").replace("/","-")}</td>
				                <td>${user.isLock}</td>
				                <td>${roleString}</td>
				                <td>
				                  <a onclick="updateUserClick('${user.id}')" href="" data-toggle="modal" data-target="#editUser"><span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>编辑</a>
				                  <a onclick="deleteUserClick('${user.id}')" href="" data-toggle="modal" data-target=".bs-example-modal-sm"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除</a>
				                </td>
				            </tr>`;
				    $("#user_table tbody").append(tr);
				});
				
				//给分页组件条赋值(新的做法)
				$("#user_pagination").html('');//清空原有的分页条
				var options={
					currentPage:currentPage,//当前页,当前页面显示数据的那个页号
					totalPages:page.totalPage,//总页数
					numberOfPages:5,//超链接的个数
					onPageClicked:function(event, originalEvent, type,page){
						//page是用户点击的页号,就是准备跳转page页上去
						//alert(page);
						findUsersByPage(page);
					}					
				};
				$("#user_pagination").bootstrapPaginator(options);
			}
			
		},
		error:function(){
			alert("请求失败!!");
		}
	});
	return geshu;
}