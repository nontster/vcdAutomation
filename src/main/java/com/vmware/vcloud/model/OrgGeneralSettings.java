package com.vmware.vcloud.model;

public class OrgGeneralSettings {
	private Integer storedVmQuota;
	private Integer deployedVMQuota;
	private Boolean canPublishCatalogs;
	
	public OrgGeneralSettings() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OrgGeneralSettings(Integer storedVmQuota, Integer deployedVMQuota, Boolean canPublishCatalogs) {
		super();
		this.storedVmQuota = storedVmQuota;
		this.deployedVMQuota = deployedVMQuota;
		this.canPublishCatalogs = canPublishCatalogs;
	}

	public Integer getStoredVmQuota() {
		return storedVmQuota;
	}

	public void setStoredVmQuota(Integer storedVmQuota) {
		this.storedVmQuota = storedVmQuota;
	}

	public Integer getDeployedVMQuota() {
		return deployedVMQuota;
	}

	public void setDeployedVMQuota(Integer deployedVMQuota) {
		this.deployedVMQuota = deployedVMQuota;
	}

	public Boolean isCanPublishCatalogs() {
		return canPublishCatalogs;
	}

	public void setCanPublishCatalogs(Boolean canPublishCatalogs) {
		this.canPublishCatalogs = canPublishCatalogs;
	}
	
}
