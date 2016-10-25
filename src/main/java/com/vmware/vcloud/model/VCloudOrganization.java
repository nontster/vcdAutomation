package com.vmware.vcloud.model;

import java.util.Date;

public class VCloudOrganization {
	private String template_version;
	private String name;
	private String description;
	private String fullName;
	private boolean enabled;
	private OrderType orderType;
	private String caNumber;

	private Date startDate;
	private Date endDate;
	private User user;
	private OrgSettings orgSettings;
	private CloudResources cloudResources;
	private Vdc vdc;
	private EdgeGateway edgeGateway;
	private OrgVdcNetwork orgVdcNetwork;
	private VApp vApp;
	
	public VCloudOrganization() {
		super();
		// TODO Auto-generated constructor stub
	}

	public VCloudOrganization(String template_version, String name, String description, String fullName, boolean enabled, OrderType orderType,
			String caNumber, Date startDate, Date endDate, User user, OrgSettings orgSettings,
			CloudResources cloudResources, Vdc vdc, EdgeGateway edgeGateway, OrgVdcNetwork orgVdcNetwork, VApp vApp) {
		super();
		this.setTemplate_version(template_version);
		this.name = name;
		this.description = description;
		this.fullName = fullName;
		this.enabled = enabled;
		this.setOrderType(orderType);
		this.setCaNumber(caNumber);
		this.setStartDate(startDate);
		this.setEndDate(endDate);
		this.user = user;
		this.orgSettings = orgSettings;
		this.cloudResources = cloudResources;
		this.vdc = vdc;
		this.edgeGateway = edgeGateway;
		this.orgVdcNetwork = orgVdcNetwork;
		this.vApp = vApp;
	}

	public String getTemplate_version() {
		return template_version;
	}

	public void setTemplate_version(String template_version) {
		this.template_version = template_version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the orderType
	 */
	public OrderType getOrderType() {
		return orderType;
	}

	/**
	 * @param orderType the orderType to set
	 */
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getCaNumber() {
		return caNumber;
	}

	public void setCaNumber(String caNumber) {
		this.caNumber = caNumber;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public OrgSettings getOrgSettings() {
		return orgSettings;
	}

	public void setOrgSettings(OrgSettings orgSettings) {
		this.orgSettings = orgSettings;
	}

	public CloudResources getCloudResources() {
		return cloudResources;
	}

	public void setCloudResources(CloudResources cloudResources) {
		this.cloudResources = cloudResources;
	}

	public Vdc getVdc() {
		return vdc;
	}

	public void setVdc(Vdc vdc) {
		this.vdc = vdc;
	}


	public EdgeGateway getEdgeGateway() {
		return edgeGateway;
	}


	public void setEdgeGateway(EdgeGateway edgeGateway) {
		this.edgeGateway = edgeGateway;
	}

	public OrgVdcNetwork getOrgVdcNetwork() {
		return orgVdcNetwork;
	}

	public void setOrgVdcNetwork(OrgVdcNetwork orgVdcNetwork) {
		this.orgVdcNetwork = orgVdcNetwork;
	}

	public VApp getvApp() {
		return vApp;
	}

	public void setvApp(VApp vApp) {
		this.vApp = vApp;
	}
	
}