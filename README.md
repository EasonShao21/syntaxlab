# SyntaxLab 語法練習

以 Vue 3 與 Spring Boot 3 建置的程式語法練習網站。可依 HTML、CSS、JavaScript 等主題產生練習題、即時作答與查看解析，並保存歷史練習供複習。

## 快速開始

需求：Java 17+、Maven 3.9+、Node.js 20+。

```bash
# API（http://localhost:8080）
cd backend
mvn spring-boot:run

# 新終端機啟動 Vue（http://localhost:5173）
cd frontend
npm install
npm run dev
```

預設使用 H2 檔案資料庫，資料保存在 `backend/data/`。第一次啟動會建立題庫種子資料。

## 線上展示（GitHub Pages）

網站網址：<https://easonshao21.github.io/syntaxlab/>

推送到 `main` 後，GitHub Actions 會自動建置並部署 Vue 前端。Pages 展示模式使用瀏覽器內的示範題庫，作答紀錄保存在使用者目前的瀏覽器。Spring Boot API 與 H2 資料庫不會部署到 GitHub Pages；本機完整模式仍可依照快速開始步驟啟動前後端。

### AI 題目生成

未設定金鑰時，服務會從依語法主題整理的內建題庫產生題目，讓網站可直接試用。若設定 `OPENAI_API_KEY`，題目生成器會呼叫 OpenAI 相容的 Chat Completions API；可用 `AI_BASE_URL` 與 `AI_MODEL` 指定端點及模型。環境變數範例見 [`backend/.env.example`](backend/.env.example)。金鑰只放在後端環境變數，不要提交到版本控制。

## OpenAPI 與領域模型

- OpenAPI 3 規格原始檔：[`openapi/openapi.yaml`](openapi/openapi.yaml)
- 執行中的 Swagger UI：<http://localhost:8080/swagger-ui/index.html>
- JSON 規格：<http://localhost:8080/v3/api-docs>
- 領域模型：[`backend/src/main/java/com/syntaxlab/domain/`](backend/src/main/java/com/syntaxlab/domain/)，包含 `Topic`、`Exercise`、`Question`、`Attempt`、`Answer` 等實體。
- API DTO：[`backend/src/main/java/com/syntaxlab/api/dto/`](backend/src/main/java/com/syntaxlab/api/dto/)

主要 API：`GET /api/topics`、`POST /api/exercises/generate`、`GET /api/exercises/{id}`、`POST /api/exercises/{id}/submit`、`GET /api/attempts`、`GET /api/attempts/{id}`。

## 專案結構

```text
backend/     Spring Boot 3 REST API、JPA 領域模型、H2
frontend/    Vue 3、Vite、響應式介面
openapi/     OpenAPI 3.0 YAML 契約
```

## GitHub

原始碼 repository：[EasonShao21/syntaxlab](https://github.com/EasonShao21/syntaxlab)。推送到 `main` 後會自動更新 GitHub Pages 展示站。
