package com.vmware.vcloud.model;

public class ChildVm {
	private String nonMobileNo;
    private String name;
    private String description;
    private String templateType;
    private String computerName;
    private VCpu vCpu;
    private VMemory vMemory;
    
	/**
	 * @return the nonMobileNo
	 */
	public String getNonMobileNo() {
		return nonMobileNo;
	}

	/**
	 * @param nonMobileNo the nonMobileNo to set
	 */
	public void setNonMobileNo(String nonMobileNo) {
		this.nonMobileNo = nonMobileNo;
	}

	public ChildVm() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ChildVm(String name, String description, String templateType, String computerName, VCpu vCpu, VMemory vMemory) {
		super();
		this.name = name;
		this.description = description;
		this.templateType = templateType;
		this.setComputerName(computerName);
		this.setvCpu(vCpu);
		this.setvMemory(vMemory);
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

	public String getComputerName() {
		return computerName;
	}

	public void setComputerName(String computerName) {
		this.computerName = computerName;
	}

	public VCpu getvCpu() {
		return vCpu;
	}

	public void setvCpu(VCpu vCpu) {
		this.vCpu = vCpu;
	}

	public VMemory getvMemory() {
		return vMemory;
	}

	public void setvMemory(VMemory vMemory) {
		this.vMemory = vMemory;
	}
     
}
