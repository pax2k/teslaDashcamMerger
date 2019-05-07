package no.koteng.dashcam.model;

public class CombinedVideo {
    private Video left;
    private Video middle;
    private Video right;
    private String baseName;

    public CombinedVideo(String baseName, Video left, Video middle, Video right) {
        this.baseName = baseName;
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public Video getLeft() {
        return left;
    }

    public void setLeft(Video left) {
        this.left = left;
    }

    public Video getMiddle() {
        return middle;
    }

    public void setMiddle(Video middle) {
        this.middle = middle;
    }

    public Video getRight() {
        return right;
    }

    public void setRight(Video right) {
        this.right = right;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }
}
