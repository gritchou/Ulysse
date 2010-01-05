
package org.qualipso.factory.svn.client.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.qualipso.factory.svn.client.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _MembershipServiceException_QNAME = new QName("http://org.qualipso.factory.ws/service/membership", "MembershipServiceException");
    private final static QName _AccessDeniedException_QNAME = new QName("http://org.qualipso.factory.ws/service/membership", "AccessDeniedException");
    private final static QName _PropertyNotFoundException_QNAME = new QName("http://org.qualipso.factory.ws/service/membership", "PropertyNotFoundException");
    private final static QName _PathNotEmptyException_QNAME = new QName("http://org.qualipso.factory.ws/service/membership", "PathNotEmptyException");
    private final static QName _PathAlreadyBoundException_QNAME = new QName("http://org.qualipso.factory.ws/service/membership", "PathAlreadyBoundException");
    private final static QName _FactoryException_QNAME = new QName("http://org.qualipso.factory.ws/service/membership", "FactoryException");
    private final static QName _PathNotFoundException_QNAME = new QName("http://org.qualipso.factory.ws/service/membership", "PathNotFoundException");
    private final static QName _Folder_QNAME = new QName("http://org.qualipso.factory.ws/service/membership", "folder");
    private final static QName _InvalidPathException_QNAME = new QName("http://org.qualipso.factory.ws/service/membership", "InvalidPathException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.qualipso.factory.svn.client.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MembershipServiceException }
     * 
     */
    public MembershipServiceException createMembershipServiceException() {
        return new MembershipServiceException();
    }

    /**
     * Create an instance of {@link Group }
     * 
     */
    public Group createGroup() {
        return new Group();
    }

    /**
     * Create an instance of {@link InvalidPathException }
     * 
     */
    public InvalidPathException createInvalidPathException() {
        return new InvalidPathException();
    }

    /**
     * Create an instance of {@link PathAlreadyBoundException }
     * 
     */
    public PathAlreadyBoundException createPathAlreadyBoundException() {
        return new PathAlreadyBoundException();
    }

    /**
     * Create an instance of {@link Folder }
     * 
     */
    public Folder createFolder() {
        return new Folder();
    }

    /**
     * Create an instance of {@link PathNotEmptyException }
     * 
     */
    public PathNotEmptyException createPathNotEmptyException() {
        return new PathNotEmptyException();
    }

    /**
     * Create an instance of {@link File }
     * 
     */
    public File createFile() {
        return new File();
    }

    /**
     * Create an instance of {@link AccessDeniedException }
     * 
     */
    public AccessDeniedException createAccessDeniedException() {
        return new AccessDeniedException();
    }

    /**
     * Create an instance of {@link FactoryException }
     * 
     */
    public FactoryException createFactoryException() {
        return new FactoryException();
    }

    /**
     * Create an instance of {@link PathNotFoundException }
     * 
     */
    public PathNotFoundException createPathNotFoundException() {
        return new PathNotFoundException();
    }

    /**
     * Create an instance of {@link Profile }
     * 
     */
    public Profile createProfile() {
        return new Profile();
    }

    /**
     * Create an instance of {@link PropertyNotFoundException }
     * 
     */
    public PropertyNotFoundException createPropertyNotFoundException() {
        return new PropertyNotFoundException();
    }

    /**
     * Create an instance of {@link StringArray }
     * 
     */
    public StringArray createStringArray() {
        return new StringArray();
    }

    /**
     * Create an instance of {@link Link }
     * 
     */
    public Link createLink() {
        return new Link();
    }

    /**
     * Create an instance of {@link ProfileInfo }
     * 
     */
    public ProfileInfo createProfileInfo() {
        return new ProfileInfo();
    }

    /**
     * Create an instance of {@link ProfileInfoArray }
     * 
     */
    public ProfileInfoArray createProfileInfoArray() {
        return new ProfileInfoArray();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MembershipServiceException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://org.qualipso.factory.ws/service/membership", name = "MembershipServiceException")
    public JAXBElement<MembershipServiceException> createMembershipServiceException(MembershipServiceException value) {
        return new JAXBElement<MembershipServiceException>(_MembershipServiceException_QNAME, MembershipServiceException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AccessDeniedException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://org.qualipso.factory.ws/service/membership", name = "AccessDeniedException")
    public JAXBElement<AccessDeniedException> createAccessDeniedException(AccessDeniedException value) {
        return new JAXBElement<AccessDeniedException>(_AccessDeniedException_QNAME, AccessDeniedException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PropertyNotFoundException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://org.qualipso.factory.ws/service/membership", name = "PropertyNotFoundException")
    public JAXBElement<PropertyNotFoundException> createPropertyNotFoundException(PropertyNotFoundException value) {
        return new JAXBElement<PropertyNotFoundException>(_PropertyNotFoundException_QNAME, PropertyNotFoundException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PathNotEmptyException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://org.qualipso.factory.ws/service/membership", name = "PathNotEmptyException")
    public JAXBElement<PathNotEmptyException> createPathNotEmptyException(PathNotEmptyException value) {
        return new JAXBElement<PathNotEmptyException>(_PathNotEmptyException_QNAME, PathNotEmptyException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PathAlreadyBoundException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://org.qualipso.factory.ws/service/membership", name = "PathAlreadyBoundException")
    public JAXBElement<PathAlreadyBoundException> createPathAlreadyBoundException(PathAlreadyBoundException value) {
        return new JAXBElement<PathAlreadyBoundException>(_PathAlreadyBoundException_QNAME, PathAlreadyBoundException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FactoryException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://org.qualipso.factory.ws/service/membership", name = "FactoryException")
    public JAXBElement<FactoryException> createFactoryException(FactoryException value) {
        return new JAXBElement<FactoryException>(_FactoryException_QNAME, FactoryException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PathNotFoundException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://org.qualipso.factory.ws/service/membership", name = "PathNotFoundException")
    public JAXBElement<PathNotFoundException> createPathNotFoundException(PathNotFoundException value) {
        return new JAXBElement<PathNotFoundException>(_PathNotFoundException_QNAME, PathNotFoundException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Folder }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://org.qualipso.factory.ws/service/membership", name = "folder")
    public JAXBElement<Folder> createFolder(Folder value) {
        return new JAXBElement<Folder>(_Folder_QNAME, Folder.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvalidPathException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://org.qualipso.factory.ws/service/membership", name = "InvalidPathException")
    public JAXBElement<InvalidPathException> createInvalidPathException(InvalidPathException value) {
        return new JAXBElement<InvalidPathException>(_InvalidPathException_QNAME, InvalidPathException.class, null, value);
    }

}
