package com.vmware.vcloud.automate;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang3.RandomStringUtils;

import com.vmware.vcloud.api.rest.schema.ComposeVAppParamsType;
import com.vmware.vcloud.api.rest.schema.GuestCustomizationSectionType;
import com.vmware.vcloud.api.rest.schema.InstantiationParamsType;
import com.vmware.vcloud.api.rest.schema.NetworkConfigSectionType;
import com.vmware.vcloud.api.rest.schema.NetworkConfigurationType;
import com.vmware.vcloud.api.rest.schema.NetworkConnectionSectionType;
import com.vmware.vcloud.api.rest.schema.NetworkConnectionType;
import com.vmware.vcloud.api.rest.schema.ObjectFactory;
import com.vmware.vcloud.api.rest.schema.QueryResultAdminVAppTemplateRecordType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.SourcedCompositionItemParamType;
import com.vmware.vcloud.api.rest.schema.VAppNetworkConfigurationType;
import com.vmware.vcloud.api.rest.schema.VmType;
import com.vmware.vcloud.api.rest.schema.ovf.MsgType;
import com.vmware.vcloud.api.rest.schema.ovf.SectionType;
import com.vmware.vcloud.model.VCloudOrganization;
import com.vmware.vcloud.sdk.Expression;
import com.vmware.vcloud.sdk.Filter;
import com.vmware.vcloud.sdk.QueryParams;
import com.vmware.vcloud.sdk.RecordResult;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;
import com.vmware.vcloud.sdk.VappTemplate;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.VirtualCpu;
import com.vmware.vcloud.sdk.VirtualDisk;
import com.vmware.vcloud.sdk.VirtualMemory;
import com.vmware.vcloud.sdk.constants.FenceModeValuesType;
import com.vmware.vcloud.sdk.constants.IpAddressAllocationModeType;
import com.vmware.vcloud.sdk.constants.query.ExpressionType;
import com.vmware.vcloud.sdk.constants.query.QueryAdminVAppTemplateField;
import com.vmware.vcloud.sdk.constants.query.QueryRecordType;

public class VappUtils {

	/**
	 * Create the compose vapp params. Creating compose vapp params containing
	 * the vapp templates vms. The same vm is added 3 times with different
	 * names.
	 * 
	 * @param vappTemplateRef
	 * @param vdc
	 * @return
	 * @throws VCloudException
	 */

	static ComposeVAppParamsType createComposeParams(VcloudClient client, VCloudOrganization vCloudOrg,
			ReferenceType vappTemplateRef, Vdc vdc) throws VCloudException {
	
		// Get the href of the OrgNetwork to which we can connect the vApp network
		NetworkConfigurationType networkConfigurationType = new NetworkConfigurationType();
		if (vdc.getResource().getAvailableNetworks().getNetwork().size() == 0) {
			System.out.println("No Networks in vdc to instantiate vapp");
			System.exit(0);
		}		
		
		// Specify the NetworkConfiguration for the vApp network
		System.out.println("	Setting vApp ParantNetwork: "+ vdc.getResource().getAvailableNetworks().getNetwork().get(0).getName());
		networkConfigurationType.setParentNetwork(vdc.getResource().getAvailableNetworks().getNetwork().get(0));
		
		// FIXME: use NATROUTED and change BRIDGED later to solve vCloud Director bug
		networkConfigurationType.setFenceMode(FenceModeValuesType.NATROUTED.value()); 

		VAppNetworkConfigurationType vAppNetworkConfigurationType = new VAppNetworkConfigurationType();
		vAppNetworkConfigurationType.setConfiguration(networkConfigurationType); 
		vAppNetworkConfigurationType.setNetworkName(vdc.getResource().getAvailableNetworks().getNetwork().get(0).getName());
		
		NetworkConfigSectionType networkConfigSectionType = new NetworkConfigSectionType();
		MsgType networkInfo = new MsgType();
		networkConfigSectionType.setInfo(networkInfo);
		
		List<VAppNetworkConfigurationType> vAppNetworkConfigs = networkConfigSectionType.getNetworkConfig();
		vAppNetworkConfigs.add(vAppNetworkConfigurationType);

		// create vApp config
		InstantiationParamsType vappOrvAppTemplateInstantiationParamsType = new InstantiationParamsType(); 
		List<JAXBElement<? extends SectionType>> vappSections = vappOrvAppTemplateInstantiationParamsType.getSection();
		vappSections.add(new ObjectFactory().createNetworkConfigSection(networkConfigSectionType));
		
		ComposeVAppParamsType composeVAppParamsType = new ComposeVAppParamsType(); 
		composeVAppParamsType.setDeploy(false);
		composeVAppParamsType.setInstantiationParams(vappOrvAppTemplateInstantiationParamsType);
		composeVAppParamsType.setName(vCloudOrg.getvApp().getName());
		composeVAppParamsType.setDescription(vCloudOrg.getvApp().getDescription());
		
		List<SourcedCompositionItemParamType> items = composeVAppParamsType.getSourcedItem();

		// getting the vApp Templates first vm.
		VappTemplate vappTemplate = VappTemplate.getVappTemplateByReference(client, vappTemplateRef);
		VappTemplate vm = null; 
		
		for(VappTemplate child : vappTemplate.getChildren()){
			if(child.isVm()){
				vm = child;
			}			
		}
		
		if (vm == null) {
			System.out.println("	Could not find template VM");
			System.exit(1);
		}

		String vmHref = vm.getReference().getHref();

		SourcedCompositionItemParamType vappTemplateItem = new SourcedCompositionItemParamType();
		ReferenceType vappTemplateVMRef = new ReferenceType();
		vappTemplateVMRef.setHref(vmHref);
		vappTemplateVMRef.setName(vCloudOrg.getvApp().getChildVms().get(0).getName());
		vappTemplateItem.setSource(vappTemplateVMRef);

		NetworkConnectionSectionType networkConnectionSectionType = new NetworkConnectionSectionType();
		networkConnectionSectionType.setInfo(networkInfo);

		NetworkConnectionType networkConnectionType = new NetworkConnectionType();
		networkConnectionType.setNetwork(vdc.getResource().getAvailableNetworks().getNetwork().get(0).getName());
		networkConnectionType.setIpAddressAllocationMode(IpAddressAllocationModeType.POOL.value());
		networkConnectionType.setIsConnected(Boolean.TRUE);
		networkConnectionSectionType.getNetworkConnection().add(networkConnectionType);

		InstantiationParamsType vmInstantiationParamsType = new InstantiationParamsType();
		List<JAXBElement<? extends SectionType>> vmSections = vmInstantiationParamsType.getSection();
		vmSections.add(new ObjectFactory().createNetworkConnectionSection(networkConnectionSectionType));

		vappTemplateItem.setInstantiationParams(vmInstantiationParamsType);

		items.add(vappTemplateItem);

		return composeVAppParamsType;

	}
	

	/* Generate a random password */
	static String genPassword(){
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
		String pwd = RandomStringUtils.random( 8, 0, 0, false, false, characters.toCharArray(), new SecureRandom() );
		return pwd;
	}	
	
	/**
	 * Search the vapp template reference. Since the vapptemplate is not unique
	 * under a vdc. This method returns the first occurance of the vapptemplate
	 * in that vdc.
	 * 
	 * @return
	 * @throws VCloudException
	 */
	static ReferenceType findVappTemplateRef(VcloudClient client, String catalogName, String vappTemplateName)
			throws VCloudException {
		ReferenceType vappTemplateRef = new ReferenceType();
		
		QueryParams<QueryAdminVAppTemplateField> queryParams = new QueryParams<QueryAdminVAppTemplateField>();
		
		queryParams.setFilter(new Filter(new Expression(QueryAdminVAppTemplateField.CATALOGNAME, catalogName, ExpressionType.EQUALS)));
		
		RecordResult<QueryResultAdminVAppTemplateRecordType> vappTemplateResult = client.getQueryService().queryRecords(QueryRecordType.ADMINVAPPTEMPLATE, queryParams);
		for (QueryResultAdminVAppTemplateRecordType vappTemplateRecord : vappTemplateResult.getRecords()) { 
			if(vappTemplateRecord.getName().equals(vappTemplateName)){				
				vappTemplateRef.setName(vappTemplateName);
				vappTemplateRef.setHref(vappTemplateRecord.getHref());				
				return vappTemplateRef;
			}
		}		
		
		return null;	
	}

	static void reconfigureVms(Vapp vapp, VCloudOrganization vCloudOrg) throws VCloudException, TimeoutException {
								
		for (VM vm : vapp.getChildrenVms()) {
			System.out.println("	Reconfigure VM: " + vm.getReference().getName());
			System.out.println("	Reconfigure GuestOS...");
			
			// Set VM description 
			VmType vmType = vm.getResource();
			vmType.setDescription("Non-Mobile :"+ vCloudOrg.getNonMobileNo());
			vm.updateVM(vmType).waitForTask(0);
			 			
			// Set administrator password
			GuestCustomizationSectionType guestCustomizationSection = vm.getGuestCustomizationSection();					
			guestCustomizationSection.setComputerName(vCloudOrg.getvApp().getChildVms().get(0).getComputerName());				
			guestCustomizationSection.setAdminPasswordEnabled(Boolean.TRUE);
			guestCustomizationSection.setAdminPasswordAuto(Boolean.FALSE);
			guestCustomizationSection.setResetPasswordRequired(Boolean.TRUE);
			
			guestCustomizationSection.setChangeSid(Boolean.TRUE);
			
			String adminPass = VappUtils.genPassword();			
			guestCustomizationSection.setAdminPassword(adminPass);
			System.out.println("		Administrator password: "+ adminPass);
			
			vm.updateSection(guestCustomizationSection).waitForTask(0);
			
			// Configure CPU
			System.out.println("	Updating CPU Section...");
			VirtualCpu virtualCpuItem = vm.getCpu();
			virtualCpuItem.setCoresPerSocket(vCloudOrg.getvApp().getChildVms().get(0).getvCpu().getCoresPerSocket());
			virtualCpuItem.setNoOfCpus(vCloudOrg.getvApp().getChildVms().get(0).getvCpu().getNoOfCpus());
			vm.updateCpu(virtualCpuItem).waitForTask(0);

			// Configure Memory
			System.out.println("	Updating Memory Section...");
			VirtualMemory virtualMemoryItem = vm.getMemory();
			virtualMemoryItem.setMemorySize(BigInteger.valueOf(1024).multiply(vCloudOrg.getvApp().getChildVms().get(0).getvMemory().getMemorySize()));
			vm.updateMemory(virtualMemoryItem).waitForTask(0);

			// Configure Disks
/*			System.out.println("	Updating Disks Section...");
			List <VirtualDisk> disks = vm.getDisks();
			
			for(VirtualDisk disk: disks){				
				if (disk.isHardDisk()){
					System.out.println("		Disk size: "+ disk.getHardDiskSize());
					BigInteger newDiskSize = disk.getHardDiskSize().multiply(BigInteger.valueOf(2));
					disk.updateHardDiskSize(newDiskSize);
				}				
			}
			
			vm.updateDisks(disks).waitForTask(0);*/
			
			// Display summary
			System.out.println("	Status : " + vm.getVMStatus());
			System.out.println("	CPU : "
					+ vm.getCpu().getNoOfCpus());
			System.out.println("	Memory : "
					+ vm.getMemory().getMemorySize() + " Mb");
			for (VirtualDisk disk : vm.getDisks())
				if (disk.isHardDisk())
					System.out.println("	HardDisk : "
							+ disk.getHardDiskSize() + " Mb");
			
		}
	}
}
