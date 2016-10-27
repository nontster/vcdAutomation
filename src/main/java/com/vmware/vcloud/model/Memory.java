package com.vmware.vcloud.model;

public class Memory {
    private Integer allocated;
    private Integer overhead;
    private String units;
    private Integer used;
    private Integer limit;
    
	public Memory() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Memory(Integer allocated, Integer overhead, String units, Integer used, Integer limit) {
		super();
		this.allocated = allocated;
		this.overhead = overhead;
		this.units = units;
		this.used = used;
		this.limit = limit;
	}

	public Integer getAllocated() {
		return allocated;
	}

	public void setAllocated(Integer allocated) {
		this.allocated = allocated;
	}

	public Integer getOverhead() {
		return overhead;
	}

	public void setOverhead(Integer overhead) {
		this.overhead = overhead;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public Integer getUsed() {
		return used;
	}

	public void setUsed(Integer used) {
		this.used = used;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}
    
}
