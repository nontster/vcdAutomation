package com.vmware.vcloud.model;

public class OrgVAppTemplateLeaseSettings {
	private Boolean deleteOnStorageLeaseExpiration;
	private Integer storageLeaseSeconds;
	
	public OrgVAppTemplateLeaseSettings() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OrgVAppTemplateLeaseSettings(Boolean deleteOnStorageLeaseExpiration, Integer storageLeaseSeconds) {
		super();
		this.deleteOnStorageLeaseExpiration = deleteOnStorageLeaseExpiration;
		this.storageLeaseSeconds = storageLeaseSeconds;
	}

	public Boolean isDeleteOnStorageLeaseExpiration() {
		return deleteOnStorageLeaseExpiration;
	}

	public void setDeleteOnStorageLeaseExpiration(Boolean deleteOnStorageLeaseExpiration) {
		this.deleteOnStorageLeaseExpiration = deleteOnStorageLeaseExpiration;
	}

	public Integer getStorageLeaseSeconds() {
		return storageLeaseSeconds;
	}

	public void setStorageLeaseSeconds(Integer storageLeaseSeconds) {
		this.storageLeaseSeconds = storageLeaseSeconds;
	}
	
}
