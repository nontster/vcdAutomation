package com.vmware.vcloud.model;

public class VApp {
	private String name;
	private String description;
    private String templateType;
    private String vmName;
    private String vmDescription;
    
	public VApp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public VApp(String templateType, String vmName, String vmDescription, String name, String description) {
		super();
		this.name = name;
		this.description = description;
		this.templateType = templateType;
		this.vmName = vmName;
		this.vmDescription = vmDescription;
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

	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	public String getVmDescription() {
		return vmDescription;
	}

	public void setVmDescription(String vmDescription) {
		this.vmDescription = vmDescription;
	}
    
}
