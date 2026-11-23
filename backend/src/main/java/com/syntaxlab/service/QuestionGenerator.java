package com.syntaxlab.service;

import com.syntaxlab.domain.Question;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestClient;
import java.util.*;

@Component
public class QuestionGenerator {
  private final Map<String,List<Question>> bank=new HashMap<>();
  private final Map<String,Set<String>> lastBatchByTopic=new HashMap<>();
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
    extend("javascript",
      q("let 與 const 的主要差異？","",List.of("let 可重新指派，const 不可","const 只能存數字","let 沒有作用域","兩者相同"),0,"let 可重新指派；const 綁定後不能重新指派。"),
      q("哪個運算子同時比較值與型別？","",List.of("==","===","=","!="),1,"=== 是嚴格相等比較，不會先轉換型別。"),
      q("filter 方法會回傳什麼？","",List.of("第一個符合值","符合條件的新陣列","原陣列長度","布林值"),1,"filter 會建立只包含符合條件項目的新陣列。"),
      q("哪個關鍵字可在 async 函式中等待 Promise？","",List.of("await","defer","pause","yield"),0,"await 會等待 Promise 完成並取得結果。"),
      q("選擇性串連運算子 ?. 的用途？","",List.of("避免讀取 null 時出錯","宣告常數","轉換字串","建立迴圈"),0,"左側為 null 或 undefined 時會短路並回傳 undefined。"),
      q("哪個陣列方法會在末端加入項目？","",List.of("push","map","slice","filter"),0,"push 會將項目加到陣列尾端。"));
    extend("html",
      q("哪個標籤代表最高層級標題？","",List.of("<h1>","<heading>","<title>","<head>"),0,"h1 是頁面內容中最高層級的標題。"),
      q("表單欄位的說明文字應使用哪個元素？","",List.of("<label>","<caption>","<legend>","<summary>"),0,"label 可與表單欄位建立關聯。"),
      q("哪個標籤用來載入外部 CSS？","",List.of("<style>","<script>","<link>","<css>"),2,"link 標籤搭配 rel='stylesheet' 載入樣式表。"),
      q("按鈕在表單中的預設類型是什麼？","",List.of("submit","button","reset","menu"),0,"button 預設為 submit；不送出表單時需指定 type='button'。"),
      q("哪個元素適合放置主要導覽連結？","",List.of("<nav>","<aside>","<footer>","<address>"),0,"nav 語意元素表示主要導覽區塊。"),
      q("哪個元素適合表示一篇獨立文章？","",List.of("<article>","<div>","<span>","<meta>"),0,"article 適合可獨立分發或重用的內容。"),
      q("要建立有序清單，應使用哪個標籤？","",List.of("<ul>","<ol>","<dl>","<list>"),1,"ol 建立有順序的清單，li 表示每個項目。"));
    extend("css",
      q("哪個選擇器會選取 class 為 card 的元素？","",List.of("#card",".card","card","*card"),1,"句點加上名稱會選取對應的 class。"),
      q("哪個屬性可設定元素內部留白？","",List.of("margin","padding","gap","inset"),1,"padding 是內容與邊框之間的內距。"),
      q("Grid 中 1fr 代表什麼？","",List.of("固定 1px","一份剩餘空間","100% 寬度","一個字元"),1,"fr 單位會將可用空間分成比例份額。"),
      q("哪個媒體查詢會在寬度 600px 以下套用？","",List.of("@media (max-width: 600px)","@screen < 600","@responsive(600)","@media-width:600"),0,"max-width 條件涵蓋指定寬度以下的視窗。"),
      q("哪個單位依根元素字級縮放？","",List.of("em","rem","vh","px"),1,"rem 以根元素的 font-size 為基準。"),
      q("哪個偽類表示滑鼠位於元素上方？","",List.of(":focus",":hover",":active",":visited"),1,":hover 會在指標懸停時套用。"),
      q("哪個屬性設定元素外側空間？","",List.of("padding","margin","border","outline"),1,"margin 是元素邊框外側的間距。"));
    extend("java",
      q("Java 程式的進入點方法名稱？","",List.of("start","main","run","init"),1,"應用程式從 public static void main 開始。"),
      q("哪個修飾詞只允許同一類別直接存取？","",List.of("public","protected","private","static"),2,"private 將成員限制在宣告它的類別內。"),
      q("建立物件時會呼叫哪一種成員？","",List.of("建構子","介面","套件","例外"),0,"建構子負責初始化新建立的物件。"),
      q("哪個集合型別會排除重複元素？","",List.of("List","Set","Queue","ArrayList"),1,"Set 中的元素具有唯一性。"),
      q("哪個關鍵字表示類別實作介面？","",List.of("extends","implements","interface","super"),1,"類別使用 implements 實作介面。"),
      q("哪個關鍵字可避免方法被子類別覆寫？","",List.of("static","final","private","const"),1,"final 方法不能在子類別中覆寫。"),
      q("哪個關鍵字可從方法回傳結果？","",List.of("return","yield","break","exit"),0,"return 會結束方法並回傳指定值。"),
      q("哪個型別可表示單一字元？","",List.of("char","String","character","byte"),0,"char 是 Java 的原始字元型別。"));
    extend("python",
      q("Python 的列表索引從哪個數字開始？","",List.of("0","1","-1","視列表而定"),0,"Python 序列使用從 0 開始的索引。"),
      q("哪個關鍵字用來建立條件分支？","",List.of("when","if","case","switch"),1,"if 會依條件真假決定執行程式區塊。"),
      q("字典中如何取得 key 對應的值？","data['name']",List.of("圓括號","方括號","大括號","尖括號"),1,"字典使用方括號與 key 存取 value。"),
      q("哪個方法會把元素加到列表尾端？","",List.of("add","append","push","insertEnd"),1,"list.append(value) 會將值加入列表尾端。"),
      q("Python 的 None 通常代表什麼？","",List.of("零","空字串","沒有值","False 的別名"),2,"None 代表缺少或尚未設定的值。"),
      q("哪個關鍵字可匯入模組？","",List.of("include","import","require","using"),1,"使用 import 載入模組或套件。"),
      q("Python 區塊通常使用什麼表示？","",List.of("大括號","縮排","分號","begin/end"),1,"Python 以縮排表示程式區塊層級。"),
      q("哪個運算子可比較兩個值是否相等？","",List.of("=","==","===","equals"),1,"== 是比較相等的運算子；= 用來指定值。"));
  }
  private void extend(String topic, Question... extras) {
    List<Question> questions=new ArrayList<>(bank.get(topic));
    questions.addAll(List.of(extras));
    bank.put(topic,List.copyOf(questions));
  }
  private Question q(String p,String code,List<String> o,int answer,String why){return new Question(p,code,o,answer,why);}
  public synchronized List<Question> generate(String topic,String difficulty,int count){
    if(apiKey!=null&&!apiKey.isBlank()) return generateWithAi(topic,difficulty,count);
    var source=bank.get(topic.toLowerCase(Locale.ROOT));
    if(source==null) throw new IllegalArgumentException("Unsupported topic: "+topic);
    List<Question> candidates=source.stream().filter(q->!lastBatchByTopic.getOrDefault(topic.toLowerCase(Locale.ROOT),Set.of()).contains(q.getPrompt())).collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    if(candidates.size()<count)candidates=new ArrayList<>(source);
    Collections.shuffle(candidates);
    List<Question> selected=candidates.stream().limit(count).toList();
    lastBatchByTopic.put(topic.toLowerCase(Locale.ROOT),selected.stream().map(Question::getPrompt).collect(java.util.stream.Collectors.toSet()));
    return selected.stream().map(q->new Question(q.getPrompt(),q.getCode(),q.getOptions(),q.getCorrectOption(),q.getExplanation())).toList();
  }
  private List<Question> generateWithAi(String topic,String difficulty,int count){
    try {
      String instruction="以繁體中文建立 "+count+" 道全新的 "+topic+" 程式語法選擇題，難度為 "+difficulty+"。每次呼叫都要更換題目與考點。題組識別碼："+UUID.randomUUID()+"。僅回傳 JSON 物件，包含 questions 陣列；每題欄位為 prompt、code（字串或空字串）、options（4 個字串）、correctOption（從 0 開始的整數）、explanation（繁體中文解析）。題目不可重複。";
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
