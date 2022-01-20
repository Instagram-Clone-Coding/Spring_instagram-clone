package cloneproject.Instagram.controller;

import javax.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.dto.result.ResultCode;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.service.BlockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "차단 API")
@RestController
@RequiredArgsConstructor
public class BlockController {
    
    private final BlockService blockService;

    @ApiOperation(value = "차단")
    @PostMapping("/{blockMemberUsername}/block")
    @ApiImplicitParam(name = "blockMemberUsername", value = "차단할 계정의 username", required = true, example = "dlwlrma")
    public ResponseEntity<ResultResponse> block(@PathVariable("blockMemberUsername") @Validated
                                                 @NotBlank(message = "username이 필요합니다") String blockMemberUsername){
        boolean success = blockService.block(blockMemberUsername);
        
        ResultResponse result = ResultResponse.of(ResultCode.BLOCK_SUCCESS, success);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "차단해제")
    @PostMapping("/{blockMemberUsername}/unblock")
    @ApiImplicitParam(name = "blockMemberUsername", value = "언팔로우할 계정의 username", required = true, example = "dlwlrma")
    public ResponseEntity<ResultResponse> unblock(@PathVariable("blockMemberUsername") @Validated
                                                @NotBlank(message = "username이 필요합니다") String blockMemberUsername){
        boolean success = blockService.unblock(blockMemberUsername);
        
        ResultResponse result = ResultResponse.of(ResultCode.UNBLOCK_SUCCESS, success);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

}
