package com.vmware.vcloud.automate;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.vmware.vcloud.exception.ExternalNetworkNotFoundException;
import com.vmware.vcloud.exception.InsufficientIPAddressesException;
import com.vmware.vcloud.exception.InvalidTemplateException;
import com.vmware.vcloud.exception.MissingParameterException;
import com.vmware.vcloud.exception.MissingVMTemplateException;
import com.vmware.vcloud.exception.UserRoleNotFoundException;
import com.vmware.vcloud.exception.VdcNetworkNotAvailableException;
import com.vmware.vcloud.model.VCloudOrganization;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.admin.AdminOrganization;
import com.vmware.vcloud.sdk.admin.AdminVdc;
import com.vmware.vcloud.sdk.admin.EdgeGateway;
import com.vmware.vcloud.sdk.admin.VcloudAdmin;
import com.vmware.vcloud.sdk.constants.Version;

public class VcdPush {
	private static VcloudClient client;
	private static VcloudAdmin admin;

	private static VCloudOrganization vCloudOrg;
	private static AdminVdc adminVdc;
	private static EdgeGateway edgeGateway;

	private static String vcdurl;
	private static String username;
	private static String password;
	private static String template;	
	private static String output;	

	private static Properties prop = new Properties();
	private static InputStream input = null;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// create Options object
		Options options = new Options();
		HelpFormatter formatter = new HelpFormatter();
		CommandLineParser parser = new DefaultParser();
		Console cnsl = null;

 
		Option optHelp = Option.builder("h").longOpt("help").desc("print usage").hasArg(false).required(false)
				.argName("help").build();

		Option optBlueprint = Option.builder("t").longOpt("template").desc("template file").hasArg(true)
				.required(true).argName("file").build();

		Option optVcdurl = Option.builder("l").longOpt("vcdurl").desc("vCloud Director public URL").hasArg(true)
				.required(false).argName("url").build();

		Option optUsername = Option.builder("u").longOpt("user").desc("username").hasArg(true).required(true)
				.argName("username").build();

		Option optPassword = Option.builder("p").longOpt("password").desc("password").hasArg(true).required(false)
				.argName("password").build();
		
		Option optOutput = Option.builder("o").longOpt("output").desc("output").hasArg(true).required(false)
				.argName("output").build();

		Option optVersion = Option.builder("v").longOpt("version").desc("version").hasArg(false).required(false)
				.argName("version").build();
		
		options.addOption(optBlueprint);
		options.addOption(optVcdurl);
		options.addOption(optUsername);
		options.addOption(optPassword);
		options.addOption(optHelp);
		options.addOption(optOutput);
		options.addOption(optVersion);	
		
		if (args.length < 1) {
			System.err.println("Type Bee -h or --help for usage.");
			return;
		} else if (args[0].equals("-v") || args[0].equals("--version")) {
			System.out.println("Bee version 1.1.1");
			return;
		} else if(args[0].equals("-h") || args[0].equals("--help")){
			formatter.printHelp("bee", options); 
			return;
		}		
		
		try {
 
			// load a properties file
			FileInputStream input = new FileInputStream(System.getProperty("lazybee.confdir")+"config.properties");			
			prop.load(input);
						
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("vcdurl")) {
				vcdurl = cmd.getOptionValue("vcdurl");
			}

			if (cmd.hasOption("user")) {
				username = cmd.getOptionValue("user");

				int index = username.indexOf('@');

				if (index < 0)
					username += "@System";
			}

			if (cmd.hasOption("password")) {
				password = cmd.getOptionValue("password");
			}

			if (cmd.hasOption("template")) {
				template = cmd.getOptionValue("template");
			}

			if (cmd.hasOption("output")) {
				output = cmd.getOptionValue("output");
			}

			if (vcdurl == null) {
				vcdurl = prop.getProperty("url");
			}

			if (vcdurl.charAt(vcdurl.length() - 1) != '/') {
				vcdurl += '/';
			}

			if (password == null) {
				// creates a console object
				cnsl = System.console();

				// if console is not null
				if (cnsl != null) {
					// read password into the char array
					char[] pwd = cnsl.readPassword("Enter your password: ");
					password = new String(pwd);
				}
			}

			if (output == null) {
				Path currentRelativePath = Paths.get("");
				output = currentRelativePath.toAbsolutePath().toString() + File.separator + "report.xlsx";
			}

			ConfigParser cParser = ConfigParser.getParser(template);
			cParser.validate();
			vCloudOrg = cParser.getOrg();

			// Check template version
			if (!vCloudOrg.getTemplate_version().equalsIgnoreCase("2016-10-25")) {
				System.err.println("Invalid template version!");
				System.exit(1);
			}

			VcloudClient.setLogLevel(Level.OFF);
			System.out.println("Vcloud Login");
			client = new VcloudClient(vcdurl, Version.V5_5);

			client.login(username, password);
			System.out.println("	Login Success\n");

			System.out.println("Get Vcloud Admin");
			admin = client.getVcloudAdmin();
			System.out.println("	" + admin.getResource().getHref() + "\n");

			System.out.print("Add New Organization : ");
			AdminOrganization adminOrg = admin.createAdminOrg(OrgUtils.createNewAdminOrgType(vCloudOrg));
			Task task = OrgUtils.returnTask(client, adminOrg);
			if (task != null)
				task.waitForTask(0);
			System.out.print(adminOrg.getResource().getName() + " : ");
			System.out.println(adminOrg.getResource().getHref() + "\n");

			// Set vCloud director URL for organization
			vCloudOrg.setUrl(vcdurl + "cloud/org/" + vCloudOrg.getName() + "/");

			// Create vDC You may end using one of the following.
			adminVdc = VdcUtils.addPayAsYouGoVdc(vCloudOrg, admin, client, adminOrg, prop);

			// Create user on the organization
			UserUtils.addUserToOrg(vCloudOrg, admin, adminOrg);

			// Create org vdc networks on the organizaiton
			edgeGateway = NetworkUtils.addNatRoutedOrgVdcNetwork(client, vCloudOrg, adminVdc, adminOrg, prop);

			// find the vdc ref
			Vdc vdc = Vdc.getVdcByReference(client, adminVdc.getVdcReference());

			// find the vapp template ref
			String catalogName = null;

			if (vCloudOrg.getCloudResources() != null && vCloudOrg.getCloudResources().getCatalog() != null
					&& vCloudOrg.getCloudResources().getCatalog().getName() != null)
				catalogName = vCloudOrg.getCloudResources().getCatalog().getName();
			else
				catalogName = prop.getProperty("catalog");

			// Composed vApp.
			if (vCloudOrg.getvApp() != null && vCloudOrg.getvApp().getName() != null)
				System.out.println("vApp: " + vCloudOrg.getvApp().getName());
			else
				System.out.println("vApp: vApp_system_1");

			Vapp vapp = vdc.composeVapp(VappUtils.createComposeParams(client, vCloudOrg, catalogName, vdc));
			System.out.print("	Composing vApp : " + vapp.getResource().getName());
			List<Task> tasks = vapp.getTasks();
			if (tasks.size() > 0)
				tasks.get(0).waitForTask(0);

			// refresh the vapp
			vapp = Vapp.getVappByReference(client, vapp.getReference());

			System.out.println(" - " + vapp.getResource().getHref());

			// reconfigure Vms
			VappUtils.reconfigureVms(vapp, vCloudOrg);

			// generate report
			ReportUtils.generateReport(client, vapp, edgeGateway, vCloudOrg, adminVdc.getResource().getName(), output);
			System.out.println("---------- Completed! ----------");
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("File not found: " + e.getMessage());
		} catch (VCloudException e) {
			// TODO Auto-generated catch block
			if(e.getMessage().equalsIgnoreCase("Unauthorized"))
				System.err.println("Invalid username or password");
			else
				System.err.println("vCloud exception: \n" + e.getMessage());
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			System.err.println("Time out on connecting to: "+ vcdurl +"\n"+ e.getMessage());
		} catch (InvalidTemplateException e) {
			// TODO Auto-generated catch block
			System.err.println("InvalidTemplate exception: \n" + e.getMessage());
		} catch (UserRoleNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("UserRoleNotFound exception: \n" + e.getMessage());
		} catch (ExternalNetworkNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("ExternalNetworkNotFound exception: \n" + e.getMessage());
		} catch (InsufficientIPAddressesException e) {
			// TODO Auto-generated catch block
			System.err.println("InsufficientIPAddresses exception: \n" + e.getMessage());
		} catch (MissingVMTemplateException e) {
			// TODO Auto-generated catch block
			System.err.println("MissingVMTemplate exception: \n" + e.getMessage());
		} catch (VdcNetworkNotAvailableException e) {
			// TODO Auto-generated catch block
			System.err.println("VdcNetworkNotAvailable exception: \n" + e.getMessage());
		} catch (MissingParameterException e) {
			// TODO Auto-generated catch block
			System.err.println("MissingParameter exception: \n" + e.getMessage());
		} 

	}

}
