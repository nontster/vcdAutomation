package com.vmware.vcloud.automate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.RandomStringUtils;

import com.vmware.vcloud.api.rest.schema.AdminOrgType;
import com.vmware.vcloud.api.rest.schema.OrgGeneralSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgLeaseSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgPasswordPolicySettingsType;
import com.vmware.vcloud.api.rest.schema.OrgSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgVAppTemplateLeaseSettingsType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.TaskType;
import com.vmware.vcloud.api.rest.schema.TasksInProgressType;
import com.vmware.vcloud.exception.InvalidParameterException;
import com.vmware.vcloud.exception.InvalidTemplateException;
import com.vmware.vcloud.exception.MissingParameterException;
import com.vmware.vcloud.model.OrderType;
import com.vmware.vcloud.model.VCloudOrganization;
import com.vmware.vcloud.sdk.Expression;
import com.vmware.vcloud.sdk.Filter;
import com.vmware.vcloud.sdk.QueryParams;
import com.vmware.vcloud.sdk.ReferenceResult;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.admin.AdminOrganization;
import com.vmware.vcloud.sdk.constants.query.ExpressionType;
import com.vmware.vcloud.sdk.constants.query.QueryReferenceField;
import com.vmware.vcloud.sdk.constants.query.QueryReferenceType;

public class OrgUtils {

	static AdminOrganization findOrgByName(VcloudClient client, String orgName) throws VCloudException {

		if(!orgName.matches("^[a-zA-Z0-9_\\-]*$"))
			throw new InvalidParameterException("Allow only Alphabet, Number, Minus and Underscore");
						
		if(orgName.length() > 20)
            throw new InvalidParameterException("name parameter cannot logner than 20 characters");
		
		AdminOrganization checkedAdminOrg = null;
		//Boolean existStatus = Boolean.FALSE;
		
		QueryParams<QueryReferenceField> params = new QueryParams<QueryReferenceField>();
		Filter filter = new Filter(new Expression(QueryReferenceField.NAME, orgName, ExpressionType.EQUALS));
		params.setFilter(filter);
      
		ReferenceResult result  = client.getQueryService().queryReferences(QueryReferenceType.ORGANIZATION, params);
		
		for (ReferenceType orgReference : result.getReferences()) {
			checkedAdminOrg = AdminOrganization.getAdminOrgById(client, orgReference.getId());
			System.out.println(checkedAdminOrg.getResource().getName());
			return checkedAdminOrg;
		}
				
		return null;
/*		if(checkedAdminOrg != null){
			existStatus = Boolean.TRUE;					
		}
		checkedAdminOrg.getadminvdc				
		return existStatus;*/
	}
	
	
	/**
	 * Creates a new admin org type object
	 * 
	 * @throws VCloudException
	 * @throws MissingParameterException 
	 * 
	 */
	static AdminOrgType createNewAdminOrgType(VCloudOrganization vCloudOrg) throws VCloudException, MissingParameterException {

		// Setting orgLeaseSettings
		OrgLeaseSettingsType orgLeaseSettings = new OrgLeaseSettingsType();
				
		if(vCloudOrg.getOrgSettings()!= null && vCloudOrg.getOrgSettings().getOrgLeaseSettings() != null && vCloudOrg.getOrgSettings().getOrgLeaseSettings().isDeleteOnStorageLeaseExpiration() != null)
			orgLeaseSettings.setDeleteOnStorageLeaseExpiration(vCloudOrg.getOrgSettings().getOrgLeaseSettings().isDeleteOnStorageLeaseExpiration());
		else
			orgLeaseSettings.setDeleteOnStorageLeaseExpiration(Boolean.FALSE);
		
		if(vCloudOrg.getOrgSettings()!= null && vCloudOrg.getOrgSettings().getOrgLeaseSettings() != null && vCloudOrg.getOrgSettings().getOrgLeaseSettings().getDeploymentLeaseSeconds() != null)
			orgLeaseSettings.setDeploymentLeaseSeconds(vCloudOrg.getOrgSettings().getOrgLeaseSettings().getDeploymentLeaseSeconds());
		else
			orgLeaseSettings.setDeploymentLeaseSeconds(0);
			
		if(vCloudOrg.getOrgSettings()!= null && vCloudOrg.getOrgSettings().getOrgLeaseSettings() != null && vCloudOrg.getOrgSettings().getOrgLeaseSettings().getStorageLeaseSeconds() != null)	
			orgLeaseSettings.setStorageLeaseSeconds(vCloudOrg.getOrgSettings().getOrgLeaseSettings().getStorageLeaseSeconds());
		else
			orgLeaseSettings.setStorageLeaseSeconds(0);

		// Setting orgGeneralSettings
		OrgGeneralSettingsType orgGeneralSettings = new OrgGeneralSettingsType();
		
		if(vCloudOrg.getOrgSettings()!= null && vCloudOrg.getOrgSettings().getOrgLeaseSettings() != null && vCloudOrg.getOrgSettings().getOrgGeneralSettings().getStoredVmQuota() != null)		
			orgGeneralSettings.setStoredVmQuota(vCloudOrg.getOrgSettings().getOrgGeneralSettings().getStoredVmQuota());
		else
			orgGeneralSettings.setStoredVmQuota(0);
		
		if(vCloudOrg.getOrgSettings()!= null && vCloudOrg.getOrgSettings().getOrgLeaseSettings() != null && vCloudOrg.getOrgSettings().getOrgGeneralSettings().getDeployedVMQuota() != null)
			orgGeneralSettings.setDeployedVMQuota(vCloudOrg.getOrgSettings().getOrgGeneralSettings().getDeployedVMQuota());
		else
			orgGeneralSettings.setDeployedVMQuota(0);
			
		if(vCloudOrg.getOrgSettings()!= null && vCloudOrg.getOrgSettings().getOrgLeaseSettings() != null && vCloudOrg.getOrgSettings().getOrgGeneralSettings().isCanPublishCatalogs() != null)	
			orgGeneralSettings.setCanPublishCatalogs(vCloudOrg.getOrgSettings().getOrgGeneralSettings().isCanPublishCatalogs());
		else
			orgGeneralSettings.setCanPublishCatalogs(Boolean.FALSE);
		
		// Setting orgVAppTemplateLeaseSettings
		OrgVAppTemplateLeaseSettingsType orgVAppTemplateLeaseSettings = new OrgVAppTemplateLeaseSettingsType();
		
		if(vCloudOrg.getOrgSettings()!= null && vCloudOrg.getOrgSettings().getOrgVAppTemplateLeaseSettings() != null && vCloudOrg.getOrgSettings().getOrgVAppTemplateLeaseSettings().isDeleteOnStorageLeaseExpiration() != null)
			orgVAppTemplateLeaseSettings.setDeleteOnStorageLeaseExpiration(vCloudOrg.getOrgSettings().getOrgVAppTemplateLeaseSettings().isDeleteOnStorageLeaseExpiration());
		else
			orgVAppTemplateLeaseSettings.setDeleteOnStorageLeaseExpiration(Boolean.FALSE);
		
		if(vCloudOrg.getOrgSettings()!= null && vCloudOrg.getOrgSettings().getOrgVAppTemplateLeaseSettings() != null && vCloudOrg.getOrgSettings().getOrgVAppTemplateLeaseSettings().getStorageLeaseSeconds() != null)
			orgVAppTemplateLeaseSettings.setStorageLeaseSeconds(vCloudOrg.getOrgSettings().getOrgVAppTemplateLeaseSettings().getStorageLeaseSeconds());
		else
			orgVAppTemplateLeaseSettings.setStorageLeaseSeconds(0);
		
		// Setting orgPasswordPolicySettings
		OrgPasswordPolicySettingsType orgPasswordPolicySettings = new OrgPasswordPolicySettingsType();
		
		if(vCloudOrg.getOrgSettings()!= null && vCloudOrg.getOrgSettings().getOrgPasswordPolicySettings() != null && vCloudOrg.getOrgSettings().getOrgPasswordPolicySettings().isAccountLockoutEnabled() != null)
			orgPasswordPolicySettings.setAccountLockoutEnabled(vCloudOrg.getOrgSettings().getOrgPasswordPolicySettings().isAccountLockoutEnabled());		
		else 
			orgPasswordPolicySettings.setAccountLockoutEnabled(Boolean.TRUE);
			
		if(vCloudOrg.getOrgSettings()!= null && vCloudOrg.getOrgSettings().getOrgPasswordPolicySettings() != null && vCloudOrg.getOrgSettings().getOrgPasswordPolicySettings().getAccountLockoutIntervalMinutes() != null)
			orgPasswordPolicySettings.setAccountLockoutIntervalMinutes(vCloudOrg.getOrgSettings().getOrgPasswordPolicySettings().getAccountLockoutIntervalMinutes());
		else
			orgPasswordPolicySettings.setAccountLockoutIntervalMinutes(15);
		
		if(vCloudOrg.getOrgSettings()!= null && vCloudOrg.getOrgSettings().getOrgPasswordPolicySettings() != null && vCloudOrg.getOrgSettings().getOrgPasswordPolicySettings().getInvalidLoginsBeforeLockout() != null)
			orgPasswordPolicySettings.setInvalidLoginsBeforeLockout(vCloudOrg.getOrgSettings().getOrgPasswordPolicySettings().getInvalidLoginsBeforeLockout());
		else
			orgPasswordPolicySettings.setInvalidLoginsBeforeLockout(15);
				
		OrgSettingsType orgSettings = new OrgSettingsType();
		orgSettings.setOrgGeneralSettings(orgGeneralSettings);
		orgSettings.setVAppLeaseSettings(orgLeaseSettings);
		orgSettings.setVAppTemplateLeaseSettings(orgVAppTemplateLeaseSettings);
		orgSettings.setOrgPasswordPolicySettings(orgPasswordPolicySettings);

		AdminOrgType adminOrgType = new AdminOrgType();
		
		if(vCloudOrg.getName() != null)
			adminOrgType.setName(vCloudOrg.getName());
		else {					
			if(vCloudOrg.getShortName() != null){				
				adminOrgType.setName(getOrgNameFromOrderType(vCloudOrg));
				vCloudOrg.setName(getOrgNameFromOrderType(vCloudOrg));				
			} else {
				throw new MissingParameterException("Missing shortName parameter");
			}
		}
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		StringBuffer descBuff = new StringBuffer();
		
		if(vCloudOrg.getCaNumber() != null)
			descBuff.append("CA Number :").append(vCloudOrg.getCaNumber()).append("\n");
				
		if(vCloudOrg.getStartDate() != null && !df.format(vCloudOrg.getStartDate()).isEmpty()){
			
			descBuff.append("Start Date :").append(df.format(vCloudOrg.getStartDate()));
			
			if(vCloudOrg.getEndDate() != null && !df.format(vCloudOrg.getEndDate()).isEmpty() && vCloudOrg.getOrderType().name().equalsIgnoreCase(OrderType.TRIAL.name())){
				descBuff.append(" - ").append(df.format(vCloudOrg.getEndDate()));
			}
			
			descBuff.append("\n");
		}
		
		descBuff.append("Customer Contract :").append("\n");
		if(vCloudOrg.getUser() != null && vCloudOrg.getUser().getFullName() != null)
			descBuff.append("   Name: ").append(vCloudOrg.getUser().getFullName()).append("\n");
		if(vCloudOrg.getUser() != null && vCloudOrg.getUser().getEmailAddress() != null)
			descBuff.append("   Email: ").append(vCloudOrg.getUser().getEmailAddress()).append("\n");
		if(vCloudOrg.getUser() != null && vCloudOrg.getUser().getPhone() != null)
			descBuff.append("   Tel.: ").append(vCloudOrg.getUser().getPhone()).append("\n");
				
		if(vCloudOrg.getDescription() != null && !vCloudOrg.getDescription().isEmpty())
			adminOrgType.setDescription(vCloudOrg.getDescription());
		else
			adminOrgType.setDescription(descBuff.toString());
		
		if(vCloudOrg.getFullName() != null)
			adminOrgType.setFullName(vCloudOrg.getFullName());
		else
			throw new NullPointerException("Organization full name cannot be NULL");
			
		adminOrgType.setSettings(orgSettings);
		
		if(vCloudOrg.isEnabled() != null)
			adminOrgType.setIsEnabled(vCloudOrg.isEnabled());
		else
			adminOrgType.setIsEnabled(Boolean.TRUE);

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
	
	static String getOrgNameFromOrderType(VCloudOrganization vCloudOrg){
		
		StringBuffer orgName = new StringBuffer();	
		
		if(vCloudOrg.getOrderType().name().equalsIgnoreCase("trial"))
			orgName.append("Trial-");	
		else if(vCloudOrg.getOrderType().name().equalsIgnoreCase("test"))
			orgName.append("Test-");
		
		orgName.append(vCloudOrg.getShortName());
				
		return orgName.toString();
		
	}

}
