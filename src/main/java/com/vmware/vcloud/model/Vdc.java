package com.vmware.vcloud.model;

public class Vdc {
    private String name;
    private boolean enabled;
    private float resourceGuaranteedCpu;
    private float resourceGuaranteedMemory;
    private int vmQuota;
    private String description;
    private ComputeCapacity computeCapacity;
    private int networkQuota;
    private VdcStorageProfile vdcStorageProfile;
    
	public Vdc() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Vdc(ComputeCapacity computeCapacity, String description, int networkQuota, int vmQuota, boolean enabled,
			float resourceGuaranteedCpu, String name, float resourceGuaranteedMemory,
			VdcStorageProfile vdcStorageProfile) {
		super();
		this.computeCapacity = computeCapacity;
		this.description = description;
		this.networkQuota = networkQuota;
		this.vmQuota = vmQuota;
		this.enabled = enabled;
		this.resourceGuaranteedCpu = resourceGuaranteedCpu;
		this.name = name;
		this.resourceGuaranteedMemory = resourceGuaranteedMemory;
		this.vdcStorageProfile = vdcStorageProfile;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public float getResourceGuaranteedCpu() {
		return resourceGuaranteedCpu;
	}

	public void setResourceGuaranteedCpu(float resourceGuaranteedCpu) {
		this.resourceGuaranteedCpu = resourceGuaranteedCpu;
	}

	public float getResourceGuaranteedMemory() {
		return resourceGuaranteedMemory;
	}

	public void setResourceGuaranteedMemory(float resourceGuaranteedMemory) {
		this.resourceGuaranteedMemory = resourceGuaranteedMemory;
	}

	public int getVmQuota() {
		return vmQuota;
	}

	public void setVmQuota(int vmQuota) {
		this.vmQuota = vmQuota;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ComputeCapacity getComputeCapacity() {
		return computeCapacity;
	}

	public void setComputeCapacity(ComputeCapacity computeCapacity) {
		this.computeCapacity = computeCapacity;
	}

	public int getNetworkQuota() {
		return networkQuota;
	}

	public void setNetworkQuota(int networkQuota) {
		this.networkQuota = networkQuota;
	}

	public VdcStorageProfile getVdcStorageProfile() {
		return vdcStorageProfile;
	}

	public void setVdcStorageProfile(VdcStorageProfile vdcStorageProfile) {
		this.vdcStorageProfile = vdcStorageProfile;
	}
	
}
