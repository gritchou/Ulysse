package org.qualipso.factory.jabuti.ws;

public class AuxiliaryOperations {
	public static boolean isValidClassString(String s)
	{
		//	beginline ( a-z | A-Z | _ | $ ) ( word | $ )* ( . ( a-z | A-Z | _ | $ ) ( word | $ )* )* $
		//	beginline ( a-z | A-Z | _ | $ ) ( word | $ )* ( . ( ( a-z | A-Z | _ | $ ) ( word | $ )* | asterix ) )* $

		if(!s.matches("^[a-zA-Z_\\$][\\w\\$]*(?:\\.[a-zA-Z_\\$][\\w\\$]*)*$"))	//class
			return s.matches("^[a-zA-Z_\\$][\\w\\$]*(?:\\.[a-zA-Z_\\$][\\w\\$]*)*(?:\\.\\*)$");	//package
		return true;
	}
}
