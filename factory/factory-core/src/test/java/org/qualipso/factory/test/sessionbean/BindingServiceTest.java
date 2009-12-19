/*
 *
 * Qualipso Factory
 * Copyright (C) 2006-2010 INRIA
 * http://www.inria.fr - molli@loria.fr
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of LGPL. See licenses details in LGPL.txt
 *
 * Initial authors :
 *
 * Jérôme Blanchard / INRIA
 * Pascal Molli / Nancy Université
 * Gérald Oster / Nancy Université
 * Christophe Bouthier / INRIA
 * 
 */
package org.qualipso.factory.test.sessionbean;

import com.bm.testsuite.BaseSessionBeanFixture;
import com.bm.testsuite.dataloader.EntityInitialDataSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingServiceBean;
import org.qualipso.factory.binding.IdentifierAlreadyBoundException;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathAlreadyBoundException;
import org.qualipso.factory.binding.PathNotEmptyException;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.binding.PropertyNotFoundException;
import org.qualipso.factory.binding.entity.Node;

import java.util.HashSet;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June 2009
 */
public class BindingServiceTest extends BaseSessionBeanFixture<BindingServiceBean> {
    private static Log logger = LogFactory.getLog(BindingServiceTest.class);
    @SuppressWarnings("unchecked")
    private static final Class[] usedBeans = { Node.class };

    public BindingServiceTest() {
        super(BindingServiceBean.class, usedBeans, new NodeInitialDataSet());
    }

    public void setUp() throws Exception {
        super.setUp();
        logger.debug("setting up binding service");
    }

    public void testBind() {
        FactoryResourceIdentifier fri1 = new FactoryResourceIdentifier("FakeService1", "FakeResourceType1", "FakeID1");
        FactoryResourceIdentifier fri2 = new FactoryResourceIdentifier("FakeService2", "FakeResourceType2", "FakeID2");

        try {
            getBeanToTest().getEntityManager().getTransaction().begin();

            try {
                getBeanToTest().bind(fri1, "/");
                fail("forbidden to bind the root node");
            } catch (InvalidPathException e) {
                //OK
            } catch (Exception e) {
                fail("this is not the exception which should be raised");
            }

            try {
                getBeanToTest().bind(fri1, "/tests/test1");
                fail("the path /tests should not exists so binding /tests/test1 should raise an exception");
            } catch (PathNotFoundException e) {
                //OK
            } catch (Exception e) {
                fail("this is not the exception which should be raised");
            }

            try {
                getBeanToTest().bind(fri1, "/tésté");
                fail("the path /tésté should not not be a valid path");
            } catch (InvalidPathException e) {
                //OK
            } catch (Exception e) {
                fail("this is not the exception which should be raised");
            }

            try {
                getBeanToTest().bind(fri1, "/test");

                FactoryResourceIdentifier friX = getBeanToTest().lookup("/test");
                assertEquals(fri1, friX);
            } catch (Exception e) {
                fail(e.getMessage());
            }

            try {
                getBeanToTest().bind(fri2, "/test");
                fail("the path /test should already be bound to another identifier");
            } catch (PathAlreadyBoundException e) {
                //OK
            } catch (Exception e) {
                fail("this is not the exception which should be raised");
            }

            try {
                getBeanToTest().bind(fri1, "/test2");
                fail("the identifier fri1 should already be bound to another path");
            } catch (IdentifierAlreadyBoundException e) {
                //OK
            } catch (Exception e) {
                fail("this is not the exception which should be raised");
            }

            getBeanToTest().getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testUnbind() {
        FactoryResourceIdentifier fri1 = new FactoryResourceIdentifier("FakeService1", "FakeResourceType1", "FakeID1");
        FactoryResourceIdentifier fri2 = new FactoryResourceIdentifier("FakeService2", "FakeResourceType2", "FakeID2");

        try {
            getBeanToTest().getEntityManager().getTransaction().begin();

            try {
                getBeanToTest().bind(fri1, "/test");

                FactoryResourceIdentifier friX = getBeanToTest().lookup("/test");
                assertEquals(fri1, friX);
                getBeanToTest().bind(fri2, "/test/test2");
                friX = getBeanToTest().lookup("/test/test2");
                assertEquals(fri2, friX);
            } catch (Exception e) {
                fail(e.getMessage());
            }

            try {
                getBeanToTest().unbind("/tésté");
            } catch (InvalidPathException e) {
                //OK
            } catch (Exception e) {
                fail("this is not the exception which should be raised");
            }

            try {
                getBeanToTest().unbind("/testko");
            } catch (PathNotFoundException e) {
                //OK
            } catch (Exception e) {
                fail("this is not the exception which should be raised");
            }

            try {
                getBeanToTest().unbind("/test");
            } catch (PathNotEmptyException e) {
                //This node is not empty and should not be possible to unbind
            } catch (Exception e) {
                fail(e.getMessage());
            }

            try {
                getBeanToTest().unbind("/test/test2");
                getBeanToTest().unbind("/test");
            } catch (Exception e) {
                fail(e.getMessage());
            }

            try {
                getBeanToTest().lookup("/test/test2");
            } catch (PathNotFoundException e) {
                //OK
            } catch (Exception e) {
                fail(e.getMessage());
            }

            try {
                getBeanToTest().lookup("/test");
            } catch (PathNotFoundException e) {
                //OK
            } catch (Exception e) {
                fail(e.getMessage());
            }

            getBeanToTest().getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testList() {
        FactoryResourceIdentifier fri1 = new FactoryResourceIdentifier("FakeService1", "FakeResourceType1", "FakeID1");
        FactoryResourceIdentifier fri2 = new FactoryResourceIdentifier("FakeService2", "FakeResourceType2", "FakeID2");
        FactoryResourceIdentifier fri3 = new FactoryResourceIdentifier("FakeService2", "FakeResourceType2", "FakeID3");
        FactoryResourceIdentifier fri4 = new FactoryResourceIdentifier("FakeService2", "FakeResourceType2", "FakeID4");
        FactoryResourceIdentifier fri5 = new FactoryResourceIdentifier("FakeService2", "FakeResourceType2", "FakeID5");
        FactoryResourceIdentifier fri6 = new FactoryResourceIdentifier("FakeService2", "FakeResourceType2", "FakeID6");
        FactoryResourceIdentifier fri7 = new FactoryResourceIdentifier("FakeService2", "FakeResourceType2", "FakeID7");
        FactoryResourceIdentifier fri8 = new FactoryResourceIdentifier("FakeService2", "FakeResourceType2", "FakeID8");

        try {
            getBeanToTest().getEntityManager().getTransaction().begin();

            try {
                getBeanToTest().bind(fri1, "/test");
                getBeanToTest().bind(fri2, "/test/test1");
                getBeanToTest().bind(fri3, "/test/test2");
                getBeanToTest().bind(fri4, "/test/test3");
                getBeanToTest().bind(fri5, "/test/test4");
                getBeanToTest().bind(fri6, "/test2");
                getBeanToTest().bind(fri7, "/test2/test1");
                getBeanToTest().bind(fri8, "/test2/test2");

                try {
                    getBeanToTest().list("/tésté");
                } catch (InvalidPathException e) {
                    //OK
                } catch (Exception e) {
                    fail("this is not the exception which should be raised");
                }

                try {
                    getBeanToTest().list("/testeur");
                } catch (PathNotFoundException e) {
                    //OK
                } catch (Exception e) {
                    fail("this is not the exception which should be raised");
                }

                String[] children1 = getBeanToTest().list("/test");

                HashSet<String> expectedChildren1 = new HashSet<String>();
                expectedChildren1.add("/test/test1");
                expectedChildren1.add("/test/test2");
                expectedChildren1.add("/test/test3");
                expectedChildren1.add("/test/test4");

                HashSet<String> vChildren1 = new HashSet<String>();

                for (String child : children1) {
                    vChildren1.add(child);
                }

                assertTrue(children1.length == 4);
                assertEquals(expectedChildren1, vChildren1);

                String[] children2 = getBeanToTest().list("/test2");

                HashSet<String> expectedChildren2 = new HashSet<String>();
                expectedChildren2.add("/test2/test1");
                expectedChildren2.add("/test2/test2");

                HashSet<String> vChildren2 = new HashSet<String>();

                for (String child : children2) {
                    vChildren2.add(child);
                }

                assertTrue(children2.length == 2);
                assertEquals(expectedChildren2, vChildren2);
            } catch (Exception e) {
                fail(e.getMessage());
            }

            getBeanToTest().getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testRebind() {
        FactoryResourceIdentifier fri1 = new FactoryResourceIdentifier("FakeService1", "FakeResourceType1", "FakeID1");
        FactoryResourceIdentifier fri2 = new FactoryResourceIdentifier("FakeService2", "FakeResourceType2", "FakeID2");
        FactoryResourceIdentifier fri3 = new FactoryResourceIdentifier("FakeService2", "FakeResourceType2", "FakeID3");
        FactoryResourceIdentifier fri4 = new FactoryResourceIdentifier("FakeService2", "FakeResourceType2", "FakeID4");

        try {
            getBeanToTest().getEntityManager().getTransaction().begin();

            try {
                getBeanToTest().rebind(fri1, "/");
                fail("forbidden to bind the root node");
            } catch (InvalidPathException e) {
                //OK
            } catch (Exception e) {
                fail("this is not the exception which should be raised");
            }

            try {
                getBeanToTest().rebind(fri1, "/tests/test1");
                fail("the path /tests should not exists so binding /tests/test1 should raise an exception");
            } catch (PathNotFoundException e) {
                //OK
            } catch (Exception e) {
                fail("this is not the exception which should be raised");
            }

            try {
                getBeanToTest().rebind(fri1, "/tésté");
                fail("the path /tésté should not not be a valid path");
            } catch (InvalidPathException e) {
                //OK
            } catch (Exception e) {
                fail("this is not the exception which should be raised");
            }

            try {
                getBeanToTest().bind(fri1, "/test1");

                FactoryResourceIdentifier friX = getBeanToTest().lookup("/test1");
                assertEquals(fri1, friX);
                getBeanToTest().bind(fri2, "/test2");
                friX = getBeanToTest().lookup("/test2");
                assertEquals(fri2, friX);
            } catch (Exception e) {
                fail(e.getMessage());
            }

            try {
                getBeanToTest().rebind(fri1, "/test2");
            } catch (IdentifierAlreadyBoundException e) {
                //OK
            } catch (Exception e) {
                fail("this is not the exception which should be raised");
            }

            try {
                getBeanToTest().rebind(fri1, "/test2");
            } catch (IdentifierAlreadyBoundException e) {
                //OK
            } catch (Exception e) {
                fail("this is not the exception which should be raised");
            }

            try {
                getBeanToTest().rebind(fri3, "/test1");
                getBeanToTest().rebind(fri1, "/test12");
                assertEquals(fri1, getBeanToTest().lookup("/test12"));
                getBeanToTest().rebind(fri4, "/test12");
                assertEquals(fri4, getBeanToTest().lookup("/test12"));
            } catch (Exception e) {
                fail(e.getMessage());
            }

            getBeanToTest().getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testGetProperty() {
        FactoryResourceIdentifier fri1 = new FactoryResourceIdentifier("FakeService1", "FakeResourceType1", "FakeID1");
        FactoryResourceIdentifier fri2 = new FactoryResourceIdentifier("FakeService2", "FakeResourceType2", "FakeID2");
        FactoryResourceIdentifier fri3 = new FactoryResourceIdentifier("FakeService2", "FakeResourceType2", "FakeID3");

        try {
            getBeanToTest().getEntityManager().getTransaction().begin();

            try {
                getBeanToTest().getProperty("/test", "myproperty", false);
                fail("the path /test should not exists so getting property should raise an exception");
            } catch (PathNotFoundException e) {
                //OK
            } catch (Exception e) {
                fail("this is not the exception which should be raised");
            }

            try {
                getBeanToTest().getProperty("/tésté", "myproperty", false);
                fail("the path /tésté should not not be a valid path");
            } catch (InvalidPathException e) {
                //OK
            } catch (Exception e) {
                fail("this is not the exception which should be raised");
            }

            try {
                getBeanToTest().bind(fri1, "/test1");
                getBeanToTest().bind(fri2, "/test1/test2");
                getBeanToTest().bind(fri3, "/test1/test2/test3");
                getBeanToTest().setProperty("/test1", "property1", "value1");
                getBeanToTest().setProperty("/test1/test2", "property2", "value2");
                getBeanToTest().setProperty("/test1/test2/test3", "property31", "value31");
                getBeanToTest().setProperty("/test1/test2/test3", "property32", "value32");
                getBeanToTest().setProperty("/test1/test2/test3", "property33", "value33");
            } catch (Exception e) {
                fail(e.getMessage());
            }

            try {
                getBeanToTest().getProperty("/test1", "property4", false);
            } catch (PropertyNotFoundException e) {
                //OK
            } catch (Exception e) {
                fail("this is not the exception which should be raised");
            }

            try {
                String prop1 = getBeanToTest().getProperty("/test1", "property1", false);
                assertEquals("value1", prop1);

                String prop2 = getBeanToTest().getProperty("/test1/test2", "property2", false);
                assertEquals("value2", prop2);

                String prop31 = getBeanToTest().getProperty("/test1/test2/test3", "property31", false);
                assertEquals("value31", prop31);

                String prop32 = getBeanToTest().getProperty("/test1/test2/test3", "property32", false);
                assertEquals("value32", prop32);

                String prop33 = getBeanToTest().getProperty("/test1/test2/test3", "property33", false);
                assertEquals("value33", prop33);

                String prop311 = getBeanToTest().getProperty("/test1/test2/test3", "property1", true);
                assertEquals("value1", prop311);
            } catch (Exception e) {
                fail(e.getMessage());
            }

            getBeanToTest().getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testGetProperties() {
        FactoryResourceIdentifier fri1 = new FactoryResourceIdentifier("FakeService1", "FakeResourceType1", "FakeID1");
        FactoryResourceIdentifier fri2 = new FactoryResourceIdentifier("FakeService2", "FakeResourceType2", "FakeID2");
        FactoryResourceIdentifier fri3 = new FactoryResourceIdentifier("FakeService2", "FakeResourceType2", "FakeID3");

        try {
            getBeanToTest().getEntityManager().getTransaction().begin();

            try {
                getBeanToTest().getProperties("/test", false);
                fail("the path /test should not exists so getting property should raise an exception");
            } catch (PathNotFoundException e) {
                //OK
            } catch (Exception e) {
                fail("this is not the exception which should be raised");
            }

            try {
                getBeanToTest().getProperties("/tésté", false);
                fail("the path /tésté should not not be a valid path");
            } catch (InvalidPathException e) {
                //OK
            } catch (Exception e) {
                fail("this is not the exception which should be raised");
            }

            try {
                getBeanToTest().bind(fri1, "/test1");
                getBeanToTest().bind(fri2, "/test1/test2");
                getBeanToTest().bind(fri3, "/test1/test2/test3");
                getBeanToTest().setProperty("/test1", "property1", "value1");
                getBeanToTest().setProperty("/test1/test2", "property2", "value2");
                getBeanToTest().setProperty("/test1/test2/test3", "property31", "value31");
                getBeanToTest().setProperty("/test1/test2/test3", "property32", "value32");
                getBeanToTest().setProperty("/test1/test2/test3", "property33", "value33");
            } catch (Exception e) {
                fail(e.getMessage());
            }

            try {
                FactoryResourceProperty[] props1 = getBeanToTest().getProperties("/test1/test2/test3", false);

                HashSet<FactoryResourceProperty> expectedProperties1 = new HashSet<FactoryResourceProperty>();
                expectedProperties1.add(new FactoryResourceProperty("property31", "value31"));
                expectedProperties1.add(new FactoryResourceProperty("property32", "value32"));
                expectedProperties1.add(new FactoryResourceProperty("property33", "value33"));

                HashSet<FactoryResourceProperty> vProperties1 = new HashSet<FactoryResourceProperty>();

                for (FactoryResourceProperty prop : props1) {
                    vProperties1.add(prop);
                }

                assertTrue(props1.length == 3);
                assertEquals(expectedProperties1, vProperties1);

                FactoryResourceProperty[] props2 = getBeanToTest().getProperties("/test1/test2/test3", true);

                HashSet<FactoryResourceProperty> expectedProperties2 = new HashSet<FactoryResourceProperty>();
                expectedProperties2.add(new FactoryResourceProperty("property31", "value31"));
                expectedProperties2.add(new FactoryResourceProperty("property32", "value32"));
                expectedProperties2.add(new FactoryResourceProperty("property33", "value33"));
                expectedProperties2.add(new FactoryResourceProperty("property2", "value2"));
                expectedProperties2.add(new FactoryResourceProperty("property1", "value1"));

                HashSet<FactoryResourceProperty> vProperties2 = new HashSet<FactoryResourceProperty>();

                for (FactoryResourceProperty prop : props2) {
                    vProperties2.add(prop);
                }

                assertTrue(props2.length == 5);
                assertEquals(expectedProperties2, vProperties2);
            } catch (Exception e) {
                fail(e.getMessage());
            }

            getBeanToTest().getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public static class NodeInitialDataSet extends EntityInitialDataSet<Node> {
        public NodeInitialDataSet() {
            super(Node.class);
        }

        public void create() {
            Node root = new Node();
            root.setId("1,618033-9887-4989-4848-204586834365");
            root.setPathPart("");
            this.add(root);
        }
    }
}
