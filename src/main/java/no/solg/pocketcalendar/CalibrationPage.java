package no.solg.pocketcalendar;

import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public class CalibrationPage {
    public static void createPage(String filename) throws Exception {
        final float circleRadius = 1.0f;
        Document doc = new Document(PageSize.A4);

        System.out.println("Default margins:");
        System.out.println("Top: " + doc.topMargin() + " Bottom: " + doc.bottomMargin() + " Left: " + doc.leftMargin()
                + " Right: " + doc.rightMargin());
        System.out.println("Setting margins to zero.");
        doc.setMargins(0.0f, 0.0f, 0.0f, 0.0f);

        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(filename));
        doc.open();
        PdfContentByte cb = writer.getDirectContent();
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        cb.setFontAndSize(bf, 10);
        Rectangle rect = doc.getPageSize();
        float maxLengthX = rect.getWidth() / 3;
        float maxLengthY = rect.getHeight() / 3;
        float step = 2.0f;
        final int numPoints = 15;
        final float startX = rect.getWidth() * 0.4f;
        final float endX = rect.getWidth() * 0.6f;
        final float startY = rect.getHeight() * 0.4f;
        final float endY = rect.getHeight() * 0.6f;
        final float spacingX = (endX - startX) / (numPoints - 1);
        final float spacingY = (endY - startY) / (numPoints - 1);
        for (int i = 0; i < numPoints; i++) {
            float x;
            float y;

            // Draw bottom part.
            x = startX + (i * spacingX);
            y = rect.getTop() - (i * step);
            cb.circle(x, y, circleRadius);
            cb.moveTo(x, y);
            cb.lineTo(x, rect.getTop() - maxLengthY + (i * spacingY));
            cb.lineTo(x + maxLengthX - (i * spacingX), rect.getTop() - maxLengthY + (i * spacingY));
            cb.stroke();
            cb.beginText();
            cb.showTextAligned(PdfContentByte.ALIGN_LEFT, String.format("%.2f", i * step),
                    x + maxLengthX - (i * spacingX), rect.getTop() - maxLengthY + (i * spacingY), 0);
            cb.endText();

            // Draw right part.
            x = rect.getRight() - (i * step);
            y = startY + (i * spacingY);
            cb.circle(x, y, circleRadius);
            cb.moveTo(x, y);
            cb.lineTo(rect.getRight() - maxLengthX, y);
            cb.stroke();
            cb.beginText();
            cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, String.format("%.2f", i * step),
                    rect.getRight() - maxLengthX, y, 0);
            cb.endText();

            // Draw top part.
            x = startX + (i * spacingX);
            y = (i * step);
            cb.circle(x, y, circleRadius);
            cb.moveTo(x, y);
            cb.lineTo(x, maxLengthY - (i * spacingY));
            cb.lineTo(x + maxLengthX - (i * spacingX), maxLengthY - (i * spacingY));
            cb.stroke();
            cb.beginText();
            cb.showTextAligned(PdfContentByte.ALIGN_LEFT, String.format("%.2f", i * step),
                    x + maxLengthX - (i * spacingX), maxLengthY - (i * spacingY), 0);
            cb.endText();

            // Draw left part.
            x = i * step;
            y = rect.getTop() - startY - (i * spacingY);
            cb.circle(x, y, circleRadius);
            cb.moveTo(x, y);
            cb.lineTo(maxLengthX, y);
            cb.stroke();
            cb.beginText();
            cb.showTextAligned(PdfContentByte.ALIGN_LEFT, String.format("%.2f", i * step), maxLengthX, y, 0);
            cb.endText();
        }
        doc.close();
    }
}
