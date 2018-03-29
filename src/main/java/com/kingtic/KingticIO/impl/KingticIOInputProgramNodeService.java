package com.kingtic.KingticIO.impl;

import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.ProgramNodeService;
import com.ur.urcap.api.domain.URCapAPI;
import com.ur.urcap.api.domain.data.DataModel;

import java.io.InputStream;

public class KingticIOInputProgramNodeService implements ProgramNodeService {

	public KingticIOInputProgramNodeService() {
	}

	@Override
	public String getId() {
		return "KingticInputNode";
	}

	@Override
	public String getTitle() {
		return "设置输入";
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
		return new KingticIOInputProgramNodeContribution(api, model);
	}
}
