package cloneproject.Instagram.domain.search.controller;

import java.util.List;

import javax.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.domain.search.dto.SearchDTO;
import cloneproject.Instagram.domain.search.service.SearchService;
import cloneproject.Instagram.global.result.ResultCode;
import cloneproject.Instagram.global.result.ResultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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

		return ResponseEntity.ok(ResultResponse.of(ResultCode.SEARCH_SUCCESS, searchDTOs));
	}

	@ApiOperation(value = "검색 조회수 증가, 최근 검색 기록 업데이트")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "entityName", value = "조회수 증가시킬 식별 name", required = true, example = "dlwlrma"),
		@ApiImplicitParam(name = "entityType", value = "조회수 증가시킬 type", required = true, example = "MEMBER")
	})
	@PostMapping(value = "/topsearch/mark")
	public ResponseEntity<ResultResponse> markSearchedEntity(@RequestParam String entityName,
		@RequestParam String entityType) {
		searchService.markSearchedEntity(entityName, entityType);

		return ResponseEntity.ok(ResultResponse.of(ResultCode.MARK_SEARCHED_ENTITY_SUCCESS));
	}

	@ApiOperation(value = "최근 검색 기록(15개 조회)")
	@GetMapping(value = "/topsearch/recent/top")
	public ResponseEntity<ResultResponse> getTop15RecentSearch() {
		Page<SearchDTO> searchDTOs = searchService.getTop15RecentSearches();

		return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_TOP_15_RECENT_SEARCH_SUCCESS, searchDTOs));
	}

	@ApiOperation(value = "최근 검색 기록 무한스크롤")
	@GetMapping(value = "/topsearch/recent")
	public ResponseEntity<ResultResponse> getRecentSearch(@Min(1) @RequestParam int page) {
		Page<SearchDTO> searchDTOs = searchService.getRecentSearches(page);

		return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_RECENT_SEARCH_SUCCESS, searchDTOs));
	}

	@ApiOperation(value = "최근 검색 기록 삭제")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "entityName", value = "삭제할 식별 name", required = true, example = "dlwlrma"),
		@ApiImplicitParam(name = "entityType", value = "삭제할 type", required = true, example = "MEMBER")
	})
	@DeleteMapping(value = "/topsearch/recent")
	public ResponseEntity<ResultResponse> deleteRecentSearch(@RequestParam String entityName,
		@RequestParam String entityType) {
		searchService.deleteRecentSearch(entityName, entityType);

		return ResponseEntity.ok(ResultResponse.of(ResultCode.DELETE_RECENT_SEARCH_SUCCESS));
	}

	@ApiOperation(value = "최근 검색 기록 모두 삭제")
	@DeleteMapping(value = "/topsearch/recent/all")
	public ResponseEntity<ResultResponse> deleteAllRecentSearch() {
		searchService.deleteAllRecentSearch();

		return ResponseEntity.ok(ResultResponse.of(ResultCode.DELETE_ALL_RECENT_SEARCH_SUCCESS));
	}

}
