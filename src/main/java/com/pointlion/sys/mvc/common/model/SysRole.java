package com.pointlion.sys.mvc.common.model;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.sys.mvc.common.dto.ZtreeNode;
import com.pointlion.sys.mvc.common.model.base.BaseSysRole;
import com.pointlion.sys.mvc.common.utils.UuidUtil;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class SysRole extends BaseSysRole<SysRole> {
	public static final SysRole dao = new SysRole();
	/***
	 * 根据主键查询
	 */
	public SysRole getById(String id){
		return SysRole.dao.findById(id);
	}
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
		String idarr[] = ids.split(",");
    	for(String roleid : idarr){
			SysRole menu = SysRole.dao.getById(roleid);
			if(menu!=null){
				menu.delete();//删除角色
				deleteAllRoleAuthByRolieid(roleid);//删除角色下的所有权限
			}
    	}
	}
	/***
	 * 查询角色分页
	 * @param curr
	 * @param pagesize
	 * @return
	 */
	public Page<SysRole> getRolePage(Integer curr , Integer pagesize){
		return SysRole.dao.paginate(curr, pagesize, "select * ", " from sys_role");
	}
	/***
	 * 删除角色下所有权限
	 * @param roleid
	 * @return
	 */
	public Integer deleteAllRoleAuthByRolieid(String roleid){
		return Db.update("delete from sys_role_auth where role_id='"+roleid+"'");
	}
	/***
	 * 给角色赋权
	 * @param data
	 * @return
	 */
	@Before(Tx.class)
	public Boolean changeRoleAuth(String roleid , String data){
		if(StrKit.notBlank(data)){
			deleteAllRoleAuthByRolieid(roleid);//删除角色下所有权限
			String darr[] = data.split(",");//添加权限
			for(String d:darr){
				SysRoleAuth ro = new SysRoleAuth();
				ro.setId(UuidUtil.getUUID());
				ro.setMenuId(d);
				ro.setRoleId(roleid);
				ro.save();
			}
		}
		return true;
	}
	
	/***
	 * 获取角色下的 所有权限
	 * @param roleid
	 * @return
	 */
	public List<SysRoleAuth> getRoleAuthByRoleId(String roleid){
		return SysRoleAuth.dao.find("select * from sys_role_auth where role_id='"+roleid+"'");
	}
	/***
	 * 根据用户的id 查询所有的权限(菜单)
	 * @param userid
	 * @return
	 */
	public List<SysMenu> getRoleAuthByUserid(String userid,String if_show,String parentId){
		String sql = "select DISTINCT m.* from sys_user u ,sys_user_role ur , sys_role_auth ra ,sys_menu m where ur.user_id=u.id and u.id='"+userid+"' and ur.role_id=ra.role_id and ra.menu_id=m.id ";
		if(StrKit.notBlank(if_show)){
			sql = sql + " and m.if_show='"+if_show+"' ";
		}
		if(StrKit.notBlank(parentId)){
			sql = sql + " and m.parent_id='"+parentId+"' ";
		}
		sql = sql + " ORDER BY m.sort ";
		return SysMenu.dao.find(sql);
		
	}
	
	
	/***
	 * 获取所有角色
	 * 给用户赋值角色时候用
	 */
	public List<ZtreeNode> getAllRoleTreeNode(){
		List<SysRole> rolelist = SysRole.dao.find("select * from sys_role");
		List<ZtreeNode> treelist = new ArrayList<ZtreeNode>();
		for(SysRole role:rolelist){
			ZtreeNode tree = new ZtreeNode();
			tree.setId(role.getId());
			tree.setName(role.getName());
			treelist.add(tree);
		}
		return treelist;
	}
	/***
	 * 获取用户下所有角色
	 */
	public List<SysRole> getAllRoleByUserid(String id){
		return SysRole.dao.find("select r.* from sys_role r ,sys_user_role ur , sys_user u where ur.user_id=u.id and ur.role_id=r.id and u.id='"+id+"'");
	}
}
