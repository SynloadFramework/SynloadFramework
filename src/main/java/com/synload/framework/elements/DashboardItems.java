package com.synload.framework.elements;

import java.util.List;

import com.synload.sf.dashboard.Dashboard;
import com.synload.sf.dashboard.DashboardGroup;
import com.synload.sf.handlers.Response;

public class DashboardItems extends Response{
	public List<DashboardGroup> groups = null;
	public DashboardItems(){
		//this.groups = Dashboard.groups;
		this.setTemplate(this.getTemplate("./elements/dashboard/itemlist.html"));
		this.addJavascript(this.getFileData("./elements/js/menu_change.js"));
		this.data.put("uname", "home");
		this.setParent(".items[page='dashboard']");
		this.setAction("alone");
		this.setParentTemplate("dashboardPage");
	}
}
