package com.vmware.vcloud.automate;

import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.vmware.vcloud.api.rest.schema.AdminOrgType;
import com.vmware.vcloud.api.rest.schema.OrgEmailSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgGeneralSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgLeaseSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgPasswordPolicySettingsType;
import com.vmware.vcloud.api.rest.schema.OrgSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgVAppTemplateLeaseSettingsType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.RoleType;
import com.vmware.vcloud.api.rest.schema.SmtpServerSettingsType;
import com.vmware.vcloud.api.rest.schema.TaskType;
import com.vmware.vcloud.api.rest.schema.TasksInProgressType;
import com.vmware.vcloud.api.rest.schema.UserType;
import com.vmware.vcloud.api.rest.schema.UsersListType;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.admin.AdminOrganization;
import com.vmware.vcloud.sdk.admin.User;
import com.vmware.vcloud.sdk.admin.VcloudAdmin;
import com.vmware.vcloud.sdk.constants.Version;

public class VcdPush {
	private static VcloudClient client;
	private static VcloudAdmin admin;
	private static String config;
	private static String vcdurl;
	private static String username;
	private static String password;
	
	

	private static void addUserToOrg(AdminOrganization adminOrg)
			throws TimeoutException {
		UserType newUserType = new UserType();

		// Credentias
		newUserType.setName("API_Sample_user");
		newUserType.setPassword("password");
		newUserType.setIsEnabled(true);

		// Role : 'Customer Managed Service'
		ReferenceType usrRoleRef = admin.getRoleRefByName("Customer Managed Service");
		newUserType.setRole(usrRoleRef);

		// COntact Info:
		newUserType.setFullName("User Full Name");
		newUserType.setEmailAddress("user@company.com");
		// Use defaults for rest of the fields.

		try {
			User user = adminOrg.createUser(newUserType);

			System.out.println("Creating API Sample User : "
					+ user.getResource().getName() + " : "
					+ user.getResource().getHref());
			List<Task> tasks = user.getTasks();
			if (tasks.size() > 0)
				tasks.get(0).waitForTask(0);

		} catch (VCloudException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * Check for tasks if any
	 * 
	 * @param user
	 * @return {@link Task}
	 * @throws VCloudException
	 */
	public static Task returnTask(User user) throws VCloudException {
		TasksInProgressType tasksInProgress = user.getResource().getTasks();
		if (tasksInProgress != null)
			for (TaskType task : tasksInProgress.getTask()) {
				return new Task(client, task);
			}
		return null;
	}	
	
	/**
	 * Creates a new admin org type object
	 * @throws VCloudException 
	 * 
	 */
	private static AdminOrgType createNewAdminOrgType() throws VCloudException {

		SmtpServerSettingsType smtpServerSettings = new SmtpServerSettingsType();
		smtpServerSettings.setHost("custom");
		smtpServerSettings.setIsUseAuthentication(true);
		smtpServerSettings.setPassword("custom");
		smtpServerSettings.setUsername("custom");

		OrgEmailSettingsType orgEmailSettings = new OrgEmailSettingsType();
		orgEmailSettings.setIsDefaultOrgEmail(true);
		orgEmailSettings.setIsDefaultSmtpServer(true);
		orgEmailSettings.setFromEmailAddress("custom@custom.com");
		orgEmailSettings.setDefaultSubjectPrefix("");
		orgEmailSettings.setSmtpServerSettings(smtpServerSettings);

		OrgLeaseSettingsType orgLeaseSettings = new OrgLeaseSettingsType();
		orgLeaseSettings.setDeleteOnStorageLeaseExpiration(false);
		orgLeaseSettings.setDeploymentLeaseSeconds(0);
		orgLeaseSettings.setStorageLeaseSeconds(0);

		OrgGeneralSettingsType orgGeneralSettings = new OrgGeneralSettingsType();
		orgGeneralSettings.setStoredVmQuota(0);
		orgGeneralSettings.setDeployedVMQuota(0);
		orgGeneralSettings.setCanPublishCatalogs(false);
		
		OrgVAppTemplateLeaseSettingsType orgVAppTemplateLeaseSettings = new OrgVAppTemplateLeaseSettingsType();
		orgVAppTemplateLeaseSettings.setDeleteOnStorageLeaseExpiration(false);
		orgVAppTemplateLeaseSettings.setStorageLeaseSeconds(0);	
		
		OrgPasswordPolicySettingsType orgPasswordPolicySettings = new OrgPasswordPolicySettingsType();
		orgPasswordPolicySettings.setAccountLockoutEnabled(true);
		orgPasswordPolicySettings.setAccountLockoutIntervalMinutes(15);
		orgPasswordPolicySettings.setInvalidLoginsBeforeLockout(15);
		
		OrgSettingsType orgSettings = new OrgSettingsType();
		orgSettings.setOrgGeneralSettings(orgGeneralSettings);
		orgSettings.setVAppLeaseSettings(orgLeaseSettings);
		orgSettings.setOrgEmailSettings(orgEmailSettings);
		orgSettings.setVAppTemplateLeaseSettings(orgVAppTemplateLeaseSettings);
		orgSettings.setOrgPasswordPolicySettings(orgPasswordPolicySettings);
		
		AdminOrgType adminOrgType = new AdminOrgType();
		adminOrgType.setName("CustomAdminOrg");
		adminOrgType.setDescription("Custom Admin Org Desc");
		adminOrgType.setFullName("Custom Admin Org Full Name");
		adminOrgType.setSettings(orgSettings);
		adminOrgType.setIsEnabled(true);
		
		return adminOrgType;
	}

	/**
	 * Check for tasks if any
	 * 
	 * @param adminOrg
	 * @return {@link Task}
	 * @throws VCloudException
	 */
	public static Task returnTask(AdminOrganization adminOrg)
			throws VCloudException {
		TasksInProgressType tasksInProgress = adminOrg.getResource().getTasks();
		if (tasksInProgress != null)
			for (TaskType task : tasksInProgress.getTask()) {
				return new Task(client, task);
			}
		return null;
	}
	
	public static void main(String[] args) throws VCloudException, TimeoutException {
		// TODO Auto-generated method stub
		// create Options object
		Options options = new Options();
		HelpFormatter formatter = new HelpFormatter();
		CommandLineParser parser = new DefaultParser();
		
		Option optConfig = new Option( "config", true, "configuration file" );
		Option optVcdurl = new Option( "vcdurl", true, "vCloud Director url" );
		Option optUsername = new Option( "u", true, "username" );
		Option optPassword = new Option( "p", true, "password" );
		Option optHelp = new Option( "help", "print this message" );		
		Option optDebug = new Option( "debug", "print debugging information" );
		
		options.addOption(optConfig);
		options.addOption(optVcdurl);
		options.addOption(optUsername);
		options.addOption(optPassword);
		options.addOption(optHelp);
		options.addOption(optDebug);
		
		if(args.length < 4) {
			formatter.printHelp("vcdpush", options);
			System.exit(1);
		}
		
		try {
			CommandLine cmd = parser.parse( options, args);
			
			if(cmd.hasOption("help"))
				formatter.printHelp("vcdpush", options); // automatically generate the help statement
			else {

				config = cmd.getOptionValue("config");
				vcdurl = cmd.getOptionValue("vcdurl");
				username = cmd.getOptionValue("u");
				password = cmd.getOptionValue("p");
								
				VcloudClient.setLogLevel(Level.OFF);
				System.out.println("Vcloud Login");
				client = new VcloudClient(vcdurl, Version.V5_5); 
				
				client.login(username, password);
				System.out.println("	Login Success\n");

				System.out.println("Get Vcloud Admin");
				admin = client.getVcloudAdmin();
				System.out.println("	" + admin.getResource().getHref() + "\n");					

				System.out.println("Add New Organization");
				AdminOrganization adminOrg = admin.createAdminOrg(createNewAdminOrgType());
				Task task = returnTask(adminOrg);
				if (task != null)
					task.waitForTask(0);
				System.out.println("	" + adminOrg.getResource().getName());
				System.out.println("	" + adminOrg.getResource().getHref() + "\n");

				// Create user on the organization
				addUserToOrg(adminOrg);
				
				
				System.out.println("Update Organization to Disabled");
				adminOrg.getResource().setIsEnabled(false);
				adminOrg.updateAdminOrg(adminOrg.getResource());
				task = returnTask(adminOrg);
				if (task != null)
					task.waitForTask(0);
				System.out.println("	Updated\n");

				System.out.println("Delete Organization");
				adminOrg.delete();
				System.out.println("	Deleted\n");
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
		}
		
	
	}

}
