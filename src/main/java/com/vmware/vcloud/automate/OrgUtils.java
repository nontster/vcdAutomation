package com.vmware.vcloud.automate;

import com.vmware.vcloud.api.rest.schema.AdminOrgType;
import com.vmware.vcloud.api.rest.schema.OrgGeneralSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgLeaseSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgPasswordPolicySettingsType;
import com.vmware.vcloud.api.rest.schema.OrgSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgVAppTemplateLeaseSettingsType;
import com.vmware.vcloud.api.rest.schema.TaskType;
import com.vmware.vcloud.api.rest.schema.TasksInProgressType;
import com.vmware.vcloud.model.VCloudOrganization;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.admin.AdminOrganization;

public class OrgUtils {

	/**
	 * Creates a new admin org type object
	 * 
	 * @throws VCloudException
	 * 
	 */
	static AdminOrgType createNewAdminOrgType(VCloudOrganization vCloudOrg) throws VCloudException {

		/*
		 * SmtpServerSettingsType smtpServerSettings = new
		 * SmtpServerSettingsType(); smtpServerSettings.setHost("custom");
		 * smtpServerSettings.setIsUseAuthentication(true);
		 * smtpServerSettings.setPassword("custom");
		 * smtpServerSettings.setUsername("custom");
		 * 
		 * OrgEmailSettingsType orgEmailSettings = new OrgEmailSettingsType();
		 * orgEmailSettings.setIsDefaultOrgEmail(true);
		 * orgEmailSettings.setIsDefaultSmtpServer(true);
		 * orgEmailSettings.setFromEmailAddress("custom@custom.com");
		 * orgEmailSettings.setDefaultSubjectPrefix("");
		 * orgEmailSettings.setSmtpServerSettings(smtpServerSettings);
		 */

		OrgLeaseSettingsType orgLeaseSettings = new OrgLeaseSettingsType();
		orgLeaseSettings.setDeleteOnStorageLeaseExpiration(
				vCloudOrg.getOrgSettings().getOrgLeaseSettings().isDeleteOnStorageLeaseExpiration());
		orgLeaseSettings
				.setDeploymentLeaseSeconds(vCloudOrg.getOrgSettings().getOrgLeaseSettings().getDeploymentLeaseSeconds());
		orgLeaseSettings.setStorageLeaseSeconds(vCloudOrg.getOrgSettings().getOrgLeaseSettings().getStorageLeaseSeconds());

		OrgGeneralSettingsType orgGeneralSettings = new OrgGeneralSettingsType();
		orgGeneralSettings.setStoredVmQuota(vCloudOrg.getOrgSettings().getOrgGeneralSettings().getStoredVmQuota());
		orgGeneralSettings.setDeployedVMQuota(vCloudOrg.getOrgSettings().getOrgGeneralSettings().getDeployedVMQuota());
		orgGeneralSettings.setCanPublishCatalogs(vCloudOrg.getOrgSettings().getOrgGeneralSettings().isCanPublishCatalogs());

		OrgVAppTemplateLeaseSettingsType orgVAppTemplateLeaseSettings = new OrgVAppTemplateLeaseSettingsType();
		orgVAppTemplateLeaseSettings.setDeleteOnStorageLeaseExpiration(
				vCloudOrg.getOrgSettings().getOrgVAppTemplateLeaseSettings().isDeleteOnStorageLeaseExpiration());
		orgVAppTemplateLeaseSettings.setStorageLeaseSeconds(
				vCloudOrg.getOrgSettings().getOrgVAppTemplateLeaseSettings().getStorageLeaseSeconds());

		OrgPasswordPolicySettingsType orgPasswordPolicySettings = new OrgPasswordPolicySettingsType();
		orgPasswordPolicySettings.setAccountLockoutEnabled(
				vCloudOrg.getOrgSettings().getOrgPasswordPolicySettings().isAccountLockoutEnabled());
		orgPasswordPolicySettings.setAccountLockoutIntervalMinutes(
				vCloudOrg.getOrgSettings().getOrgPasswordPolicySettings().getAccountLockoutIntervalMinutes());
		orgPasswordPolicySettings.setInvalidLoginsBeforeLockout(
				vCloudOrg.getOrgSettings().getOrgPasswordPolicySettings().getInvalidLoginsBeforeLockout());

		OrgSettingsType orgSettings = new OrgSettingsType();
		orgSettings.setOrgGeneralSettings(orgGeneralSettings);
		orgSettings.setVAppLeaseSettings(orgLeaseSettings);
		// orgSettings.setOrgEmailSettings(orgEmailSettings);
		orgSettings.setVAppTemplateLeaseSettings(orgVAppTemplateLeaseSettings);
		orgSettings.setOrgPasswordPolicySettings(orgPasswordPolicySettings);

		AdminOrgType adminOrgType = new AdminOrgType();
		adminOrgType.setName(vCloudOrg.getName());
		adminOrgType.setDescription(vCloudOrg.getDescription());
		adminOrgType.setFullName(vCloudOrg.getFullName());
		adminOrgType.setSettings(orgSettings);
		adminOrgType.setIsEnabled(vCloudOrg.isEnabled());

		return adminOrgType;
	}

	/**
	 * Check for tasks if any
	 * 
	 * @param adminOrg
	 * @return {@link Task}
	 * @throws VCloudException
	 */
	static Task returnTask(VcloudClient client, AdminOrganization adminOrg) throws VCloudException {
		TasksInProgressType tasksInProgress = adminOrg.getResource().getTasks();
		if (tasksInProgress != null)
			for (TaskType task : tasksInProgress.getTask()) {
				return new Task(client, task);
			}
		return null;
	}

}
