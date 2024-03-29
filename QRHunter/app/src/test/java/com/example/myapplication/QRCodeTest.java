package com.example.myapplication;

import com.google.common.hash.Hashing;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class QRCodeTest {

    private String codeContents1 = "BFG5DG154";
    private String codeContents2 = "G3G5DG154";

    /**
     * Creates a mock instance of the QR code class
     * @param codeContents String representation of the QR Code contents
     * @return QRCode instance to be used in testing
     */
    public QRCode createMockQRCode(String codeContents) {
        QRCode mockQR = new QRCode(codeContents, null);
        return mockQR;
    }

    /**
     * Tests to see if the QR Code makes unique names for 2 qr codes
     */
    @Test
    public void setNameTest() {
        QRCode qrCode1 = createMockQRCode(codeContents1);
        QRCode qrCode2 = createMockQRCode(codeContents2);
        Assert.assertNotEquals(qrCode1.getName(), qrCode2.getName());
    }

    /**
     * Tests to see if QR code properly calculates unique scores
     */
    @Test
    public void setScoreTest() {
        QRCode qrCode1 = createMockQRCode(codeContents1);
        QRCode qrCode2 = createMockQRCode(codeContents2);
        Assert.assertNotEquals(qrCode1.getScore(), qrCode2.getScore());
    }

    /**
     * Tests to see if QR code properly calculates score
     */
    @Test
    public void getScoreTest() {
        QRCode qrCode = createMockQRCode(codeContents1);
        Assert.assertEquals(qrCode.getScore(), 38);
    }

    /**
     * Tests to see if QR code properly returns hash
     */
    @Test
    public void getHash() {
        QRCode qrCode = createMockQRCode(codeContents1);
        Assert.assertEquals(qrCode.getHash(), Hashing.sha256()
                .hashString(codeContents1, StandardCharsets.UTF_8)
                .toString());
    }

    /**
     * Tests to see if QR code properly returns location
     */
    @Test
    public void getLocation() {
        QRCode qrCode = createMockQRCode(codeContents1);
        Assert.assertEquals(qrCode.getLocation(), null);
    }

}
