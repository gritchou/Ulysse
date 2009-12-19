package org.qualipso.factory.ssh.shell.command;

import org.qualipso.factory.Factory;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.browser.BrowserService;

import bsh.CallStack;
import bsh.EvalError;
import bsh.Interpreter;

public class cd {
	
	private static BrowserService browser;
	
	public static void invoke(Interpreter env, CallStack callstack) {
		try {
			String dir = (String) env.eval("bsh.cwd");
			invoke(env, callstack, dir);
		} catch (EvalError e) {
			e.printStackTrace();
		}
	}
	
	public static void invoke(Interpreter env, CallStack callstack, String dir) {
		try {
			if ( browser == null ) {
				 browser = (BrowserService) Factory.findService("browser");
			}
			FactoryResource resource = browser.findResource(dir);
			env.eval("bsh.cwd = " + resource.getResourcePath());
		} catch (EvalError e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			env.getErr().print(e.getMessage());
		}
	}

}
