package com.firoz.rafsan.ftptv;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class FTPLibTest {

    String fm_test_url = "http://fmftp.net/data/disk-1/movies/hindidub/777%20Charlie%20%282022%29/";
    String tp_test_url = "http://ftp.timepassbd.live/timepassbd-data/ftp3/TV_SERIES/ANIMATION_TV_SHOW/C/Cautious%20Hero%3A%20The%20Hero%20Is%20Overpowered%20but%20Overly%20Cautious%20TV%20Series%20%282019%E2%80%93%20%29/";

    @Test
    public void fmftp_works() throws Exception {
        assertEquals(FTPLib.listDir(fm_test_url, true).size(), 1);
    }

    @Test
    public void timepassbd_works() throws Exception {
        assertEquals(FTPLib.listDir(tp_test_url, false).size(), 1);
    }

    @Test
    public void yes() {
        assertEquals(FTPLib.o(), "yes");
    }
}