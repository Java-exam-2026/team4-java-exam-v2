# データベース設計ドキュメント

## 概要
Javaテストシステムのデータベース設計書。PostgreSQL向けのDDLとER図を含む。

## ER図 (概念図)

```
┌─────────────────┐         ┌──────────────────┐
│     users       │         │    chapters      │
├─────────────────┤         ├──────────────────┤
│ id (PK)         │         │ id (PK)          │
│ username        │◄────┐   │ chapter_code     │
│ password        │     │   │ title            │
│ display_name    │     │   │ sort_order       │
│ role            │     │   └──────────────────┘
│ created_at      │     │            ▲
│ updated_at      │     │            │
└─────────────────┘     │            │
         │              │            │
         │              │            │
         ▼              │            │
┌─────────────────┐     │   ┌──────────────────┐
│ user_progress   │─────┘   │    questions     │
├─────────────────┤         ├──────────────────┤
│ id (PK)         │─────────►│ id (PK)          │
│ user_id (FK)    │         │ chapter_id (FK)  │
│ chapter_id (FK) │         │ question_text    │
│ score           │         │ options (JSONB)  │
│ passed          │         │ correct_answer   │
│ last_attempted  │         │ created_at       │
└─────────────────┘         │ updated_at       │
UNIQUE(user_id, chapter_id) └──────────────────┘
```

## テーブル定義

### 1. users (ユーザーテーブル)
ユーザーアカウント情報を管理。一般ユーザーと管理者を区別。

| カラム名 | データ型 | 制約 | 説明 |
|---------|---------|------|------|
| id | UUID | PRIMARY KEY | ユーザーID |
| username | VARCHAR(50) | UNIQUE NOT NULL | ログインID |
| password | VARCHAR(255) | NOT NULL | ハッシュ化されたパスワード (BCrypt) |
| display_name | VARCHAR(100) | NOT NULL | 表示名 |
| role | VARCHAR(20) | NOT NULL | 役割 (ROLE_USER / ROLE_ADMIN) |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 作成日時 |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 更新日時 |

### 2. chapters (章テーブル)
Java学習の章を管理。問題は章ごとに分類される。

| カラム名 | データ型 | 制約 | 説明 |
|---------|---------|------|------|
| id | UUID | PRIMARY KEY | 章ID |
| chapter_code | VARCHAR(20) | UNIQUE NOT NULL | 章番号 (例: '3.2', '7.1.1') |
| title | VARCHAR(200) | NOT NULL | 章タイトル (例: '整数', '値の比較') |
| sort_order | INTEGER | NOT NULL | 表示順序 |

### 3. questions (問題テーブル)
各章の問題を管理。選択肢と正解を含む。

| カラム名 | データ型 | 制約 | 説明 |
|---------|---------|------|------|
| id | UUID | PRIMARY KEY | 問題ID |
| chapter_id | UUID | FOREIGN KEY | 章ID (chapters.id) |
| question_text | TEXT | NOT NULL | 問題文 |
| options | JSONB | NOT NULL | 選択肢 (例: {"A": "選択肢1", "B": "選択肢2"}) |
| correct_answer | VARCHAR(1) | NOT NULL | 正解 (A, B, C, D のいずれか) |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 作成日時 |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 更新日時 |

**インデックス:**
- `idx_questions_chapter_id` ON chapter_id (章ごとの問題検索用)

### 4. user_progress (進捗管理テーブル)
ユーザーごとの章別進捗状況を記録。

| カラム名 | データ型 | 制約 | 説明 |
|---------|---------|------|------|
| id | UUID | PRIMARY KEY | 進捗ID |
| user_id | UUID | FOREIGN KEY | ユーザーID (users.id) |
| chapter_id | UUID | FOREIGN KEY | 章ID (chapters.id) |
| score | INTEGER | NOT NULL | 得点 (0-100) |
| passed | BOOLEAN | NOT NULL | 合否フラグ (85%以上でtrue) |
| last_attempted_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | 最終解答日時 |

**制約:**
- UNIQUE(user_id, chapter_id): 同じユーザーが同じ章の進捗を複数持たない

**インデックス:**
- `idx_user_progress_user_id` ON user_id (ユーザー別進捗検索用)

## DDL (PostgreSQL)

```sql
-- users テーブル
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- chapters テーブル
CREATE TABLE chapters (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    chapter_code VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(200) NOT NULL,
    sort_order INTEGER NOT NULL
);

-- questions テーブル
CREATE TABLE questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    chapter_id UUID NOT NULL,
    question_text TEXT NOT NULL,
    options JSONB NOT NULL,
    correct_answer VARCHAR(1) NOT NULL CHECK (correct_answer IN ('A', 'B', 'C', 'D')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE
);

CREATE INDEX idx_questions_chapter_id ON questions(chapter_id);

-- user_progress テーブル
CREATE TABLE user_progress (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    chapter_id UUID NOT NULL,
    score INTEGER NOT NULL CHECK (score >= 0 AND score <= 100),
    passed BOOLEAN NOT NULL,
    last_attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE,
    UNIQUE(user_id, chapter_id)
);

CREATE INDEX idx_user_progress_user_id ON user_progress(user_id);

-- updated_at自動更新トリガー
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_questions_updated_at BEFORE UPDATE ON questions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

## 初期データ例

```sql
-- 管理者ユーザー (パスワード: admin123)
INSERT INTO users (username, password, display_name, role) VALUES
('admin', '$2a$10$xJWN.2gPVX0mU8R1FQKGYuV5Yq0ZwKYOQQ3qP1v1dXx7kQgKL.Zzy', '管理者', 'ROLE_ADMIN');

-- 一般ユーザー (パスワード: user123)
INSERT INTO users (username, password, display_name, role) VALUES
('testuser', '$2a$10$xJWN.2gPVX0mU8R1FQKGYuV5Yq0ZwKYOQQ3qP1v1dXx7kQgKL.Zzy', 'テストユーザー', 'ROLE_USER');

-- 章データ
INSERT INTO chapters (chapter_code, title, sort_order) VALUES
('1.1', 'Javaの基礎', 1),
('2.1', '変数とデータ型', 2),
('3.1', '演算子', 3),
('3.2', '整数', 4);
```

## 設計ポリシー

1. **セキュリティ**: パスワードは必ずBCryptでハッシュ化
2. **一貫性**: UUID主キーで分散環境にも対応
3. **順序制御**: 章は`sort_order`で表示順を制御
4. **進捗管理**: 同じユーザー×章の組み合わせは1レコードのみ (UNIQUE制約)
5. **カスケード削除**: 章や問題を削除した場合、関連データも削除
6. **監査**: created_at/updated_atで変更履歴を追跡

## バージョン履歴

| バージョン | 日付 | 変更内容 |
|-----------|------|---------|
| 1.0 | 2025-10-26 | 初版作成 |
