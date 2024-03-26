package com.scalosphere;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

    //TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
    // click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class MergeFootnoteWithVerses {
    static String pattern = "<sup>(\\d+(?:\\.\\w+)?)</sup>";
    static Pattern regex = Pattern.compile(pattern);
    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String fileName = "MergeFootnoteWIthVerses.sql"; // Name of the file to write
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter =null;
        // Create a BufferedWriter object to efficiently write characters to the file

        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName), StandardCharsets.UTF_8));
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:quran.kan.hamzah.db");
            System.out.println("Opened database successfully");

            statement = connection.createStatement();

            resultSet = statement.executeQuery("SELECT * FROM verses_content");
            while (resultSet.next()) {
                int docid = resultSet.getInt("docid");
                //System.out.println("docid----" + docid);
                String c2text = resultSet.getString("c2text");
                String c3footnotes = resultSet.getString("c3footnotes");

                Matcher matcher = regex.matcher(c2text);

                StringBuffer replacedText = new StringBuffer();
                while (matcher.find()) {
                    String footnoteNumber = matcher.group(1);
                    String replacement = getReplacement(c3footnotes, footnoteNumber);
                    matcher.appendReplacement(replacedText, replacement);
                }
                matcher.appendTail(replacedText);




                String text = replacedText.toString();

                if(text.contains("'")) {
                    text = text.replaceAll("'", "''");
                    //System.out.println("after udpate "+text);
                }

                bufferedWriter.write("UPDATE verses_content SET c2text= '" + text
                        + "' WHERE docid = " + docid + "; ");
                bufferedWriter.newLine(); // Move to the next line
            }

            // statement.executeUpdate(data.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (resultSet != null)
                    resultSet.close();
                if (statement != null)
                    statement.close();
                if (connection != null)
                    connection.close();
                if (bufferedWriter != null)
                    bufferedWriter.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to get replacement content for a given footnote number
    private static String getReplacement(String c3footnotes, String footnoteNumber) {
        System.out.println("c3footnotes--->"+c3footnotes+"---footnoteNumber--"+footnoteNumber);

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
