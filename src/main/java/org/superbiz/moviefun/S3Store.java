package org.superbiz.moviefun;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.tika.Tika;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;

public class S3Store implements BlobStore {

    AmazonS3Client s3Client;
    String bucketName;

    public S3Store(AmazonS3Client s3Client, String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;

        //s3Client.createBucket(this.bucketName);
    }

    @Override
    public void put(Blob blob) throws IOException {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(blob.contentType);

        s3Client.putObject(bucketName, blob.name, blob.inputStream, metadata);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {

        Optional<Blob> image = Optional.empty();

        try {

            S3Object cloudImage = s3Client.getObject(bucketName, name);
            image = Optional.of(new Blob(name, cloudImage.getObjectContent()
                        , cloudImage.getObjectMetadata().getContentType()));
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which" +
                    " means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());

        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means"+
                    " the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }

        return image;
    }
}
