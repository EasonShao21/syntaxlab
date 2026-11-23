package com.syntaxlab.repository;
import com.syntaxlab.domain.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ExerciseRepository extends JpaRepository<Exercise,Long> {}
