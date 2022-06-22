package cloneproject.Instagram.domain.alarm.controller;

import static cloneproject.Instagram.global.result.ResultCode.*;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.domain.alarm.dto.AlarmDto;
import cloneproject.Instagram.domain.alarm.service.AlarmService;
import cloneproject.Instagram.global.result.ResultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@Api(tags = "알림 API")
@Validated
@RestController
@RequiredArgsConstructor
public class AlarmController {

	private final AlarmService alarmService;

	@ApiOperation(value = "알림 목록 페이징 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "A001 - 알림 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value = "페이지", example = "1", required = true),
		@ApiImplicitParam(name = "size", value = "페이지당 개수", example = "10", required = true)
	})
	@GetMapping(value = "/alarms")
	public ResponseEntity<ResultResponse> getAlarms(@RequestParam int page, @RequestParam int size) {
		final Page<AlarmDto> response = alarmService.getAlarms(page, size);

		return ResponseEntity.ok(ResultResponse.of(GET_ALARMS_SUCCESS, response));
	}

}
