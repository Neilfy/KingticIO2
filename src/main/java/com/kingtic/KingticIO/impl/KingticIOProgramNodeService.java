package com.kingtic.KingticIO.impl;

import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.ProgramNodeService;
import com.ur.urcap.api.domain.URCapAPI;
import com.ur.urcap.api.domain.data.DataModel;

import java.io.InputStream;
import java.util.ResourceBundle;

public class KingticIOProgramNodeService implements ProgramNodeService {

	private ResourceBundle KingticStrings;
	public KingticIOProgramNodeService(ResourceBundle KingticStrings) {
		this.KingticStrings = KingticStrings;
	}

	@Override
	public String getId() {
		return "KingticOutputNode";
	}

	@Override
	public String getTitle() {
		return KingticStrings.getString("PNodeC_Out_Title");
	}

	@Override
	public InputStream getHTML() {
		InputStream is = this.getClass().getResourceAsStream("/com/kingtic/KingticIO/impl/programnode.html");
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
		return new KingticIOProgramNodeContribution(api, model, this.KingticStrings);
	}
	
}
