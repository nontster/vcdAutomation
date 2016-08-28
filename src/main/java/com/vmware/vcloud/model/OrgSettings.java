package com.vmware.vcloud.model;

public class OrgSettings {
	private OrgLeaseSettings orgLeaseSettings;
	private OrgGeneralSettings orgGeneralSettings;
	private OrgVAppTemplateLeaseSettings orgVAppTemplateLeaseSettings;
	private OrgPasswordPolicySettings orgPasswordPolicySettings;

	public OrgSettings() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OrgSettings(OrgLeaseSettings orgLeaseSettings, OrgGeneralSettings orgGeneralSettings,
			OrgVAppTemplateLeaseSettings orgVAppTemplateLeaseSettings,
			OrgPasswordPolicySettings orgPasswordPolicySettings) {

		this.orgLeaseSettings = orgLeaseSettings;
		this.orgGeneralSettings = orgGeneralSettings;
		this.orgVAppTemplateLeaseSettings = orgVAppTemplateLeaseSettings;
		this.orgPasswordPolicySettings = orgPasswordPolicySettings;
	}

	public OrgLeaseSettings getOrgLeaseSettings() {
		return orgLeaseSettings;
	}

	public void setOrgLeaseSettings(OrgLeaseSettings orgLeaseSettings) {
		this.orgLeaseSettings = orgLeaseSettings;
	}

	public OrgGeneralSettings getOrgGeneralSettings() {
		return orgGeneralSettings;
	}

	public void setOrgGeneralSettings(OrgGeneralSettings orgGeneralSettings) {
		this.orgGeneralSettings = orgGeneralSettings;
	}

	public OrgVAppTemplateLeaseSettings getOrgVAppTemplateLeaseSettings() {
		return orgVAppTemplateLeaseSettings;
	}

	public void setOrgVAppTemplateLeaseSettings(OrgVAppTemplateLeaseSettings orgVAppTemplateLeaseSettings) {
		this.orgVAppTemplateLeaseSettings = orgVAppTemplateLeaseSettings;
	}

	public OrgPasswordPolicySettings getOrgPasswordPolicySettings() {
		return orgPasswordPolicySettings;
	}

	public void setOrgPasswordPolicySettings(OrgPasswordPolicySettings orgPasswordPolicySettings) {
		this.orgPasswordPolicySettings = orgPasswordPolicySettings;
	}
	
}
