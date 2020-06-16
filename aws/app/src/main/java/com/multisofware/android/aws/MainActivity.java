package com.multisofware.android.aws;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {

        Float similarityThreshold = 70F;
        String sourceImage = "source.jpg";
        String targetImage = "target.jpg";
        ByteBuffer sourceImageBytes = null;
        ByteBuffer targetImageBytes = null;


        AmazonRekognitionClient rekognitionClient = new AmazonRekognitionClient(new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return "AKIAIPMQA5JKUG37UOSA";
            }

            @Override
            public String getAWSSecretKey() {
                return "2jO9KI9FnxwD9V2YOjH8iSGJzXx/PT5s0S4dL+4f";
            }
        });

        try (InputStream inputStream = new FileInputStream(new File(sourceImage))) {
            sourceImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        } catch (Exception e) {
            Log.e(TAG, "Failed to load source image " + sourceImage, e);
            return;
        }
        try (InputStream inputStream = new FileInputStream(new File(targetImage))) {
            targetImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        } catch (Exception e) {
            Log.e(TAG, "Failed to load target image " + targetImage, e);
            return;
        }

        Image source = new Image()
                .withBytes(sourceImageBytes);
        Image target = new Image()
                .withBytes(targetImageBytes);

        CompareFacesRequest request = new CompareFacesRequest()
                .withSourceImage(source)
                .withTargetImage(target)
                .withSimilarityThreshold(similarityThreshold);


        CompareFacesResult compareFacesResult = rekognitionClient.compareFaces(request);

        List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
        for (CompareFacesMatch match : faceDetails) {
            ComparedFace face = match.getFace();
            BoundingBox position = face.getBoundingBox();
            Log.d(TAG, "Face at " + position.getLeft().toString()
                    + " " + position.getTop()
                    + " matches with " + match.getSimilarity().toString()
                    + "% confidence.");

        }
        List<ComparedFace> uncompared = compareFacesResult.getUnmatchedFaces();

        Log.d(TAG, "There was " + uncompared.size() + " face(s) that did not match");

    }
}