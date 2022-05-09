package cloneproject.Instagram.domain.member.controller;

import static cloneproject.Instagram.global.result.ResultCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.domain.member.service.BlockService;
import cloneproject.Instagram.global.result.ResultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "차단 API")
@RestController
@RequiredArgsConstructor
@Validated
public class BlockController {

	private final BlockService blockService;

	@ApiOperation(value = "차단")
	@PostMapping("/{blockMemberUsername}/block")
	@ApiImplicitParam(name = "blockMemberUsername", value = "차단할 계정의 username", required = true, example = "dlwlrma")
	public ResponseEntity<ResultResponse> block(@PathVariable("blockMemberUsername") String blockMemberUsername) {
		final boolean success = blockService.block(blockMemberUsername);

		return ResponseEntity.ok(ResultResponse.of(BLOCK_SUCCESS, success));
	}

	@ApiOperation(value = "차단해제")
	@DeleteMapping("/{blockMemberUsername}/block")
	@ApiImplicitParam(name = "blockMemberUsername", value = "차단해제할 계정의 username", required = true, example = "dlwlrma")
	public ResponseEntity<ResultResponse> unblock(@PathVariable("blockMemberUsername") String blockMemberUsername) {
		final boolean success = blockService.unblock(blockMemberUsername);

		return ResponseEntity.ok(ResultResponse.of(UNBLOCK_SUCCESS, success));
	}

}
