package fi.sasu.uploadvidoeapi;

import fi.sasu.uploadvidoeapi.Controller.Video;
import fi.sasu.uploadvidoeapi.Controller.VideoUpload;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import static fi.sasu.uploadvidoeapi.Controller.FileExtension.MP4;

@SpringBootTest
class DownloadVidoeApiApplicationTests {

    String video =  Files.readAllLines(Path.of("src/test/resources/video.txt")).get(0);

    DownloadVidoeApiApplicationTests() throws IOException {
    }

    @Test
    void testmultithreading() throws IOException {
        ArrayList<Video> videos = new ArrayList<>(Arrays.asList(
                new Video(video,"1" ,MP4),
                new Video(video,"2"  ,MP4),
                new Video(video,"3"  ,MP4),
                new Video(video,"4"  ,MP4),
                new Video(video,"5"  ,MP4),
                new Video(video,"6"  ,MP4),
                new Video(video,"7"  ,MP4),
                new Video(video,"8"  ,MP4)
                ));
         new VideoUpload(videos).runAll().done();

    }

}
