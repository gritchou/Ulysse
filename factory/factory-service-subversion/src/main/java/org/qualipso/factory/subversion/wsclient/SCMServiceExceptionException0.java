
/**
 * SCMServiceExceptionException0.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */

package org.qualipso.factory.subversion.wsclient;

public class SCMServiceExceptionException0 extends java.lang.Exception{
    
    private org.qualipso.factory.subversion.wsclient.SCMServiceStub.SCMServiceExceptionE faultMessage;
    
    public SCMServiceExceptionException0() {
        super("SCMServiceExceptionException0");
    }
           
    public SCMServiceExceptionException0(java.lang.String s) {
       super(s);
    }
    
    public SCMServiceExceptionException0(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(org.qualipso.factory.subversion.wsclient.SCMServiceStub.SCMServiceExceptionE msg){
       faultMessage = msg;
    }
    
    public org.qualipso.factory.subversion.wsclient.SCMServiceStub.SCMServiceExceptionE getFaultMessage(){
       return faultMessage;
    }
}
    