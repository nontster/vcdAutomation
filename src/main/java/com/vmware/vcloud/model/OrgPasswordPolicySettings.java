package com.vmware.vcloud.model;

public class OrgPasswordPolicySettings {
	private Boolean accountLockoutEnabled;
	private Integer accountLockoutIntervalMinutes;
	private Integer invalidLoginsBeforeLockout;
	
	public OrgPasswordPolicySettings() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public OrgPasswordPolicySettings(Boolean accountLockoutEnabled, Integer accountLockoutIntervalMinutes,
			Integer invalidLoginsBeforeLockout) {
		super();
		this.accountLockoutEnabled = accountLockoutEnabled;
		this.accountLockoutIntervalMinutes = accountLockoutIntervalMinutes;
		this.invalidLoginsBeforeLockout = invalidLoginsBeforeLockout;
	}

	public Boolean isAccountLockoutEnabled() {
		return accountLockoutEnabled;
	}

	public void setAccountLockoutEnabled(Boolean accountLockoutEnabled) {
		this.accountLockoutEnabled = accountLockoutEnabled;
	}

	public Integer getAccountLockoutIntervalMinutes() {
		return accountLockoutIntervalMinutes;
	}

	public void setAccountLockoutIntervalMinutes(Integer accountLockoutIntervalMinutes) {
		this.accountLockoutIntervalMinutes = accountLockoutIntervalMinutes;
	}

	public Integer getInvalidLoginsBeforeLockout() {
		return invalidLoginsBeforeLockout;
	}

	public void setInvalidLoginsBeforeLockout(Integer invalidLoginsBeforeLockout) {
		this.invalidLoginsBeforeLockout = invalidLoginsBeforeLockout;
	}
	
}
