package com.vmware.vcloud.model;

public class IPRange {
	private String startAddress;
	private String endAddress;
	
	public IPRange() {
		super();
		// TODO Auto-generated constructor stub
	}

	public IPRange(String startAddress, String endAddress) {
		super();
		this.startAddress = startAddress;
		this.endAddress = endAddress;
	}

	public String getStartAddress() {
		return startAddress;
	}

	public void setStartAddress(String startAddress) {
		this.startAddress = startAddress;
	}

	public String getEndAddress() {
		return endAddress;
	}

	public void setEndAddress(String endAddress) {
		this.endAddress = endAddress;
	}
	
}
