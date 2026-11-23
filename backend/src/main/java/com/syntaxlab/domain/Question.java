package com.syntaxlab.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Question {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column(length = 2000, nullable = false) private String prompt;
  @Column(length = 3000) private String code;
  @ElementCollection @OrderColumn private List<String> options = new ArrayList<>();
  private int correctOption;
  @Column(length = 2000) private String explanation;
  protected Question() {}
  public Question(String prompt, String code, List<String> options, int correctOption, String explanation) {
    this.prompt=prompt; this.code=code; this.options=new ArrayList<>(options); this.correctOption=correctOption; this.explanation=explanation;
  }
  public Long getId(){return id;} public String getPrompt(){return prompt;} public String getCode(){return code;}
  public List<String> getOptions(){return options;} public int getCorrectOption(){return correctOption;} public String getExplanation(){return explanation;}
}
