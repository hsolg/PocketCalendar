package no.solg.pocketcalendar;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;

public class TestUtil {
    public static void drawPage(PdfContentByte cb, String str) throws Exception {
        cb.moveTo(0, 0);
        cb.lineTo(PageSize.A4.getWidth(), PageSize.A4.getHeight());
        cb.closePath();
        cb.stroke();
        cb.moveTo(PageSize.A4.getWidth(), 0);
        cb.lineTo(0, PageSize.A4.getHeight());
        cb.closePath();
        cb.stroke();
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        cb.beginText();
        cb.setFontAndSize(bf, 50);
        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, str, PageSize.A4.getWidth() / 2,
                PageSize.A4.getHeight() * (2f / 3), 0);
        cb.endText();
    }

    public static void drawPoint(PdfContentByte cb) throws Exception {
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        cb.beginText();
        cb.setFontAndSize(bf, 12);
        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "LL", -100, -100, 0);
        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "UL", -100, 100, 0);
        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "UR", 100, 100, 0);
        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "LR", 100, -100, 0);
        cb.endText();
    }
}
