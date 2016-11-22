package com.vmware.vcloud.automate;

import java.util.List;
import java.util.concurrent.TimeoutException;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.UserType;
import com.vmware.vcloud.exception.UserRoleNotFoundException;
import com.vmware.vcloud.model.VCloudOrganization;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.admin.AdminOrganization;
import com.vmware.vcloud.sdk.admin.User;
import com.vmware.vcloud.sdk.admin.VcloudAdmin;

public class UserUtils {
	
	static void addUserToOrg(VCloudOrganization vCloudOrg, VcloudAdmin admin, AdminOrganization adminOrg) throws TimeoutException, UserRoleNotFoundException, VCloudException {
		
		UserType newUserType = new UserType();

		// Credential	
		if(vCloudOrg.getUser() != null && vCloudOrg.getUser().getName() != null)
			newUserType.setName(vCloudOrg.getUser().getName());
		else {
			newUserType.setName(vCloudOrg.getShortName() + "_admin");
			
			if(vCloudOrg.getUser() == null)
				vCloudOrg.setUser(new com.vmware.vcloud.model.User());
			
			vCloudOrg.getUser().setName(vCloudOrg.getShortName() + "_admin");
		}
		
		if(vCloudOrg.getUser() != null && vCloudOrg.getUser().getPassword() != null)
			newUserType.setPassword(vCloudOrg.getUser().getPassword());
		else{
			String newPassword = VappUtils.genPassword();
			newUserType.setPassword(newPassword);
			vCloudOrg.getUser().setPassword(newPassword);
		}
			
		if(vCloudOrg.getUser() != null && vCloudOrg.getUser().isEnabled() != null)
			newUserType.setIsEnabled(vCloudOrg.getUser().isEnabled());
		else
			newUserType.setIsEnabled(Boolean.TRUE);

		// Role : 'Customer Managed Service'
		ReferenceType usrRoleRef = null;
		
		if(vCloudOrg.getUser() != null && vCloudOrg.getUser().getRoleName() != null)
			usrRoleRef = admin.getRoleRefByName(vCloudOrg.getUser().getRoleName());
		else
			usrRoleRef = admin.getRoleRefByName("Customer Managed Service");
		
		if(usrRoleRef == null)
			throw new UserRoleNotFoundException("User role not found");
		
		newUserType.setRole(usrRoleRef);

		// Contact Info:
		if(vCloudOrg.getUser() != null && vCloudOrg.getUser().getFullName() != null)
			newUserType.setFullName(vCloudOrg.getUser().getFullName());
		
		if(vCloudOrg.getUser() != null && vCloudOrg.getUser().getEmailAddress() != null)
			newUserType.setEmailAddress(vCloudOrg.getUser().getEmailAddress());
		
		if (vCloudOrg.getUser() != null && vCloudOrg.getUser().getPhone() != null)
			newUserType.setTelephone(vCloudOrg.getUser().getPhone());
		
		// Use defaults for rest of the fields.
		User user = adminOrg.createUser(newUserType);

		System.out.println("Creating admin user for organization : " 
				+ user.getResource().getName() + " : "
				+ user.getResource().getHref() + "\n");
		List<Task> tasks = user.getTasks();
		if (tasks.size() > 0)
			tasks.get(0).waitForTask(0);
		
	}
}
