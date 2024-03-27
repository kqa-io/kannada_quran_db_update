package com.scalosphere;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.apache.poi.ss.usermodel.*;

public class ReadKannadaTransliterationEXCEL {
    public static void main(String[] args) {
        String fileName = "kannada_transliteration_google.sql"; // Name of the file to write
        BufferedWriter bufferedWriter = null;

        try {

            bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName), StandardCharsets.UTF_8));

            // Open the Excel file
            FileInputStream fis = new FileInputStream(new File("kannada_transliteration.xlsx"));

            // Create a workbook instance
            Workbook workbook = WorkbookFactory.create(fis);

            // Get the first sheet from the workbook
            Sheet sheet = workbook.getSheetAt(0);
            // Skip first row flag
            boolean skipFirstRow = true;

            // Iterate through each row of the sheet
            for (Row row : sheet) {
                // Iterate through each cell of the row

                if (row.getRowNum() == 0) {
                    continue;
                }

                int surah = (int) row.getCell(0).getNumericCellValue();
                int ayah = (int) row.getCell(1).getNumericCellValue();

                if (row.getCell(3) == null) {
                    continue;
                }

                String kannadaText = row.getCell(3).getStringCellValue();
                bufferedWriter.write("UPDATE verses_content SET c2text= '" + escapeQuotes(kannadaText)
                        + "' WHERE c0sura = " + surah + " and c1ayah = " + ayah + "; ");
                bufferedWriter.newLine(); // Move to the next line

                System.out.println("kannadaText------->"+kannadaText);
            }

            // Close the workbook and file input stream
            workbook.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null)
                    bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String escapeQuotes(String text) {
        return text.replace("'", "''");
    }
}
