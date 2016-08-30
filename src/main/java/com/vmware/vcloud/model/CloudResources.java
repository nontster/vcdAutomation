package com.vmware.vcloud.model;

public class CloudResources {
	private ProviderVdc providerVdc;
	private NetworkPool networkPool;
	
	public CloudResources() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CloudResources(ProviderVdc providerVdc, NetworkPool networkPool) {
		super();
		this.providerVdc = providerVdc;
		this.networkPool = networkPool;
	}

	public ProviderVdc getProviderVdc() {
		return providerVdc;
	}

	public void setProviderVdc(ProviderVdc providerVdc) {
		this.providerVdc = providerVdc;
	}

	public NetworkPool getNetworkPool() {
		return networkPool;
	}

	public void setNetworkPool(NetworkPool networkPool) {
		this.networkPool = networkPool;
	}
	
}
