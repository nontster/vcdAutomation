package com.vmware.vcloud.model;

public class CloudResources {
	private ProviderVdc providerVdc;
	private NetworkPool networkPool;
	private ExternalNetwork externalNetwork;
	private Catalog catalog;
	
	public CloudResources() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CloudResources(ProviderVdc providerVdc, NetworkPool networkPool, ExternalNetwork externalNetwork, Catalog catalog) {
		super();
		this.providerVdc = providerVdc;
		this.networkPool = networkPool;
		this.externalNetwork = externalNetwork;
		this.setCatalog(catalog);
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

	public ExternalNetwork getExternalNetwork() {
		return externalNetwork;
	}

	public void setExternalNetwork(ExternalNetwork externalNetwork) {
		this.externalNetwork = externalNetwork;
	}

	public Catalog getCatalog() {
		return catalog;
	}

	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}
}
