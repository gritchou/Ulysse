package org.qualipso.factory.ssh.shell.command;

import org.qualipso.factory.Factory;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.browser.BrowserService;

import bsh.CallStack;
import bsh.EvalError;
import bsh.Interpreter;

public class ls {
	
	private static BrowserService browser;
	
	public static void invoke(Interpreter env, CallStack callstack) {
		try {
			if ( browser == null ) {
				 browser = (BrowserService) Factory.findService("browser");
			}
			String pwd = (String) env.eval("bsh.cwd");
			String[] childs = browser.listChildren(pwd);
			env.print("\r\nTotal " + childs.length);
			for (String child : childs) {
				env.print("\r\n" + child);
			}
		} catch (EvalError e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			env.getErr().print(e.getMessage());
		}
	}
}