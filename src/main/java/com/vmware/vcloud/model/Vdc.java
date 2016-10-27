package com.vmware.vcloud.model;

public class Vdc {
    private String name;
    private Boolean enabled;
    private Float resourceGuaranteedCpu;
    private Float resourceGuaranteedMemory;
    private Integer vmQuota;
    private String description;
    private ComputeCapacity computeCapacity;
    private Integer networkQuota;
    private VdcStorageProfile vdcStorageProfile;
    
	public Vdc() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Vdc(ComputeCapacity computeCapacity, String description, Integer networkQuota, Integer vmQuota, Boolean enabled,
			Float resourceGuaranteedCpu, String name, Float resourceGuaranteedMemory,
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

	public Boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Float getResourceGuaranteedCpu() {
		return resourceGuaranteedCpu;
	}

	public void setResourceGuaranteedCpu(Float resourceGuaranteedCpu) {
		this.resourceGuaranteedCpu = resourceGuaranteedCpu;
	}

	public Float getResourceGuaranteedMemory() {
		return resourceGuaranteedMemory;
	}

	public void setResourceGuaranteedMemory(Float resourceGuaranteedMemory) {
		this.resourceGuaranteedMemory = resourceGuaranteedMemory;
	}

	public Integer getVmQuota() {
		return vmQuota;
	}

	public void setVmQuota(Integer vmQuota) {
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

	public Integer getNetworkQuota() {
		return networkQuota;
	}

	public void setNetworkQuota(Integer networkQuota) {
		this.networkQuota = networkQuota;
	}

	public VdcStorageProfile getVdcStorageProfile() {
		return vdcStorageProfile;
	}

	public void setVdcStorageProfile(VdcStorageProfile vdcStorageProfile) {
		this.vdcStorageProfile = vdcStorageProfile;
	}
	
}
