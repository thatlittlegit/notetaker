package somethingrandom.notetaker.gcalendar;

import okhttp3.*;
import okio.ByteString;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Scanner;

// https://developers.google.com/identity/protocols/oauth2/native-app

public class Client {
    private static final String OAUTH_CLIENT_ID = "1094948025113-ej3dvu6bs0ctmsfftd8p677o9mkv16m0.apps.googleusercontent.com";
    private int usedPort = 0;

    public static void doTheDemo(OkHttpClient client) throws IOException {
        Client c = new Client();
        String verifier = c.getCodeVerifier();
        System.out.println("got verifier: " + verifier);
        String code = c.getAuthorizationCode(verifier);
        System.out.println("got authorization code: " + code);
        String token = c.getAuthorizationToken(client, code, verifier);
        System.out.println("got authorization token: " + token);

        c.addEvent(client, token, "It works on October 2nd");
    }

    private String getCodeVerifier() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);

        for (int i = 0; i < bytes.length; i++) {
            boolean ok;
            while (true) {
                if (bytes[i] < 0) {
                    // definitely not valid, at least make it positive
                    bytes[i] *= -1;
                }

                ok = Character.isAlphabetic(bytes[i]);
                ok |= Character.isDigit(bytes[i]);
                ok |= bytes[i] == '-';
                ok |= bytes[i] == '.';
                ok |= bytes[i] == '_';
                ok |= bytes[i] == '~';

                if (ok) {
                    break;
                }

                bytes[i] = (byte) random.nextInt();
            }
        }

        return ByteString.of(bytes).string(StandardCharsets.US_ASCII);
    }

    private String getAuthorizationCode(String verifier) throws IOException {
        // This code is PERFECT and will never need refactoring.
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(0));

        usedPort = server.getLocalPort();

        String uri = "https://accounts.google.com/o/oauth2/v2/auth";
        uri += "?client_id=" + OAUTH_CLIENT_ID;
        uri += "&redirect_uri=http://127.0.0.1:" + usedPort;
        uri += "&scope=https://www.googleapis.com/auth/calendar";
        uri += "&code_challenge=" + verifier;
        uri += "&code_challenge_method=plain"; // TODO we should do S256
        uri += "&response_type=code";

        URI parsed;
        try {
            parsed = new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        Desktop.getDesktop().browse(parsed);

        while (true) {
            Socket socket = server.accept();
            InputStream stream = socket.getInputStream();
            Scanner sc = new Scanner(stream);
            String line = sc.nextLine();

            HttpUrl provided = HttpUrl.parse("http://127.0.0.1/" + line.split("\\s")[1]);
            assert provided != null;

            String code = provided.queryParameter("code");
            if (code != null) {
                socket.getOutputStream().write("HTTP/1.0 200 OK\r\nContent-Type: text/plain; charset=utf-8\r\n\r\nAll done! You can go back to Notetaker now.".getBytes());
                socket.close();
                server.close();
                return code;
            }

            socket.getOutputStream().write("HTTP/1.0 400 Bad Request\r\nContent-Type: text/plain; charset=utf-8\\r\\n\\r\\nThe request wasn't valid; this is probably our bug.".getBytes());
            socket.close();
        }
    }

    private String getAuthorizationToken(OkHttpClient client, String code, String verifier) throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("oauth2.googleapis.com")
                .addPathSegment("token")
                .build();

        RequestBody body = new FormBody.Builder()
                .add("client_id", OAUTH_CLIENT_ID)
                .add("client_secret", System.getenv("OAUTH_CLIENT_SECRET"))
                .add("code", code)
                .add("grant_type", "authorization_code")
                .add("code_verifier", verifier)
                .add("redirect_uri", "http://127.0.0.1:" + usedPort)
                .build();

        Request req = new Request.Builder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(req).execute()) {
            JSONObject parsed = new JSONObject(response.body().string());

            return parsed.getString("access_token");
        }
    }

    private void addEvent(OkHttpClient client, String token, String eventName) throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("www.googleapis.com")
                .addPathSegments("calendar/v3/calendars/primary/events/quickAdd")
                .addQueryParameter("text", eventName)
                .build();

        Request req = new Request.Builder()
                .post(RequestBody.create(null, ""))
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response resp = client.newCall(req).execute()) {
            System.out.println("event creation gave " + resp.code() + ": " + resp.message());
            System.out.println(resp.body().string());
        }
    }
}
