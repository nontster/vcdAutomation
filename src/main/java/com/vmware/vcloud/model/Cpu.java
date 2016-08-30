package com.vmware.vcloud.model;

public class Cpu {
	private long allocated;
	private long overhead;
	private String units;
	private long used;
	private long limit;
	
	public Cpu() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Cpu(int allocated, int overhead, String units, int used, int limit) {
		super();
		this.allocated = allocated;
		this.overhead = overhead;
		this.units = units;
		this.used = used;
		this.limit = limit;
	}

	public long getAllocated() {
		return allocated;
	}

	public void setAllocated(int allocated) {
		this.allocated = allocated;
	}

	public long getOverhead() {
		return overhead;
	}

	public void setOverhead(int overhead) {
		this.overhead = overhead;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public long getUsed() {
		return used;
	}

	public void setUsed(int used) {
		this.used = used;
	}

	public long getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}
