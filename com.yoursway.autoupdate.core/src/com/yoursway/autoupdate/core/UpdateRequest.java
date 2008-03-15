package com.yoursway.autoupdate.core;

import java.util.Collection;

public class UpdateRequest {
	public ApplicationInstallation installation;
	public Collection<FileAction> actions;

	public UpdateRequest(ApplicationInstallation installation,
			Collection<FileAction> actions) {
		this.installation = installation;
		this.actions = actions;
	}
}