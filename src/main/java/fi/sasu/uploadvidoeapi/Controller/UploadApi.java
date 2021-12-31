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
import java.util.Random;

import static fi.sasu.uploadvidoeapi.Controller.Util.decodeBase64;
import static fi.sasu.uploadvidoeapi.Controller.Constant.FILEPATH;

class Constant {
    private Constant() {
    }

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
        logger.info("main thread " + Thread.currentThread().getName());

        // factory to generate and start thread
        RunnableForDownload test1 = new RunnableForDownload(inputs.input(), "", FileExtension.MP4);
        RunnableForDownload test2 = new RunnableForDownload(inputs.input2(), "", FileExtension.MP4);

        Thread thread1 = new Thread(test1);
        Thread thread2 = new Thread(test2);
        thread1.start();
        thread2.start();

        // wait until all thread are finished then tell success,
        // Monitor Status of thread and send information for a failure

        return "success";
    }
}

record Inputs(String input, String input2) {
}

record Inputs2(ArrayList<String> videos) {
}

class RunnableForDownload implements Runnable {
    Logger logger = (Logger) LoggerFactory.getLogger(RunnableForDownload.class);
    final String singletonIdentifier = Integer.toHexString(new Random().nextInt());
    private final String input;
    private final String path;

    RunnableForDownload(@NotNull String input, @NotNull String fileName, @NotNull FileExtension extension) {
        this.input = input;
        this.path = FILEPATH + fileName + singletonIdentifier + extension.getExtension();
    }

    @Override
    public void run() {
        logger.info("thread name {}", Thread.currentThread().getName());
        logger.info("ThreadForDownload {}", this.input);
        createFile();
        writeMethod();
    }

    public void createFile() {
        try {
            File file = new File(this.path);
            if (file.createNewFile()) {
                logger.info("File created: " + file.getName());
            } else {
                logger.info("File already exists.");
            }
        } catch (IOException e) {
            logger.info("An error occurred.");
            e.printStackTrace();
        }
    }


    public void writeMethod() {
        try {
            byte[] decoded = decodeBase64(this.input);
            try (FileOutputStream myWriter = new FileOutputStream(this.path)) {
                myWriter.write(decoded);
                logger.info("Successfully wrote to the file.");
            }
        } catch (IOException e) {
            logger.info("An error occurred.");
            e.printStackTrace();
        }
    }

}

class Util {
    private Util() {
    }

    public static byte[] decodeBase64(String input) {
        Base64 base64 = new Base64();
        return base64.decode(input.getBytes());
    }
}