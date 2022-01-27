package cloneproject.Instagram.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.dto.alarm.AlarmDTO;
import cloneproject.Instagram.dto.result.ResultCode;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.service.AlarmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "알림 API")
@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @ApiOperation(value = "알림 목록 조회")
    @GetMapping(value = "/alarms")
    public ResponseEntity<ResultResponse> getAlarms(){
        List<AlarmDTO> alarms = alarmService.getAlarms();

        ResultResponse result = ResultResponse.of(ResultCode.GET_ALARMS_SUCCESS, alarms);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    
}
