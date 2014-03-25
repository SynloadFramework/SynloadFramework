package com.synload.sf.dashboard;

import java.util.ArrayList;
import java.util.List;

public class Dashboard {
	public static List<DashboardGroup> groups = new ArrayList<DashboardGroup>();

	public static List<DashboardGroup> getItems() {
		return groups;
	}
	public static void addItems(DashboardGroup item) {
		Dashboard.groups.add(item);
	}
}
