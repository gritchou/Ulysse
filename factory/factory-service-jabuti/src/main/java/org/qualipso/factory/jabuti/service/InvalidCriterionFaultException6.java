
/**
 * InvalidCriterionFaultException6.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

package org.qualipso.factory.jabuti.service;

public class InvalidCriterionFaultException6 extends java.lang.Exception{
    
    private org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.InvalidCriterionFaultE faultMessage;
    
    public InvalidCriterionFaultException6() {
        super("InvalidCriterionFaultException6");
    }
           
    public InvalidCriterionFaultException6(java.lang.String s) {
       super(s);
    }
    
    public InvalidCriterionFaultException6(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.InvalidCriterionFaultE msg){
       faultMessage = msg;
    }
    
    public org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.InvalidCriterionFaultE getFaultMessage(){
       return faultMessage;
    }
}
    