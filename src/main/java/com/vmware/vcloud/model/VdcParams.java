package com.vmware.vcloud.model;

public class VdcParams {
    private String name;
    private boolean enabled;
    private double resourceGuaranteedCpu;
    private double resourceGuaranteedMemory;
    private int vmQuota;
    private String description;
    private ComputeCapacity computeCapacity;
    private int networkQuota;
    private VdcStorageProfile vdcStorageProfile;
    
	public VdcParams() {
		super();
		// TODO Auto-generated constructor stub
	}

	public VdcParams(String name, boolean enabled, float resourceGuaranteedCpu, float resourceGuaranteedMemory,
			int vmQuota, String description, ComputeCapacity computeCapacity, int networkQuota,
			VdcStorageProfile vdcStorageProfile) {
		super();
		this.name = name;
		this.enabled = enabled;
		this.resourceGuaranteedCpu = resourceGuaranteedCpu;
		this.resourceGuaranteedMemory = resourceGuaranteedMemory;
		this.vmQuota = vmQuota;
		this.description = description;
		this.computeCapacity = computeCapacity;
		this.networkQuota = networkQuota;
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

	public double getResourceGuaranteedCpu() {
		return resourceGuaranteedCpu;
	}

	public void setResourceGuaranteedCpu(float resourceGuaranteedCpu) {
		this.resourceGuaranteedCpu = resourceGuaranteedCpu;
	}

	public double getResourceGuaranteedMemory() {
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
