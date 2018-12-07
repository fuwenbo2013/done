package com.tarena.vo;

import java.io.Serializable;
import java.util.List;

public class Page implements Serializable{
	
	private int currentPage;
	private int pageSize;
	private int previousPage;
	private int nextPage;
	private int totalCount;
	private int totalPage;
	
	private List data;//当前页的那页数据
	
	private String roleKeyword;
	private String userKeyword;
	
	private int begin;
	private List<Integer> nums;
	
	public String getUserKeyword() {
		return userKeyword;
	}

	public void setUserKeyword(String userKeyword) {
		this.userKeyword = userKeyword;
	}

	public List<Integer> getNums() {
		return nums;
	}

	public void setNums(List<Integer> nums) {
		this.nums = nums;
	}

	public int getBegin() {
		begin=(currentPage-1)*pageSize;
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPreviousPage() {
		return previousPage;
	}

	public void setPreviousPage(int previousPage) {
		this.previousPage = previousPage;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public List getData() {
		return data;
	}

	public void setData(List data) {
		this.data = data;
	}

	public String getRoleKeyword() {
		return roleKeyword;
	}

	public void setRoleKeyword(String roleKeyword) {
		this.roleKeyword = roleKeyword;
	}

	@Override
	public String toString() {
		return "Page [currentPage=" + currentPage + ", pageSize=" + pageSize + ", previousPage=" + previousPage
				+ ", nextPage=" + nextPage + ", totalCount=" + totalCount + ", totalPage=" + totalPage + ", data="
				+ data + ", roleKeyword=" + roleKeyword + ", begin=" + begin + ", nums=" + nums + "]";
	}
	

}
