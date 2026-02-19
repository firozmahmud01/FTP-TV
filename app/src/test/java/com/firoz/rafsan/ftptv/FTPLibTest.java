package com.firoz.rafsan.ftptv;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class FTPLibTest {

    String fm_test_url = "http://fmftp.net/data/disk-1/movies/hindidub/777%20Charlie%20%282022%29/";
    String fm_test_url2 = "http://fmftp.net/data/disk-1/movies/hindidub/";
    String fm_test_url3 = "http://fmftp.net/data/disk-1/movies/hindidub/777%20Charlie%20%282022%29/";
    String tp_test_url = "http://ftp.timepassbd.live/timepassbd-data/ftp3/TV_SERIES/ANIMATION_TV_SHOW/C/Cautious%20Hero%3A%20The%20Hero%20Is%20Overpowered%20but%20Overly%20Cautious%20TV%20Series%20%282019%E2%80%93%20%29/";
    String tp_test_url2 = "http://ftp.timepassbd.live/timepassbd-data/ftp3/TV_SERIES/ANIMATION_TV_SHOW/C/Cautious%20Hero%3A%20The%20Hero%20Is%20Overpowered%20but%20Overly%20Cautious%20TV%20Series%20%282019%E2%80%93%20%29/S01/";
    String tp_test_url3 = "http://ftp.timepassbd.live/timepassbd-data/ftp4/MOVIES/INDIAN%20BANGLA/2025/Dhumketu%20%282025%29/";
    String tp_test_plot = "Megh is a married person, who works as manager of a tea estate in North Sikkim. In a certain crisis he became jobless and struggles to make ends problems of his life because there are not many options for livelihood around in the hills. He fights to bring back joy into his family's life.";
    String fm_test_plot = "Dharma is stuck in a rut with his negative and lonely lifestyle and spends each day in the comfort of his loneliness. A pup named Charlie who is naughty and energetic which is a complete contrast with the Dharma’s character enters his life and gives him a new perspective towards it.";
    String tp_test_image_url = "http://timepassbd.live/Admin/main/images/tt6376792/poster/o9Vu4M5KiA9EuWRBxd3Xn55oVXJ.jpg";
    String fm_test_image_url = "http://fmftp.net/content-images/movies/posters/qArPmXH0aWsT3SEtYl8XrU2Oz48.jpg";

    @Test
    public void fmftp_works() throws Exception {
        assertEquals(1, FTPLib.listDir(fm_test_url, true).size());
    }

    @Test
    public void timepassbd_works() throws Exception {
        assertEquals(1, FTPLib.listDir(tp_test_url, false).size());
    }

    @Test
    public void fm_gives_correct_item_1() throws Exception {
        FTPItem epstine = FTPLib.listDir(fm_test_url, true).get(0);
        assertEquals("777 Charlie (2022).mp4", epstine.getName());
        assertEquals(fm_test_url+"777%20Charlie%20%282022%29.mp4", epstine.getItemURL());
        assertFalse(epstine.isDir());
    }

    @Test
    public void fm_gives_correct_item_2() throws Exception {
        FTPItem epstine = FTPLib.listDir(fm_test_url2, true).get(0);
        assertEquals("2018 (2023)", epstine.getName());
        assertEquals(fm_test_url2+"2018%20%282023%29/", epstine.getItemURL());
        assertTrue(epstine.isDir());
    }

    @Test
    public void tp_gives_correct_item_1() throws Exception {
        FTPItem epstine = FTPLib.listDir(tp_test_url, false).get(0);
        assertEquals("S01", epstine.getName());
        assertEquals(tp_test_url+"S01/", epstine.getItemURL());
        assertTrue(epstine.isDir());
    }

    @Test
    public void tp_gives_correct_item_2() throws Exception {
        FTPItem epstine = FTPLib.listDir(tp_test_url2, false).get(0);
        assertEquals("Shinchou Yuusha Kono Yuusha ga Ore Tueee Kuse ni Shinchou Sugiru - 01 (BD 1280x720 x264 AAC).mp4", epstine.getName());
        assertEquals(tp_test_url2+"Shinchou%20Yuusha%20Kono%20Yuusha%20ga%20Ore%20Tueee%20Kuse%20ni%20Shinchou%20Sugiru%20-%2001%20%28BD%201280x720%20x264%20AAC%29.mp4", epstine.getItemURL());
        assertFalse(epstine.isDir());
    }

    @Test
    public void tp_gives_correct_metadata() throws Exception {
        FTPItem epstine = FTPLib.listDir(tp_test_url3, false).get(0);
        FTPMetadata ff = FTPLib.getMetaData(epstine, epstine.getName().substring(0,5), false);
        assertEquals(tp_test_image_url, ff.getImageURL());
        assertEquals(tp_test_plot, ff.getPlot());
        assertEquals("6.7 / 10", ff.getRating());
    }

    @Test
    public void fm_gives_correct_metadata() throws Exception {
        FTPItem epstine = FTPLib.listDir(fm_test_url3, true).get(0);
        FTPMetadata ff = FTPLib.getMetaData(epstine, epstine.getName().substring(0,5), true);
        assertEquals(fm_test_image_url, ff.getImageURL());
        assertEquals(fm_test_plot, ff.getPlot());
        assertEquals("7.6 / 10", ff.getRating());
    }
}