package com.syntaxlab.api.dto;

import com.syntaxlab.domain.*;
import java.time.Instant;
import java.util.List;

public final class ApiDtos {
  private ApiDtos() {}
  public record GenerateRequest(String topic, String difficulty, Integer count) {}
  public record SubmitRequest(List<Answer> answers) {}
  public record Answer(Long questionId, Integer selectedOption) {}
  public record QuestionView(Long id,String prompt,String code,List<String> options,Integer selectedOption,Integer correctOption,String explanation) {}
  public record ExerciseView(Long id,String topic,String difficulty,Instant createdAt,List<QuestionView> questions) {}
  public record AttemptView(Long id,Long exerciseId,String topic,int score,int total,Instant completedAt,List<QuestionView> questions) {}
  public static ExerciseView exercise(Exercise e, boolean reveal) {
    return new ExerciseView(e.getId(),e.getTopic(),e.getDifficulty(),e.getCreatedAt(),e.getQuestions().stream().map(q -> new QuestionView(q.getId(),q.getPrompt(),q.getCode(),q.getOptions(),null,reveal?q.getCorrectOption():null,reveal?q.getExplanation():null)).toList());
  }
  public static AttemptView attempt(Attempt a, Exercise e) {
    List<QuestionView> qs=e.getQuestions().stream().map(q -> {
      int idx=e.getQuestions().indexOf(q);
      return new QuestionView(q.getId(),q.getPrompt(),q.getCode(),q.getOptions(),idx<a.getSelectedOptions().size()?a.getSelectedOptions().get(idx):null,q.getCorrectOption(),q.getExplanation());
    }).toList();
    return new AttemptView(a.getId(),a.getExerciseId(),a.getTopic(),a.getScore(),a.getTotal(),a.getCompletedAt(),qs);
  }
}
