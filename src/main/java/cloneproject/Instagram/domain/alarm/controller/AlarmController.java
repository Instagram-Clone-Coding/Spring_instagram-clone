package cloneproject.Instagram.domain.alarm.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.domain.alarm.dto.AlarmDto;
import cloneproject.Instagram.domain.alarm.service.AlarmService;
import cloneproject.Instagram.global.result.ResultCode;
import cloneproject.Instagram.global.result.ResultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Api(tags = "알림 API")
@Validated
@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @ApiOperation(value = "알림 목록 페이징 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "페이지", example = "1", required = true),
            @ApiImplicitParam(name = "size", value = "페이지당 개수", example = "10", required = true)
    })
    @GetMapping(value = "/alarms")
    public ResponseEntity<ResultResponse> getAlarms(
            @NotNull(message = "page는 필수입니다.") @RequestParam int page,
            @NotNull(message = "size는 필수입니다.") @RequestParam int size) {
        final Page<AlarmDto> alarms = alarmService.getAlarms(page, size);

        final ResultResponse result = ResultResponse.of(ResultCode.GET_ALARMS_SUCCESS, alarms);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

}
