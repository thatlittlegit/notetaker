package somethingrandom.notetaker.app;

import okhttp3.OkHttpClient;
import somethingrandom.notetaker.gcalendar.Client;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello, world! " + addOne(5));

        System.out.print("Client is ");
        OkHttpClient okHttpClient = new OkHttpClient();
        System.out.println("created!");

        Client.doTheDemo(okHttpClient);
        System.out.println("all done!");
    }

    public static int addOne(int x) {
        return x + 1;
    }
}
