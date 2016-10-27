package com.vmware.vcloud.automate;

import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.RandomStringUtils;

import com.vmware.vcloud.api.rest.schema.CapacityWithUsageType;
import com.vmware.vcloud.api.rest.schema.ComputeCapacityType;
import com.vmware.vcloud.api.rest.schema.CreateVdcParamsType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.VdcStorageProfileParamsType;
import com.vmware.vcloud.model.VCloudOrganization;
import com.vmware.vcloud.sdk.Organization;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.admin.AdminOrganization;
import com.vmware.vcloud.sdk.admin.AdminVdc;
import com.vmware.vcloud.sdk.admin.ProviderVdc;
import com.vmware.vcloud.sdk.admin.VcloudAdmin;
import com.vmware.vcloud.sdk.constants.AllocationModelType;

public class VdcUtils {
	
	/**
	 * Adding the pay as you go vdc.
	 * 
	 * @param adminClient
	 * @param adminOrg
	 * @throws VCloudException
	 * @throws TimeoutException
	 */
	static AdminVdc addPayAsYouGoVdc(VCloudOrganization org, VcloudAdmin adminClient, VcloudClient client, AdminOrganization adminOrg)
			throws VCloudException, TimeoutException {
		CreateVdcParamsType createVdcParams = new CreateVdcParamsType();

		// Select Provider VDC
		String providerVdc = null;
		
		if(org.getCloudResources() != null && org.getCloudResources().getProviderVdc() != null && org.getCloudResources().getProviderVdc().getName() != null)
			providerVdc = org.getCloudResources().getProviderVdc().getName();
		else
			providerVdc = "TLS1-ALLOC-PVDC01";
				
		ReferenceType pvdcRef = adminClient.getProviderVdcRefByName(providerVdc);
		createVdcParams.setProviderVdcReference(pvdcRef);
		ProviderVdc pvdc = ProviderVdc.getProviderVdcByReference(client, pvdcRef);

		// Select Allocation Model - 'Pay As You Go' Model
		createVdcParams.setAllocationModel(AllocationModelType.ALLOCATIONVAPP.value());

		// guaranteed 20% CPU/Memory resources
		
		Float resourceGuaranteedCpu = null;
		Float resourceGuaranteedMemory = null;
		
		if(org.getVdc() != null && org.getVdc().getResourceGuaranteedCpu() != null)
			resourceGuaranteedCpu = org.getVdc().getResourceGuaranteedCpu();
		else
			resourceGuaranteedCpu = new Float(0.2);
		
		if(org.getVdc() != null && org.getVdc().getResourceGuaranteedMemory() != null)
			resourceGuaranteedMemory = org.getVdc().getResourceGuaranteedMemory();
		else
			resourceGuaranteedMemory = new Float(0.2);
		
		createVdcParams.setResourceGuaranteedCpu(Double.parseDouble(resourceGuaranteedCpu.toString())); 																											 																											
		createVdcParams.setResourceGuaranteedMemory(Double.parseDouble(resourceGuaranteedMemory.toString())); 

		// Rest all Defaults for the 'Pay As You Go Model' configuration.
		// Compute Capacity -- this is needed. UI Uses defaults.
		ComputeCapacityType computeCapacity = new ComputeCapacityType();
		
		// Setting CPU section
		CapacityWithUsageType cpu = new CapacityWithUsageType();
		
		if (org.getVdc() != null 
				&& org.getVdc().getComputeCapacity() != null
				&& org.getVdc().getComputeCapacity().getCpu() != null
				&& org.getVdc().getComputeCapacity().getCpu().getAllocated() != null)
			cpu.setAllocated(new Long(org.getVdc().getComputeCapacity().getCpu().getAllocated()));
		else
			cpu.setAllocated(new Long(0));
		
		if (org.getVdc() != null 
				&& org.getVdc().getComputeCapacity() != null
				&& org.getVdc().getComputeCapacity().getCpu() != null
				&& org.getVdc().getComputeCapacity().getCpu().getOverhead() != null)
			cpu.setOverhead(new Long(org.getVdc().getComputeCapacity().getCpu().getOverhead()));
		else
			cpu.setOverhead(new Long(0));
		
		if (org.getVdc() != null 
				&& org.getVdc().getComputeCapacity() != null
				&& org.getVdc().getComputeCapacity().getCpu() != null
				&& org.getVdc().getComputeCapacity().getCpu().getUnits() != null)				
			cpu.setUnits(org.getVdc().getComputeCapacity().getCpu().getUnits());
		else
			cpu.setUnits("MHz");
		
		if (org.getVdc() != null 
				&& org.getVdc().getComputeCapacity() != null
				&& org.getVdc().getComputeCapacity().getCpu() != null
				&& org.getVdc().getComputeCapacity().getCpu().getUsed() != null)				
			cpu.setUsed(new Long(org.getVdc().getComputeCapacity().getCpu().getUsed()));		
		else
			cpu.setUsed(new Long(0));
		
		if (org.getVdc() != null 
				&& org.getVdc().getComputeCapacity() != null
				&& org.getVdc().getComputeCapacity().getCpu() != null
				&& org.getVdc().getComputeCapacity().getCpu().getLimit() != null)				
			cpu.setLimit(org.getVdc().getComputeCapacity().getCpu().getLimit());
		else
			cpu.setLimit(new Long(0));
				
		computeCapacity.setCpu(cpu);

		// Setting Memory section
		CapacityWithUsageType mem = new CapacityWithUsageType();
		
		if (org.getVdc() != null 
				&& org.getVdc().getComputeCapacity() != null
				&& org.getVdc().getComputeCapacity().getMemory() != null
				&& org.getVdc().getComputeCapacity().getMemory().getAllocated() != null)			
			mem.setAllocated(new Long(org.getVdc().getComputeCapacity().getMemory().getAllocated()));		
		else
			mem.setAllocated(new Long(0));
		
		if (org.getVdc() != null 
				&& org.getVdc().getComputeCapacity() != null
				&& org.getVdc().getComputeCapacity().getMemory() != null
				&& org.getVdc().getComputeCapacity().getMemory().getOverhead() != null)		
			mem.setOverhead(new Long(org.getVdc().getComputeCapacity().getMemory().getOverhead()));
		else
			mem.setOverhead(new Long(0));
		
		if (org.getVdc() != null 
				&& org.getVdc().getComputeCapacity() != null
				&& org.getVdc().getComputeCapacity().getMemory() != null
				&& org.getVdc().getComputeCapacity().getMemory().getUnits() != null)					
			mem.setUnits(org.getVdc().getComputeCapacity().getMemory().getUnits());
		else
			mem.setUnits("MB");
		
		if (org.getVdc() != null 
				&& org.getVdc().getComputeCapacity() != null
				&& org.getVdc().getComputeCapacity().getMemory() != null
				&& org.getVdc().getComputeCapacity().getMemory().getUsed() != null)			
			mem.setUsed(new Long(org.getVdc().getComputeCapacity().getMemory().getUsed()));
		else
			mem.setUsed(new Long(0));

		if (org.getVdc() != null 
				&& org.getVdc().getComputeCapacity() != null
				&& org.getVdc().getComputeCapacity().getMemory() != null
				&& org.getVdc().getComputeCapacity().getMemory().getLimit() != null)
			mem.setLimit(org.getVdc().getComputeCapacity().getMemory().getLimit());
		else
			mem.setLimit(new Long(0));
		
		computeCapacity.setMemory(mem);

		createVdcParams.setComputeCapacity(computeCapacity);

		// Select Network Pool
		
		String networkPool = org.getCloudResources().getNetworkPool().getName();
		
		if(networkPool == null)
			networkPool = "TLS1-ALLOC-PVDC01-VXLAN-NP";
		
		// Set network pool
		ReferenceType netPoolRef = pvdc.getVMWNetworkPoolRefByName(networkPool);
		createVdcParams.setNetworkPoolReference(netPoolRef);
		
		// Set network quota
		if(org.getVdc() != null && org.getVdc().getNetworkQuota() != null)
			createVdcParams.setNetworkQuota(org.getVdc().getNetworkQuota());
		else
			createVdcParams.setNetworkQuota(1);
			
			
		// Name this Organization vDC		
		if(org.getVdc() != null && org.getVdc().getName() != null)
			createVdcParams.setName(org.getVdc().getName());
		else {
			String orgName = generateVdcName(org);

			createVdcParams.setName(orgName);
			
			com.vmware.vcloud.model.Vdc vdc = new com.vmware.vcloud.model.Vdc();
			org.setVdc(vdc);
			org.getVdc().setName(orgName);
		}
		
		// Set VDC description
		if(org.getVdc() != null && org.getVdc().getDescription() != null)						
			createVdcParams.setDescription(org.getVdc().getDescription());
		else
			createVdcParams.setDescription("VDC for " + org.getFullName());
		
		// Is VDC enable by default
		if(org.getVdc() != null && org.getVdc().isEnabled() != null)	
			createVdcParams.setIsEnabled(org.getVdc().isEnabled());
		else
			createVdcParams.setIsEnabled(Boolean.TRUE);

		// Setting vdcStorageProfile
		VdcStorageProfileParamsType vdcStorageProfile = new VdcStorageProfileParamsType();
		
		if (org.getVdc() != null 
				&& org.getVdc().getVdcStorageProfile() != null
				&& org.getVdc().getVdcStorageProfile().isEnabled() != null)
			vdcStorageProfile.setEnabled(org.getVdc().getVdcStorageProfile().isEnabled());
		else
			vdcStorageProfile.setEnabled(Boolean.TRUE);

		if (org.getVdc() != null 
				&& org.getVdc().getVdcStorageProfile() != null
				&& org.getVdc().getVdcStorageProfile().isDef() != null)
			vdcStorageProfile.setDefault(org.getVdc().getVdcStorageProfile().isDef());
		else
			vdcStorageProfile.setDefault(Boolean.TRUE);
			
		if (org.getVdc() != null 
				&& org.getVdc().getVdcStorageProfile() != null
				&& org.getVdc().getVdcStorageProfile().getLimit() != null)			
			vdcStorageProfile.setLimit(org.getVdc().getVdcStorageProfile().getLimit());
		else
			vdcStorageProfile.setLimit(0);
		
		if (org.getVdc() != null 
				&& org.getVdc().getVdcStorageProfile() != null
				&& org.getVdc().getVdcStorageProfile().getUnits() != null)			
			vdcStorageProfile.setUnits(org.getVdc().getVdcStorageProfile().getUnits());
		else
			vdcStorageProfile.setUnits("MB");

		ReferenceType providerVdcStorageProfileRef = pvdc.getProviderVdcStorageProfileRefs().iterator().next();
		vdcStorageProfile.setProviderVdcStorageProfile(providerVdcStorageProfileRef);
		createVdcParams.getVdcStorageProfile().add(vdcStorageProfile);

		// Only Thin Provision allow
		createVdcParams.setIsThinProvision(Boolean.TRUE);

		AdminVdc adminVdc = adminOrg.createAdminVdc(createVdcParams);

		System.out.println("Creating Pay As You Go Vdc : " + adminVdc.getResource().getName() + " : "
				+ adminVdc.getResource().getHref() + "\n");
		List<Task> tasks = adminVdc.getTasks();
		if (tasks.size() > 0)
			tasks.get(0).waitForTask(0);
		
		return adminVdc;

	}
	
	/**
	 * Finding a vdc
	 * 
	 * @param vdcName
	 * @param orgName
	 * @return {@link Vdc}
	 * @throws VCloudException
	 */
	public static Vdc findVdc(VcloudClient client, String orgName, String vdcName) throws VCloudException {
		ReferenceType orgRef = client.getOrgRefsByName().get(orgName);
		Organization org = Organization.getOrganizationByReference(client, orgRef);
		ReferenceType vdcRef = org.getVdcRefByName(vdcName);
		return Vdc.getVdcByReference(client, vdcRef);
	}	
	
	static String generateVdcName(VCloudOrganization org){
		
		String characters = "abcdefghijklmnopqrstuvwxyz0123456789";	
		StringBuffer vdcName = new StringBuffer();
		
		if(org.getOrderType().name().equalsIgnoreCase("trial"))
			vdcName.append("trial-");
		if(org.getShortName() != null && !org.getShortName().isEmpty())
			vdcName.append(org.getShortName());
		else
			vdcName.append(RandomStringUtils.random( 6, characters ));
		
		vdcName.append("-vdc");
		
		return vdcName.toString();
	}
	
}
