package no.solg.pocketcalendar;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 *
 */
@Command(name = "pocketcalendar", mixinStandardHelpOptions = true, description = "Generate a single-page foldable pocket calendar")
public class App implements Callable<Integer> {

    @Parameters(index = "0", description = "PDF filename")
    private File file;

    @Option(names = { "-m", "--month" }, description = "Start month, e.g. 2020-01")
    private String startMonth = null;

    @Option(names = { "-c", "--calibrate" }, description = "Print calibration page")
    private boolean calibrate = false;

    @Option(names = { "-p", "--parameters" }, description = "Calibration parameters top,right,bottom,left e.g 16,16,16,16")
    private String calibrationParameters = null;

    public static void main(String[] args) throws Exception {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    public Integer call() throws Exception {
        if (calibrate) {
            CalibrationPage.createPage(file.getAbsolutePath());
        } else {
            Calendar cal;
            if (startMonth != null) {
                Pattern p = Pattern.compile("(\\d+)-(\\d+)");
                Matcher m = p.matcher(startMonth);
                if (m.matches()) {
                    int year = Integer.parseInt(m.group(1));
                    int month = Integer.parseInt(m.group(2));
                    cal = new GregorianCalendar(year, month - 1, 1);
                } else {
                    System.err.println("Invalid start month");
                    return 1;
                }
            } else {
                cal = new GregorianCalendar();
            }
            MarginBox marginBox = null;
            if (calibrationParameters != null) {
                Pattern p = Pattern.compile("([\\d\\.]+),([\\d\\.]+),([\\d\\.]+),([\\d\\.]+)");
                Matcher m = p.matcher(calibrationParameters);
                if (m.matches()) {
                    final double top = Double.parseDouble(m.group(1));
                    final double right = Double.parseDouble(m.group(2));
                    final double bottom = Double.parseDouble(m.group(3));
                    final double left = Double.parseDouble(m.group(4));
                    // Use a single margin size in each dimension to make sure the page is centered.
                    final double horizontalMargin = Math.max(left, right);
                    final double verticalMargin = Math.max(top, bottom);
                    marginBox = new MarginBox(verticalMargin, horizontalMargin, verticalMargin, horizontalMargin);
                }
            }
            CalendarPage.createCalendar(file.getAbsolutePath(), cal, marginBox);
        }
        return 0;
    }
}
