package com.vmware.vcloud.model;

public class OrgVAppTemplateLeaseSettings {
	private boolean deleteOnStorageLeaseExpiration;
	private int storageLeaseSeconds;
	
	public OrgVAppTemplateLeaseSettings() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OrgVAppTemplateLeaseSettings(boolean deleteOnStorageLeaseExpiration, int storageLeaseSeconds) {
		super();
		this.deleteOnStorageLeaseExpiration = deleteOnStorageLeaseExpiration;
		this.storageLeaseSeconds = storageLeaseSeconds;
	}

	public boolean isDeleteOnStorageLeaseExpiration() {
		return deleteOnStorageLeaseExpiration;
	}

	public void setDeleteOnStorageLeaseExpiration(boolean deleteOnStorageLeaseExpiration) {
		this.deleteOnStorageLeaseExpiration = deleteOnStorageLeaseExpiration;
	}

	public int getStorageLeaseSeconds() {
		return storageLeaseSeconds;
	}

	public void setStorageLeaseSeconds(int storageLeaseSeconds) {
		this.storageLeaseSeconds = storageLeaseSeconds;
	}
	
}
