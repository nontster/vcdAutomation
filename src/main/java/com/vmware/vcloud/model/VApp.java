package com.vmware.vcloud.model;

import java.util.List;

public class VApp {
	private String name;
	private String description;
    private List <ChildVm> childVms;
    
	public VApp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public VApp(String name, String description, List <ChildVm> childVms) {
		super();
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List <ChildVm> getChildVms() {
		return childVms;
	}

	public void setChildVms(List <ChildVm> childVms) {
		this.childVms = childVms;
	}

}
