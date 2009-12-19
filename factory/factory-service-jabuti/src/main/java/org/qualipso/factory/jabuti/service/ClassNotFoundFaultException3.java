
/**
 * ClassNotFoundFaultException3.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

package org.qualipso.factory.jabuti.service;

public class ClassNotFoundFaultException3 extends java.lang.Exception{
    
    private org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.ClassNotFoundFaultE faultMessage;
    
    public ClassNotFoundFaultException3() {
        super("ClassNotFoundFaultException3");
    }
           
    public ClassNotFoundFaultException3(java.lang.String s) {
       super(s);
    }
    
    public ClassNotFoundFaultException3(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.ClassNotFoundFaultE msg){
       faultMessage = msg;
    }
    
    public org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.ClassNotFoundFaultE getFaultMessage(){
       return faultMessage;
    }
}
    