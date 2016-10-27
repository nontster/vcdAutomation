package com.vmware.vcloud.model;

public class IpScope {
	private String netmask;
	private String gateway;
	private Boolean enabled;
	private Boolean inherited;	
	private String dns1;
	private String dns2;
	private IpRange ipRange;
	
	public IpScope() {
		super();
		// TODO Auto-generated constructor stub
	}

	public IpScope(String netmask, String gateway, Boolean enabled, Boolean inherited, String dns1, String dns2,
			IpRange ipRange) {
		super();
		this.netmask = netmask;
		this.gateway = gateway;
		this.enabled = enabled;
		this.inherited = inherited;
		this.dns1 = dns1;
		this.dns2 = dns2;
		this.ipRange = ipRange;
	}

	public String getNetmask() {
		return netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public Boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean isInherited() {
		return inherited;
	}

	public void setInherited(Boolean inherited) {
		this.inherited = inherited;
	}

	public String getDns1() {
		return dns1;
	}

	public void setDns1(String dns1) {
		this.dns1 = dns1;
	}

	public String getDns2() {
		return dns2;
	}

	public void setDns2(String dns2) {
		this.dns2 = dns2;
	}

	public IpRange getIpRange() {
		return ipRange;
	}

	public void setIpRange(IpRange ipRange) {
		this.ipRange = ipRange;
	}
	
}
