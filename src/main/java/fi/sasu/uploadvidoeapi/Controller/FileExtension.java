package fi.sasu.uploadvidoeapi.Controller;

public enum FileExtension {
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
