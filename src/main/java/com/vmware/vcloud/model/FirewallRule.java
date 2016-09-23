package com.vmware.vcloud.model;

public class FirewallRule {
    private String description;
    private Protocol protocol; 
    private String sourceIp;
    private String destIp;
    private String destPort;
    
	public FirewallRule() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FirewallRule(String description, Protocol protocol, String sourceIp, String destIp, String destPort) {
		super();
		this.description = description;
		this.protocol = protocol;
		this.sourceIp = sourceIp;
		this.destIp = destIp;
		this.destPort = destPort;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

	public String getDestIp() {
		return destIp;
	}

	public void setDestIp(String destIp) {
		this.destIp = destIp;
	}

	public String getDestPort() {
		return destPort;
	}

	public void setDestPort(String destPort) {
		this.destPort = destPort;
	}
    
}
