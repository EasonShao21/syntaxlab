package com.syntaxlab.repository;
import com.syntaxlab.domain.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AttemptRepository extends JpaRepository<Attempt,Long> {
  List<Attempt> findAllByOrderByCompletedAtDesc();
  List<Attempt> findByTopicIgnoreCaseOrderByCompletedAtDesc(String topic);
}
