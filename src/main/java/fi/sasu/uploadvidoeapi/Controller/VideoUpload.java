package fi.sasu.uploadvidoeapi.Controller;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Phaser;
import java.util.stream.Stream;

import static java.lang.System.exit;

public class VideoUpload {
    private Stream<Thread> videos = null;
    private final int POOL_SIZE = 8;
    public Boolean allSucceeded;
    protected static volatile ConcurrentHashMap<String, Boolean> Status = new ConcurrentHashMap<>();

    public VideoUpload(List<Video> videos) {
        if (videos.size() <= POOL_SIZE) {
            this.videos = videos.stream()
                    .map(item -> new Thread(new VideoFuture(item.video(), item.fileName(), item.type())));
        } else {
            System.out.println("pool is too big");
            exit(0);
        }

    }

    public boolean done() {
        return !Status.containsValue(false);
    }
    private Phaser phaser;
    public VideoUpload runAll() {

        // limit how many thread is started,
        this.videos.forEach(Thread::start);
        return this;
    }

}
