package com.vmware.vcloud.automate;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.vmware.vcloud.model.FirewallRule;
import com.vmware.vcloud.model.Protocol;
import com.vmware.vcloud.model.VCloudOrganization;

public class YamlTest {

	@Test
	public void YamlUnmarshallingTest() throws FileNotFoundException{
		
		ConfigParser cParser = ConfigParser.getParser("");
		VCloudOrganization org = cParser.getOrg();
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		assertEquals("2016-10-01", org.getTemplate_version());
		assertEquals("CustomOrg", org.getName());
		assertEquals("Custom Org Desc", org.getDescription());
		assertEquals("Custom Org Full Name", org.getFullName());
		assertEquals(true, org.isEnabled());
		assertEquals("7650000123", org.getCaNumber());
		assertEquals("9000012345", org.getNonMobileNo());
		assertEquals(true, org.isTrial());
		

		assertEquals("2016-10-01", df.format(org.getStartDate()));
		assertEquals("2016-10-31", df.format(org.getEndDate()));
		
		// User
		assertEquals("sampleuser", org.getUser().getName());
		assertEquals("samplepassword", org.getUser().getPassword());
		assertEquals(true, org.getUser().isEnabled());
		assertEquals("Customer Managed Service", org.getUser().getRoleName());
		assertEquals("User Full Name", org.getUser().getFullName());
		assertEquals("user@company.com", org.getUser().getEmailAddress());
		assertEquals("1175", org.getUser().getPhone());
		
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
		assertEquals("GLOBAL-VM-TEMPLATES-CATALOG", org.getCloudResources().getCatalog().getName());
		
		// Vdc
		assertEquals("customer-vdc", org.getVdc().getName());
		assertEquals(true, org.getVdc().isEnabled());
		assertEquals(0.2, org.getVdc().getResourceGuaranteedCpu(), 0.01);
		assertEquals(0.2, org.getVdc().getResourceGuaranteedMemory(),0.01);
		assertEquals(0, org.getVdc().getVmQuota());
		assertEquals("VDC for Customer", org.getVdc().getDescription());
		
		// Cpu
		assertEquals(0, org.getVdc().getComputeCapacity().getCpu().getAllocated());
		assertEquals(0, org.getVdc().getComputeCapacity().getCpu().getOverhead());
		assertEquals("MHz", org.getVdc().getComputeCapacity().getCpu().getUnits());
		assertEquals(0, org.getVdc().getComputeCapacity().getCpu().getUsed());
		assertEquals(0, org.getVdc().getComputeCapacity().getCpu().getLimit());

		// Memory
		assertEquals(0, org.getVdc().getComputeCapacity().getMemory().getAllocated());
		assertEquals(0, org.getVdc().getComputeCapacity().getMemory().getOverhead());
		assertEquals("MB", org.getVdc().getComputeCapacity().getMemory().getUnits());
		assertEquals(0, org.getVdc().getComputeCapacity().getMemory().getUsed());
		assertEquals(0, org.getVdc().getComputeCapacity().getMemory().getLimit());
		
		// Network quota
		assertEquals(1, org.getVdc().getNetworkQuota());
		
		// Vdc Storage Profile
		assertEquals(true, org.getVdc().getVdcStorageProfile().isEnabled());
		assertEquals(true, org.getVdc().getVdcStorageProfile().isDef());
		assertEquals(0, org.getVdc().getVdcStorageProfile().getLimit());
		assertEquals("MB", org.getVdc().getVdcStorageProfile().getUnits());
		
		// Edge Gateway
		assertEquals("custom-edgege-01", org.getEdgeGateway().getName());
		assertEquals("Custom Edge Gateway", org.getEdgeGateway().getDescription());
		assertTrue(org.getEdgeGateway().getGatewayFeatures().getFirewallService().isEnabled());
		assertFalse(org.getEdgeGateway().getGatewayFeatures().getFirewallService().isLogDefaultAction());
			
		assertEquals("PING OUT", org.getEdgeGateway().getGatewayFeatures().getFirewallService().getFirewallRules().get(0).getDescription());
		assertEquals(Protocol.ICMP, org.getEdgeGateway().getGatewayFeatures().getFirewallService().getFirewallRules().get(0).getProtocol());
		assertEquals("10.1.1.0/24", org.getEdgeGateway().getGatewayFeatures().getFirewallService().getFirewallRules().get(0).getSourceIp());
		assertEquals("Any", org.getEdgeGateway().getGatewayFeatures().getFirewallService().getFirewallRules().get(0).getDestIp());
		assertEquals("Any", org.getEdgeGateway().getGatewayFeatures().getFirewallService().getFirewallRules().get(0).getDestPort());	

		assertEquals("DNS OUT", org.getEdgeGateway().getGatewayFeatures().getFirewallService().getFirewallRules().get(1).getDescription());
		assertEquals(Protocol.UDP, org.getEdgeGateway().getGatewayFeatures().getFirewallService().getFirewallRules().get(1).getProtocol());
		assertEquals("10.1.1.0/24", org.getEdgeGateway().getGatewayFeatures().getFirewallService().getFirewallRules().get(1).getSourceIp());
		assertEquals("Any", org.getEdgeGateway().getGatewayFeatures().getFirewallService().getFirewallRules().get(1).getDestIp());
		assertEquals("53", org.getEdgeGateway().getGatewayFeatures().getFirewallService().getFirewallRules().get(1).getDestPort());
		
		
		// Gateway Configuration
		assertEquals("COMPACT", org.getEdgeGateway().getGatewayConfiguration().getGatewayBackingConfig().COMPACT.name());
		assertEquals("Custom gateway interface", org.getEdgeGateway().getGatewayConfiguration().getGatewayInterfaces().get(0).getDisplayName());
		assertEquals(false, org.getEdgeGateway().getGatewayConfiguration().isHaEnabled());
		assertEquals(false, org.getEdgeGateway().getGatewayConfiguration().isUseDefaultRouteForDnsRelay());
		assertEquals("192.168.1.1", org.getEdgeGateway().getGatewayConfiguration().getGatewayInterfaces().get(0).getSubnetParticipation().getGateway());
		assertEquals("255.255.255.0", org.getEdgeGateway().getGatewayConfiguration().getGatewayInterfaces().get(0).getSubnetParticipation().getNetmask());
		assertEquals("192.168.1.101", org.getEdgeGateway().getGatewayConfiguration().getGatewayInterfaces().get(0).getSubnetParticipation().getIpRanges().get(0).getStartAddress());		
		assertEquals("192.168.1.101", org.getEdgeGateway().getGatewayConfiguration().getGatewayInterfaces().get(0).getSubnetParticipation().getIpRanges().get(0).getEndAddress());		
		assertEquals(true, org.getEdgeGateway().getGatewayConfiguration().getGatewayInterfaces().get(0).isUseForDefaultRoute());
		
		// orgVdcNetwork Configuration
		assertEquals("custom-orgnet-01", org.getOrgVdcNetwork().getName());
		assertEquals("Custom Organization network", org.getOrgVdcNetwork().getDescription());
		assertEquals(true, org.getOrgVdcNetwork().getConfiguration().isRetainNetInfoAcrossDeployments());
		assertEquals("NATROUTED", org.getOrgVdcNetwork().getConfiguration().getFenceMode().NATROUTED.name());
		assertEquals("255.255.255.0", org.getOrgVdcNetwork().getConfiguration().getIpScopes().get(0).getNetmask());
		assertEquals("10.1.1.1", org.getOrgVdcNetwork().getConfiguration().getIpScopes().get(0).getGateway());
		assertEquals(true, org.getOrgVdcNetwork().getConfiguration().getIpScopes().get(0).isEnabled());
		assertEquals(true, org.getOrgVdcNetwork().getConfiguration().getIpScopes().get(0).isInherited());
		assertEquals("192.168.2.1", org.getOrgVdcNetwork().getConfiguration().getIpScopes().get(0).getDns1());
		assertEquals("192.168.2.2", org.getOrgVdcNetwork().getConfiguration().getIpScopes().get(0).getDns2());
		assertEquals("10.1.1.11", org.getOrgVdcNetwork().getConfiguration().getIpScopes().get(0).getIpRange().getStartAddress());
		assertEquals("10.1.1.254", org.getOrgVdcNetwork().getConfiguration().getIpScopes().get(0).getIpRange().getEndAddress());
		
		// vApp Configuration
		assertEquals("vApp_system_1", org.getvApp().getName());
		assertEquals("vApp_system_1", org.getvApp().getDescription());
		assertEquals("CENTOS7", org.getvApp().getChildVms().get(0).getTemplateType());
		assertEquals("custom-CENTOS7", org.getvApp().getChildVms().get(0).getName());
		assertEquals("Custom VM", org.getvApp().getChildVms().get(0).getDescription());
		assertEquals("CustomServer-1", org.getvApp().getChildVms().get(0).getComputerName());
		
		// CPU
		assertEquals(4, org.getvApp().getChildVms().get(0).getvCpu().getNoOfCpus());
		assertEquals(4, org.getvApp().getChildVms().get(0).getvCpu().getCoresPerSocket());
		// Memory
		assertEquals(BigInteger.valueOf(8), org.getvApp().getChildVms().get(0).getvMemory().getMemorySize());
		
	}
}
