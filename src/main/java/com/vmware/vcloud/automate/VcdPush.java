package com.vmware.vcloud.automate;

import java.io.FileNotFoundException;
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
import com.vmware.vcloud.api.rest.schema.CapacityWithUsageType;
import com.vmware.vcloud.api.rest.schema.ComputeCapacityType;
import com.vmware.vcloud.api.rest.schema.CreateVdcParamsType;
import com.vmware.vcloud.api.rest.schema.OrgGeneralSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgLeaseSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgPasswordPolicySettingsType;
import com.vmware.vcloud.api.rest.schema.OrgSettingsType;
import com.vmware.vcloud.api.rest.schema.OrgVAppTemplateLeaseSettingsType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.TaskType;
import com.vmware.vcloud.api.rest.schema.TasksInProgressType;
import com.vmware.vcloud.api.rest.schema.UserType;
import com.vmware.vcloud.api.rest.schema.VdcStorageProfileParamsType;
import com.vmware.vcloud.model.Organization;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.admin.AdminOrganization;
import com.vmware.vcloud.sdk.admin.AdminVdc;
import com.vmware.vcloud.sdk.admin.EdgeGateway;
import com.vmware.vcloud.sdk.admin.ProviderVdc;
import com.vmware.vcloud.sdk.admin.User;
import com.vmware.vcloud.sdk.admin.VcloudAdmin;
import com.vmware.vcloud.sdk.constants.AllocationModelType;
import com.vmware.vcloud.sdk.constants.Version;


public class VcdPush {
	private static VcloudClient client;
	private static VcloudAdmin admin;
	
	private static Organization org;
	private static AdminVdc adminVdc;
	private static EdgeGateway edgeGateway;
	
	private static String config;
	private static String vcdurl;
	private static String username;
	private static String password;
	
	
	/**
	 * Adding the pay as you go vdc.
	 * 
	 * @param adminClient
	 * @param adminOrg
	 * @throws VCloudException
	 * @throws TimeoutException
	 */
	private static void addPayAsYouGoVdc(VcloudAdmin adminClient, AdminOrganization adminOrg) throws VCloudException, TimeoutException {
		CreateVdcParamsType createVdcParams = new CreateVdcParamsType();

		// Select Provider VDC
		ReferenceType pvdcRef = adminClient.getProviderVdcRefByName(org.getCloudResources().getProviderVdc().getName());
		createVdcParams.setProviderVdcReference(pvdcRef);
		ProviderVdc pvdc = ProviderVdc.getProviderVdcByReference(client, pvdcRef);

		// Select Allocation Model - 'Pay As You Go' Model
		createVdcParams.setAllocationModel(AllocationModelType.ALLOCATIONVAPP.value());

		createVdcParams.setResourceGuaranteedCpu(org.getVdc().getVdcParams().getResourceGuaranteedCpu()); // 20% CPU Resources
		// guaranteed
		createVdcParams.setResourceGuaranteedMemory(org.getVdc().getVdcParams().getResourceGuaranteedMemory()); // 20% Memory
		// resources
		// guaranteed
		// Rest all Defaults for the 'Pay As You Go Model' configuration.

		// COmpute Capacity -- this is needed. UI Uses defaults.
		ComputeCapacityType computeCapacity = new ComputeCapacityType();
		CapacityWithUsageType cpu = new CapacityWithUsageType();
		cpu.setAllocated(org.getVdc().getVdcParams().getComputeCapacity().getCpu().getAllocated()); 
		cpu.setOverhead(org.getVdc().getVdcParams().getComputeCapacity().getCpu().getOverhead());
		cpu.setUnits(org.getVdc().getVdcParams().getComputeCapacity().getCpu().getUnits());
		cpu.setUsed(org.getVdc().getVdcParams().getComputeCapacity().getCpu().getUsed());
		cpu.setLimit(org.getVdc().getVdcParams().getComputeCapacity().getCpu().getLimit());

		computeCapacity.setCpu(cpu);

		CapacityWithUsageType mem = new CapacityWithUsageType();
		mem.setAllocated(org.getVdc().getVdcParams().getComputeCapacity().getMemory().getAllocated());
		mem.setOverhead(org.getVdc().getVdcParams().getComputeCapacity().getMemory().getOverhead());
		mem.setUnits(org.getVdc().getVdcParams().getComputeCapacity().getMemory().getUnits());
		mem.setUsed(org.getVdc().getVdcParams().getComputeCapacity().getMemory().getUsed());
		mem.setLimit(org.getVdc().getVdcParams().getComputeCapacity().getMemory().getLimit());

		computeCapacity.setMemory(mem);

		createVdcParams.setComputeCapacity(computeCapacity);

		// Select Network Pool
		ReferenceType netPoolRef = pvdc.getVMWNetworkPoolRefByName(org.getCloudResources().getNetworkPool().getName());
		createVdcParams.setNetworkPoolReference(netPoolRef);
		createVdcParams.setNetworkQuota(24);

		// Name this Organization vDC
		createVdcParams.setName(org.getVdc().getVdcParams().getName());
		createVdcParams.setDescription(org.getVdc().getVdcParams().getDescription());
		createVdcParams.setIsEnabled(org.getVdc().getVdcParams().isEnabled());

		VdcStorageProfileParamsType vdcStorageProfile = new VdcStorageProfileParamsType();
		vdcStorageProfile.setEnabled(org.getVdc().getVdcParams().getVdcStorageProfile().isEnabled());
		vdcStorageProfile.setDefault(org.getVdc().getVdcParams().getVdcStorageProfile().isDef());
		vdcStorageProfile.setLimit(org.getVdc().getVdcParams().getVdcStorageProfile().getLimit());
		vdcStorageProfile.setUnits(org.getVdc().getVdcParams().getVdcStorageProfile().getUnits());

		ReferenceType providerVdcStorageProfileRef = pvdc.getProviderVdcStorageProfileRefs().iterator().next();
		vdcStorageProfile.setProviderVdcStorageProfile(providerVdcStorageProfileRef);
		createVdcParams.getVdcStorageProfile().add(vdcStorageProfile);

		AdminVdc adminVdc = adminOrg.createAdminVdc(createVdcParams);

		System.out.println("Creating API Sample Pay As You Go Vdc : "
				+ adminVdc.getResource().getName() + " : "
				+ adminVdc.getResource().getHref());
		List<Task> tasks = adminVdc.getTasks();
		if (tasks.size() > 0)
			tasks.get(0).waitForTask(0);

	}	

	
	private static void addUserToOrg(AdminOrganization adminOrg)
			throws TimeoutException {
		UserType newUserType = new UserType();

		// Credentias
		newUserType.setName("sampleuser");
		newUserType.setPassword("samplepassword");
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

/*		SmtpServerSettingsType smtpServerSettings = new SmtpServerSettingsType();
		smtpServerSettings.setHost("custom");
		smtpServerSettings.setIsUseAuthentication(true);
		smtpServerSettings.setPassword("custom");
		smtpServerSettings.setUsername("custom");

		OrgEmailSettingsType orgEmailSettings = new OrgEmailSettingsType();
		orgEmailSettings.setIsDefaultOrgEmail(true);
		orgEmailSettings.setIsDefaultSmtpServer(true);
		orgEmailSettings.setFromEmailAddress("custom@custom.com");
		orgEmailSettings.setDefaultSubjectPrefix("");
		orgEmailSettings.setSmtpServerSettings(smtpServerSettings);*/

		OrgLeaseSettingsType orgLeaseSettings = new OrgLeaseSettingsType();
		orgLeaseSettings.setDeleteOnStorageLeaseExpiration(org.getOrgSettings().getOrgLeaseSettings().isDeleteOnStorageLeaseExpiration());
		orgLeaseSettings.setDeploymentLeaseSeconds(org.getOrgSettings().getOrgLeaseSettings().getDeploymentLeaseSeconds());
		orgLeaseSettings.setStorageLeaseSeconds(org.getOrgSettings().getOrgLeaseSettings().getStorageLeaseSeconds());

		OrgGeneralSettingsType orgGeneralSettings = new OrgGeneralSettingsType();
		orgGeneralSettings.setStoredVmQuota(org.getOrgSettings().getOrgGeneralSettings().getStoredVmQuota());
		orgGeneralSettings.setDeployedVMQuota(org.getOrgSettings().getOrgGeneralSettings().getDeployedVMQuota());
		orgGeneralSettings.setCanPublishCatalogs(org.getOrgSettings().getOrgGeneralSettings().isCanPublishCatalogs());
		
		OrgVAppTemplateLeaseSettingsType orgVAppTemplateLeaseSettings = new OrgVAppTemplateLeaseSettingsType();
		orgVAppTemplateLeaseSettings.setDeleteOnStorageLeaseExpiration(org.getOrgSettings().getOrgVAppTemplateLeaseSettings().isDeleteOnStorageLeaseExpiration());
		orgVAppTemplateLeaseSettings.setStorageLeaseSeconds(org.getOrgSettings().getOrgVAppTemplateLeaseSettings().getStorageLeaseSeconds());	
		
		OrgPasswordPolicySettingsType orgPasswordPolicySettings = new OrgPasswordPolicySettingsType();
		orgPasswordPolicySettings.setAccountLockoutEnabled(org.getOrgSettings().getOrgPasswordPolicySettings().isAccountLockoutEnabled());
		orgPasswordPolicySettings.setAccountLockoutIntervalMinutes(org.getOrgSettings().getOrgPasswordPolicySettings().getAccountLockoutIntervalMinutes());
		orgPasswordPolicySettings.setInvalidLoginsBeforeLockout(org.getOrgSettings().getOrgPasswordPolicySettings().getInvalidLoginsBeforeLockout());
		
		OrgSettingsType orgSettings = new OrgSettingsType();
		orgSettings.setOrgGeneralSettings(orgGeneralSettings);
		orgSettings.setVAppLeaseSettings(orgLeaseSettings);
		//orgSettings.setOrgEmailSettings(orgEmailSettings);
		orgSettings.setVAppTemplateLeaseSettings(orgVAppTemplateLeaseSettings);
		orgSettings.setOrgPasswordPolicySettings(orgPasswordPolicySettings);
		
		AdminOrgType adminOrgType = new AdminOrgType();
		adminOrgType.setName(org.getName()); 
		adminOrgType.setDescription(org.getDescription());
		adminOrgType.setFullName(org.getFullName());
		adminOrgType.setSettings(orgSettings);
		adminOrgType.setIsEnabled(org.isEnabled());
		
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
	
	public static void main(String[] args) throws VCloudException, TimeoutException, FileNotFoundException {
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
		
		System.out.println(args.length);
		
		if(args.length < 6) {
			formatter.printHelp("vcdpush", options);
			System.exit(1);
		}
		
		try {
			CommandLine cmd = parser.parse( options, args);
			
			if(cmd.hasOption("help"))
				formatter.printHelp("vcdpush", options); // automatically generate the help statement
			else {

				vcdurl = cmd.getOptionValue("vcdurl");
				username = cmd.getOptionValue("u");
				password = cmd.getOptionValue("p");
				config = cmd.getOptionValue("config");
																					
				ConfigParser cParser = ConfigParser.getParser(config);
				org = cParser.getOrg();
				
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

				// Create vDC You may end using one of the following.
				addPayAsYouGoVdc(admin, adminOrg);

				// Create user on the organization
				addUserToOrg(adminOrg);

				// Create catalog on the organization
				//addCatalog(adminOrg, org);

				// Create org vdc networks on the organizaiton
				//addBridgedOrgVdcNetwork(adminOrg, org);
				//addNatRoutedOrgVdcNetwork(adminOrg, org);
				//addIsolatedOrgVdcNetwork(adminOrg, org);
				
							
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
