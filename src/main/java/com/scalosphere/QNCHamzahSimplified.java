package com.scalosphere;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QNCHamzahSimplified {
    static String pattern = "\\[(\\d+)(?:\\.\\w+)?\\]";
    static Pattern regex = Pattern.compile(pattern);

    public static void main(String[] args) {
        String fileName = "quran.kan.hamzah.simplified.sql"; // Name of the file to write
        BufferedWriter bufferedWriter = null;
        OkHttpClient client = new OkHttpClient();
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName), StandardCharsets.UTF_8));

            for (int surahNumber = 1; surahNumber <= 114; surahNumber++) {

                String responseBody = makeWSCall(surahNumber, client);
                if (responseBody != null) {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray resultArray = jsonResponse.getJSONArray("result");

                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject obj = resultArray.getJSONObject(i);
                        String translation = obj.getString("translation");
                        String footnotes = obj.getString("footnotes");
                        int docid = obj.getInt("id");

                        Matcher matcher = regex.matcher(translation);
                        StringBuffer replacedText = new StringBuffer();
                        while (matcher.find()) {
                            String footnoteNumber = matcher.group(1);
                            String replacement = getReplacement(footnotes, footnoteNumber);
                            matcher.appendReplacement(replacedText, replacement);
                        }
                        matcher.appendTail(replacedText);


                        String updatedTranslation = replacedText.toString();

                        // Update the c3footnotes column with the content between [ and ]
                        bufferedWriter.write("UPDATE verses_content SET c2text= '" + escapeQuotes(updatedTranslation)
                                + "' WHERE docid = " + docid + "; ");
                        bufferedWriter.newLine(); // Move to the next line

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (bufferedWriter != null)
                    bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String makeWSCall(int surahNumber, OkHttpClient client) throws IOException {
        String url = "https://quranenc.com/api/v1/translation/sura/kannada_hamza/" + surahNumber;

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute(); // Execute synchronously
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            return null;
        }

    }

    private static String escapeQuotes(String text) {
        return text.replace("'", "''");
    }

    private static String getReplacement(String c3footnotes, String footnoteNumber) {
        String pattern = "\\[" + Pattern.quote(footnoteNumber) + "\\]\\s*(.*?)\\s*(?=\\[\\d+(?:\\.\\w+)?\\]|$)";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(c3footnotes);
        if (matcher.find()) {
            return " [[" + matcher.group(1) + "]] ";
        } else {
            return "";
        }
    }

}
