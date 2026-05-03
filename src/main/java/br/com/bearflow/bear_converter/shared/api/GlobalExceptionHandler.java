package br.com.bearflow.bear_converter.shared.api;

import br.com.bearflow.bear_converter.auth.application.InvalidCredentialsException;
import br.com.bearflow.bear_converter.auth.application.InvalidRefreshTokenException;
import br.com.bearflow.bear_converter.conversions.application.ImagePdfNotSupportedException;
import br.com.bearflow.bear_converter.conversions.application.InvalidPdfUploadException;
import br.com.bearflow.bear_converter.conversions.application.PdfComplexityNotSupportedException;
import br.com.bearflow.bear_converter.users.application.DuplicateUserEmailException;
import br.com.bearflow.bear_converter.users.application.UnsafeTextException;
import br.com.bearflow.bear_converter.users.application.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
		List<String> errors = exception.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(error -> error.getField() + ": " + error.getDefaultMessage())
			.toList();
		return ResponseEntity.badRequest()
			.body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Invalid request data", errors));
	}

	@ExceptionHandler(UnsafeTextException.class)
	public ResponseEntity<ErrorResponse> handleUnsafeText(UnsafeTextException exception) {
		return ResponseEntity.badRequest()
			.body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Invalid request data", List.of(exception.getMessage())));
	}

	@ExceptionHandler(DuplicateUserEmailException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateUserEmailException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(ErrorResponse.of(HttpStatus.CONFLICT.value(), exception.getMessage()));
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(ErrorResponse.of(HttpStatus.NOT_FOUND.value(), exception.getMessage()));
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException exception) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
			.body(ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(), exception.getMessage()));
	}

	@ExceptionHandler(InvalidRefreshTokenException.class)
	public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException exception) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
			.body(ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(), exception.getMessage()));
	}

	@ExceptionHandler(InvalidPdfUploadException.class)
	public ResponseEntity<ErrorResponse> handleInvalidPdfUpload(InvalidPdfUploadException exception) {
		return ResponseEntity.badRequest()
			.body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), exception.getMessage()));
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException exception) {
		return ResponseEntity.badRequest()
			.body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "PDF file exceeds the maximum size of 10MB"));
	}

	@ExceptionHandler(ImagePdfNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleImagePdfNotSupported(ImagePdfNotSupportedException exception) {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
			.body(ErrorResponse.of(HttpStatus.UNPROCESSABLE_CONTENT.value(), exception.getMessage()));
	}

	@ExceptionHandler(PdfComplexityNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handlePdfComplexityNotSupported(PdfComplexityNotSupportedException exception) {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
			.body(ErrorResponse.of(HttpStatus.UNPROCESSABLE_CONTENT.value(), exception.getMessage()));
	}
}
