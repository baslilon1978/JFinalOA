/**
 * 
 */
package com.pointlion.sys.mvc.admin.dept;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.sys.interceptor.MainPageTitleInterceptor;
import com.pointlion.sys.mvc.common.base.BaseController;
import com.pointlion.sys.mvc.common.model.SysDept;
import com.pointlion.sys.mvc.common.utils.UuidUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lion
 * @date 2017年2月16日 下午4:04:25
 * @qq 439635374
 */
@Before(MainPageTitleInterceptor.class)
public class DeptController extends BaseController{
	/***
	 * 获得列表页
	 */
	public void getListPage(){
    	render("/WEB-INF/admin/dept/list.html");
    }
    /***
     * 获取编辑页面
     */
    public void getEditPage(){
    	//添加和修改
    	String id = getPara("id");
    	if(StrKit.notBlank(id)){
			SysDept dept = SysDept.dao.getById(id);
    		setAttr("o", dept);
    	}
    	render("/WEB-INF/admin/dept/edit.html");
    }
	/***
	 * 查询分页数据
	 */
	public void listData(){
		String curr = getPara("pageNumber");
		String pageSize = getPara("pageSize");
		Page<Record> page = SysDept.dao.getDeptPage(Integer.valueOf(curr),Integer.valueOf(pageSize));
		renderPage(page.getList(),"" ,page.getTotalRow());
	}
    /***
     * 保存
     */
    public void save(){
		SysDept dept = getModel(SysDept.class);
    	if(StrKit.notBlank(dept.getId())){
    		dept.update();
    	}else{
    		dept.setId(UuidUtil.getUUID());
    		dept.save();
    	}
    	renderSuccess();
    }
    /***
     * 删除
     * @throws Exception
     */
    public void delete() throws Exception{
		SysDept.dao.deleteByIds(getPara("ids"));
		renderSuccess("删除成功!");
    }
    


    /**************************************************************************/
	private String pageTitle = "部门管理";//模块页面标题
	private String breadHomeMethod = "getListPage";//面包屑首页方法
	
	public Map<String,String> getPageTitleBread() {
		Map<String,String> pageTitleBread = new HashMap<String,String>();
		pageTitleBread.put("pageTitle", pageTitle);
		pageTitleBread.put("breadHomeMethod", breadHomeMethod);
		return pageTitleBread;
	}
}
