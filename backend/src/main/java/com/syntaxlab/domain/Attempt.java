package com.syntaxlab.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Attempt {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private Long exerciseId;
  private String topic;
  private int score;
  private int total;
  private Instant completedAt=Instant.now();
  @ElementCollection @OrderColumn private List<Integer> selectedOptions=new ArrayList<>();
  protected Attempt() {}
  public Attempt(Long exerciseId,String topic,int score,int total,List<Integer> answers){this.exerciseId=exerciseId;this.topic=topic;this.score=score;this.total=total;this.selectedOptions=answers;}
  public Long getId(){return id;} public Long getExerciseId(){return exerciseId;} public String getTopic(){return topic;}
  public int getScore(){return score;} public int getTotal(){return total;} public Instant getCompletedAt(){return completedAt;}
  public List<Integer> getSelectedOptions(){return selectedOptions;}
}
