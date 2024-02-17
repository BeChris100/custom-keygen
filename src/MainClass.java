import java.util.Scanner;
import java.util.regex.Pattern;

public class MainClass {

    // Key format: 1983-XXXXXXXXX-XXXX-XXXXXXX

    private static final Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[A-Fa-f])[0-9A-Fa-f]+$");

    private static boolean isCodeValid(String str) {
        return pattern.matcher(str).matches();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your key: ");
        String key = sc.nextLine();

        if (!key.startsWith("1983")) {
            System.err.println("Invalid key!");
            System.exit(1);
        }

        String[] spl = key.split("-", 4);
        if (spl.length != 4) {
            System.err.println("Invalid key!");
            System.exit(1);
        }

        for (int i = 1; i < spl.length; i++) {
            try {
                String entry = spl[i];

                if (!isCodeValid(entry)) {
                    System.err.println("Invalid key!");
                    System.exit(1);
                }

                long l = Long.parseLong(entry, 16);
                entry = String.valueOf(l);

                if (i == 1 && entry.length() != 9 && l % 27 != 0) {
                    System.err.println("Invalid key!");
                    System.exit(1);
                }

                if (i == 2 && entry.length() != 4 && l % 16 != 0) {
                    System.err.println("Invalid key!");
                    System.exit(1);
                }

                if (i == 3 && entry.length() != 7 && l % 21 != 0) {
                    System.err.println("Invalid key!");
                    System.exit(1);
                }
            } catch (NumberFormatException ex) {
                System.err.println("Invalid key!");
                ex.printStackTrace();
                System.exit(1);
            }
        }

        System.out.println("Key valid! Given key: " + key);
    }

}
