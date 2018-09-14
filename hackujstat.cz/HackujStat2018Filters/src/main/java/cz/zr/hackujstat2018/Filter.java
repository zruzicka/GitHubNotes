package cz.zr.hackujstat2018;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Initial filter implementation for input CSV files.
 * 
 * @author ZRuzicka
 */
public class Filter {
    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                System.err.println("Input CSV file must be specified via argument.");
                System.exit(0);
            }
            String inputFile = args[0];
            System.out.println("Input file: " + inputFile);
            File f = new File(inputFile);
            BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));

            String[] header = null;
            String readLine = "";
            int i = 1;
            while ((readLine = b.readLine()) != null) {
                String[] columns = readLine.split(",");
                if (i == 1) { // Defines header columns.
                    header = columns;
                }

                System.out.println("(" + i + ") input:" + readLine); // TMP output.

                i++;
                if (i == 10) { // TMP condition to break whole files processing.
                    break;
                }
            }
            System.out.println(Arrays.toString(header)); // TMP header output.

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
