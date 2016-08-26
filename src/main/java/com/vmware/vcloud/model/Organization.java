package com.vmware.vcloud.model;

public class Organization {
	private String name;
	private String description;
	private String fullName;
	private boolean enabled;
	
	public Organization() {
		super();
		// TODO Auto-generated constructor stub
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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	class OrgSettings {
		class OrgLeaseSettings{
			private boolean deleteOnStorageLeaseExpiration;
			private int deploymentLeaseSeconds;
			private int storageLeaseSeconds;
			
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
	}
	
	
}
