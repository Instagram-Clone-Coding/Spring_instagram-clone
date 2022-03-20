package cloneproject.Instagram.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.dto.result.ResultCode;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.dto.search.SearchDTO;
import cloneproject.Instagram.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = "검색 API")
@RestController
@RequiredArgsConstructor
public class SearchController {
    
    private final SearchService searchService;

    @ApiOperation(value = "검색")
    @ApiImplicitParam(name = "text", value = "검색내용", required = true, example = "dlwl")
    @GetMapping(value = "/topsearch")
    public ResponseEntity<ResultResponse> searchText(@RequestParam String text) {
        List<SearchDTO> searchDTOs = searchService.searchByText(text);

        ResultResponse result = ResultResponse.of(ResultCode.SEARCH_SUCCESS, searchDTOs);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "유저 검색 조회수 증가")
    @ApiImplicitParam(name = "username", value = "조회수 증가시킬 username", required = true, example = "dlwlrma")
    @PostMapping(value = "/topsearch/member/upcount")
    public ResponseEntity<ResultResponse> increaseSearchMemberCount(@RequestParam String username) {
        searchService.increaseSearchMemberCount(username);

        ResultResponse result = ResultResponse.of(ResultCode.INCREASE_SEARCH_COUNT_SUCCESS, null);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "해시태그 검색 조회수 증가")
    @ApiImplicitParam(name = "name", value = "조회수 증가시킬 hashtag", required = true, example = "만두")
    @PostMapping(value = "/topsearch/hashtag/upcount")
    public ResponseEntity<ResultResponse> increaseSearchHashtagCount(@RequestParam String name) {
        searchService.increaseSearchHashtagCount(name);

        ResultResponse result = ResultResponse.of(ResultCode.INCREASE_SEARCH_COUNT_SUCCESS, null);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }
    
}
