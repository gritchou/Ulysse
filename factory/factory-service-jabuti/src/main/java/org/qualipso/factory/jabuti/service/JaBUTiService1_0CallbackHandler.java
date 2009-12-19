
/**
 * JaBUTiService1_0CallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

    package org.qualipso.factory.jabuti.service;

    /**
     *  JaBUTiService1_0CallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class JaBUTiService1_0CallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public JaBUTiService1_0CallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public JaBUTiService1_0CallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for getAllCoveredAndUncoveredRequiredElements method
            * override this method for handling normal response from getAllCoveredAndUncoveredRequiredElements operation
            */
           public void receiveResultgetAllCoveredAndUncoveredRequiredElements(
                    org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.GetAllCoveredAndUncoveredRequiredElementsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAllCoveredAndUncoveredRequiredElements operation
           */
            public void receiveErrorgetAllCoveredAndUncoveredRequiredElements(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for sendTraceFile method
            * override this method for handling normal response from sendTraceFile operation
            */
           public void receiveResultsendTraceFile(
                    org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.SendTraceFileResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from sendTraceFile operation
           */
            public void receiveErrorsendTraceFile(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getInstrumentedProject method
            * override this method for handling normal response from getInstrumentedProject operation
            */
           public void receiveResultgetInstrumentedProject(
                    org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.GetInstrumentedProjectResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getInstrumentedProject operation
           */
            public void receiveErrorgetInstrumentedProject(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getCoverageByCriteria method
            * override this method for handling normal response from getCoverageByCriteria operation
            */
           public void receiveResultgetCoverageByCriteria(
                    org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.GetCoverageByCriteriaResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getCoverageByCriteria operation
           */
            public void receiveErrorgetCoverageByCriteria(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getAllRequiredElements method
            * override this method for handling normal response from getAllRequiredElements operation
            */
           public void receiveResultgetAllRequiredElements(
                    org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.GetAllRequiredElementsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAllRequiredElements operation
           */
            public void receiveErrorgetAllRequiredElements(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for addTestCases method
            * override this method for handling normal response from addTestCases operation
            */
           public void receiveResultaddTestCases(
                    org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.AddTestCasesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from addTestCases operation
           */
            public void receiveErroraddTestCases(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for ignoreClasses method
            * override this method for handling normal response from ignoreClasses operation
            */
           public void receiveResultignoreClasses(
                    org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.IgnoreClassesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from ignoreClasses operation
           */
            public void receiveErrorignoreClasses(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getCoverageByMethods method
            * override this method for handling normal response from getCoverageByMethods operation
            */
           public void receiveResultgetCoverageByMethods(
                    org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.GetCoverageByMethodsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getCoverageByMethods operation
           */
            public void receiveErrorgetCoverageByMethods(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getGraph method
            * override this method for handling normal response from getGraph operation
            */
           public void receiveResultgetGraph(
                    org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.GetGraphResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getGraph operation
           */
            public void receiveErrorgetGraph(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getMetrics method
            * override this method for handling normal response from getMetrics operation
            */
           public void receiveResultgetMetrics(
                    org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.GetMetricsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getMetrics operation
           */
            public void receiveErrorgetMetrics(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for selectClassesToInstrument method
            * override this method for handling normal response from selectClassesToInstrument operation
            */
           public void receiveResultselectClassesToInstrument(
                    org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.SelectClassesToInstrumentResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from selectClassesToInstrument operation
           */
            public void receiveErrorselectClassesToInstrument(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getCoverageByClasses method
            * override this method for handling normal response from getCoverageByClasses operation
            */
           public void receiveResultgetCoverageByClasses(
                    org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.GetCoverageByClassesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getCoverageByClasses operation
           */
            public void receiveErrorgetCoverageByClasses(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for deleteProject method
            * override this method for handling normal response from deleteProject operation
            */
           public void receiveResultdeleteProject(
                    org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.DeleteProjectResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from deleteProject operation
           */
            public void receiveErrordeleteProject(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for cleanProject method
            * override this method for handling normal response from cleanProject operation
            */
           public void receiveResultcleanProject(
                    org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.CleanProjectResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from cleanProject operation
           */
            public void receiveErrorcleanProject(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for createProject method
            * override this method for handling normal response from createProject operation
            */
           public void receiveResultcreateProject(
                    org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.CreateProjectResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from createProject operation
           */
            public void receiveErrorcreateProject(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getRequiredElementsByCriterion method
            * override this method for handling normal response from getRequiredElementsByCriterion operation
            */
           public void receiveResultgetRequiredElementsByCriterion(
                    org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.GetRequiredElementsByCriterionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getRequiredElementsByCriterion operation
           */
            public void receiveErrorgetRequiredElementsByCriterion(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for updateProject method
            * override this method for handling normal response from updateProject operation
            */
           public void receiveResultupdateProject(
                    org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.UpdateProjectResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from updateProject operation
           */
            public void receiveErrorupdateProject(java.lang.Exception e) {
            }
                


    }
    