package com.syntaxlab.api;

import com.syntaxlab.api.dto.ApiDtos;
import com.syntaxlab.domain.*;
import com.syntaxlab.repository.*;
import com.syntaxlab.service.QuestionGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins="http://localhost:5173")
public class PracticeController {
  private final ExerciseRepository exercises; private final AttemptRepository attempts; private final QuestionGenerator generator;
  private final List<Topic> topics=List.of(
    new Topic("html","HTML","⌘","網頁內容與語意結構",12),new Topic("css","CSS","◈","樣式、版面與響應式設計",18),
    new Topic("javascript","JavaScript","JS","互動、語法與程式邏輯",24),new Topic("java","Java","☕","物件導向與核心語法",14),new Topic("python","Python","Py","簡潔語法與資料處理",16));
  public PracticeController(ExerciseRepository exercises,AttemptRepository attempts,QuestionGenerator generator){this.exercises=exercises;this.attempts=attempts;this.generator=generator;}
  @GetMapping("/topics") public List<Topic> topics(){return topics;}
  @PostMapping("/exercises/generate") @ResponseStatus(HttpStatus.CREATED)
  public ApiDtos.ExerciseView generate(@RequestBody ApiDtos.GenerateRequest request){
    if(request.topic()==null || request.difficulty()==null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"topic and difficulty are required");
    int count=request.count()==null?5:Math.max(1,Math.min(10,request.count()));
    try { var exercise=exercises.save(new Exercise(request.topic().toLowerCase(Locale.ROOT),request.difficulty(),generator.generate(request.topic(),request.difficulty(),count))); return ApiDtos.exercise(exercise,false); }
    catch(IllegalArgumentException e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());}
  }
  @GetMapping("/exercises/{id}") public ApiDtos.ExerciseView exercise(@PathVariable Long id){return ApiDtos.exercise(findExercise(id),false);}
  @PostMapping("/exercises/{id}/submit") @ResponseStatus(HttpStatus.CREATED)
  public ApiDtos.AttemptView submit(@PathVariable Long id,@RequestBody ApiDtos.SubmitRequest request){
    Exercise e=findExercise(id); List<ApiDtos.Answer> submitted=request.answers()==null?List.of():request.answers();
    Map<Long,Integer> selected=new HashMap<>(); submitted.forEach(a->{if(a.questionId()!=null&&a.selectedOption()!=null)selected.put(a.questionId(),a.selectedOption());});
    int score=0;List<Integer> ordered=new ArrayList<>();
    for(Question q:e.getQuestions()){Integer answer=selected.get(q.getId());ordered.add(answer==null?-1:answer);if(answer!=null&&answer==q.getCorrectOption())score++;}
    return ApiDtos.attempt(attempts.save(new Attempt(e.getId(),e.getTopic(),score,e.getQuestions().size(),ordered)),e);
  }
  @GetMapping("/attempts") public List<ApiDtos.AttemptView> history(@RequestParam(required=false)String topic,@RequestParam(defaultValue="50")int limit){
    var rows=topic==null||topic.isBlank()?attempts.findAllByOrderByCompletedAtDesc():attempts.findByTopicIgnoreCaseOrderByCompletedAtDesc(topic);
    return rows.stream().limit(Math.max(1,Math.min(100,limit))).map(a->ApiDtos.attempt(a,findExercise(a.getExerciseId()))).toList();
  }
  @GetMapping("/attempts/{id}") public ApiDtos.AttemptView attempt(@PathVariable Long id){Attempt a=attempts.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Attempt not found"));return ApiDtos.attempt(a,findExercise(a.getExerciseId()));}
  private Exercise findExercise(Long id){return exercises.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Exercise not found"));}
}
