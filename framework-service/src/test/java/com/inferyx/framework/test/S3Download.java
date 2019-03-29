package com.inferyx.framework.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class S3Download {

	public S3Download() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		String bucket_name = "inferyx";
		String key_name = "rule/rule";
		String file_name = "/home/joy/git/rule_s3";
		// Way - 1 : Have the credentials from setup or env variable
		/*final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1)
												.withForceGlobalBucketAccessEnabled(true)
												.build();*/
		// Way - 2 : Pass the credentials
		BasicAWSCredentials awsCreds = new BasicAWSCredentials("<AWS Access Key ID>", "<AWS Secret Access Key>");
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1)
												.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
												.withForceGlobalBucketAccessEnabled(true)
												.build();
		 
		try {
		    S3Object o = s3.getObject(bucket_name, key_name);
		    S3ObjectInputStream s3is = o.getObjectContent();
		    FileOutputStream fos = new FileOutputStream(new File(file_name));
		    byte[] read_buf = new byte[1024];
		    int read_len = 0;
		    while ((read_len = s3is.read(read_buf)) > 0) {
		        fos.write(read_buf, 0, read_len);
		    }
		    s3is.close();
		    fos.close();
		} catch (AmazonServiceException e) {
		    System.err.println(e.getErrorMessage());
		    System.exit(1);
		} catch (FileNotFoundException e) {
		    System.err.println(e.getMessage());
		    System.exit(1);
		} catch (IOException e) {
		    System.err.println(e.getMessage());
		    System.exit(1);
		}
	}

}
