package com.kingtic.KingticIO.impl;

import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.ProgramNodeService;
import com.ur.urcap.api.domain.URCapAPI;
import com.ur.urcap.api.domain.data.DataModel;

import java.io.InputStream;
import java.util.ResourceBundle;

public class KingticIOInputProgramNodeService implements ProgramNodeService {

	private ResourceBundle KingticStrings;
	public KingticIOInputProgramNodeService(ResourceBundle KingticStrings) {
		this.KingticStrings = KingticStrings;
	}

	@Override
	public String getId() {
		return "KingticInputNode";
	}

	@Override
	public String getTitle() {
		return KingticStrings.getString("PNodeS_In_Title");
	}

	@Override
	public InputStream getHTML() {
		InputStream is = this.getClass().getResourceAsStream("/com/kingtic/KingticIO/impl/programnodeInput.html");
		return is;
	}

	@Override
	public boolean isDeprecated() {
		return false;
	}

	@Override
	public boolean isChildrenAllowed() {
		return false;
	}

	@Override
	public ProgramNodeContribution createNode(URCapAPI api, DataModel model) {
		return new KingticIOInputProgramNodeContribution(api, model, KingticStrings);
	}
}
