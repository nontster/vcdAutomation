package com.vmware.vcloud.model;

public class VCpu {
    private Integer noOfCpus; 
    private Integer coresPerSocket;
    
	public VCpu() {
		super();
		// TODO Auto-generated constructor stub
	}

	public VCpu(Integer noOfCpus, Integer coresPerSocket) {
		super();
		this.noOfCpus = noOfCpus;
		this.coresPerSocket = coresPerSocket;
	}

	public Integer getNoOfCpus() {
		return noOfCpus;
	}

	public void setNoOfCpus(Integer noOfCpus) {
		this.noOfCpus = noOfCpus;
	}

	public Integer getCoresPerSocket() {
		return coresPerSocket;
	}

	public void setCoresPerSocket(Integer coresPerSocket) {
		this.coresPerSocket = coresPerSocket;
	}
    

}
