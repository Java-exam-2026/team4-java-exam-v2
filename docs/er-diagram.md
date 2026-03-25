# ER Diagram / ER図

## Overview / 概要

このER図は `src/main/resources/schema.sql` をもとに、`team4-java-exam-v2` の主要テーブル構成とリレーションを Mermaid 記法で表したものです。

- `users`: ログインユーザー情報
- `chapters`: 学習章情報
- `questions`: 各章に属する問題
- `user_progress`: ユーザーごとの章別進捗
- `user_answers`: ユーザーごとの問題回答履歴

## Mermaid

```mermaid
erDiagram
    USERS ||--o{ USER_PROGRESS : has
    CHAPTERS ||--o{ USER_PROGRESS : tracked_in
    CHAPTERS ||--o{ QUESTIONS : contains
    USERS ||--o{ USER_ANSWERS : submits
    CHAPTERS ||--o{ USER_ANSWERS : answered_in
    QUESTIONS ||--o{ USER_ANSWERS : target

    USERS {
        VARCHAR_36 id PK
        VARCHAR_50 username UK
        VARCHAR_255 password
        VARCHAR_100 display_name
        VARCHAR_20 role
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    CHAPTERS {
        VARCHAR_36 id PK
        VARCHAR_20 chapter_code UK
        VARCHAR_200 title
        INTEGER sort_order
    }

    QUESTIONS {
        VARCHAR_36 id PK
        VARCHAR_36 chapter_id FK
        TEXT question_text
        TEXT options
        VARCHAR_20 question_type
        TEXT correct_answer
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    USER_PROGRESS {
        VARCHAR_36 id PK
        VARCHAR_36 user_id FK
        VARCHAR_36 chapter_id FK
        INTEGER score
        BOOLEAN passed
        BOOLEAN has_submitted
        TIMESTAMP last_attempted_at
    }

    USER_ANSWERS {
        VARCHAR_36 id PK
        VARCHAR_36 user_id FK
        VARCHAR_36 chapter_id FK
        VARCHAR_36 question_id FK
        TEXT selected_answer
        BOOLEAN is_correct
        TIMESTAMP answered_at
    }
```

## Notes / 補足

- `user_progress` には `UNIQUE(user_id, chapter_id)` 制約があります。
- `user_answers` には `UNIQUE(user_id, chapter_id, question_id)` 制約があります。
- `questions.chapter_id` は `chapters.id` を参照します。
- `user_progress.user_id` は `users.id`、`user_progress.chapter_id` は `chapters.id` を参照します。
- `user_answers.user_id` は `users.id`、`user_answers.chapter_id` は `chapters.id`、`user_answers.question_id` は `questions.id` を参照します。
- 外部キーはいずれも `ON DELETE CASCADE` です。
