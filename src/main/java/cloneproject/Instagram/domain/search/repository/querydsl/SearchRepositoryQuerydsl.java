package cloneproject.Instagram.domain.search.repository.querydsl;

import java.util.List;

import cloneproject.Instagram.domain.search.dto.SearchDTO;

public interface SearchRepositoryQuerydsl {
    
    List<SearchDTO> searchByText(Long loginedId, String text);
    List<SearchDTO> searchByTextOnlyHashtag(Long loginedId, String text);

}
