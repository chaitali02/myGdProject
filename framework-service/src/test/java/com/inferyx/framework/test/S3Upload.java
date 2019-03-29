package com.inferyx.framework.test;

import java.io.File;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3Upload {

	public S3Upload() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		String file_path = "/home/joy/Desktop/rule";
		String bucket_name = "inferyx";
		String key_name = "rule/rule";
		System.out.format("Uploading %s to S3 bucket %s...\n", file_path, bucket_name);
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
		    s3.putObject(bucket_name, key_name, new File(file_path));
		} catch (AmazonServiceException e) {
		    System.err.println(e.getErrorMessage());
		    System.exit(1);
		}
	}

}
