package com.vmware.vcloud.automate;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Test;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.vmware.vcloud.model.Organization;

public class YamlTest {

	@Test
	public void YamlUnmarshallingTest() throws FileNotFoundException{
		
		ConfigParser cParser = ConfigParser.getParser("");
		Organization org = cParser.getOrg();
		
		assertEquals("CustomAdminOrg", org.getName());
		assertEquals("Custom Admin Org Desc",org.getDescription());
		assertEquals("Custom Admin Org Full Name",org.getFullName());
		assertEquals(true, org.isEnabled());
		
		assertEquals("sampleuser", org.getUser().getName());
		assertEquals("samplepassword", org.getUser().getPassword());
		assertEquals(true, org.getUser().isEnabled());
		assertEquals("Customer Managed Service", org.getUser().getRoleName());
		assertEquals("User Full Name", org.getUser().getFullName());
		assertEquals("user@company.com", org.getUser().getEmailAddress());
		
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
		
	}
}
