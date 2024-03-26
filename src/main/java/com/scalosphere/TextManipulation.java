package com.scalosphere;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextManipulation {
    static String pattern = "[(\\d+(?:\\.\\w+)?)]";
    static Pattern regex = Pattern.compile(pattern);
    public static void main(String[] args) {


        String text1 = "ಸರ್ವಲೋಕಗಳ ಪರಿಪಾಲಕನಾದ[1] ಅಲ್ಲಾಹನಿಗೆ ಸರ್ವಸ್ತುತಿ.[2]";
        String text2 = "[1] ಇಲ್ಲಿ 'ರಬ್ಬ್' ಅನ್ನು ಪರಿಪಾಲಕ ಎಂದು ಅನುವಾದ ಮಾಡಲಾಗಿದೆ. \n[2] 'ಅಲ್-ಹಮ್ದ್' (ಸರ್ವಸ್ತುತಿ) ಎಂಬ ಪದದಲ್ಲಿ ಸ್ತುತಿ, ಪ್ರಶಂಸೆ, ಹೊಗಳಿಕೆಗಳೆಲ್ಲವೂ ಒಳಗೊಳ್ಳುತ್ತದೆ.";

        Matcher matcher = regex.matcher(text1);
        while (matcher.find()) {
            String footnoteNumber = matcher.group(1);
            System.out.println(footnoteNumber);
        }

    }
}
