package com.vmware.vcloud.model;

import java.math.BigInteger;

public class VMemory {
	private BigInteger memorySize;

	public VMemory() {
		super();
		// TODO Auto-generated constructor stub
	}

	public VMemory(BigInteger memorySize) {
		super();
		this.memorySize = memorySize;
	}

	public BigInteger getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(BigInteger memorySize) {
		this.memorySize = memorySize;
	}
	
}
