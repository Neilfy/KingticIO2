package com.kingtic.KingticIO.impl;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.kingtic.KingticIO.impl.KingticIOInputProgramNodeService;
import com.kingtic.KingticIO.impl.KingticIOInstallationNodeService;
import com.kingtic.KingticIO.impl.KingticIOProgramNodeService;
import com.kingtic.KingticIO.impl.MyDaemonDaemonService;
import com.ur.urcap.api.contribution.DaemonService;
import com.ur.urcap.api.contribution.InstallationNodeService;
import com.ur.urcap.api.contribution.ProgramNodeService;

/**
 * Hello world activator for the OSGi bundle URCAPS contribution
 *
 */
public class Activator implements BundleActivator {
	private ResourceBundle KingticStrings;
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		this.KingticStrings = ResourceBundle.getBundle("KingticIO", Locale.getDefault());
		MyDaemonDaemonService daemonService = new MyDaemonDaemonService();
		KingticIOInstallationNodeService installationNodeService = new KingticIOInstallationNodeService(daemonService, KingticStrings);

		bundleContext.registerService(InstallationNodeService.class, installationNodeService, null);
		bundleContext.registerService(ProgramNodeService.class, new KingticIOProgramNodeService(KingticStrings), null);
		bundleContext.registerService(ProgramNodeService.class, new KingticIOInputProgramNodeService(KingticStrings), null);
		bundleContext.registerService(DaemonService.class, daemonService, null);
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("Activator says Goodbye World!");
	}
}

