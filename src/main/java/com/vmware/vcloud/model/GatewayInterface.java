package com.vmware.vcloud.model;

public class GatewayInterface {
	private String displayName;
	private InterfaceTypeEnums interfaceType;
	private SubnetParticipation subnetParticipation;
	private boolean useForDefaultRoute;
	
	public GatewayInterface() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GatewayInterface(String displayName, InterfaceTypeEnums interfaceType,
			SubnetParticipation subnetParticipation, boolean useForDefaultRoute) {
		super();
		this.displayName = displayName;
		this.interfaceType = interfaceType;
		this.subnetParticipation = subnetParticipation;
		this.useForDefaultRoute = useForDefaultRoute;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public InterfaceTypeEnums getInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(InterfaceTypeEnums interfaceType) {
		this.interfaceType = interfaceType;
	}

	public SubnetParticipation getSubnetParticipation() {
		return subnetParticipation;
	}

	public void setSubnetParticipation(SubnetParticipation subnetParticipation) {
		this.subnetParticipation = subnetParticipation;
	}

	public boolean isUseForDefaultRoute() {
		return useForDefaultRoute;
	}

	public void setUseForDefaultRoute(boolean useForDefaultRoute) {
		this.useForDefaultRoute = useForDefaultRoute;
	}

}
