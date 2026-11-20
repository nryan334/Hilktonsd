package net.frozenorb.foxtrot.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.frozenorb.foxtrot.Foxtrot;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class TebexAPI {

    public static String createGiftCard(double amount, String note) {
        String secret = Foxtrot.getInstance().getConfig().getString("tebexSecret", "");
        
        if (secret.isEmpty() || secret.equals("YOUR_SECRET_KEY_HERE")) {
            System.out.println("[TebexAPI] Failed to create giftcard: 'tebexSecret' is missing in config.yml.");
            return null;
        }

        try {
            URL url = new URL("https://plugin.tebex.io/gift-cards");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-Tebex-Secret", secret);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{\"amount\": " + amount + ", \"note\": \"" + note + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                try (Scanner scanner = new Scanner(conn.getInputStream(), "utf-8")) {
                    String responseBody = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                    JsonObject jsonObject = new JsonParser().parse(responseBody).getAsJsonObject();
                    if (jsonObject.has("data")) {
                        JsonObject data = jsonObject.getAsJsonObject("data");
                        if (data.has("code")) {
                            return data.get("code").getAsString();
                        }
                    }
                }
            } else {
                System.out.println("[TebexAPI] Failed to create giftcard. Response Code: " + responseCode);
                try (Scanner scanner = new Scanner(conn.getErrorStream(), "utf-8")) {
                    String responseBody = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                    System.out.println("[TebexAPI] Error Body: " + responseBody);
                }
            }
        } catch (Exception e) {
            System.out.println("[TebexAPI] Exception while creating gift card:");
            e.printStackTrace();
        }

        return null;
    }
}
