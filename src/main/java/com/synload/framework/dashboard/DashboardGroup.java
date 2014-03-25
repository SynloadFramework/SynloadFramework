package com.synload.sf.dashboard;

import java.util.ArrayList;
import java.util.List;

public class DashboardGroup {
	public List<DashboardItem> items = new ArrayList<DashboardItem>();
	public String name, flag = "";
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<DashboardItem> getDashboards() {
		return items;
	}
	public void addDashboards(DashboardItem d) {
		this.items.add(d);
	}
}
