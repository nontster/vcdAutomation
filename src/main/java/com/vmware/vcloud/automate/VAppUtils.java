package com.vmware.vcloud.automate;

import java.util.List;

import javax.xml.bind.JAXBElement;

import com.vmware.vcloud.api.rest.schema.ComposeVAppParamsType;
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
import com.vmware.vcloud.api.rest.schema.ovf.MsgType;
import com.vmware.vcloud.api.rest.schema.ovf.SectionType;
import com.vmware.vcloud.model.VCloudOrganization;
import com.vmware.vcloud.sdk.Expression;
import com.vmware.vcloud.sdk.Filter;
import com.vmware.vcloud.sdk.QueryParams;
import com.vmware.vcloud.sdk.RecordResult;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VappTemplate;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.constants.FenceModeValuesType;
import com.vmware.vcloud.sdk.constants.IpAddressAllocationModeType;
import com.vmware.vcloud.sdk.constants.query.ExpressionType;
import com.vmware.vcloud.sdk.constants.query.QueryAdminVAppTemplateField;
import com.vmware.vcloud.sdk.constants.query.QueryRecordType;

public class VAppUtils {

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
		System.out.println("Setting vApp ParantNetwork: "+ vdc.getResource().getAvailableNetworks().getNetwork().get(0).getName());
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
			System.out.println("Could not find template VM");
			System.exit(1);
		}
		
		String vmHref = vm.getReference().getHref();

		SourcedCompositionItemParamType vappTemplateItem = new SourcedCompositionItemParamType();
		ReferenceType vappTemplateVMRef = new ReferenceType();
		vappTemplateVMRef.setHref(vmHref);
		vappTemplateVMRef.setName(vCloudOrg.getvApp().getName());
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
	

	/**
	 * Search the vapp template reference. Since the vapptemplate is not unique
	 * under a vdc. This method returns the first occurance of the vapptemplate
	 * in that vdc.
	 * 
	 * @return
	 * @throws VCloudException
	 */
	public static ReferenceType findVappTemplateRef(VcloudClient client, String catalogName, String vappTemplateName)
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

	
}
