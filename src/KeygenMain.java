import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class KeygenMain {

    private static final int[] divisors = {27, 16, 21},
            lengths = {9, 4, 7};

    private static final Random rnd = new Random();
    private static final Scanner sc = new Scanner(System.in);

    private static String mkHexNumber(int length, int divisor) {
        boolean valid = false;
        String number = "";

        while (!valid) {
            StringBuilder ss = new StringBuilder();
            boolean hasDigit = false, hasAlpha = false;

            for (int i = 0; i < length; i++) {
                int digit = rnd.nextInt(16);
                if (digit < 10) hasDigit = true;
                else hasAlpha = true;

                ss.append(Integer.toHexString(digit));
            }

            number = ss.toString();
            long l = Long.parseLong(number, 16);
            if (hasDigit && hasAlpha && l % divisor == 0)
                valid = true;
        }

        return number;
    }

    private static String mkKey() {
        String[] parts = new String[3];
        for (int i = 0; i < divisors.length; i++)
            parts[i] = mkHexNumber(lengths[i], divisors[i]);

        return String.format("1983-%s-%s-%s", parts[0], parts[1], parts[2]);
    }

    private static long keys = 10; // Number of keys to generate for each thread
    private static File outFile = null;
    private static FileOutputStream fos;
    private static final List<Thread> completedThreads = new ArrayList<>();

    private static void ktg(long threads) {
        long keysToGen = keys * threads;
        if (keysToGen > Integer.MAX_VALUE) {
            System.err.println("Cannot go beyond the Integer maximum value");
            System.err.printf("Given %d, maximum Integer value is %d\n", keysToGen, Integer.MAX_VALUE);
            System.err.println("Cannot process values");
            System.exit(1);
        } else if (keysToGen > 10_000) {
            System.err.println("Generating keys above 10,000 might overload the system, causing performance to drop.");
            System.err.println("You have currently set to " + keysToGen);
            System.err.println("If you wish to continue with this selection, enter \"Continue\" (case irrelevant), or type in a smaller amount of keys per thread.");
            System.err.println("Entering \"Exit\" will close this App.");
            System.err.println();
            System.err.print("Your selection: ");

            String choice = sc.nextLine().trim();
            if (choice.isEmpty())
                return;

            if (choice.contains(" "))
                choice = choice.replaceAll(" ", "");

            if (choice.equalsIgnoreCase("Exit") || choice.equalsIgnoreCase("Quit"))
                System.exit(0);

            if (choice.equalsIgnoreCase("Continue"))
                return;

            if (choice.contains(","))
                choice = choice.replaceAll(",", "");

            if (choice.contains("."))
                choice = choice.replaceAll("\\.", "");

            if (choice.contains("_"))
                choice = choice.replaceAll("_", "");

            try {
                long ignore = Long.parseLong(choice);
                ktg(threads);
            } catch (NumberFormatException ignore) {
                System.err.println("Invalid number!");
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
        long threads = 1; // One thread for * keys, where '*' is the amount of keys

        if (args != null && args.length >= 1) {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];

                if (arg.startsWith("-k")) {
                    if (arg.contains("."))
                        arg = arg.replaceAll("\\.", "");

                    if (arg.contains(","))
                        arg = arg.replaceAll(",", "");

                    if (arg.contains("_"))
                        arg = arg.replaceAll("_", "");

                    try {
                        keys = Long.parseLong(arg.substring(2));
                    } catch (NumberFormatException ignore) {
                        System.err.println("In argument " + (i + 1) + ", received " + arg + "; should receive -k10 with a valid number");
                        System.exit(1);
                    }
                }

                if (arg.startsWith("-t")) {
                    if (arg.contains("."))
                        arg = arg.replaceAll("\\.", "");

                    if (arg.contains(","))
                        arg = arg.replaceAll(",", "");

                    if (arg.contains("_"))
                        arg = arg.replaceAll("_", "");

                    try {
                        threads = Long.parseLong(arg.substring(2));
                    } catch (NumberFormatException ignore) {
                        System.err.println("In argument " + (i + 1) + ", received " + arg + "; should receive -t1 with a valid number");
                        System.exit(1);
                    }
                }

                if (arg.startsWith("-o"))
                    outFile = new File(arg.substring(2));
            }
        }

        if (threads > 20000) {
            System.err.println("Overloading the Thread count might lead to the program crashing.");
            System.err.println("Setting the Thread count down to 100");

            threads = 100;
        }

        ktg(threads);

        for (int thIndex = 0; thIndex < threads; thIndex++) {
            Thread th = new Thread(() -> {
                for (int k = 0; k < keys; k++) {
                    String key = mkKey();
                    System.out.println(key);

                    try {
                        if (outFile != null) {
                            if (fos == null)
                                fos = new FileOutputStream(outFile);

                            fos.write(key.getBytes(StandardCharsets.UTF_8));
                            fos.write("\n".getBytes(StandardCharsets.UTF_8));
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace(System.err);
                    }
                }

                completedThreads.add(Thread.currentThread());
            });
            th.setName("KeyGen Thread " + (thIndex + 1));
            th.start();
        }

        if (completedThreads.size() == threads) {
            try {
                fos.close();
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

}
