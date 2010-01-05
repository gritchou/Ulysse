
package org.factory.qualipso.service.skillmanagement.wsclient;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.factory.qualipso.service.skillmanagement.wsclient package. 
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

    private final static QName _SkillServiceException_QNAME = new QName("http://org.qualipso.factory.ws/service/skill", "SkillServiceException");
    private final static QName _FactoryException_QNAME = new QName("http://org.qualipso.factory.ws/service/skill", "FactoryException");
    private final static QName _Folder_QNAME = new QName("http://org.qualipso.factory.ws/service/skill", "folder");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.factory.qualipso.service.skillmanagement.wsclient
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FactoryException }
     * 
     */
    public FactoryException createFactoryException() {
        return new FactoryException();
    }

    /**
     * Create an instance of {@link SupportSQLComp }
     * 
     */
    public SupportSQLComp createSupportSQLComp() {
        return new SupportSQLComp();
    }

    /**
     * Create an instance of {@link Link }
     * 
     */
    public Link createLink() {
        return new Link();
    }

    /**
     * Create an instance of {@link SkillServiceException }
     * 
     */
    public SkillServiceException createSkillServiceException() {
        return new SkillServiceException();
    }

    /**
     * Create an instance of {@link Group }
     * 
     */
    public Group createGroup() {
        return new Group();
    }

    /**
     * Create an instance of {@link ArrayOfSupportSQL }
     * 
     */
    public ArrayOfSupportSQL createArrayOfSupportSQL() {
        return new ArrayOfSupportSQL();
    }

    /**
     * Create an instance of {@link SupportSQLLev }
     * 
     */
    public SupportSQLLev createSupportSQLLev() {
        return new SupportSQLLev();
    }

    /**
     * Create an instance of {@link ArrayOfSupportSQLUserComp }
     * 
     */
    public ArrayOfSupportSQLUserComp createArrayOfSupportSQLUserComp() {
        return new ArrayOfSupportSQLUserComp();
    }

    /**
     * Create an instance of {@link StringArray }
     * 
     */
    public StringArray createStringArray() {
        return new StringArray();
    }

    /**
     * Create an instance of {@link Profile }
     * 
     */
    public Profile createProfile() {
        return new Profile();
    }

    /**
     * Create an instance of {@link SupportSQLUserComp }
     * 
     */
    public SupportSQLUserComp createSupportSQLUserComp() {
        return new SupportSQLUserComp();
    }

    /**
     * Create an instance of {@link ArrayOfSupportSQLComp }
     * 
     */
    public ArrayOfSupportSQLComp createArrayOfSupportSQLComp() {
        return new ArrayOfSupportSQLComp();
    }

    /**
     * Create an instance of {@link SupportSQLTop }
     * 
     */
    public SupportSQLTop createSupportSQLTop() {
        return new SupportSQLTop();
    }

    /**
     * Create an instance of {@link SupportSQLUser }
     * 
     */
    public SupportSQLUser createSupportSQLUser() {
        return new SupportSQLUser();
    }

    /**
     * Create an instance of {@link File }
     * 
     */
    public File createFile() {
        return new File();
    }

    /**
     * Create an instance of {@link ArrayOfSupportSQLUser }
     * 
     */
    public ArrayOfSupportSQLUser createArrayOfSupportSQLUser() {
        return new ArrayOfSupportSQLUser();
    }

    /**
     * Create an instance of {@link ArrayOfSupportSQLTop }
     * 
     */
    public ArrayOfSupportSQLTop createArrayOfSupportSQLTop() {
        return new ArrayOfSupportSQLTop();
    }

    /**
     * Create an instance of {@link ArrayOfSupportSQLLev }
     * 
     */
    public ArrayOfSupportSQLLev createArrayOfSupportSQLLev() {
        return new ArrayOfSupportSQLLev();
    }

    /**
     * Create an instance of {@link SupportSQLCompTop }
     * 
     */
    public SupportSQLCompTop createSupportSQLCompTop() {
        return new SupportSQLCompTop();
    }

    /**
     * Create an instance of {@link SupportSQLGapTopic }
     * 
     */
    public SupportSQLGapTopic createSupportSQLGapTopic() {
        return new SupportSQLGapTopic();
    }

    /**
     * Create an instance of {@link ArrayOfSupportSQLCompTop }
     * 
     */
    public ArrayOfSupportSQLCompTop createArrayOfSupportSQLCompTop() {
        return new ArrayOfSupportSQLCompTop();
    }

    /**
     * Create an instance of {@link Folder }
     * 
     */
    public Folder createFolder() {
        return new Folder();
    }

    /**
     * Create an instance of {@link ArrayOfSupportSQLGapTopic }
     * 
     */
    public ArrayOfSupportSQLGapTopic createArrayOfSupportSQLGapTopic() {
        return new ArrayOfSupportSQLGapTopic();
    }

    /**
     * Create an instance of {@link SupportSQL }
     * 
     */
    public SupportSQL createSupportSQL() {
        return new SupportSQL();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SkillServiceException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://org.qualipso.factory.ws/service/skill", name = "SkillServiceException")
    public JAXBElement<SkillServiceException> createSkillServiceException(SkillServiceException value) {
        return new JAXBElement<SkillServiceException>(_SkillServiceException_QNAME, SkillServiceException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FactoryException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://org.qualipso.factory.ws/service/skill", name = "FactoryException")
    public JAXBElement<FactoryException> createFactoryException(FactoryException value) {
        return new JAXBElement<FactoryException>(_FactoryException_QNAME, FactoryException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Folder }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://org.qualipso.factory.ws/service/skill", name = "folder")
    public JAXBElement<Folder> createFolder(Folder value) {
        return new JAXBElement<Folder>(_Folder_QNAME, Folder.class, null, value);
    }

}
