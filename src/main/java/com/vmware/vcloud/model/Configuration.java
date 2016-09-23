package com.vmware.vcloud.model;

import java.util.List;

public class Configuration {
	private boolean retainNetInfoAcrossDeployments;
	private FenceMode fenceMode;
	private List<IpScope> ipScopes;
	
	public Configuration() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Configuration(boolean retainNetInfoAcrossDeployments, FenceMode fenceMode, List<IpScope> ipScopes) {
		super();
		this.retainNetInfoAcrossDeployments = retainNetInfoAcrossDeployments;
		this.fenceMode = fenceMode;
		this.ipScopes = ipScopes;
	}

	public boolean isRetainNetInfoAcrossDeployments() {
		return retainNetInfoAcrossDeployments;
	}

	public void setRetainNetInfoAcrossDeployments(boolean retainNetInfoAcrossDeployments) {
		this.retainNetInfoAcrossDeployments = retainNetInfoAcrossDeployments;
	}

	public FenceMode getFenceMode() {
		return fenceMode;
	}

	public void setFenceMode(FenceMode fenceMode) {
		this.fenceMode = fenceMode;
	}

	public List<IpScope> getIpScopes() {
		return ipScopes;
	}

	public void setIpScopes(List<IpScope> ipScopes) {
		this.ipScopes = ipScopes;
	}
	
}
