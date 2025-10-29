package net.altofeather.ChASM;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        File extention = new File("test.chasm");
        Scanner extentionReader = new Scanner(extention);
        StringBuilder extentionData = new StringBuilder();
        while (extentionReader.hasNextLine()) extentionData.append(extentionReader.nextLine());
        extentionReader.close();

        ExtendableCompiler e = new ExtendableCompiler(extentionData.toString().toCharArray());

        File program = new File("test.soprano");
        Scanner programReader = new Scanner(program);
        StringBuilder programData = new StringBuilder();
        while (programReader.hasNextLine()) programData.append(programReader.nextLine());
        programReader.close();

        System.out.println(e.runCompiler(programData.toString()).toString());
    }
}