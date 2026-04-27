package br.com.bearflow.bear_converter.conversions.support;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class PdfTestFiles {

	private PdfTestFiles() {
	}

	public static byte[] vectorPdf() throws IOException {
		try (PDDocument document = new PDDocument()) {
			PDPage page = new PDPage(PDRectangle.A4);
			document.addPage(page);
			try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
				contentStream.setStrokingColor(Color.BLACK);
				contentStream.moveTo(80, 120);
				contentStream.lineTo(260, 120);
				contentStream.stroke();
				contentStream.beginText();
				contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
				contentStream.newLineAtOffset(80, 150);
				contentStream.showText("Circuit A");
				contentStream.endText();
			}
			return save(document);
		}
	}

	public static byte[] imagePdf() throws IOException {
		try (PDDocument document = new PDDocument()) {
			PDPage page = new PDPage(PDRectangle.A4);
			document.addPage(page);
			BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
			for (int x = 0; x < image.getWidth(); x++) {
				for (int y = 0; y < image.getHeight(); y++) {
					image.setRGB(x, y, Color.WHITE.getRGB());
				}
			}
			try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
				contentStream.drawImage(LosslessFactory.createFromImage(document, image), 80, 120, 120, 120);
			}
			return save(document);
		}
	}

	public static byte[] multiPageVectorPdf() throws IOException {
		try (PDDocument document = new PDDocument()) {
			for (int index = 0; index < 2; index++) {
				PDPage page = new PDPage(PDRectangle.A4);
				document.addPage(page);
				try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
					contentStream.moveTo(80, 120);
					contentStream.lineTo(260, 120);
					contentStream.stroke();
				}
			}
			return save(document);
		}
	}

	private static byte[] save(PDDocument document) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		document.save(outputStream);
		return outputStream.toByteArray();
	}
}
