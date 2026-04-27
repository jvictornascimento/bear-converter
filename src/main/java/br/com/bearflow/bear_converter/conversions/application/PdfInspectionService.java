package br.com.bearflow.bear_converter.conversions.application;

import br.com.bearflow.bear_converter.cadmodel.drawing.Drawing;
import br.com.bearflow.bear_converter.cadmodel.drawing.DrawingMetadata;
import br.com.bearflow.bear_converter.cadmodel.drawing.DrawingPage;
import br.com.bearflow.bear_converter.cadmodel.drawing.DrawingUnits;
import br.com.bearflow.bear_converter.cadmodel.geometry.BoundingBox;
import br.com.bearflow.bear_converter.cadmodel.geometry.Point2;
import br.com.bearflow.bear_converter.cadmodel.layer.Layer;
import br.com.bearflow.bear_converter.cadmodel.report.ConversionIssue;
import br.com.bearflow.bear_converter.cadmodel.report.ConversionIssueSeverity;
import br.com.bearflow.bear_converter.cadmodel.report.ConversionReport;
import br.com.bearflow.bear_converter.conversions.domain.PdfComplexityLevel;
import br.com.bearflow.bear_converter.conversions.domain.PdfDocumentType;
import br.com.bearflow.bear_converter.conversions.domain.PdfInspectionResult;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfInspectionService {

	private static final String DEFAULT_LAYER = "0";

	public PdfInspectionResult inspect(byte[] pdfBytes) throws IOException {
		try (PDDocument document = Loader.loadPDF(pdfBytes)) {
			InspectionMetrics metrics = inspectMetrics(document);
			PdfDocumentType documentType = metrics.hasText() || metrics.hasVectorGraphics()
				? PdfDocumentType.VECTOR
				: PdfDocumentType.IMAGE;
			PdfComplexityLevel complexityLevel = classify(metrics);
			return new PdfInspectionResult(documentType, complexityLevel, createDrawing(document, complexityLevel, metrics));
		}
	}

	private InspectionMetrics inspectMetrics(PDDocument document) throws IOException {
		InspectionMetrics metrics = new InspectionMetrics(document.getNumberOfPages(), hasExtractableText(document));
		for (PDPage page : document.getPages()) {
			VectorDetectionEngine engine = new VectorDetectionEngine(page);
			engine.processPage(page);
			metrics.add(engine.metrics());
		}
		return metrics;
	}

	private boolean hasExtractableText(PDDocument document) throws IOException {
		return !new PDFTextStripper().getText(document).isBlank();
	}

	private PdfComplexityLevel classify(InspectionMetrics metrics) {
		if (metrics.pageCount() > 1 || metrics.hasHybridRasterAndVector()) {
			return PdfComplexityLevel.PROFESSIONAL;
		}
		if (metrics.hatchCount() > 0 || metrics.curveCount() > 10) {
			return PdfComplexityLevel.COMPLEX;
		}
		if (metrics.curveCount() > 2) {
			return PdfComplexityLevel.MEDIUM;
		}
		return PdfComplexityLevel.SIMPLE;
	}

	private Drawing createDrawing(PDDocument document, PdfComplexityLevel complexityLevel, InspectionMetrics metrics) {
		return new Drawing(
			new DrawingMetadata(null, "PDF", Instant.now()),
			DrawingUnits.UNKNOWN,
			createPages(document),
			List.of(new Layer(DEFAULT_LAYER, true)),
			List.of(),
			List.of(),
			List.of(),
			createReport(complexityLevel, metrics)
		);
	}

	private List<DrawingPage> createPages(PDDocument document) {
		List<DrawingPage> pages = new ArrayList<>();
		int pageNumber = 1;
		for (PDPage page : document.getPages()) {
			PDRectangle mediaBox = page.getMediaBox();
			pages.add(new DrawingPage(
				pageNumber,
				new BoundingBox(
					new Point2(BigDecimal.ZERO, BigDecimal.ZERO),
					new Point2(BigDecimal.valueOf(mediaBox.getWidth()), BigDecimal.valueOf(mediaBox.getHeight()))
				)
			));
			pageNumber++;
		}
		return pages;
	}

	private ConversionReport createReport(PdfComplexityLevel complexityLevel, InspectionMetrics metrics) {
		List<ConversionIssue> issues = new ArrayList<>();
		if (complexityLevel == PdfComplexityLevel.COMPLEX || complexityLevel == PdfComplexityLevel.PROFESSIONAL) {
			issues.add(new ConversionIssue(
				ConversionIssueSeverity.BLOCKER,
				"PREMIUM_COMPLEXITY",
				"PDF needs premium conversion features"
			));
		}
		if (metrics.imageCount() > 0) {
			issues.add(new ConversionIssue(
				ConversionIssueSeverity.WARNING,
				"RASTER_CONTENT",
				"PDF contains raster image content"
			));
		}
		return new ConversionReport(List.copyOf(issues));
	}

	private static class VectorDetectionEngine extends PDFGraphicsStreamEngine {

		private boolean hasPathSegment;
		private boolean hasVectorGraphics;
		private int curveCount;
		private int hatchCount;
		private int imageCount;

		protected VectorDetectionEngine(PDPage page) {
			super(page);
		}

		PageMetrics metrics() {
			return new PageMetrics(hasVectorGraphics, curveCount, hatchCount, imageCount);
		}

		@Override
		public void appendRectangle(Point2D point1, Point2D point2, Point2D point3, Point2D point4) {
			hasPathSegment = true;
		}

		@Override
		public void drawImage(PDImage pdImage) {
			imageCount++;
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
			curveCount++;
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
			hatchCount++;
			confirmVectorPath();
		}

		@Override
		public void fillAndStrokePath(int windingRule) {
			hatchCount++;
			confirmVectorPath();
		}

		@Override
		public void shadingFill(COSName shadingName) {
			hasVectorGraphics = true;
			hatchCount++;
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

	private record PageMetrics(
		boolean hasVectorGraphics,
		int curveCount,
		int hatchCount,
		int imageCount
	) {
	}

	private static class InspectionMetrics {

		private final int pageCount;
		private final boolean hasText;
		private boolean hasVectorGraphics;
		private int curveCount;
		private int hatchCount;
		private int imageCount;

		InspectionMetrics(int pageCount, boolean hasText) {
			this.pageCount = pageCount;
			this.hasText = hasText;
		}

		void add(PageMetrics pageMetrics) {
			hasVectorGraphics = hasVectorGraphics || pageMetrics.hasVectorGraphics();
			curveCount += pageMetrics.curveCount();
			hatchCount += pageMetrics.hatchCount();
			imageCount += pageMetrics.imageCount();
		}

		boolean hasText() {
			return hasText;
		}

		boolean hasVectorGraphics() {
			return hasVectorGraphics;
		}

		boolean hasHybridRasterAndVector() {
			return imageCount > 0 && (hasText || hasVectorGraphics);
		}

		int pageCount() {
			return pageCount;
		}

		int curveCount() {
			return curveCount;
		}

		int hatchCount() {
			return hatchCount;
		}

		int imageCount() {
			return imageCount;
		}
	}
}
