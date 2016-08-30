package com.vmware.vcloud.model;

public class ComputeCapacity {
	private Cpu cpu;
	private Memory memory;
	
	public ComputeCapacity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ComputeCapacity(Cpu cpu, Memory memory) {
		super();
		this.cpu = cpu;
		this.memory = memory;
	}

	public Cpu getCpu() {
		return cpu;
	}

	public void setCpu(Cpu cpu) {
		this.cpu = cpu;
	}

	public Memory getMemory() {
		return memory;
	}

	public void setMemory(Memory memory) {
		this.memory = memory;
	}
	
}
