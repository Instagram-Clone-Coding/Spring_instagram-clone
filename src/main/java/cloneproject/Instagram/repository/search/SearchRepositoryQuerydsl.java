package cloneproject.Instagram.repository.search;

import java.util.List;

import cloneproject.Instagram.dto.search.SearchDTO;

public interface SearchRepositoryQuerydsl {
    
    List<SearchDTO> searchByText(Long loginedId, String text);
    List<SearchDTO> searchByTextOnlyHashtag(Long loginedId, String text);

}
