package com.vmware.vcloud.model;

public class OrgPasswordPolicySettings {
	private boolean accountLockoutEnabled;
	private int accountLockoutIntervalMinutes;
	private int invalidLoginsBeforeLockout;
	
	public OrgPasswordPolicySettings() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public OrgPasswordPolicySettings(boolean accountLockoutEnabled, int accountLockoutIntervalMinutes,
			int invalidLoginsBeforeLockout) {
		super();
		this.accountLockoutEnabled = accountLockoutEnabled;
		this.accountLockoutIntervalMinutes = accountLockoutIntervalMinutes;
		this.invalidLoginsBeforeLockout = invalidLoginsBeforeLockout;
	}

	public boolean isAccountLockoutEnabled() {
		return accountLockoutEnabled;
	}

	public void setAccountLockoutEnabled(boolean accountLockoutEnabled) {
		this.accountLockoutEnabled = accountLockoutEnabled;
	}

	public int getAccountLockoutIntervalMinutes() {
		return accountLockoutIntervalMinutes;
	}

	public void setAccountLockoutIntervalMinutes(int accountLockoutIntervalMinutes) {
		this.accountLockoutIntervalMinutes = accountLockoutIntervalMinutes;
	}

	public int getInvalidLoginsBeforeLockout() {
		return invalidLoginsBeforeLockout;
	}

	public void setInvalidLoginsBeforeLockout(int invalidLoginsBeforeLockout) {
		this.invalidLoginsBeforeLockout = invalidLoginsBeforeLockout;
	}
	
}
