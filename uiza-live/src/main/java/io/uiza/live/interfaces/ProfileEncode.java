package io.uiza.live.interfaces;

//

/**
 * Profile Encoding for Live
 * Codec H264
 */
public enum ProfileEncode {
    P1080(2800 * 1024, 1920, 1080), //bandwidth 2800 Kbps
    P720(1400 * 1024, 1280, 720), // //bandwidth 1400 Kbps
    P360(600 * 1024, 640, 360); // //bandwidth 600 Kbps

    /**
     * H264 in kb.
     */
    private final int bitrate;
    /**
     * resolution in px.
     */
    private final int width;
    /**
     * resolution in px.
     */
    private final int height;

    ProfileEncode(int bitrate, int width, int height) {
        this.bitrate = bitrate;
        this.width = width;
        this.height = height;
    }

    public int getBitrate() {
        return bitrate;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}