package no.koteng.dashcam.model;

import java.io.File;

public class Video {

    private File acualFile;
    private Position position;

    public Video(File acualFile, Position position) {
        this.acualFile = acualFile;
        this.position = position;
    }

    public File getAcualFile() {
        return acualFile;
    }

    public void setAcualFile(File acualFile) {
        this.acualFile = acualFile;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
