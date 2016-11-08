package com.vmware.vcloud.automate;

import com.vmware.vcloud.api.rest.schema.NetworkConnectionType;
import com.vmware.vcloud.model.ChildVm;
import com.vmware.vcloud.model.VCloudOrganization;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VM;
import com.vmware.vcloud.sdk.Vapp;

public class ReportUtils {
	static void generateReport(Vapp vapp, VCloudOrganization vCloudOrg) throws VCloudException {
		
		System.out.println("---------- Reporting ----------");
		System.out.println("    "+vCloudOrg.getFullName());
		System.out.println("    "+vCloudOrg.getUrl());
		System.out.println("    "+vCloudOrg.getUser().getName() +":"+vCloudOrg.getUser().getPassword());
		
		for(ChildVm childVm : vCloudOrg.getvApp().getChildVms())
		System.out.println("        Administrative password: "+childVm.getPassword());
		
		for(VM vm: vapp.getChildrenVms()){
			System.out.println("    "+vm.getResource().getName());
			System.out.println("    (Mem) "+vm.getMemory().getMemorySize() + " GB");
			System.out.println("    (Cpu) "+vm.getCpu().getCoresPerSocket());
			
			for(NetworkConnectionType networkConnectionType : vm.getNetworkConnections()){
				System.out.println("        (NAT) "+networkConnectionType.getIpAddress() +"-"+networkConnectionType.getExternalIpAddress());
			}
		}
		
	}
}
