package save;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SaveTransaction {
    public static void saveTransaction(String info, String name) {
        if (info != null) {
            final String filename = name + "Transactions.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));) {
                writer.write(info);
                writer.newLine();
            } catch (IOException e) {
                System.out.println(e.toString());
                System.out.println("Could not find file " + filename);
            }
        }
    }
}
