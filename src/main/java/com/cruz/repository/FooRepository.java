package com.cruz.repository;

import com.cruz.domain.Foo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Foo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FooRepository extends JpaRepository<Foo, Long> {}
