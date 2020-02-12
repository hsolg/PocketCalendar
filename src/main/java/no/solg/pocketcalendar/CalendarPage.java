package no.solg.pocketcalendar;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.Calendar;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public class CalendarPage {
    private static final DateFormatSymbols dfs = new DateFormatSymbols();
    static final String monthNames[] = dfs.getMonths();

    public static void drawMonthCalendarTable(PdfContentByte cb, Calendar cal, int offsetX, int offsetY) throws DocumentException, IOException {
        final int firstDayOfWeek = 2; // monday
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - cal.getActualMinimum(Calendar.DAY_OF_MONTH) + 1;
        cal = (Calendar)cal.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDay = cal.get(Calendar.DAY_OF_WEEK);
        int x = ((firstDay - firstDayOfWeek) + 7) % 7;
        int y = 0;
        cb.beginText();
        cb.setFontAndSize(bf, 25);
        cb.showTextAligned(PdfContentByte.ALIGN_CENTER,
                String.format("%s %d", monthNames[cal.get(Calendar.MONTH)], cal.get(Calendar.YEAR)), offsetX + 140,
                offsetY - 20, 0);
        for (int i = 0; i < daysInMonth; i++) {
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, String.format(" %2d", i + 1), offsetX + (40 * x),
                    offsetY - 60 - (35 * y), 0);
            x++;
            if (x == 7) {
                x = 0;
                y++;
            }
        }
        cb.endText();
    }

    public static void drawMonthCalendarList(PdfContentByte cb, Calendar cal) throws DocumentException, IOException {
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        BaseFont bfi = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - cal.getActualMinimum(Calendar.DAY_OF_MONTH) + 1;
        cal = (Calendar)cal.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cb.beginText();
        cb.setFontAndSize(bf, 25);
        cb.showTextAligned(PdfContentByte.ALIGN_CENTER,
                String.format("%s %d", monthNames[cal.get(Calendar.MONTH)], cal.get(Calendar.YEAR)),
                PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() - 30, 0);
        cb.endText();
        final float lineHeight = (PageSize.A4.getHeight() - 40 - 10) / daysInMonth;
        for (int i = 0; i < daysInMonth; i++) {
            final float lineY = PageSize.A4.getHeight() - 40 - (lineHeight * (i + 1));
            cb.moveTo(40, lineY);
            cb.lineTo(PageSize.A4.getWidth() - 20, lineY);
            cb.stroke();
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if ((dayOfWeek == Calendar.SATURDAY) || (dayOfWeek == Calendar.SUNDAY)) {
                cb.setFontAndSize(bfi, 25);
            } else {
                cb.setFontAndSize(bf, 25);
            }
            cb.beginText();
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, String.format("%2d", i + 1), 20,
                    PageSize.A4.getHeight() - 40 - (lineHeight * (i + 1)), 0);
            cb.endText();
            cal.add(Calendar.DATE, 1);
        }
    }

    public static void drawMonthPage(PdfContentByte cb, Calendar cal, int offset) throws Exception {
        if (offset == 0) {
            drawMonthCalendarList(cb, cal);
        } else {
            Calendar monthCal = (Calendar) cal.clone();
            monthCal.add(Calendar.MONTH, offset);
            drawMonthCalendarList(cb, monthCal);
        }
    }

    public static void drawHalfYearPage(PdfContentByte cb, Calendar cal, int offset) throws DocumentException, IOException {
        Calendar monthCal = (Calendar) cal.clone();
        if (offset != 0) {
            monthCal.add(Calendar.MONTH, offset);
        }
        drawMonthCalendarTable(cb, monthCal, 20, (int) PageSize.A4.getHeight() - 20);
        monthCal.add(Calendar.MONTH, 1);
        drawMonthCalendarTable(cb, monthCal, (int) (PageSize.A4.getWidth() / 2) + 20,
                (int) PageSize.A4.getHeight() - 20);
        monthCal.add(Calendar.MONTH, 1);
        drawMonthCalendarTable(cb, monthCal, 20, (int) (PageSize.A4.getHeight() * (2f / 3f)) - 20);
        monthCal.add(Calendar.MONTH, 1);
        drawMonthCalendarTable(cb, monthCal, (int) (PageSize.A4.getWidth() / 2) + 20,
                (int) (PageSize.A4.getHeight() * (2f / 3f)) - 20);
        monthCal.add(Calendar.MONTH, 1);
        drawMonthCalendarTable(cb, monthCal, 20, (int) (PageSize.A4.getHeight() / 3) - 20);
        monthCal.add(Calendar.MONTH, 1);
        drawMonthCalendarTable(cb, monthCal, (int) (PageSize.A4.getWidth() / 2) + 20,
                (int) (PageSize.A4.getHeight() / 3) - 20);
    }

    public static void createCalendar(String filename, Calendar cal, MarginBox calibrationParameters) throws Exception {
        Document doc = new Document();
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(filename));
        doc.open();
        PdfContentByte cb = writer.getDirectContent();

        // Rotate page 180 degrees because the top of the page is less important
        // than the bottom and a part of the bottom is usually lost in printouts.
        cb.concatCTM(-1f, 0f, 0f, -1f, PageSize.A4.getWidth(), PageSize.A4.getHeight());

        cb.saveState();

        // Apply calibration parameters
        if (calibrationParameters != null) {
            // Translate
            // Use right and top instead of left and bottom because the page has been rotated 180 degrees.
            cb.concatCTM(1, 0, 0, 1, calibrationParameters.getRight(), calibrationParameters.getTop());
            // Scale
            final double newWidth = PageSize.A4.getWidth() - (calibrationParameters.getLeft() + calibrationParameters.getRight());
            final double newHeight = PageSize.A4.getHeight() - 2 * (calibrationParameters.getBottom() + calibrationParameters.getTop());
            cb.concatCTM(newWidth / PageSize.A4.getWidth(), 0f, 0f, newHeight / PageSize.A4.getHeight(), 0f, 0f);
        }

        // Calculate scale factor for calendar pages.
        float sf = PageSize.A4.getWidth() / (2 * PageSize.A4.getHeight());
        cb.concatCTM(sf * 0f, sf * 1f, sf * -1f, sf * 0f, PageSize.A4.getWidth(), 0f);

        // Draw pages.
        drawMonthPage(cb, cal, 3);

        cb.saveState();
        cb.concatCTM(1f, 0f, 0f, 1f, PageSize.A4.getWidth(), 0f);
        drawMonthPage(cb, cal, 0);
        cb.restoreState();

        cb.saveState();
        cb.concatCTM(1f, 0f, 0f, 1f, 2 * PageSize.A4.getWidth(), PageSize.A4.getHeight());
        cb.concatCTM(-1f, 0f, 0f, -1f, 0, PageSize.A4.getHeight());
        drawMonthPage(cb, cal, 1);
        cb.restoreState();

        cb.saveState();
        cb.concatCTM(1f, 0f, 0f, 1f, PageSize.A4.getWidth(), PageSize.A4.getHeight());
        cb.concatCTM(-1f, 0f, 0f, -1f, 0, PageSize.A4.getHeight());
        drawMonthPage(cb, cal, 2);
        cb.restoreState();

        cb.restoreState();


        cb.saveState();

        if (calibrationParameters != null) {
            // Translate
            // Use right and top instead of left and bottom because the page has been rotated 180 degrees.
            cb.concatCTM(1, 0, 0, 1, calibrationParameters.getRight(), 3 * calibrationParameters.getTop());
            // Scale
            final double newWidth = PageSize.A4.getWidth() - (calibrationParameters.getLeft() + calibrationParameters.getRight());
            final double newHeight = PageSize.A4.getHeight() - 2 * (calibrationParameters.getBottom() + calibrationParameters.getTop());
            cb.concatCTM(newWidth / PageSize.A4.getWidth(), 0f, 0f, newHeight / PageSize.A4.getHeight(), 0f, 0f);
        }

        cb.concatCTM(sf * 0f, sf * 1f, sf * -1f, sf * 0f, PageSize.A4.getWidth(), 0f);

        cb.saveState();
        cb.concatCTM(1f, 0f, 0f, 1f, 2 * PageSize.A4.getWidth(), 0f);
        drawHalfYearPage(cb, cal, 0);
        cb.restoreState();

        cb.saveState();
        cb.concatCTM(1f, 0f, 0f, 1f, 3 * PageSize.A4.getWidth(), 0f);
        drawHalfYearPage(cb, cal, 6);
        cb.restoreState();

        cb.saveState();
        cb.concatCTM(1f, 0f, 0f, 1f, 3 * PageSize.A4.getWidth(), PageSize.A4.getHeight());
        drawMonthPage(cb, cal, 5);
        cb.restoreState();

        cb.saveState();
        cb.concatCTM(1f, 0f, 0f, 1f, 2 * PageSize.A4.getWidth(), PageSize.A4.getHeight());
        drawMonthPage(cb, cal, 4);
        cb.restoreState();

        cb.restoreState();

        doc.close();
    }
}
