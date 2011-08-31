package be.dieterdemeyer.metro.online.client;

import be.dieterdemeyer.metro.online.infrastructure.OnlineMetroDownloader;

public class DownloadClient {

    public static void main(String[] args) throws Exception {
        new OnlineMetroDownloader().execute();
    }

}
