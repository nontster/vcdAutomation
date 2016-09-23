package com.vmware.vcloud.model;

public class VCpu {
    private int noOfCpus; 
    private int coresPerSocket;
    
	public VCpu() {
		super();
		// TODO Auto-generated constructor stub
	}

	public VCpu(int noOfCpus, int coresPerSocket) {
		super();
		this.noOfCpus = noOfCpus;
		this.coresPerSocket = coresPerSocket;
	}

	public int getNoOfCpus() {
		return noOfCpus;
	}

	public void setNoOfCpus(int noOfCpus) {
		this.noOfCpus = noOfCpus;
	}

	public int getCoresPerSocket() {
		return coresPerSocket;
	}

	public void setCoresPerSocket(int coresPerSocket) {
		this.coresPerSocket = coresPerSocket;
	}
    

}
