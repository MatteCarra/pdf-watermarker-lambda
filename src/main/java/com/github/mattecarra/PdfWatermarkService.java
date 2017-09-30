package com.github.mattecarra;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.StorageClass;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import java.io.*;

/**
 * Created by matteo on 09/04/17.
 */
public class PdfWatermarkService {
    private final AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();

    public static void main(String[] args) throws Throwable {
        WatermarkInput i = new WatermarkInput();
        i.setS3InputFileKey("testInput.pdf");
        i.setS3OutputKey("testOutput.pdf");
        i.setUser("MatteCarra");
        i.setLicenseText("Product sold to MatteCarra");
        PdfWatermarkService s = new PdfWatermarkService();
        s.handleRequest(i, null);
    }

    public Integer handleRequest(WatermarkInput watermarkInput, Context context) throws Throwable {
        S3Object s3Object = s3.getObject(watermarkInput.getS3InputBucket(), watermarkInput.getS3InputFileKey());

        File file = null;
        try {
            file = File.createTempFile("watermarked", ".pdf");

            PdfDocument pdfDoc = new PdfDocument(new PdfReader(s3Object.getObjectContent()), new PdfWriter(file));
            Document doc = new Document(pdfDoc);
            int n = pdfDoc.getNumberOfPages();
            PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
            Paragraph p = new Paragraph(watermarkInput.getLicenseText()).setFont(font).setFontSize(30).setFontColor(Color.BLUE);
            Paragraph invisibleP = new Paragraph("" + watermarkInput.getUser()).setFontSize(1);

            // transparency
            PdfExtGState gs1 = new PdfExtGState();
            gs1.setFillOpacity(0.1f);

            PdfExtGState gs2 = new PdfExtGState();
            gs2.setFillOpacity(0f);

            PdfCanvas over;
            Rectangle pagesize;
            float x, y;

            for(int i = 1; i <= n; i ++){
                PdfPage pdfPage = pdfDoc.getPage(i);
                pagesize = pdfPage.getPageSizeWithRotation();
                pdfPage.setIgnorePageRotationForContent(true);
                x = (pagesize.getLeft() + pagesize.getRight()) / 2;
                y = (pagesize.getTop() + pagesize.getBottom()) / 2;

                over = new PdfCanvas(pdfDoc.getPage(i));
                over.saveState();
                over.setExtGState(gs1);
                doc.showTextAligned(p, x, y, i, TextAlignment.CENTER, VerticalAlignment.TOP, (float) Math.toRadians(45));

                over.saveState();
                over.setExtGState(gs2);
                doc.showTextAligned(invisibleP, pagesize.getRight(), pagesize.getBottom(), i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);

                over.restoreState();
            }

            doc.close();

            String objectKey = watermarkInput.getS3OutputKey();
            s3.putObject(new PutObjectRequest(watermarkInput.getS3OutputBucket(), objectKey, file).withStorageClass(StorageClass.ReducedRedundancy));
            return 1;
        } finally {
            if(file != null) file.delete();
        }
    }
}