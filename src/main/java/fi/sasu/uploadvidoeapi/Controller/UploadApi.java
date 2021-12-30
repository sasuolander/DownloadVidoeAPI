package fi.sasu.uploadvidoeapi.Controller;

import ch.qos.logback.classic.Logger;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static fi.sasu.uploadvidoeapi.Controller.constant.FILEPATH;

class constant {
    static final String FILEPATH = "./temp/";
}

enum FileExtension {
    MP4(".mp4"),
    MOV(".mov"),
    WMV(".wmv"),
    AVI(".avi"),
    FLV(".flv"),
    TXT(".txt"); // for initial testing only
    private final String extension;

    FileExtension(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return this.extension;
    }

}

@RestController
public class UploadApi {
    Logger logger = (Logger) LoggerFactory.getLogger(UploadApi.class);

    @PostMapping("/download")
    public String downloadApi(@RequestBody Inputs inputs) {
        logger.info(inputs.input());
        logger.info(inputs.input2());
        logger.info("main thread " + Thread.currentThread().getName());
        ThreadForDownload test1 = new ThreadForDownload(inputs.input());
        ThreadForDownload test2 = new ThreadForDownload(inputs.input2());
        test2.start();
        test1.start();
        return "success";
    }
}

// how to model video as byte
// how playable it is
record Inputs(String input, String input2) {
}

record Inputs2(ArrayList<String> videos) {
}

class ThreadForDownload extends Thread {
    Logger logger = (Logger) LoggerFactory.getLogger(ThreadForDownload.class);
    volatile Long status;
    String singletonFilename = Integer.toHexString(new Random().nextInt());
    String input;

    ThreadForDownload(String input) {
        this.input = input;
    }

    @Override
    public void run() {
        logger.info("thread name " + Thread.currentThread().getName());
        logger.info("ThreadForDownload  " + this.input);
        createFile("test", FileExtension.TXT);
        writeMethod(this.input, "test", FileExtension.TXT);
    }

    public String createFile(String filename, FileExtension extension) {
        try {
            File file = new File(FILEPATH + filename + this.singletonFilename + extension.getExtension());
            if (file.createNewFile()) {

                logger.info("File created: " + file.getName());
                return file.getPath();
            } else {
                logger.info("File already exists.");
                return file.getPath();
            }
        } catch (IOException e) {
            logger.info("An error occurred.");
            e.printStackTrace();
            return null;
        }
    }


    public void writeMethod(String input, String filename, FileExtension extension) {
        try {

            FileWriter myWriter = new FileWriter(FILEPATH + filename + this.singletonFilename + extension.getExtension());
            myWriter.write(input);
            myWriter.close();
            logger.info("Successfully wrote to the file.");
        } catch (IOException e) {
            logger.info("An error occurred.");
            e.printStackTrace();
        }
    }

}

class Util {
    public static @NotNull String decodeBase64(String input) {
        Base64 base64 = new Base64();
        return new String(base64.decode(input.getBytes()));
    }

    public static @NotNull String encodeBase64(String input) {
        String originalInput = "test input";
        Base64 base64 = new Base64();
        return new String(base64.encode(input.getBytes()));
    }
}