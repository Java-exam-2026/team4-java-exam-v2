# フロントエンド画面遷移図

## 画面構成

### 1. 画面一覧

| 画面名 | パス | 必要な権限 | 説明 |
|-------|------|----------|------|
| ログイン画面 | `/login` | なし | ユーザー認証 |
| ダッシュボード | `/dashboard` | ROLE_USER | 章一覧と進捗表示 |
| クイズ画面 | `/quiz/{chapterCode}` | ROLE_USER | 問題解答 |
| 管理者ダッシュボード | `/admin/dashboard` | ROLE_ADMIN | 全体進捗確認 |
| 問題一覧 | `/admin/questions` | ROLE_ADMIN | 問題管理 |
| 問題作成/編集 | `/admin/questions/new`<br>`/admin/questions/edit/{id}` | ROLE_ADMIN | 問題のCRUD |

---

## 2. 画面遷移図

```
┌─────────────────────┐
│   ログイン画面        │
│   /login            │
└──────────┬──────────┘
           │ ログイン成功
           │
           ├─────────────────────────────────────┐
           │                                     │
           ▼ (ROLE_USER)                       ▼ (ROLE_ADMIN)
┌─────────────────────┐              ┌─────────────────────┐
│  ダッシュボード       │              │ 管理者ダッシュボード  │
│  /dashboard         │              │ /admin/dashboard    │
│                     │              │                     │
│ - 章一覧表示         │              │ - 全ユーザー進捗     │
│ - 進捗ステータス表示 │              │ - サマリー統計       │
│ - 次の章へボタン     │              ├─────────────────────┤
│   (前章合格で有効)   │              │ [問題管理へ]        │
└──────────┬──────────┘              └──────────┬──────────┘
           │                                     │
           │ 章を選択                             │
           ▼                                     ▼
┌─────────────────────┐              ┌─────────────────────┐
│   クイズ画面         │              │   問題一覧           │
│   /quiz/{code}      │              │   /admin/questions  │
│                     │              │                     │
│ - 20問表示          │              │ - ページネーション   │
│ - 選択肢ラジオボタン │              │ - 検索フィルター     │
│ - 解答送信          │              ├─────────────────────┤
└──────────┬──────────┘              │ [新規作成]          │
           │                         │ [編集] [削除]       │
           │ 送信                     └──────────┬──────────┘
           ▼                                     │
┌─────────────────────┐                          │ 新規/編集
│   結果画面           │                          ▼
│   (モーダル)         │              ┌─────────────────────┐
│                     │              │   問題作成/編集       │
│ - スコア表示         │              │   /admin/questions/  │
│ - 合否判定           │              │   new または edit/:id│
│ - ダッシュボードへ   │              │                     │
└─────────────────────┘              │ - 章選択             │
                                     │ - 問題文入力         │
                                     │ - 選択肢入力 (A-D)   │
                                     │ - 正解選択           │
                                     ├─────────────────────┤
                                     │ [保存] [キャンセル]  │
                                     └─────────────────────┘
```

---

## 3. ナビゲーションガード (router.beforeEach)

### ルール:

1. **未認証ユーザー:**
   - `/login` 以外のページにアクセス → `/login` にリダイレクト

2. **ROLE_USER:**
   - `/admin/**` にアクセス → `/dashboard` にリダイレクト

3. **ROLE_ADMIN:**
   - 全ページアクセス可能

### 実装イメージ:

```javascript
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // ログイン画面は誰でもアクセス可能
  if (to.path === '/login') {
    return next()
  }
  
  // 未認証はログインへリダイレクト
  if (!authStore.isAuthenticated) {
    return next('/login')
  }
  
  // 管理者ページは管理者のみ
  if (to.path.startsWith('/admin') && !authStore.isAdmin) {
    return next('/dashboard')
  }
  
  next()
})
```

---

## 4. 画面詳細

### 4.1. ログイン画面 (/login)

**コンポーネント:** `LoginView.vue`

**表示内容:**
- ユーザー名入力フィールド
- パスワード入力フィールド
- ログインボタン

**動作:**
1. フォーム送信 → `POST /api/auth/login`
2. 成功 → JWTトークンとユーザー情報をPiniaストアに保存
3. ロールに応じてリダイレクト:
   - `ROLE_USER` → `/dashboard`
   - `ROLE_ADMIN` → `/admin/dashboard`

---

### 4.2. ダッシュボード (/dashboard)

**コンポーネント:** `DashboardView.vue`

**表示内容:**
- ユーザー名表示
- 章一覧カード (章番号、タイトル、ステータスバッジ)
  - 🟢 合格 (passed = true)
  - 🔴 不合格 (passed = false, score != null)
  - ⚪ 未解答 (score = null)
- 「クイズを始める」ボタン (前章が合格していない場合は無効化)

**動作:**
1. マウント時に `GET /api/progress/my-progress` で進捗を取得
2. 前章が合格していない場合、次の章へのボタンを無効化
3. ボタンクリック → `/quiz/{chapterCode}` へ遷移

**ロック制御ロジック:**
```javascript
function isChapterUnlocked(chapterIndex) {
  if (chapterIndex === 0) return true // 最初の章は常に解放
  return progress[chapterIndex - 1]?.passed === true
}
```

---

### 4.3. クイズ画面 (/quiz/{chapterCode})

**コンポーネント:** `QuizView.vue`

**表示内容:**
- 章タイトル
- 問題カード (1〜20問)
  - 問題文
  - 選択肢 (A, B, C, D) - ラジオボタン
- 解答送信ボタン

**動作:**
1. マウント時に `GET /api/quiz/{chapterCode}` で問題を取得
2. ユーザーが選択肢を選択 → ローカルステートに保存
3. 「送信」ボタンクリック → `POST /api/quiz/submit/{chapterCode}`
4. 結果をモーダルで表示
   - スコア表示
   - 合否表示 (85%以上で合格)
   - 「ダッシュボードへ戻る」ボタン

---

### 4.4. 管理者ダッシュボード (/admin/dashboard)

**コンポーネント:** `AdminDashboardView.vue`

**表示内容:**
- 全ユーザーの進捗一覧 (テーブル形式)
  - ユーザー名、章、スコア、合否、最終解答日時
- サマリー統計 (オプション)
  - 登録ユーザー数
  - 平均合格率
- 「問題管理へ」ボタン → `/admin/questions`

**動作:**
1. マウント時に `GET /api/progress/all` で全進捗を取得

---

### 4.5. 問題一覧 (/admin/questions)

**コンポーネント:** `AdminQuestionListView.vue`

**表示内容:**
- 問題一覧テーブル
  - ID、章番号、問題文 (省略表示)、正解
  - 編集ボタン、削除ボタン
- ページネーション
- 「新規作成」ボタン → `/admin/questions/new`

**動作:**
1. マウント時に `GET /api/admin/questions?page=0&size=20` で問題を取得
2. 編集ボタン → `/admin/questions/edit/{id}` へ遷移
3. 削除ボタン → 確認ダイアログ → `DELETE /api/admin/questions/{id}`

---

### 4.6. 問題作成/編集 (/admin/questions/new, /admin/questions/edit/{id})

**コンポーネント:** `AdminQuestionEditView.vue`

**表示内容:**
- 章選択ドロップダウン
- 問題文入力フィールド (テキストエリア)
- 選択肢入力フィールド (A, B, C, D)
- 正解選択ラジオボタン (A, B, C, D)
- 保存ボタン、キャンセルボタン

**動作:**
1. 編集モードの場合、マウント時に `GET /api/admin/questions/{id}` でデータ取得
2. 保存ボタンクリック:
   - 新規: `POST /api/admin/questions`
   - 編集: `PUT /api/admin/questions/{id}`
3. 成功 → `/admin/questions` へ遷移

---

## 5. 共通コンポーネント

### NavBar.vue
- ロゴ
- ユーザー名表示
- ログアウトボタン
- 管理者の場合: 「管理画面」リンク

### QuestionCard.vue
- 問題文表示
- 選択肢ラジオボタン
- プロパティ: question, modelValue
- イベント: update:modelValue

---

## バージョン履歴

| バージョン | 日付 | 変更内容 |
|-----------|------|---------|
| 1.0 | 2025-10-26 | 初版作成 |
