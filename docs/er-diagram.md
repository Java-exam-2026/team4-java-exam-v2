# ER Diagram / ER図

## Overview / 概要

このER図は `src/main/resources/schema.sql` をもとに、`team4-java-exam-v2` の主要テーブル構成とリレーションを Mermaid 記法で表したものです。

- `users`: ログインユーザー情報
- `chapters`: 学習章情報
- `questions`: 各章に属する問題
- `user_progress`: ユーザーごとの章別進捗
- `user_answers`: ユーザーごとの問題解答履歴

## ER Diagram (Mermaid) / ER図（Mermaid記法）

```mermaid
erDiagram
    
    USERS {
        VARCHAR id PK "NOT NULL,ユーザーID(最大36文字)"
        VARCHAR username UK "NOT NULL,ユーザー名(最大50文字)" 
        VARCHAR password "NOT NULL,ユーザーパスワード(最大255文字)"
        VARCHAR display_name "NOT NULL,ユーザー表示名(最大100文字)"
        VARCHAR role "NOT NULL,ユーザー権限(ROLE_USER(一般ユーザー),ROLE_ADMIN(管理者))"
        TIMESTAMP created_at "ユーザー作成日のタイムスタンプ"
        TIMESTAMP updated_at "ユーザー情報アップデート時のタイムスタンプ"
    }

    CHAPTERS {
        VARCHAR id PK "チャプターID(最大36文字)"
        VARCHAR chapter_code UK "NOT NULL,チャプター数(最大20文字)"
        VARCHAR title "NOT NULL,チャプタータイトル(最大200文字)"
        INTEGER sort_order "NOT NULL,表示順"
    }

    QUESTIONS {
        VARCHAR id PK "問題ID(最大36文字)"
        VARCHAR chapter_id FK "NOT NULL,所属チャプターID(最大36文字)"
        TEXT question_text "NOT NULL,問題文"
        TEXT options "NOT NULL,選択肢(JSON形式)"
        VARCHAR question_type "NOT NULL,問題形式(最大20文字)"
        TEXT correct_answer "NOT NULL,答え"
        TIMESTAMP created_at "問題作成日のタイムスタンプ"
        TIMESTAMP updated_at "問題情報アップデート時のタイムスタンプ"
    }

    USER_PROGRESS {
        VARCHAR id PK "各ユーザーの進捗ID(最大36文字)"
        VARCHAR user_id FK "NOT NULL,ユーザーID(最大36文字)"
        VARCHAR chapter_id FK "NOT NULL,チャプターID(最大36文字)"
        INTEGER score "NOT NULL,スコア(0〜100)"
        BOOLEAN passed "NOT NULL,合否"
        BOOLEAN has_submitted "NOT NULL,提出済みフラグ"
        TIMESTAMP last_attempted_at "最終受験日時のタイムスタンプ"
    }

    USER_ANSWERS {
        VARCHAR id PK "各ユーザーの解答ID(最大36文字)"
        VARCHAR user_id FK "NOT NULL,ユーザーID(最大36文字)"
        VARCHAR chapter_id FK "NOT NULL,チャプターID(最大36文字)"
        VARCHAR question_id FK "NOT NULL,問題ID(最大36文字)"
        TEXT selected_answer "NOT NULL,選択した回答"
        BOOLEAN is_correct "NOT NULL,正誤判定"
        TIMESTAMP answered_at "回答日時のタイムスタンプ"
    }

    AUDIT_LOGS {
        VARCHAR id PK "ログID(最大36文字)"
        VARCHAR actor_user_id  "NOT NULL,操作者のユーザーID(最大36文字)"
        VARCHAR actor_username "NOT NULL,操作者のユーザー名(最大50文字)"
        VARCHAR actor_user_display_name "NOT NULL,操作者のユーザー表示名(最大100文字)"
        VARCHAR target_type "NOT NULL,操作対象(USER,QUESTION)"
        VARCHAR target_id "NOT NULL,操作対象のID(最大36文字)"
        TEXT target_name "NOT NULL,操作対象の名前" 
        VARCHAR action_type "NOT NULL 操作の種類(作成:CREATE,編集:EDIT,削除:DELETE)"
        BOOLEAN action_status "NOT NULL,操作の成功失敗を表すフラグ"
        TEXT changes_json "NULL,変更内容(JSON形式)"
        TIMESTAMP created_at "ログ作成日のタイムスタンプ"
    }

    CHAPTERS ||--o{ QUESTIONS : has
    USERS ||--o{ USER_PROGRESS : has
    CHAPTERS ||--o{ USER_PROGRESS : tracks
    USERS ||--o{ USER_ANSWERS : answers
    CHAPTERS ||--o{ USER_ANSWERS : in
    QUESTIONS ||--o{ USER_ANSWERS : for

```
## Relationship Details / リレーション詳細

| Relation / 関連 | Type / 種別 | 説明 (日本語) |
|---|---|---|   
| CHAPTERS → QUESTIONS | 1 : N (One-to-Many) | 各章は0個以上の問題を持つ |
| USERS → USER_PROGRESS | 1 : N (One-to-Many) | 各ユーザーは0以上の章の進捗を持つ |
| CHAPTERS → USER_PROGRESS | 1 : N (One-to-Many) | 章は0個以上の章の進捗を持つ |
| USERS → USER_ANSWERS | 1 : N (One-to-Many) | ユーザーはは0個以上の解答を持つ |
| QUESTIONS → USER_ANSWERS | 1 : N (One-to-Many) | 問題は0個以上のUSER_ANSWERを持つ |
| USER → AUDIT_LOGS | 1 : N (One-to-Many) | ユーザーは0個以上のAUDIT_LOGSを持つ |

## Constraints / 制約

| Table / テーブル | Column / カラム | 制約 (日本語) |
|---|---|---|
| users | id | PK / NOT NULL / ユーザーID(最大36文字) |
| users | username | UK / NOT NULL / ユーザー名(最大50文字) |
| users | password | NOT NULL / パスワード(最大255文字) |
| users | display_name | NOT NULL / 表示名(最大100文字) |
| users | role | NOT NULL / ROLE_USER / ROLE_ADMIN |
| users | created_at | NOT NULL / 作成日時 |
| users | updated_at | NOT NULL / 更新日時 |
| chapters | id | PK /  NOT NULL / チャプターID(最大36文字) |
| chapters | chapter_code | UK / NOT NULL / チャプターコード(最大20文字) |
| chapters | title | NOT NULL / チャプタータイトル(最大200文字) |
| chapters | sort_order |  NOT NULL / 表示順 |
| questions | id | PK / NOT NULL / 問題ID(最大36文字) |
| questions | chapter_id | FK / NOT NULL / ユーザーID(最大36文字) |
| questions | question_text | NOT NULL / 問題文 |
| questions | options | NOT NULL / 選択肢(JSON形式) |
| questions | question_type | NOT NULL / 問題形式(最大20文字) |
| questions | correct_answer | NOT NULL / 正解 |
| questions | created_at | NOT NULL / 作成日時 |
| questions | updated_at | NOT NULL / 更新日時 |
| user_progress | id | PK / 各ユーザーの進捗ID(最大36文字) |
| user_progress | user_id | FK(→ users.id) / NOT NULL / ユーザーID(最大36文字) |
| user_progress | chapter_id | FK(→ chapters.id) / NOT NULL / チャプターID(最大36文字) |
| user_progress | score | NOT NULL / スコア(0〜100) |
| user_progress | passed | NOT NULL / 合否 |
| user_progress | has_submitted | NOT NULL / 提出済みフラグ |
| user_progress | last_attempted_at | 最終受験日 |
| user_progress |  (user_id, chapter_id) | UNIQUE |
| user_answers | id | PK / NOT NULL / 解答ID(最大36文字) |
| user_answers | user_id | FK(→ users.id) / NOT NULL / ユーザーID(最大36文字) |
| user_answers | chapter_id | FK(→ chapters.id) / NOT NULL / チャプターID(最大36文字) |
| user_answers | question_id | FK(→ questions.id) / NOT NULL / 問題ID(最大36文字) |
| user_answers | selected_answer | NOT NULL / 選択した回答 |
| user_answers | is_correct | NOT NULL / 正誤判定 |
| user_answers | answered_at | 回答日時 |
| user_answers | (user_id, chapter_id, question_id) | UNIQUE |
| users / chapters / questions / user_progress / user_answers | id | PK / NOT NULL |
| 各テーブル | 外部キー（user_id / chapter_id / question_id） | ON DELETE CASCADE |
| AUDIT_LOGS | `id` | Primary Key, VARCHAR(36) | 主キー |



## Notes / 補足

- `user_progress` には `UNIQUE(user_id, chapter_id)` 制約があります。
- `user_answers` には `UNIQUE(user_id, chapter_id, question_id)` 制約があります。
- `questions.chapter_id` は `chapters.id` を参照します。
- `user_progress.user_id` は `users.id`、`user_progress.chapter_id` は `chapters.id` を参照します。
- `user_answers.user_id` は `users.id`、`user_answers.chapter_id` は `chapters.id`、`user_answers.question_id` は `questions.id` を参照します。
- 外部キーはいずれも `ON DELETE CASCADE` です。
