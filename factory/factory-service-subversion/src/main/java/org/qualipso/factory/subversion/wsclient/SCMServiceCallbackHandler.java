
/**
 * SCMServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */

    package org.qualipso.factory.subversion.wsclient;

    /**
     *  SCMServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class SCMServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public SCMServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public SCMServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for updateUsers method
            * override this method for handling normal response from updateUsers operation
            */
           public void receiveResultupdateUsers(
                    org.qualipso.factory.subversion.wsclient.SCMServiceStub.UpdateUsersResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from updateUsers operation
           */
            public void receiveErrorupdateUsers(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getCommitsInfoWithoutRev method
            * override this method for handling normal response from getCommitsInfoWithoutRev operation
            */
           public void receiveResultgetCommitsInfoWithoutRev(
                    org.qualipso.factory.subversion.wsclient.SCMServiceStub.GetCommitsInfoWithoutRevResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getCommitsInfoWithoutRev operation
           */
            public void receiveErrorgetCommitsInfoWithoutRev(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for removeUsers method
            * override this method for handling normal response from removeUsers operation
            */
           public void receiveResultremoveUsers(
                    org.qualipso.factory.subversion.wsclient.SCMServiceStub.RemoveUsersResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from removeUsers operation
           */
            public void receiveErrorremoveUsers(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for setUserRoles method
            * override this method for handling normal response from setUserRoles operation
            */
           public void receiveResultsetUserRoles(
                    org.qualipso.factory.subversion.wsclient.SCMServiceStub.SetUserRolesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from setUserRoles operation
           */
            public void receiveErrorsetUserRoles(java.lang.Exception e) {
            }
                
               // No methods generated for meps other than in-out
                
           /**
            * auto generated Axis2 call back method for getCommitsInfoWithFromRev method
            * override this method for handling normal response from getCommitsInfoWithFromRev operation
            */
           public void receiveResultgetCommitsInfoWithFromRev(
                    org.qualipso.factory.subversion.wsclient.SCMServiceStub.GetCommitsInfoWithFromRevResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getCommitsInfoWithFromRev operation
           */
            public void receiveErrorgetCommitsInfoWithFromRev(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for findRepositoryByName method
            * override this method for handling normal response from findRepositoryByName operation
            */
           public void receiveResultfindRepositoryByName(
                    org.qualipso.factory.subversion.wsclient.SCMServiceStub.FindRepositoryByNameResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from findRepositoryByName operation
           */
            public void receiveErrorfindRepositoryByName(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getCommitsInfoWithFromRevTo method
            * override this method for handling normal response from getCommitsInfoWithFromRevTo operation
            */
           public void receiveResultgetCommitsInfoWithFromRevTo(
                    org.qualipso.factory.subversion.wsclient.SCMServiceStub.GetCommitsInfoWithFromRevToResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getCommitsInfoWithFromRevTo operation
           */
            public void receiveErrorgetCommitsInfoWithFromRevTo(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for createRepository method
            * override this method for handling normal response from createRepository operation
            */
           public void receiveResultcreateRepository(
                    org.qualipso.factory.subversion.wsclient.SCMServiceStub.CreateRepositoryResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from createRepository operation
           */
            public void receiveErrorcreateRepository(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for deleteRepository method
            * override this method for handling normal response from deleteRepository operation
            */
           public void receiveResultdeleteRepository(
                    org.qualipso.factory.subversion.wsclient.SCMServiceStub.DeleteRepositoryResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from deleteRepository operation
           */
            public void receiveErrordeleteRepository(java.lang.Exception e) {
            }
                


    }
    