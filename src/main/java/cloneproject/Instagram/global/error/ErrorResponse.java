package cloneproject.Instagram.global.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

	private int status;
	private String code;
	private String message;
	private List<FieldError> errors;

	private ErrorResponse(final ErrorCode code, final List<FieldError> errors) {
		this.message = code.getMessage();
		this.status = code.getStatus();
		this.errors = errors;
		this.code = code.getCode();
	}

	private ErrorResponse(final ErrorCode code) {
		this.message = code.getMessage();
		this.status = code.getStatus();
		this.code = code.getCode();
		this.errors = new ArrayList<>();
	}

	public static ErrorResponse of(final ErrorCode code, final BindingResult bindingResult) {
		return new ErrorResponse(code, FieldError.of(bindingResult));
	}

	public static ErrorResponse of(final ErrorCode code, final Set<ConstraintViolation<?>> constraintViolations) {
		return new ErrorResponse(code, FieldError.of(constraintViolations));
	}

	public static ErrorResponse of(final ErrorCode code, final String missingParameterName) {
		return new ErrorResponse(code, FieldError.of(missingParameterName, "", "parameter가 필요합니다"));
	}

	public static ErrorResponse of(final ErrorCode code) {
		return new ErrorResponse(code);
	}

	public static ErrorResponse of(final ErrorCode code, final List<FieldError> errors) {
		return new ErrorResponse(code, errors);
	}

	public static ErrorResponse of(MethodArgumentTypeMismatchException e) {
		final String value = e.getValue() == null ? "" : e.getValue().toString();
		final List<FieldError> errors = FieldError.of(e.getName(), value, e.getErrorCode());
		return new ErrorResponse(ErrorCode.INPUT_TYPE_INVALID, errors);
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class FieldError {
		private String field;
		private String value;
		private String reason;

		public FieldError(final String field, final String value, final String reason) {
			this.field = field;
			this.value = value;
			this.reason = reason;
		}

		public static List<FieldError> of(final String field, final String value, final String reason) {
			List<FieldError> fieldErrors = new ArrayList<>();
			fieldErrors.add(new FieldError(field, value, reason));
			return fieldErrors;
		}

		private static List<FieldError> of(final BindingResult bindingResult) {
			final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
			return fieldErrors.stream()
				.map(error -> new FieldError(
					error.getField(),
					error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
					error.getDefaultMessage()))
				.collect(Collectors.toList());
		}

		private static List<FieldError> of(final Set<ConstraintViolation<?>> constraintViolations) {
			List<ConstraintViolation<?>> lists = new ArrayList<>(constraintViolations);
			return lists.stream()
				.map(error -> {
					final String invalidValue =
						error.getInvalidValue() == null ? "" : error.getInvalidValue().toString();
					final int index = error.getPropertyPath().toString().indexOf(".");
					final String propertyPath = error.getPropertyPath().toString().substring(index + 1);
					return new FieldError(propertyPath, invalidValue, error.getMessage());
				})
				.collect(Collectors.toList());
		}

	}

}
