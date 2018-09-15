package cz.zr.hackujstat2018;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import cz.zr.hackujstat2018.Filter.Rules;

/**
 * Initial filter implementation for input CSV files.
 * 
 * @author ZRuzicka
 */
public class Filter {

    /** Types of validation rules. */
    enum Rules {
        NOT_EMPTY, APPEND
    }

    /* Debug and settings flags. */
    static boolean applyDemoProcessing = false;
    static boolean isInputDebug = false;

    String[] targetColumns = new String[] { "id", "hodnota", "rok", "datum", "okres_kod", "okres", "kraj_txt",
            "pohlavi", "obcanstvi_kod", "obcanstvi", "vek_txt", "vek_index" };
    String[] sourceColumns = new String[] { "idhod", "hodnota", "rok", "rok", "vuzemi_kod", "vuzemi_txt", "kraj_txt",
            "pohlavi_txt", "stobcan_kod", "stobcan_txt", "vek_txt", "vek_kod" };
    Rules[] targetColumnsRules = new Rules[] { null, null, null, Rules.APPEND, null, null, Rules.NOT_EMPTY,
            Rules.NOT_EMPTY, Rules.NOT_EMPTY, Rules.NOT_EMPTY, Rules.NOT_EMPTY, Rules.NOT_EMPTY };
    Columns outputColumns = new Columns(targetColumns, sourceColumns, targetColumnsRules);

    public static void main(String[] args) {
        long started = System.currentTimeMillis();
        Filter filter = new Filter();
        try {
            if (args.length < 1) {
                System.err.println("Input CSV file must be specified via argument.");
                System.exit(0);
            }
            String inputFile = args[0];
            System.out.println("Input file: " + inputFile);
            File f = new File(inputFile);
            BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));

            Writer fstream = new OutputStreamWriter(new FileOutputStream(inputFile + ".filtered.csv"),
                    StandardCharsets.UTF_8);
            BufferedWriter bw = new BufferedWriter(fstream);

            bw.write(filter.outputColumns.getTargetColumnsString() + "\n");

            String[] header = null;
            String readLine = "";
            int i = 1;
            while ((readLine = b.readLine()) != null) {
                if (i == 1) { // Defines header columns.
                    header = readLine.replaceAll("\"", "").split(",");
                    filter.outputColumns.initSourceColumnIndices(header);
                    filter.outputColumns.columnsDebugInfo(header);
                    i++;
                    continue;
                }
                String[] columns = readLine.split(",");
                String output = filter.outputColumns.transformInputData(columns);
                if (isInputDebug) {
                    System.out.println("(" + i + ") input:" + readLine); // TMP output.
                    System.out.println("(" + i + ") output:" + output); // TMP output.
                }
                if (output != null) {
                    bw.write(output + "\n");
                }

                i++;
                if (applyDemoProcessing && i == 10) {
                    // Demo mode processes only a small subset of data instead of whole file.
                    break;
                }
            }

            b.close();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        long finished = System.currentTimeMillis();
        System.out.println("Finished in " + (finished - started) + " ms.");
    }
}

/**
 * Carries columns transformation definition.
 */
class Columns {

    final String[] targetColumns;
    final String[] correspondingSourceColumns;
    final int[] sourceColumnIndices;
    final Rules[] targetColumnsRules;

    public Columns(String[] targetColumns, String[] correspondingSourceColumns, Rules[] targetColumnsRules) {
        super();
        this.targetColumns = targetColumns;
        this.correspondingSourceColumns = correspondingSourceColumns;
        this.targetColumnsRules = targetColumnsRules;
        sourceColumnIndices = new int[targetColumns.length];
    }

    public void initSourceColumnIndices(String[] inputHeader) {
        int columnIndex = 0;
        for (String searchedSourceColumn : correspondingSourceColumns) {
            for (int i = 0; i < inputHeader.length; i++) {
                String inputColumn = inputHeader[i];
                if (inputColumn.equals(searchedSourceColumn)) {
                    sourceColumnIndices[columnIndex] = i;
                }
            }
            columnIndex++;
        }
    }

    public void columnsDebugInfo(String[] header) {
        System.out.println("targetColumns: " + Arrays.toString(targetColumns));
        System.out.println("correspondingSourceColumns: " + Arrays.toString(correspondingSourceColumns));
        System.out.println("sourceColumnIndices: " + Arrays.toString(sourceColumnIndices));
        System.out.println("full input header: " + Arrays.toString(header));
    }

    public String transformInputData(String[] inputValues) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sourceColumnIndices.length; i++) {
            int columnIndex = sourceColumnIndices[i];
            if (columnIndex < inputValues.length) {
                String columnValue = inputValues[columnIndex].trim();
                if (Rules.NOT_EMPTY.equals(targetColumnsRules[i])
                        && (columnValue.length() == 0 || "\"\"".equals(columnValue))) {
                    // Column data does not match validation rule constraint. -> Whole row should be skipped.
                    return null;
                } else {
                    sb.append(columnValue + ",");
                }
            }
        }
        return sb.toString();
    }

    public String getTargetColumnsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < targetColumns.length; i++) {
            sb.append("\"" + targetColumns[i] + "\"");
            if (i < targetColumns.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
