-- ======================================================
-- 1. テーブル削除 (外部キー制約を考慮し、子テーブルから削除)
-- ======================================================
DROP TABLE IF EXISTS user_answers;
DROP TABLE IF EXISTS user_progress;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS chapters;
DROP TABLE IF EXISTS users;

-- ======================================================
-- 2. テーブル作成 (親テーブルから作成)
-- ======================================================

-- users テーブル
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- chapters テーブル
CREATE TABLE chapters (
    id VARCHAR(36) PRIMARY KEY,
    chapter_code VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(200) NOT NULL,
    sort_order INTEGER NOT NULL
);

-- questions テーブル
CREATE TABLE questions (
    id VARCHAR(36) PRIMARY KEY,
    chapter_id VARCHAR(36) NOT NULL,
    question_text TEXT NOT NULL,
    options TEXT NOT NULL,
    question_type VARCHAR(20) NOT NULL DEFAULT 'SINGLE_CHOICE',
    correct_answer TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE
);

-- user_progress テーブル (進捗管理用)
CREATE TABLE user_progress (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    chapter_id VARCHAR(36) NOT NULL,
    score INTEGER NOT NULL DEFAULT 0 CHECK (score >= 0 AND score <= 100),
    passed BOOLEAN NOT NULL DEFAULT FALSE,
    -- 課題要件：ステータス管理
    status TEXT NOT NULL DEFAULT 'IN_PROGRESS' CHECK (status IN ('IN_PROGRESS', 'COMPLETED')),
    last_attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE,
    UNIQUE(user_id, chapter_id)
);

-- user_answers テーブル (個別の回答保存用)
CREATE TABLE user_answers (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    chapter_id VARCHAR(36) NOT NULL,
    question_id VARCHAR(36) NOT NULL,
    selected_answer TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE, -- 一時保存時は判定前のためデフォルト値を設定
    has_submitted BOOLEAN DEFAULT FALSE NOT NULL, -- 課題要件：一時保存フラグ
    answered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    UNIQUE(user_id, chapter_id, question_id)
);

-- ======================================================
-- 3. インデックス作成 (検索パフォーマンス向上)
-- ======================================================
CREATE INDEX idx_questions_chapter_id ON questions(chapter_id);
CREATE INDEX idx_user_progress_user_id ON user_progress(user_id);
CREATE INDEX idx_user_answers_user_chapter ON user_answers(user_id, chapter_id);
CREATE INDEX idx_user_answers_question ON user_answers(question_id);

-- ======================================================
-- 4. 運用時のクエリ例 (プログラムから呼び出す際の参考に)
-- ======================================================

/*
  【一時保存：回答のアップサート】
  すでに回答があれば更新、なければ新規作成。常に has_submitted = false にする。
*/
-- INSERT INTO user_answers (id, user_id, chapter_id, question_id, selected_answer, is_correct, has_submitted)
-- VALUES ('uuid-1', 'user-50', 'chap-1', 'q-1', '選択した回答内容', false, false)
-- ON CONFLICT(user_id, chapter_id, question_id) 
-- DO UPDATE SET selected_answer = EXCLUDED.selected_answer, has_submitted = false;

/*
  【最終提出：ステータス更新】
  回答を確定させ、進捗を COMPLETED にする。
*/
-- UPDATE user_answers SET has_submitted = true WHERE user_id = 'user-50' AND chapter_id = 'chap-1';
-- UPDATE user_progress SET status = 'COMPLETED' WHERE user_id = 'user-50' AND chapter_id = 'chap-1';