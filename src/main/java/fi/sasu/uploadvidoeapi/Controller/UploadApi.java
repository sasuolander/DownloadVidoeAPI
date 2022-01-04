package fi.sasu.uploadvidoeapi.Controller;

import ch.qos.logback.classic.Logger;
import lombok.val;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static fi.sasu.uploadvidoeapi.Controller.Util.decodeBase64;
import static fi.sasu.uploadvidoeapi.Controller.Constant.FILEPATH;

class Constant {
    private Constant() {
    }

    static final String FILEPATH = "./temp/";
}

record Video(String video, String fileName, FileExtension type) {
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
    public String downloadApi(@RequestBody ArrayList<Video> videos) {
        logger.info("main thread " + Thread.currentThread().getName());

        val test = new VideoUpload(videos).runAll().done();
        // wait until all thread are finished then tell success,
        // Monitor Status of thread and send information for a failure

        return "success";
    }
}


class VideoUpload extends VideoFuture {
    private final Stream<Thread> videos;

    public VideoUpload(List<Video> videos) {
        this.videos = videos.stream()
                .map(item -> new Thread(new VideoFuture(item.video(), item.fileName(), item.type())));
    }

    public boolean done() {
        return !this.Status.containsValue(false);
    }

    public VideoUpload runAll() {
        this.videos.forEach(Thread::run);
        return this;
    }

}

class VideoFuture extends RunnableForDownload {
    private final Logger logger = (Logger) LoggerFactory.getLogger(VideoFuture.class);
    public Boolean allSucseeded;
    protected volatile ConcurrentHashMap<String, Boolean> Status;

    VideoFuture() {
    }

    VideoFuture(@NotNull String input, @NotNull String fileName, @NotNull FileExtension extension) {
        super(input, fileName, extension);
    }

    @Override
    public void run() {
        Status.put(this.id, false);
        logger.info("thread name {}", Thread.currentThread().getName());
        logger.info("ThreadForDownload {}", super.input);
        createFile();
        writeMethod();
        Status.put(this.id, true);
    }
}

class RunnableForDownload implements Runnable {
    private final Random random = new Random();
    private final Logger logger = (Logger) LoggerFactory.getLogger(RunnableForDownload.class);
    private String path = null;
    protected String input = null;
    protected final String id = Integer.toHexString(random.nextInt());

    RunnableForDownload() {
    }

    RunnableForDownload(@NotNull String input, @NotNull String fileName, @NotNull FileExtension extension) {
        this.input = input;
        this.path = FILEPATH + fileName + id + extension.getExtension();
    }

    @Override
    public void run() {
        // abstract
    }

    protected void createFile() {
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

    protected void writeMethod() {
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