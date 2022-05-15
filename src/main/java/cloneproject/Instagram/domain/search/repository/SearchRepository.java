package cloneproject.Instagram.domain.search.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.domain.search.entity.Search;
import cloneproject.Instagram.domain.search.repository.querydsl.SearchRepositoryQuerydsl;

public interface SearchRepository extends JpaRepository<Search, Long>, SearchRepositoryQuerydsl {

}
