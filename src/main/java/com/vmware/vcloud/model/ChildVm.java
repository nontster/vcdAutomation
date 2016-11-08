package com.vmware.vcloud.model;

public class ChildVm {
	private String id;
	private String nonMobileNo;
    private String name;
    private String description;
    private String templateType;
    private String computerName;
    private VCpu vCpu;
    private VMemory vMemory;
    private Integer storageSize;
    private String userName;
    private String password;
    
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
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

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

	/**
	 * @return the storageSize
	 */
	public Integer getStorageSize() {
		return storageSize;
	}

	/**
	 * @param storageSize the storageSize to set
	 */
	public void setStorageSize(Integer storageSize) {
		this.storageSize = storageSize;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
     
}
