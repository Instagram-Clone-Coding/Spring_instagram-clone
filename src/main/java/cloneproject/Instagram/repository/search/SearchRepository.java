package cloneproject.Instagram.repository.search;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.entity.search.Search;

public interface SearchRepository extends JpaRepository<Search, Long>, SearchRepositoryQuerydsl{
    
}
