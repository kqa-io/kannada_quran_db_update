package com.scalosphere;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
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

public class MoveFootnotesBackToFootnoteColumn {
    static String pattern = "\\[\\[(.*?)\\]\\]";
    static Pattern regex = Pattern.compile(pattern);

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String fileName = "MoveFootnotesBackToFootnoteColumn.sql"; // Name of the file to write
        BufferedWriter bufferedWriter = null;

        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName), StandardCharsets.UTF_8));
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:quran.kan.hamzah.db");
            System.out.println("Opened database successfully");

            statement = connection.createStatement();

            resultSet = statement.executeQuery("SELECT * FROM verses_content");
            int i= 0;
            while (resultSet.next()) {
                int docid = resultSet.getInt("docid");
                StringBuilder updatedC3footnotes = new StringBuilder();
                StringBuffer updatedC2text = new StringBuffer();
                String c2text = resultSet.getString("c2text");

                Matcher matcher = regex.matcher(c2text);

                while (matcher.find()) {
                    i++;

                   // update footnote
                    String contentToUpdate = matcher.group(1);
                    updatedC3footnotes.append("[").append(i).append("]").append(contentToUpdate);

                   //update verses
                    matcher.appendReplacement(updatedC2text, "<sup>" + i + "</sup>");
                }
                matcher.appendTail(updatedC2text);

                // Update the c3footnotes column with the content between [ and ]
                bufferedWriter.write("UPDATE verses_content SET c2text= '" + escapeQuotes(updatedC2text.toString())
                        + "' WHERE docid = " + docid + "; ");
                bufferedWriter.newLine(); // Move to the next line


                // Update the c3footnotes column with the content between [ and ]
                bufferedWriter.write("UPDATE verses_content SET c3footnotes= '" + escapeQuotes(updatedC3footnotes.toString())
                        + "' WHERE docid = " + docid + "; ");
                bufferedWriter.newLine(); // Move to the next line
            }

        } catch (SQLException | ClassNotFoundException | IOException e) {
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
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to escape single quotes in text
    private static String escapeQuotes(String text) {
        return text.replace("'", "''");
    }
}
