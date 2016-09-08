package com.vmware.vcloud.automate;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Test;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.vmware.vcloud.model.VCloudOrganization;

public class YamlTest {

	@Test
	public void YamlUnmarshallingTest() throws FileNotFoundException{
		
		ConfigParser cParser = ConfigParser.getParser("");
		VCloudOrganization org = cParser.getOrg();
		
		assertEquals("CustomAdminOrg", org.getName());
		assertEquals("Custom Admin Org Desc",org.getDescription());
		assertEquals("Custom Admin Org Full Name",org.getFullName());
		assertEquals(true, org.isEnabled());
		
		// User
		assertEquals("sampleuser", org.getUser().getName());
		assertEquals("samplepassword", org.getUser().getPassword());
		assertEquals(true, org.getUser().isEnabled());
		assertEquals("Customer Managed Service", org.getUser().getRoleName());
		assertEquals("User Full Name", org.getUser().getFullName());
		assertEquals("user@company.com", org.getUser().getEmailAddress());
		
		// OrgSettings
		assertEquals(false, org.getOrgSettings().getOrgLeaseSettings().isDeleteOnStorageLeaseExpiration());
		assertEquals(0, org.getOrgSettings().getOrgLeaseSettings().getDeploymentLeaseSeconds());
		assertEquals(0, org.getOrgSettings().getOrgLeaseSettings().getStorageLeaseSeconds());
		
		assertEquals(0, org.getOrgSettings().getOrgGeneralSettings().getStoredVmQuota());
		assertEquals(0, org.getOrgSettings().getOrgGeneralSettings().getDeployedVMQuota());
		assertEquals(false, org.getOrgSettings().getOrgGeneralSettings().isCanPublishCatalogs());
		
		assertEquals( false, org.getOrgSettings().getOrgVAppTemplateLeaseSettings().isDeleteOnStorageLeaseExpiration());
		assertEquals(0, org.getOrgSettings().getOrgVAppTemplateLeaseSettings().getStorageLeaseSeconds());
		
		assertEquals(true, org.getOrgSettings().getOrgPasswordPolicySettings().isAccountLockoutEnabled());
		assertEquals( 15, org.getOrgSettings().getOrgPasswordPolicySettings().getAccountLockoutIntervalMinutes());
		assertEquals(15, org.getOrgSettings().getOrgPasswordPolicySettings().getInvalidLoginsBeforeLockout());
		
		// CloudResources
		assertEquals("TLS1-ALLOC-PVDC01", org.getCloudResources().getProviderVdc().getName());
		assertEquals("TLS1-ALLOC-PVDC01-VXLAN-NP", org.getCloudResources().getNetworkPool().getName());
		assertEquals("Tenant-External-Internet02", org.getCloudResources().getExternalNetwork().getName());
		
		// Vdc
		assertEquals("customer-vdc", org.getVdc().getVdcParams().getName());
		assertEquals(true, org.getVdc().getVdcParams().isEnabled());
		assertEquals(0.2, org.getVdc().getVdcParams().getResourceGuaranteedCpu(), 0.01);
		assertEquals(0.2, org.getVdc().getVdcParams().getResourceGuaranteedMemory(),0.01);
		assertEquals(0, org.getVdc().getVdcParams().getVmQuota());
		assertEquals("VDC for Customer", org.getVdc().getVdcParams().getDescription());
		
		// Cpu
		assertEquals(0, org.getVdc().getVdcParams().getComputeCapacity().getCpu().getAllocated());
		assertEquals(0, org.getVdc().getVdcParams().getComputeCapacity().getCpu().getOverhead());
		assertEquals("MHz", org.getVdc().getVdcParams().getComputeCapacity().getCpu().getUnits());
		assertEquals(0, org.getVdc().getVdcParams().getComputeCapacity().getCpu().getUsed());
		assertEquals(0, org.getVdc().getVdcParams().getComputeCapacity().getCpu().getLimit());

		// Memory
		assertEquals(0, org.getVdc().getVdcParams().getComputeCapacity().getMemory().getAllocated());
		assertEquals(0, org.getVdc().getVdcParams().getComputeCapacity().getMemory().getOverhead());
		assertEquals("MB", org.getVdc().getVdcParams().getComputeCapacity().getMemory().getUnits());
		assertEquals(0, org.getVdc().getVdcParams().getComputeCapacity().getMemory().getUsed());
		assertEquals(0, org.getVdc().getVdcParams().getComputeCapacity().getMemory().getLimit());
		
		// Network quota
		assertEquals(1, org.getVdc().getVdcParams().getNetworkQuota());
		
		// Vdc Storage Profile
		assertEquals(true, org.getVdc().getVdcParams().getVdcStorageProfile().isEnabled());
		assertEquals(true, org.getVdc().getVdcParams().getVdcStorageProfile().isDef());
		assertEquals(0, org.getVdc().getVdcParams().getVdcStorageProfile().getLimit());
		assertEquals("MB", org.getVdc().getVdcParams().getVdcStorageProfile().getUnits());
		
		// Edge Gateway
		assertEquals("custom-edgege-01", org.getEdgeGateway().getGatewayParams().getName());
		assertEquals("Custom edge gateway", org.getEdgeGateway().getGatewayParams().getDescription());
		
		// Gateway Configuration
		assertEquals("COMPACT", org.getEdgeGateway().getGatewayParams().getGatewayConfiguration().getGatewayBackingConfig().name());
		assertEquals("Custom gateway interface", org.getEdgeGateway().getGatewayParams().getGatewayConfiguration().getGatewayInterfaces().get(0).getDisplayName());
		assertEquals(false, org.getEdgeGateway().getGatewayParams().getGatewayConfiguration().isHaEnabled());
		assertEquals(false, org.getEdgeGateway().getGatewayParams().getGatewayConfiguration().isUseDefaultRouteForDnsRelay());
		assertEquals("10.147.74.253", org.getEdgeGateway().getGatewayParams().getGatewayConfiguration().getGatewayInterfaces().get(0).getSubnetParticipation().getGateway());
		assertEquals("255.255.255.0", org.getEdgeGateway().getGatewayParams().getGatewayConfiguration().getGatewayInterfaces().get(0).getSubnetParticipation().getNetmask());
		assertEquals("10.147.74.238", org.getEdgeGateway().getGatewayParams().getGatewayConfiguration().getGatewayInterfaces().get(0).getSubnetParticipation().getIpRanges().get(0).getStartAddress());		
		assertEquals("10.147.74.239", org.getEdgeGateway().getGatewayParams().getGatewayConfiguration().getGatewayInterfaces().get(0).getSubnetParticipation().getIpRanges().get(0).getEndAddress());		
		assertEquals(true, org.getEdgeGateway().getGatewayParams().getGatewayConfiguration().getGatewayInterfaces().get(0).isUseForDefaultRoute());		
	}
}
