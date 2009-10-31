package org.qualipso.factory.s3;

import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.security.AWSCredentials;

public class S3ServiceBean {
	
	private String awsAccessKey = "YOUR_AWS_ACCESS_KEY";
	private String awsSecretKey = "YOUR_AWS_SECRET_KEY";
	
	public S3ServiceBean() {
//		AWSCredentials awsCredentials = new AWSCredentials(awsAccessKey, awsSecretKey);
//		org.jets3t.service.S3Service s3Service = new RestS3Service(awsCredentials);
//		S3Bucket bucket = new S3Bucket();
	}


}
