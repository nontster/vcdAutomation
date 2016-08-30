package com.vmware.vcloud.model;

public class Vdc {
	private VdcParams vdcParams;

	public Vdc() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Vdc(VdcParams vdcParams) {
		super();
		this.vdcParams = vdcParams;
	}

	public VdcParams getVdcParams() {
		return vdcParams;
	}

	public void setVdcParams(VdcParams vdcParams) {
		this.vdcParams = vdcParams;
	}
	
}
