//@ sourceURL=role.js
var roleId;
$(function(){
	//第一次没模糊条件的的所有数据的额第一页
	findRolesByPage(1);
	//给角色的模糊按钮添加click事件
	$("#rolePanel .row button").on("click",function(){
		findRolesByPage(1);
	});
	//给新增的表单添加submit事件
	$("#addPanel form").submit(function(){
		return addRole();		
	});
	//给修改的表单添加submit事件
	$("#editRole form").submit(function(){
		return updateRole();
	});
	$('#editRole').on('hidden.bs.modal', function (e) {
		  alert("藏完了");
	});
	//给删除的modal框的确定按钮添加click事件
	$(".bs-example-modal-sm button:eq(1)").click(function(){
		deleteRole();
	});
});
//删除角色信息
function deleteRole(){
	$.ajax({
		url:basePath+"role/deleteRole/"+roleId,
		type:"delete",
		dataType:"json",
		success:function(result){
			if(result.status==1){
				alert(result.message);
				//删除页面中指定的行
				$("#tr_"+roleId).remove();
				//关闭删除modal框
				$(".bs-example-modal-sm").modal('toggle');
			}else if(result.status==0){
				alert("删除角色失败!!");
			}
		},
	});
}
function deleteClick(rid){
	roleId=rid;
}
//更新角色的方法
function updateRole(){
	//获取修改后的角色信息
	var newRoleName=$("#editRole form #role_name").val();
	
	//发送异步请求
	$.ajax({
		url:basePath+"role/updateRole",
		type:"post",
		data:{
			"id":roleId,
			"name":newRoleName
		},
		dataType:"json",
		success:function(result){
			if(result.status==1){
				//更改页面的数据
				$("#tr_"+roleId).find("td:eq(2)").text(newRoleName);
				//关闭模态框
				$("#editRole").modal('hide');
			}else if(result.status==0){
				alert("角色修改失败!!");
			}
		},
		error:function(){
			alert("请求失败!!");
		}
	});
	
	return false;
}
function updateClick(rid){
	//alert(rid +"   "+rname);
	roleId=rid;
	var rname=$("#tr_"+rid).find("td:eq(2)").text();
	$("#editRole form #role_name").val(rname);
}
//新增角色
function addRole(){
	//获取新角色的名字
	var newRole=$("#addPanel form input[type=text]").val();
	
	//发送异步请求
	$.ajax({
		url:basePath+"role/addRole/"+newRole,
		type:"post",
		dataType:"json",
		success:function(result){
			if(result.status==1){
				alert(result.message);
			}else if(result.status==0){
				alert("角色添加失败!");
			}
		},
		error:function(){
			alert("请求失败!");
		}
	});
	
	return false;
}
//查询指定页号那页数据
function findRolesByPage(currentPage){
	//alert(currentPage);
	//获取模糊关键字
	var roleKeyword=$("#rolePanel .row input[type=text]").val();
	//roleKeyword="aabbcc";
	if(roleKeyword==''){
		roleKeyword="undefined";
	}
	//alert(roleKeyword);
	$.ajax({
		url:basePath+"role/findRolesByPage",
		type:"get",
		data:{
			"currentPage":currentPage,
	        "roleKeyword":roleKeyword
		},
		dataType:"json",
		success:function(result){
			if(result.status==1){
				//顺利获取到数据库的数据,并正确返回js端
				var page=result.data;
				var roles=page.data;
				//清空表格
				$("#role_table tbody").html("");
				$(roles).each(function(index,role){
					//index:遍历到第几个对象,从零开始
					//role:遍历到的那个对象
					var tr1='<tr>'+
			              '<td>'+(index+1)+'</td>'+
			              '<td>'+role.id+'</td>'+
			              '<td>'+role.name+'</td>'+
			              '<td>'+
			              '</td>'+
			            '</tr>';
			            
					var tr2='<tr id="tr_'+role.id+'">'+
			              '<td>'+(index+1)+'</td>'+
			              '<td>'+role.id+'</td>'+
			              '<td>'+role.name+'</td>'+
			              '<td>'+
			                '<a onclick="updateClick(\''+role.id+'\')" href="" data-toggle="modal" data-target="#editRole" ><span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>编辑</a>'+
			                '<a onclick="deleteClick(\''+role.id+'\')" href="" data-toggle="modal" data-target=".bs-example-modal-sm"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除</a>'+
			              '</td>'+
			            '</tr>';
					if(role.name!='超级管理员' && role.name!="讲师" && role.name!="学员"){
						$("#role_table tbody").append(tr2);
					}else{
						$("#role_table tbody").append(tr1);
					}
					
					
				});
								
				//清空分页条
				$("#role_pagination").html("");
				if(page.totalPage>1){
					//添加分页条
					var previousPage='<li>'+
										  '<a href="javascript:findRolesByPage('+page.previousPage+')" aria-label="Previous">'+
								            '<span aria-hidden="true">&laquo;</span>'+
								          '</a>'+
								      '</li>';
					$("#role_pagination").append(previousPage);
					$(page.nums).each(function(n,value){
						var middlePage='<li><a href="javascript:findRolesByPage('+value+')">'+value+'</a></li>';
						$("#role_pagination").append(middlePage);
					});
					
					var nextPage='<li>'+
								  '<a href="javascript:findRolesByPage('+page.nextPage+')" aria-label="Next">'+
						            '<span aria-hidden="true">&raquo;</span>'+
						          '</a>'+
						        '</li>';
					$("#role_pagination").append(nextPage);
					
				}
				
				
			}
		},
		error:function(){
			alert("请求失败!");
		}
	});
	
}
