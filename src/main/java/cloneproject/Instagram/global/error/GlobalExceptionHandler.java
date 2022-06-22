package cloneproject.Instagram.global.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import cloneproject.Instagram.global.error.exception.BusinessException;

import static cloneproject.Instagram.global.error.ErrorCode.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import javax.validation.ConstraintViolationException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler
	protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
		MissingServletRequestParameterException e) {
		final ErrorResponse response = ErrorResponse.of(INPUT_VALUE_INVALID, e.getParameterName());
		return new ResponseEntity<>(response, BAD_REQUEST);
	}

	@ExceptionHandler
	protected ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
		final ErrorResponse response = ErrorResponse.of(INPUT_VALUE_INVALID, e.getConstraintViolations());
		return new ResponseEntity<>(response, BAD_REQUEST);
	}

	@ExceptionHandler
	protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		final ErrorResponse response = ErrorResponse.of(INPUT_VALUE_INVALID, e.getBindingResult());
		return new ResponseEntity<>(response, BAD_REQUEST);
	}

	@ExceptionHandler
	protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
		final ErrorResponse response = ErrorResponse.of(INPUT_VALUE_INVALID, e.getBindingResult());
		return new ResponseEntity<>(response, BAD_REQUEST);
	}

	@ExceptionHandler
	protected ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(
		MissingServletRequestPartException e) {
		final ErrorResponse response = ErrorResponse.of(INPUT_VALUE_INVALID, e.getRequestPartName());
		return new ResponseEntity<>(response, BAD_REQUEST);
	}

	@ExceptionHandler
	protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e) {
		final ErrorResponse response = ErrorResponse.of(e);
		return new ResponseEntity<>(response, BAD_REQUEST);
	}

	@ExceptionHandler
	protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		final ErrorResponse response = ErrorResponse.of(HTTP_MESSAGE_NOT_READABLE);
		return new ResponseEntity<>(response, BAD_REQUEST);
	}

	@ExceptionHandler
	protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
		HttpRequestMethodNotSupportedException e) {
		final List<ErrorResponse.FieldError> errors = new ArrayList<>();
		errors.add(new ErrorResponse.FieldError("http method", e.getMethod(), METHOD_NOT_ALLOWED.getMessage()));
		final ErrorResponse response = ErrorResponse.of(HTTP_HEADER_INVALID, errors);
		return new ResponseEntity<>(response, BAD_REQUEST);
	}

	@ExceptionHandler
	protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
		final ErrorCode errorCode = e.getErrorCode();
		final ErrorResponse response = ErrorResponse.of(errorCode, e.getErrors());
		return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
	}

	@ExceptionHandler
	protected ResponseEntity<ErrorResponse> handleException(Exception e) {
		final ErrorResponse response = ErrorResponse.of(INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
