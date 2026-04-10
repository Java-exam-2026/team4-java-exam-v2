erDiagram
    
    USERS {
        VARCHAR id PK "NOT NULL,ユーザーID"
        VARCHAR username UK "NOT NULL,ユーザー名" 
        VARCHAR password "NOT NULL,パスワード"
        VARCHAR display_name "NOT NULL,表示名"
        VARCHAR role "NOT NULL,権限"
        TIMESTAMP created_at "作成日"
        TIMESTAMP updated_at "更新日"
    }

    CHAPTERS {
        VARCHAR id PK "チャプターID"
        VARCHAR chapter_code UK "NOT NULL,コード"
        VARCHAR title "NOT NULL,タイトル"
        INTEGER sort_order "NOT NULL,表示順"
    }

    QUESTIONS {
        VARCHAR id PK "問題ID"
        VARCHAR chapter_id FK "NOT NULL,チャプターID"
        TEXT question_text "NOT NULL,問題文"
        TEXT options "NOT NULL,選択肢(JSON)"
        VARCHAR question_type "NOT NULL,形式"
        TEXT correct_answer "NOT NULL,答え"
        TIMESTAMP created_at "作成日"
        TIMESTAMP updated_at "更新日"
    }

    USER_PROGRESS {
        VARCHAR id PK "進捗ID"
        VARCHAR user_id FK "NOT NULL,ユーザーID"
        VARCHAR chapter_id FK "NOT NULL,チャプターID"
        INTEGER score "NOT NULL,スコア"
        BOOLEAN passed "NOT NULL,合否"
        VARCHAR status "NOT NULL,状態(IN_PROGRESS/COMPLETED)" %% 追加
        TIMESTAMP last_attempted_at "最終受験日時"
    }

    USER_ANSWERS {
        VARCHAR id PK "解答ID"
        VARCHAR user_id FK "NOT NULL,ユーザーID"
        VARCHAR chapter_id FK "NOT NULL,チャプターID"
        VARCHAR question_id FK "NOT NULL,問題ID"
        TEXT selected_answer "NOT NULL,回答"
        BOOLEAN is_correct "NOT NULL,正誤"
        BOOLEAN has_submitted "NOT NULL,提出済みフラグ" %% 追加
        TIMESTAMP answered_at "回答日時"
    }

    CHAPTERS ||--o{ QUESTIONS : has
    USERS ||--o{ USER_PROGRESS : has
    CHAPTERS ||--o{ USER_PROGRESS : tracks
    USERS ||--o{ USER_ANSWERS : answers
    CHAPTERS ||--o{ USER_ANSWERS : in
    QUESTIONS ||--o{ USER_ANSWERS : for