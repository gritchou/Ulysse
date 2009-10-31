
package ws.factory.qualipso.org.service.project;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import ws.factory.qualipso.org.resource.folder.Folder;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ws.factory.qualipso.org.service.project package. 
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

    private final static QName _FactoryException_QNAME = new QName("http://org.qualipso.factory.ws/service/project", "FactoryException");
    private final static QName _Folder_QNAME = new QName("http://org.qualipso.factory.ws/service/project", "folder");
    private final static QName _ProjectException_QNAME = new QName("http://org.qualipso.factory.ws/service/project", "ProjectException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ws.factory.qualipso.org.service.project
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProjectException }
     * 
     */
    public ProjectException createProjectException() {
        return new ProjectException();
    }

    /**
     * Create an instance of {@link FactoryException }
     * 
     */
    public FactoryException createFactoryException() {
        return new FactoryException();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FactoryException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://org.qualipso.factory.ws/service/project", name = "FactoryException")
    public JAXBElement<FactoryException> createFactoryException(FactoryException value) {
        return new JAXBElement<FactoryException>(_FactoryException_QNAME, FactoryException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Folder }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://org.qualipso.factory.ws/service/project", name = "folder")
    public JAXBElement<Folder> createFolder(Folder value) {
        return new JAXBElement<Folder>(_Folder_QNAME, Folder.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProjectException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://org.qualipso.factory.ws/service/project", name = "ProjectException")
    public JAXBElement<ProjectException> createProjectException(ProjectException value) {
        return new JAXBElement<ProjectException>(_ProjectException_QNAME, ProjectException.class, null, value);
    }

}
