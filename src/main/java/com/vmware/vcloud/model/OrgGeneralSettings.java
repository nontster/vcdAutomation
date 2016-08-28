package com.vmware.vcloud.model;

public class OrgGeneralSettings {
	private int storedVmQuota;
	private int deployedVMQuota;
	private boolean canPublishCatalogs;
	
	public OrgGeneralSettings() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OrgGeneralSettings(int storedVmQuota, int deployedVMQuota, boolean canPublishCatalogs) {
		super();
		this.storedVmQuota = storedVmQuota;
		this.deployedVMQuota = deployedVMQuota;
		this.canPublishCatalogs = canPublishCatalogs;
	}

	public int getStoredVmQuota() {
		return storedVmQuota;
	}

	public void setStoredVmQuota(int storedVmQuota) {
		this.storedVmQuota = storedVmQuota;
	}

	public int getDeployedVMQuota() {
		return deployedVMQuota;
	}

	public void setDeployedVMQuota(int deployedVMQuota) {
		this.deployedVMQuota = deployedVMQuota;
	}

	public boolean isCanPublishCatalogs() {
		return canPublishCatalogs;
	}

	public void setCanPublishCatalogs(boolean canPublishCatalogs) {
		this.canPublishCatalogs = canPublishCatalogs;
	}
	
}
