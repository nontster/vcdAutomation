package com.vmware.vcloud.automate;

import java.util.List;
import java.util.concurrent.TimeoutException;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.api.rest.schema.UserType;
import com.vmware.vcloud.model.VCloudOrganization;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.admin.AdminOrganization;
import com.vmware.vcloud.sdk.admin.User;
import com.vmware.vcloud.sdk.admin.VcloudAdmin;

public class UserUtils {
	static void addUserToOrg(VCloudOrganization vCloudOrg, VcloudAdmin admin, AdminOrganization adminOrg) throws TimeoutException {
		UserType newUserType = new UserType();

		// Credentias
		newUserType.setName(vCloudOrg.getUser().getName());
		newUserType.setPassword(vCloudOrg.getUser().getPassword());
		newUserType.setIsEnabled(vCloudOrg.getUser().isEnabled());

		// Role : 'Customer Managed Service'
		ReferenceType usrRoleRef = admin.getRoleRefByName(vCloudOrg.getUser().getRoleName());
		newUserType.setRole(usrRoleRef);

		// COntact Info:
		newUserType.setFullName(vCloudOrg.getUser().getFullName());
		newUserType.setEmailAddress(vCloudOrg.getUser().getEmailAddress());
		// Use defaults for rest of the fields.

		try {
			User user = adminOrg.createUser(newUserType);

			System.out.println("Creating admin user for organization : " + user.getResource().getName() + " : "
					+ user.getResource().getHref());
			List<Task> tasks = user.getTasks();
			if (tasks.size() > 0)
				tasks.get(0).waitForTask(0);

		} catch (VCloudException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
