import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class MainClass {

    // Key format: 1983-XXXXXXXXX-XXXX-XXXXXXX

    private static final Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[A-Fa-f])[0-9A-Fa-f]+$");
    private static final Scanner sc = new Scanner(System.in);
    private static final List<String> keysToProcess = new ArrayList<>();

    private static File outFile;

    private static boolean isCodeValid(String str) {
        return pattern.matcher(str).matches();
    }

    private static boolean isKeyInvalid(String entry, int index, String key, boolean beginning) {
        if (beginning) {
            if (!key.startsWith("1983"))
                return true;

            String[] _spl = key.split("-", 4);
            return _spl.length != 4;
        } else {
            try {
                if (!key.startsWith("1983"))
                    return true;

                if (!isCodeValid(entry))
                    return true;

                long l;
                if (index == 0)
                    l = Long.parseLong(entry);
                else
                    l = Long.parseLong(entry, 16);

                return switch (index) {
                    case 0 -> l != 1983;
                    case 1 -> l % 27 != 0;
                    case 2 -> l % 16 != 0;
                    case 3 -> l % 21 != 0;
                    default -> throw new IndexOutOfBoundsException("Index got thrown out of bounds; received " + index);
                };
            } catch (NumberFormatException ignore) {
                return true;
            }
        }
    }

    public static void main(String[] args) {
        if (args != null && args.length >= 1) {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];

                if (arg.startsWith("-i")) {
                    if (!keysToProcess.isEmpty())
                        keysToProcess.clear();

                    try {
                        FileInputStream fis = new FileInputStream(arg.substring(2));
                        byte[] buff = new byte[1024];
                        int len;
                        StringBuilder str = new StringBuilder();

                        while ((len = fis.read(buff, 0, 1024)) != -1)
                            str.append(new String(buff, 0, len));

                        fis.close();

                        List<String> keysList = new ArrayList<>();
                        for (String key : str.toString().split("\n")) {
                            if (key.trim().isEmpty())
                                continue;

                            if (keysList.contains(key))
                                continue;

                            keysList.add(key);
                        }

                        keysToProcess.addAll(keysList);
                    } catch (IOException ex) {
                        System.err.println("While opening " + arg.substring(2) + " (arg = " + (i + 1) + "), an error occurred.");
                        ex.printStackTrace(System.err);
                        System.exit(1);
                    }
                }

                if (arg.startsWith("-o"))
                    outFile = new File(arg.substring(2));
            }
        }

        if (keysToProcess.isEmpty()) {
            System.out.print("Enter your key: ");
            String key = sc.nextLine();

            if (isKeyInvalid(null, 0, key, true)) {
                System.err.println("Invalid key!");
                System.exit(1);
            }

            String[] spl = key.split("-");
            if (spl.length != 4) {
                System.err.println("Invalid key!");
                System.exit(1);
            }

            for (int i = 0; i < spl.length; i++) {
                if (i == 0)
                    continue;

                if (isKeyInvalid(spl[i], i, key, false)) {
                    System.err.println("Invalid key!");
                    System.exit(1);
                }
            }

            System.out.println("Key valid!");
            return;
        }

        List<KeyProcess> keyProcessList = new ArrayList<>();
        for (String key : keysToProcess) {
            if (key.trim().isEmpty())
                continue;

            String[] splits = key.split("-");
            if (splits.length != 4) {
                keyProcessList.add(new KeyProcess(key, false));
                continue;
            }

            try {
                long e0 = Long.parseLong(splits[0]),
                        e1 = Long.parseLong(splits[1], 16),
                        e2 = Long.parseLong(splits[2], 16),
                        e3 = Long.parseLong(splits[3], 16);

                keyProcessList.add(new KeyProcess(key,
                        e0 == 1983 &&
                        e1 % 27 == 0 &&
                        e2 % 16 == 0 &&
                        e3 % 21 == 0));
            } catch (NumberFormatException ignore) {
            }
        }

        int validProcesses = 0, invalidProcesses = 0;

        for (KeyProcess process : keyProcessList) {
            if (process.valid())
                validProcesses++;
            else
                invalidProcesses++;
        }

        System.out.println("Processed keys: " + (validProcesses + invalidProcesses));
        System.out.println("Valid keys: " + validProcesses);
        System.out.println("Invalid keys: " + invalidProcesses);

        if (outFile != null) {
            try {
                FileOutputStream fos = new FileOutputStream(outFile);

                for (KeyProcess process : keyProcessList) {
                    if (process.valid()) {
                        fos.write(process.key().getBytes(StandardCharsets.UTF_8));
                        fos.write("\n".getBytes(StandardCharsets.UTF_8));
                    }
                }

                fos.close();
            } catch (IOException ex) {
                System.err.println("Could not open the output file");
                ex.printStackTrace(System.err);
            }
        }
    }

    record KeyProcess(String key, boolean valid) {
    }

}
