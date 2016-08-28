package com.vmware.vcloud.model;

public class OrgLeaseSettings {
	private boolean deleteOnStorageLeaseExpiration;
	private int deploymentLeaseSeconds;
	private int storageLeaseSeconds;
	
	public OrgLeaseSettings() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OrgLeaseSettings(boolean deleteOnStorageLeaseExpiration, int deploymentLeaseSeconds,
			int storageLeaseSeconds) {
		super();
		this.deleteOnStorageLeaseExpiration = deleteOnStorageLeaseExpiration;
		this.deploymentLeaseSeconds = deploymentLeaseSeconds;
		this.storageLeaseSeconds = storageLeaseSeconds;
	}

	public boolean isDeleteOnStorageLeaseExpiration() {
		return deleteOnStorageLeaseExpiration;
	}

	public void setDeleteOnStorageLeaseExpiration(boolean deleteOnStorageLeaseExpiration) {
		this.deleteOnStorageLeaseExpiration = deleteOnStorageLeaseExpiration;
	}

	public int getDeploymentLeaseSeconds() {
		return deploymentLeaseSeconds;
	}

	public void setDeploymentLeaseSeconds(int deploymentLeaseSeconds) {
		this.deploymentLeaseSeconds = deploymentLeaseSeconds;
	}

	public int getStorageLeaseSeconds() {
		return storageLeaseSeconds;
	}

	public void setStorageLeaseSeconds(int storageLeaseSeconds) {
		this.storageLeaseSeconds = storageLeaseSeconds;
	}

}
