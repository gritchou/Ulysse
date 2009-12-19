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
package org.qualipso.factory.binding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jboss.ejb3.annotation.SecurityDomain;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.entity.Node;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.annotation.Resource;

import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


/**
 * Implementation of the Binding Service.<br/>
 * <br/>
 * Implementation is based on a EJB 3.0 Stateless Session Bean. Because internal visibility only, this bean does not implement
 * Remote interface but only Local one.
 * Bean name follow naming conventions of the factory and use the specific local service prefix.<br/>
 * <br/>
 * Bean security is configured for JBoss AS 5 and rely on JAAS to ensure Authentication and Authorization of user.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June 2009
 */
@Stateless(name = BindingService.SERVICE_NAME, mappedName = FactoryNamingConvention.LOCAL_SERVICE_PREFIX + BindingService.SERVICE_NAME)
@SecurityDomain(value = "JBossWSDigest")
public class BindingServiceBean implements BindingService {
    private static Log logger = LogFactory.getLog(BindingServiceBean.class);

    /**
     * A fixed ID for root node of the naming tree.
     */
    private final String ROOT_NODE_ID = "1,618033-9887-4989-4848-204586834365";
    private SessionContext ctx;
    private EntityManager em;

    public BindingServiceBean() {
    }

    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return this.em;
    }

    @Resource
    public void setSessionContext(SessionContext ctx) {
        this.ctx = ctx;
    }

    public SessionContext getSessionContext() {
        return this.ctx;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void bind(FactoryResourceIdentifier identifier, String path)
        throws BindingServiceException, InvalidPathException, PathNotFoundException, PathAlreadyBoundException, IdentifierAlreadyBoundException {
        logger.info("bind(...) called");
        logger.debug("params : path=" + path + ", identifier=" + identifier);

        PathHelper.valid(path);

        String npath = PathHelper.normalize(path);

        if (PathHelper.isRoot(npath)) {
            throw new InvalidPathException("forbidden to bind the root node");
        }

        try {
            findNodeByBindedResourceIdentifier(identifier);
            throw new IdentifierAlreadyBoundException("the identifier [" + identifier + "] is already bound to another path");
        } catch (PathNotFoundException e) {
        }

        try {
            findNodeByPath(npath);
            throw new PathAlreadyBoundException("the path [" + npath + "] is already bound");
        } catch (PathNotFoundException e) {
        }

        rebind(identifier, npath);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public FactoryResourceIdentifier lookup(String path)
        throws InvalidPathException, PathNotFoundException {
        logger.info("lookup(...) called");
        logger.debug("params : path=" + path);

        PathHelper.valid(path);

        String npath = PathHelper.normalize(path);

        Node node = findNodeByPath(npath);

        return FactoryResourceIdentifier.deserialize(node.getBindedResourceIdentifier());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void unbind(String path) throws BindingServiceException, InvalidPathException, PathNotFoundException, PathNotEmptyException {
        logger.info("unbind(...) called");
        logger.debug("params : path=" + path);

        PathHelper.valid(path);

        String npath = PathHelper.normalize(path);

        if (PathHelper.isRoot(npath)) {
            throw new InvalidPathException("forbidden to unbind the root node");
        }

        Node node = findNodeByPath(npath);

        if (node.getChildren().size() != 0) {
            throw new PathNotEmptyException("unable to unbind this name because it has children");
        }

        try {
            Node parent = em.find(Node.class, node.getParent());
            parent.removeChild(node.getPathPart());
            em.remove(node);
            em.merge(parent);
        } catch (Exception e) {
            ctx.setRollbackOnly();
            throw new BindingServiceException(e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void rebind(FactoryResourceIdentifier identifier, String path)
        throws BindingServiceException, InvalidPathException, PathNotFoundException, IdentifierAlreadyBoundException {
        logger.info("rebind(...) called");
        logger.debug("params : path=" + path + ", identifier=" + identifier);

        PathHelper.valid(path);

        String npath = PathHelper.normalize(path);

        if (PathHelper.isRoot(npath)) {
            throw new InvalidPathException("forbidden to rebind the root node");
        }

        try {
            findNodeByBindedResourceIdentifier(identifier);
            throw new IdentifierAlreadyBoundException("the identifier [" + identifier + "] is already bound to another path");
        } catch (PathNotFoundException e) {
        }

        Node parent = findNodeByPath(PathHelper.getParentPath(npath));
        Node node = null;

        try {
            if (parent.getChildren().containsKey(PathHelper.getPathPart(npath))) {
                node = em.find(Node.class, parent.getChild(PathHelper.getPathPart(npath)));
                node.setBindedResourceIdentifier(identifier.serialize());
                em.merge(node);
            } else {
                node = new Node();
                node.setId(UUID.randomUUID().toString());
                node.setParent(parent.getId());
                node.setPathPart(PathHelper.getPathPart(npath));
                node.setBindedResourceIdentifier(identifier.serialize());
                parent.addChild(PathHelper.getPathPart(npath), node.getId());
                em.persist(node);
                em.merge(parent);
            }

            logger.debug("identifier [" + identifier + "] bound to path [" + npath + "]");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ctx.setRollbackOnly();
            throw new BindingServiceException(e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String[] list(String path) throws InvalidPathException, PathNotFoundException {
        logger.info("list(...) called");
        logger.debug("params : path=" + path);

        PathHelper.valid(path);

        String npath = PathHelper.normalize(path);

        Node node = findNodeByPath(npath);

        String nodepath = buildNodePath(node);
        String[] children = new String[node.getChildren().size()];
        int i = 0;

        for (String child : node.getChildren().keySet()) {
            children[i] = PathHelper.normalize(nodepath + "/" + child);
            i++;
        }

        return children;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setProperty(String path, String name, String value)
        throws BindingServiceException, InvalidPathException, PathNotFoundException {
        logger.info("setProperty(...) called");
        logger.debug("params : path=" + path + ", name=" + name + ", value=" + value);

        PathHelper.valid(path);

        String npath = PathHelper.normalize(path);

        Node node = findNodeByPath(npath);

        node.setProperty(name, value);

        try {
            em.merge(node);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ctx.setRollbackOnly();
            throw new BindingServiceException(e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String getProperty(String path, String name, boolean inherited)
        throws InvalidPathException, PathNotFoundException, PropertyNotFoundException {
        logger.info("getProperty(...) called");
        logger.debug("params : path=" + path + ", name=" + name + ", inherited=" + inherited);

        PathHelper.valid(path);

        String npath = PathHelper.normalize(path);

        Node node = findNodeByPath(npath);

        if (node.getProperties().containsKey(name)) {
            return node.getProperties().get(name);
        } else if (inherited) {
            if (!PathHelper.isRoot(npath)) {
                return getProperty(PathHelper.getParentPath(npath), name, true);
            }
        }

        throw new PropertyNotFoundException("unable to found a property with name [" + name + "] for path [" + npath + "]" +
            ((inherited) ? " even inherited" : ""));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public FactoryResourceProperty[] getProperties(String path, boolean inherited)
        throws InvalidPathException, PathNotFoundException {
        logger.info("getProperties(...) called");
        logger.debug("params : path=" + path + ", inherited=" + inherited);

        PathHelper.valid(path);

        String npath = PathHelper.normalize(path);

        Node node = findNodeByPath(npath);
        Vector<FactoryResourceProperty> properties = new Vector<FactoryResourceProperty>();

        if (inherited) {
            if (!PathHelper.isRoot(npath)) {
                FactoryResourceProperty[] props = getProperties(PathHelper.getParentPath(npath), true);

                for (FactoryResourceProperty prop : props) {
                    properties.add(prop);
                }
            }
        }

        for (String propname : node.getProperties().keySet()) {
            properties.add(new FactoryResourceProperty(propname, node.getProperty(propname)));
        }

        FactoryResourceProperty[] aProperties = new FactoryResourceProperty[properties.size()];

        return properties.toArray(aProperties);
    }

    // Private Usefull Methods
    /**
     * Try to find a node by loading recursively all parents. <br/>
     * If the root node does not exists, create the root node.
     *
     * @param path the path to find
     * @return the Node object representing this path
     * @throws PathNotFoundException if the path has not been found
     * @throws InvalidPathException if the path is not valid
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private Node findNodeByPath(String path) throws PathNotFoundException, InvalidPathException {
        Node node = null;

        if (PathHelper.isRoot(path)) {
            node = em.find(Node.class, this.ROOT_NODE_ID);

            if (node == null) {
                logger.warn("Root node not found, building...");

                Node root = new Node();
                root.setId("1,618033-9887-4989-4848-204586834365");
                root.setPathPart("");
                root.setBindedResourceIdentifier("");
                root.setParent("");
                em.persist(root);
                node = root;
            }
        } else {
            Node parent = findNodeByPath(PathHelper.getParentPath(path));

            if (parent.getChildren().containsKey(PathHelper.getPathPart(path))) {
                node = em.find(Node.class, parent.getChild(PathHelper.getPathPart(path)));
            }
        }

        if (node == null) {
            throw new PathNotFoundException("unable to find a node at path " + path);
        }

        return node;
    }

    /**
     * Try to find a node by querying using FactoryResourceIdentifier
     *
     * @param identifier the FactoryResourceIdentifier which should be binded to this node
     * @return the node
     * @throws PathNotFoundException if the node is not found
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @SuppressWarnings("unchecked")
    private Node findNodeByBindedResourceIdentifier(FactoryResourceIdentifier identifier)
        throws PathNotFoundException {
        List<Node> nodes = em.createQuery("select n from Node n where n.bindedResourceIdentifier = :identifier")
                             .setParameter("identifier", identifier.serialize()).getResultList();

        if (nodes.size() > 0) {
            return nodes.get(0);
        } else {
            throw new PathNotFoundException("unable to find a node binded with resource identifier " + identifier);
        }
    }

    /**
     * Evaluate the path of a node.
     *
     * @param node the node to evaluate
     * @return the path
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private String buildNodePath(Node node) {
        return buildNodePath(node, "");
    }

    /**
     * Evaluate the path of a node recursively.
     *
     * @param node the node to evaluate
     * @param path the actual path in evaluation
     * @return the path
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private String buildNodePath(Node node, String path) {
        if ((node.getParent() != null) && !node.getParent().equals("")) {
            Node parent = em.find(Node.class, node.getParent());

            return buildNodePath(parent, "/" + node.getPathPart() + path);
        } else {
            return path;
        }
    }
}
