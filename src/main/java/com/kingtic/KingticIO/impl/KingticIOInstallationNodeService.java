package com.kingtic.KingticIO.impl;

import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.InstallationNodeService;
import com.ur.urcap.api.domain.URCapAPI;

import java.io.InputStream;
import java.util.ResourceBundle;

import com.ur.urcap.api.domain.data.DataModel;
import com.kingtic.KingticIO.impl.MyDaemonDaemonService;

public class KingticIOInstallationNodeService implements InstallationNodeService {

	private final MyDaemonDaemonService daemonService;
	private ResourceBundle KingticStrings;
	public KingticIOInstallationNodeService(MyDaemonDaemonService daemonService, ResourceBundle KingticStrings) 
	{
		this.daemonService = daemonService;
		this.KingticStrings = KingticStrings;
	}

	@Override
	public InstallationNodeContribution createInstallationNode(URCapAPI api, DataModel model) {
		return new KingticIOInstallationNodeContribution(daemonService, KingticStrings, model);
	}

	@Override
	public String getTitle() {
		return KingticStrings.getString("INodeS_Title");
	}

	@Override
	public InputStream getHTML() {
		InputStream is = this.getClass().getResourceAsStream("/com/kingtic/KingticIO/impl/installation.html");
		return is;
	}
}
