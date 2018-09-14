package cz.zr.hackujstat2018;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Initial filter implementation for input CSV files.
 * 
 * @author ZRuzicka
 */
public class Filter {

    static Columns outputColumns = new Columns(new String[] { "id", "hodnota", "rok", "datum", "okres_kod", "okres",
            "kraj_txt", "pohlavi", "obcanstvi_kod", "obcanstvi", "vek_txt", "vek_index" });

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

            FileWriter fw = new FileWriter(inputFile + ".filtered.csv", false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(outputColumns.getColumnsString() + "\n");

            String[] header = null;
            String readLine = "";
            int i = 1;
            while ((readLine = b.readLine()) != null) {
                String[] columns = readLine.split(",");
                if (i == 1) { // Defines header columns.
                    header = columns;
                } else {
                    bw.write(readLine + "\n");
                }
                System.out.println("(" + i + ") input:" + readLine); // TMP output.

                i++;
                if (i == 10) { // TMP condition to break whole files processing.
                    break;
                }
            }
            System.out.println(Arrays.toString(header)); // TMP header output.

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

/**
 * Carries columns definition.
 */
class Columns {

    final String[] columns;

    public Columns(String[] columns) {
        super();
        this.columns = columns;
    }

    public String[] getColumns() {
        return columns;
    }

    public String getColumnsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            sb.append("\"" + columns[i] + "\"");
            if (i < columns.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
