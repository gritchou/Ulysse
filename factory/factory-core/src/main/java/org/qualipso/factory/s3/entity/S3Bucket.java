package org.qualipso.factory.s3.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 27 June 2009
 */
/*
@Entity
@XmlType(name = "S3Bucket", namespace = "http://org.qualipso.factory.ws/entity", propOrder =  {
    "name", "description", "membersList"}
)
@SuppressWarnings("serial")
*/
public class S3Bucket extends FactoryResource {

	@Override
	public FactoryResourceIdentifier getFactoryResourceIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResourceName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResourcePath() {
		// TODO Auto-generated method stub
		return null;
	}

}
