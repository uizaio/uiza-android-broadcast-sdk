package io.uiza.live.interfaces;

//

/**
 * Profile Encoding for Live
 * Codec H264
 */
public enum ProfileEncode {
    P1080(2800), //bandwidth 2800 Kbps
    P720(1400), // //bandwidth 1400 Kbps
    P360(600); // //bandwidth 600 Kbps

    private final int bandwidth;

    ProfileEncode(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public int getBandwidth() {
        return bandwidth;
    }
}
