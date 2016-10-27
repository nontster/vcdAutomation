package com.vmware.vcloud.model;

import java.util.List;

public class Configuration {
	private Boolean retainNetInfoAcrossDeployments;
	private FenceMode fenceMode;
	private List<IpScope> ipScopes;
	
	public Configuration() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Configuration(Boolean retainNetInfoAcrossDeployments, FenceMode fenceMode, List<IpScope> ipScopes) {
		super();
		this.retainNetInfoAcrossDeployments = retainNetInfoAcrossDeployments;
		this.fenceMode = fenceMode;
		this.ipScopes = ipScopes;
	}

	public Boolean isRetainNetInfoAcrossDeployments() {
		return retainNetInfoAcrossDeployments;
	}

	public void setRetainNetInfoAcrossDeployments(Boolean retainNetInfoAcrossDeployments) {
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
