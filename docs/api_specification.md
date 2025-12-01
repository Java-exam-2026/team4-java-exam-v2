# API仕様書

## 概要
Javaテストシステムのバックエンド REST API仕様。

## ベースURL
```
http://localhost:8080/api
```

## 認証方式
JWT (JSON Web Token) を使用。

- ログイン後、`Authorization: Bearer <token>` ヘッダーに付与
- トークンの有効期限: 24時間

---

## 1. 認証 (AuthController)

### 1.1. POST /api/auth/login
ユーザー認証を行い、JWTトークンを発行する。

**リクエスト:**
```json
{
  "username": "testuser",
  "password": "user123"
}
```

**レスポンス (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "testuser",
  "role": "ROLE_USER"
}
```

**エラー (401 Unauthorized):**
```json
{
  "error": "Invalid credentials"
}
```

---

### 1.2. GET /api/auth/me
ログイン中のユーザー情報を取得する。

**ヘッダー:**
```
Authorization: Bearer <token>
```

**レスポンス (200 OK):**
```json
{
  "username": "testuser",
  "displayName": "テストユーザー",
  "role": "ROLE_USER"
}
```

---

## 2. 章 (ChapterController)

### 2.1. GET /api/chapters
全ての章の一覧を取得する。

**ヘッダー:**
```
Authorization: Bearer <token>
```

**レスポンス (200 OK):**
```json
[
  {
    "id": "uuid-1",
    "chapterCode": "1.1",
    "title": "Javaの基礎"
  },
  {
    "id": "uuid-2",
    "chapterCode": "2.1",
    "title": "変数とデータ型"
  }
]
```

---

## 3. クイズ (QuizController)

### 3.1. GET /api/quiz/{chapterCode}
指定された章の問題を20問ランダムで取得する。

**ヘッダー:**
```
Authorization: Bearer <token>
```

**パスパラメータ:**
- `chapterCode`: 章番号 (例: "3.2")

**レスポンス (200 OK):**
```json
[
  {
    "id": "uuid-q1",
    "chapterCode": "3.2",
    "questionText": "次のうち、整数型でないものはどれか?",
    "options": {
      "A": "int",
      "B": "long",
      "C": "double",
      "D": "byte"
    }
  },
  // ... 19問
]
```

**注意:** `correctAnswer` は含まれません。

**エラー (404 Not Found):**
```json
{
  "error": "Chapter not found"
}
```

---

### 3.2. POST /api/quiz/submit/{chapterCode}
解答を送信し、採点する。

**ヘッダー:**
```
Authorization: Bearer <token>
```

**パスパラメータ:**
- `chapterCode`: 章番号

**リクエスト:**
```json
{
  "chapterCode": "3.2",
  "answers": [
    {
      "questionId": "uuid-q1",
      "selectedAnswer": "C"
    },
    {
      "questionId": "uuid-q2",
      "selectedAnswer": "A"
    }
    // ... 20問分
  ]
}
```

**レスポンス (200 OK):**
```json
{
  "chapterCode": "3.2",
  "score": 90,
  "passed": true
}
```

**採点ルール:**
- 正答率を計算 (score = 正解数 / 全問題数 × 100)
- 85%以上で `passed = true`
- `user_progress` テーブルに保存/更新

---

## 4. 進捗 (ProgressController)

### 4.1. GET /api/progress/my-progress
ログイン中のユーザーの全進捗を取得する。

**ヘッダー:**
```
Authorization: Bearer <token>
```

**レスポンス (200 OK):**
```json
[
  {
    "chapterCode": "1.1",
    "title": "Javaの基礎",
    "score": 95,
    "passed": true,
    "lastAttemptedAt": "2025-10-25T14:30:00Z"
  },
  {
    "chapterCode": "2.1",
    "title": "変数とデータ型",
    "score": null,
    "passed": false,
    "lastAttemptedAt": null
  }
]
```

**注意:** 未解答の章も含めて全章を返す。

---

### 4.2. GET /api/progress/all
全ユーザーの進捗一覧を取得する (管理者のみ)。

**ヘッダー:**
```
Authorization: Bearer <admin-token>
```

**権限:** `ROLE_ADMIN`

**レスポンス (200 OK):**
```json
[
  {
    "username": "testuser",
    "displayName": "テストユーザー",
    "chapterCode": "1.1",
    "title": "Javaの基礎",
    "score": 95,
    "passed": true,
    "lastAttemptedAt": "2025-10-25T14:30:00Z"
  },
  // ...
]
```

**エラー (403 Forbidden):**
```json
{
  "error": "Access denied"
}
```

---

## 5. 管理者: 問題管理 (AdminQuestionController)

### 5.1. GET /api/admin/questions
問題をページネーション付きで一覧取得する。

**ヘッダー:**
```
Authorization: Bearer <admin-token>
```

**権限:** `ROLE_ADMIN`

**クエリパラメータ:**
- `page`: ページ番号 (デフォルト: 0)
- `size`: 1ページあたりの件数 (デフォルト: 20)

**レスポンス (200 OK):**
```json
{
  "content": [
    {
      "id": "uuid-q1",
      "chapterCode": "3.2",
      "questionText": "次のうち、整数型でないものはどれか?",
      "options": {
        "A": "int",
        "B": "long",
        "C": "double",
        "D": "byte"
      },
      "correctAnswer": "C"
    }
  ],
  "totalPages": 5,
  "totalElements": 100,
  "number": 0,
  "size": 20
}
```

---

### 5.2. GET /api/admin/questions/{id}
特定の問題を詳細取得する。

**ヘッダー:**
```
Authorization: Bearer <admin-token>
```

**権限:** `ROLE_ADMIN`

**パスパラメータ:**
- `id`: 問題ID (UUID)

**レスポンス (200 OK):**
```json
{
  "id": "uuid-q1",
  "chapterCode": "3.2",
  "questionText": "次のうち、整数型でないものはどれか?",
  "options": {
    "A": "int",
    "B": "long",
    "C": "double",
    "D": "byte"
  },
  "correctAnswer": "C"
}
```

---

### 5.3. POST /api/admin/questions
新しい問題を作成する。

**ヘッダー:**
```
Authorization: Bearer <admin-token>
```

**権限:** `ROLE_ADMIN`

**リクエスト:**
```json
{
  "chapterCode": "3.2",
  "questionText": "次のうち、整数型でないものはどれか?",
  "options": {
    "A": "int",
    "B": "long",
    "C": "double",
    "D": "byte"
  },
  "correctAnswer": "C"
}
```

**レスポンス (201 Created):**
```json
{
  "id": "uuid-new",
  "chapterCode": "3.2",
  "questionText": "次のうち、整数型でないものはどれか?",
  "options": {
    "A": "int",
    "B": "long",
    "C": "double",
    "D": "byte"
  },
  "correctAnswer": "C"
}
```

---

### 5.4. PUT /api/admin/questions/{id}
既存の問題を更新する。

**ヘッダー:**
```
Authorization: Bearer <admin-token>
```

**権限:** `ROLE_ADMIN`

**パスパラメータ:**
- `id`: 問題ID

**リクエスト:**
```json
{
  "chapterCode": "3.2",
  "questionText": "更新された問題文",
  "options": {
    "A": "選択肢A",
    "B": "選択肢B",
    "C": "選択肢C",
    "D": "選択肢D"
  },
  "correctAnswer": "A"
}
```

**レスポンス (200 OK):**
```json
{
  "id": "uuid-q1",
  "chapterCode": "3.2",
  "questionText": "更新された問題文",
  "options": {
    "A": "選択肢A",
    "B": "選択肢B",
    "C": "選択肢C",
    "D": "選択肢D"
  },
  "correctAnswer": "A"
}
```

---

### 5.5. DELETE /api/admin/questions/{id}
既存の問題を削除する。

**ヘッダー:**
```
Authorization: Bearer <admin-token>
```

**権限:** `ROLE_ADMIN`

**パスパラメータ:**
- `id`: 問題ID

**レスポンス (204 No Content):**
(レスポンスボディなし)

---

## エラーレスポンス形式

全エンドポイント共通:

**400 Bad Request:**
```json
{
  "error": "Validation failed",
  "details": ["field: error message"]
}
```

**401 Unauthorized:**
```json
{
  "error": "Authentication required"
}
```

**403 Forbidden:**
```json
{
  "error": "Access denied"
}
```

**404 Not Found:**
```json
{
  "error": "Resource not found"
}
```

**500 Internal Server Error:**
```json
{
  "error": "Internal server error"
}
```

---

## バージョン履歴

| バージョン | 日付 | 変更内容 |
|-----------|------|---------|
| 1.0 | 2025-10-26 | 初版作成 |
