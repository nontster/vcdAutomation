package com.vmware.vcloud.model;

public class VdcStorageProfile {
    private boolean enabled;
    private boolean def;
    private int limit;
    private String units;
    
	public VdcStorageProfile() {
		super();
		// TODO Auto-generated constructor stub
	}

	public VdcStorageProfile(boolean enabled, boolean def, int limit, String units) {
		super();
		this.enabled = enabled;
		this.def = def;
		this.limit = limit;
		this.units = units;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isDef() {
		return def;
	}

	public void setDef(boolean def) {
		this.def = def;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}
    
}
