package com.vmware.vcloud.model;

public class ChildVm {
    private String name;
    private String description;
    private String templateType;
    
	public ChildVm() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ChildVm(String name, String description, String templateType) {
		super();
		this.name = name;
		this.description = description;
		this.templateType = templateType;
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

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
     
}
