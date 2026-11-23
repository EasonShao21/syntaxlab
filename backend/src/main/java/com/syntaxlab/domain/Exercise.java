package com.syntaxlab.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Exercise {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private String topic;
  private String difficulty;
  private Instant createdAt=Instant.now();
  @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.EAGER) @JoinColumn(name="exercise_id")
  @OrderColumn private List<Question> questions=new ArrayList<>();
  protected Exercise() {}
  public Exercise(String topic,String difficulty,List<Question> questions){this.topic=topic;this.difficulty=difficulty;this.questions=questions;}
  public Long getId(){return id;} public String getTopic(){return topic;} public String getDifficulty(){return difficulty;}
  public Instant getCreatedAt(){return createdAt;} public List<Question> getQuestions(){return questions;}
}
