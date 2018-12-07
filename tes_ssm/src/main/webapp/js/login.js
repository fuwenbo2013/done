//alert("login.js");
//console.log("login.js");
//相当于window.onload=function(){};
$(function(){
	//从cookie中查找指定key的cookie的值
	$("form #inputName").val(getCookie("loginName"));
	//给login.html中的登录表单添加submit事件
	$("form").submit(function(){
		return login();
		//return false:终止页面中的表单的提交按钮提交
		//return true:用页面的提交按钮提交表单,而不是此$("form").submit()
	});
});
//登录方法
function login(){
	alert("login");
	//获取页面中的数据
	var loginName=$("form #inputName").val();
	var password=$("form #inputPassword").val();
	//获取记住密码
	var remember=$("form input[type=checkbox]").get(0).checked;
	alert(remember);
	//alert(loginName +"   "+password);
	//把获取的数据异步提交给服务端Controller
	$.ajax({
		url:basePath+"user/login/name/"+loginName+"/password/"+password,//tes_ssm/user/login
		type:"get",		
		dataType:"json",
		success:function(result){
			if(result.status==1){
				window.location.href="index.html";
				//判断记住账号是否勾选
				if(remember){
					//记住账号添加到cookie中
					addCookie("loginName",loginName,5);
				}
				
			}
			alert(result.message);
		},
		error:function(){
			alert("请求失败!!!");
		}		 
	});
	
	return false;
}