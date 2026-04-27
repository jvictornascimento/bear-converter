package br.com.bearflow.bear_converter.conversions.application;

import br.com.bearflow.bear_converter.conversions.domain.PdfDocumentType;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;
import java.io.IOException;

@Service
public class PdfInspectionService {

	public PdfDocumentType inspect(byte[] pdfBytes) throws IOException {
		try (PDDocument document = Loader.loadPDF(pdfBytes)) {
			if (hasExtractableText(document) || hasVectorGraphics(document)) {
				return PdfDocumentType.VECTOR;
			}
			return PdfDocumentType.IMAGE;
		}
	}

	private boolean hasExtractableText(PDDocument document) throws IOException {
		return !new PDFTextStripper().getText(document).isBlank();
	}

	private boolean hasVectorGraphics(PDDocument document) throws IOException {
		for (PDPage page : document.getPages()) {
			VectorDetectionEngine engine = new VectorDetectionEngine(page);
			engine.processPage(page);
			if (engine.hasVectorGraphics()) {
				return true;
			}
		}
		return false;
	}

	private static class VectorDetectionEngine extends PDFGraphicsStreamEngine {

		private boolean hasPathSegment;
		private boolean hasVectorGraphics;

		protected VectorDetectionEngine(PDPage page) {
			super(page);
		}

		boolean hasVectorGraphics() {
			return hasVectorGraphics;
		}

		@Override
		public void appendRectangle(Point2D point1, Point2D point2, Point2D point3, Point2D point4) {
			hasPathSegment = true;
		}

		@Override
		public void drawImage(PDImage pdImage) {
		}

		@Override
		public void clip(int windingRule) {
		}

		@Override
		public void moveTo(float x, float y) {
		}

		@Override
		public void lineTo(float x, float y) {
			hasPathSegment = true;
		}

		@Override
		public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
			hasPathSegment = true;
		}

		@Override
		public Point2D getCurrentPoint() {
			return new Point2D.Float();
		}

		@Override
		public void closePath() {
		}

		@Override
		public void endPath() {
			hasPathSegment = false;
		}

		@Override
		public void strokePath() {
			confirmVectorPath();
		}

		@Override
		public void fillPath(int windingRule) {
			confirmVectorPath();
		}

		@Override
		public void fillAndStrokePath(int windingRule) {
			confirmVectorPath();
		}

		@Override
		public void shadingFill(COSName shadingName) {
			hasVectorGraphics = true;
		}

		@Override
		public void showAnnotation(PDAnnotation annotation) throws IOException {
			super.showAnnotation(annotation);
		}

		@Override
		public PDGraphicsState getGraphicsState() {
			return super.getGraphicsState();
		}

		private void confirmVectorPath() {
			if (hasPathSegment) {
				hasVectorGraphics = true;
				hasPathSegment = false;
			}
		}
	}
}
