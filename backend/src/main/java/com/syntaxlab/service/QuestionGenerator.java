package com.syntaxlab.service;

import com.syntaxlab.domain.Question;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestClient;
import java.util.*;

@Component
public class QuestionGenerator {
  private final Map<String,List<Question>> bank=new HashMap<>();
  private final ObjectMapper mapper=new ObjectMapper();
  private final String apiKey=System.getenv("OPENAI_API_KEY");
  private final String baseUrl=Optional.ofNullable(System.getenv("AI_BASE_URL")).orElse("https://api.openai.com/v1");
  private final String model=Optional.ofNullable(System.getenv("AI_MODEL")).orElse("gpt-4o-mini");
  private final RestClient client=RestClient.builder().baseUrl(baseUrl).build();
  public QuestionGenerator() {
    bank.put("javascript",List.of(
      q("`typeof null` 會回傳什麼？", "", List.of("null","object","undefined","number"),1,"這是 JavaScript 長期存在的特殊行為：typeof null 會回傳 object。"),
      q("哪一種宣告不能重新指派？", "", List.of("var","let","const","function"),2,"const 不能重新綁定變數，但參照到的物件內容仍可能改變。"),
      q("以下程式會輸出什麼？", "console.log(2 + '2');", List.of("4","22","NaN","錯誤"),1,"其中一個運算元是字串時，+ 會進行字串串接。"),
      q("哪個陣列方法會轉換每個項目並建立新陣列？", "", List.of("forEach","map","reduce","find"),1,"map 會回傳一個包含回呼函式結果的新陣列。")));
    bank.put("html",List.of(q("哪個元素代表頁面的主要內容？","",List.of("<section>","<main>","<article>","<body>"),1,"一個頁面應使用 main 元素標示主要內容。"),q("圖片的替代文字應設定在哪個屬性？","",List.of("title","alt","label","caption"),1,"alt 會向輔助科技傳達圖片用途。"),q("哪個元素可以建立超連結？","",List.of("<link>","<a>","<href>","<nav>"),1,"a 錨點元素透過 href 屬性建立連結。")));
    bank.put("css",List.of(q("哪個屬性可以設定文字顏色？","",List.of("font-color","color","text-color","foreground"),1,"color 屬性會設定元素的文字顏色。"),q("哪種版面配置適合單一方向排列？","",List.of("Float","Flexbox","Table","Position"),1,"Flexbox 專為沿單一軸線排列項目而設計。"),q("`box-sizing: border-box` 的寬度計算包含哪些部分？","",List.of("只有內容","內容與 padding","內容、padding 與邊框","也包含 margin"),2,"指定寬度會包含內容、padding 與邊框。")));
    bank.put("java",List.of(q("哪個關鍵字用來建立子類別？","",List.of("implements","extends","inherits","super"),1,"類別可透過 extends 繼承另一個類別。"),q("哪種型別用來儲存 true 或 false？","",List.of("bool","boolean","bit","只有 Boolean"),1,"boolean 是 Java 的原始布林型別。")));
    bank.put("python",List.of(q("哪個關鍵字用來定義函式？","",List.of("func","def","function","lambda"),1,"使用 def，接著寫函式名稱與參數。"),q("`len('code')` 的結果是什麼？","",List.of("3","4","5","錯誤"),1,"這個字串包含四個字元。")));
  }
  private Question q(String p,String code,List<String> o,int answer,String why){return new Question(p,code,o,answer,why);}
  public List<Question> generate(String topic,String difficulty,int count){
    if(apiKey!=null&&!apiKey.isBlank()) return generateWithAi(topic,difficulty,count);
    var source=bank.get(topic.toLowerCase(Locale.ROOT));
    if(source==null) throw new IllegalArgumentException("Unsupported topic: "+topic);
    List<Question> out=new ArrayList<>(); for(int i=0;i<count;i++) { Question q=source.get(i%source.size()); out.add(new Question(q.getPrompt(),q.getCode(),q.getOptions(),q.getCorrectOption(),q.getExplanation())); }
    Collections.shuffle(out); return out;
  }
  private List<Question> generateWithAi(String topic,String difficulty,int count){
    try {
      String instruction="以繁體中文建立 "+count+" 道不同的 "+topic+" 程式語法選擇題，難度為 "+difficulty+"。僅回傳 JSON 物件，包含 questions 陣列；每題欄位為 prompt、code（字串或空字串）、options（4 個字串）、correctOption（從 0 開始的整數）、explanation（繁體中文解析）。題目不可重複。";
      Map<?,?> response=client.post().uri("/chat/completions").header("Authorization","Bearer "+apiKey)
        .body(Map.of("model",model,"temperature",0.7,"response_format",Map.of("type","json_object"),"messages",List.of(Map.of("role","user","content",instruction))))
        .retrieve().body(Map.class);
      Map<?,?> choice=(Map<?,?>)((List<?>)response.get("choices")).get(0);
      Map<?,?> message=(Map<?,?>)choice.get("message");
      Map<?,?> parsed=mapper.readValue((String)message.get("content"),Map.class);
      List<?> rows=(List<?>)parsed.get("questions");
      return rows.stream().map(row->{Map<?,?> q=(Map<?,?>)row;return new Question((String)q.get("prompt"),Objects.toString(q.get("code"),""),((List<?>)q.get("options")).stream().map(Object::toString).toList(),((Number)q.get("correctOption")).intValue(),(String)q.get("explanation"));}).toList();
    } catch(Exception e) { throw new IllegalStateException("AI question generation failed: "+e.getMessage(),e); }
  }
}
