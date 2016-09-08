package com.vmware.vcloud.automate;

import java.util.List;
import java.util.concurrent.TimeoutException;

import com.vmware.vcloud.api.rest.schema.CapacityWithUsageType;
import com.vmware.vcloud.api.rest.schema.ComputeCapacityType;
import com.vmware.vcloud.api.rest.schema.CreateVdcParamsType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.VdcStorageProfileParamsType;
import com.vmware.vcloud.model.VCloudOrganization;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.admin.AdminOrganization;
import com.vmware.vcloud.sdk.admin.AdminVdc;
import com.vmware.vcloud.sdk.admin.ProviderVdc;
import com.vmware.vcloud.sdk.admin.VcloudAdmin;
import com.vmware.vcloud.sdk.constants.AllocationModelType;

public class VcdUtils {
	
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
		ReferenceType pvdcRef = adminClient.getProviderVdcRefByName(org.getCloudResources().getProviderVdc().getName());
		createVdcParams.setProviderVdcReference(pvdcRef);
		ProviderVdc pvdc = ProviderVdc.getProviderVdcByReference(client, pvdcRef);

		// Select Allocation Model - 'Pay As You Go' Model
		createVdcParams.setAllocationModel(AllocationModelType.ALLOCATIONVAPP.value());

		// guaranteed 20% CPU/Memory resources
		createVdcParams.setResourceGuaranteedCpu(Double.parseDouble(new Float(org.getVdc().getVdcParams().getResourceGuaranteedCpu()).toString())); 																											 																											
		createVdcParams.setResourceGuaranteedMemory(Double.parseDouble(new Float(org.getVdc().getVdcParams().getResourceGuaranteedMemory()).toString())); 

		// Rest all Defaults for the 'Pay As You Go Model' configuration.
		// Compute Capacity -- this is needed. UI Uses defaults.
		ComputeCapacityType computeCapacity = new ComputeCapacityType();
		CapacityWithUsageType cpu = new CapacityWithUsageType();
		cpu.setAllocated(new Long(org.getVdc().getVdcParams().getComputeCapacity().getCpu().getAllocated()));
		cpu.setOverhead(new Long(org.getVdc().getVdcParams().getComputeCapacity().getCpu().getOverhead()));
		cpu.setUnits(org.getVdc().getVdcParams().getComputeCapacity().getCpu().getUnits());
		cpu.setUsed(new Long(org.getVdc().getVdcParams().getComputeCapacity().getCpu().getUsed()));
		cpu.setLimit(org.getVdc().getVdcParams().getComputeCapacity().getCpu().getLimit());

		computeCapacity.setCpu(cpu);

		CapacityWithUsageType mem = new CapacityWithUsageType();
		mem.setAllocated(new Long(org.getVdc().getVdcParams().getComputeCapacity().getMemory().getAllocated()));
		mem.setOverhead(new Long(org.getVdc().getVdcParams().getComputeCapacity().getMemory().getOverhead()));
		mem.setUnits(org.getVdc().getVdcParams().getComputeCapacity().getMemory().getUnits());
		mem.setUsed(new Long(org.getVdc().getVdcParams().getComputeCapacity().getMemory().getUsed()));
		mem.setLimit(org.getVdc().getVdcParams().getComputeCapacity().getMemory().getLimit());

		computeCapacity.setMemory(mem);

		createVdcParams.setComputeCapacity(computeCapacity);

		// Select Network Pool
		ReferenceType netPoolRef = pvdc.getVMWNetworkPoolRefByName(org.getCloudResources().getNetworkPool().getName());
		createVdcParams.setNetworkPoolReference(netPoolRef);
		createVdcParams.setNetworkQuota(org.getVdc().getVdcParams().getNetworkQuota());

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

		createVdcParams.setIsThinProvision(true);

		AdminVdc adminVdc = adminOrg.createAdminVdc(createVdcParams);

		System.out.println("Creating Pay As You Go Vdc : " + adminVdc.getResource().getName() + " : "
				+ adminVdc.getResource().getHref());
		List<Task> tasks = adminVdc.getTasks();
		if (tasks.size() > 0)
			tasks.get(0).waitForTask(0);
		
		return adminVdc;

	}
}
