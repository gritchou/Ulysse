
/**
 * InvalidProjectIdFaultException0.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

package org.qualipso.factory.jabuti.service;

public class InvalidProjectIdFaultException0 extends java.lang.Exception{
    
    private org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.InvalidProjectIdFaultE faultMessage;
    
    public InvalidProjectIdFaultException0() {
        super("InvalidProjectIdFaultException0");
    }
           
    public InvalidProjectIdFaultException0(java.lang.String s) {
       super(s);
    }
    
    public InvalidProjectIdFaultException0(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.InvalidProjectIdFaultE msg){
       faultMessage = msg;
    }
    
    public org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.InvalidProjectIdFaultE getFaultMessage(){
       return faultMessage;
    }
}
    