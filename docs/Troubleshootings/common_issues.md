# トラブルシューティング

## 概要
Javaテストシステムの開発・運用中に発生しうる問題と解決方法をまとめたドキュメント。

---

## 1. 開発環境関連

### 1.1. Devcontainerが起動しない

**症状:**
- VSCodeでdevcontainerを開こうとするとエラーが発生
- コンテナのビルドが失敗する

**原因と解決策:**

1. **Dockerが起動していない**
   - Docker Desktopを起動してください
   - Linuxの場合: `sudo systemctl start docker`

2. **ポートの競合**
   - 既に8080、5173、5432ポートが使用されている
   - `docker ps` で既存コンテナを確認
   - `docker-compose down` で既存コンテナを停止

3. **ディスク容量不足**
   - `docker system prune -a` で不要なイメージを削除

---

### 1.2. PostgreSQLに接続できない

**症状:**
- Spring Bootアプリケーション起動時に接続エラー
- `Connection refused` または `Unknown database`

**原因と解決策:**

1. **PostgreSQLコンテナが起動していない**
   ```bash
   docker-compose ps
   docker-compose up -d postgres
   ```

2. **接続情報の誤り**
   - `application.properties` の設定を確認:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/java_exam_db
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```

3. **データベースが作成されていない**
   ```bash
   docker exec -it postgres psql -U postgres
   CREATE DATABASE java_exam_db;
   ```

---

## 2. バックエンド (Spring Boot) 関連

### 2.1. アプリケーション起動時のエラー

#### 2.1.1. Bean作成エラー

**症状:**
```
Error creating bean with name 'jwtRequestFilter'
```

**原因:**
- JWT関連のBeanが正しく設定されていない

**解決策:**
1. `JwtUtil` クラスに `@Component` アノテーションがあるか確認
2. `application.properties` に `jwt.secret` が設定されているか確認
3. 依存関係 (jjwt) が正しくインストールされているか確認

---

#### 2.1.2. テーブルが存在しない

**症状:**
```
ERROR: relation "users" does not exist
```

**原因:**
- DDLが実行されていない

**解決策:**
1. `src/main/resources/schema.sql` を確認
2. `application.properties` に以下を追加:
   ```properties
   spring.jpa.hibernate.ddl-auto=update
   spring.sql.init.mode=always
   ```
3. または手動でDDL実行:
   ```bash
   docker exec -i postgres psql -U postgres -d java_exam_db < schema.sql
   ```

---

### 2.2. JWT認証エラー

#### 2.2.1. トークンが無効

**症状:**
- フロントエンドから送信したリクエストが401エラー
- `Invalid JWT token`

**原因と解決策:**

1. **トークンの有効期限切れ**
   - トークンを再取得 (再ログイン)

2. **シークレットキーの不一致**
   - `application.properties` の `jwt.secret` を確認
   - 環境変数が正しく設定されているか確認

3. **トークンの形式エラー**
   - フロントエンドで `Authorization: Bearer <token>` の形式で送信されているか確認

---

### 2.3. CORS エラー

**症状:**
```
Access to XMLHttpRequest at 'http://localhost:8080/api/...' 
from origin 'http://localhost:5173' has been blocked by CORS policy
```

**原因:**
- Spring SecurityのCORS設定が不足

**解決策:**
`SecurityConfig` クラスに以下を追加:
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin("http://localhost:5173");
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

---

### 2.4. パスワードハッシュ化の問題

**症状:**
- ログインに失敗する
- パスワードが一致しない

**原因:**
- BCryptエンコーダーが正しく設定されていない

**解決策:**
1. `SecurityConfig` に `PasswordEncoder` Beanを定義:
   ```java
   @Bean
   public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
   }
   ```

2. 初期データのパスワードをハッシュ化:
   ```java
   String hashedPassword = passwordEncoder.encode("user123");
   ```

---

## 3. フロントエンド (Vue.js) 関連

### 3.1. ビルドエラー

#### 3.1.1. 依存関係のインストールエラー

**症状:**
```
npm ERR! code ERESOLVE
npm ERR! ERESOLVE unable to resolve dependency tree
```

**解決策:**
```bash
rm -rf node_modules package-lock.json
npm install --legacy-peer-deps
```

---

#### 3.1.2. Viteサーバーが起動しない

**症状:**
- `npm run dev` でエラー
- ポート5173が使用できない

**解決策:**
1. ポートを変更:
   ```javascript
   // vite.config.js
   export default defineConfig({
     server: {
       port: 3000
     }
   })
   ```

2. 既存プロセスを終了:
   ```bash
   lsof -ti:5173 | xargs kill -9  # Linuxの場合
   ```

---

### 3.2. Piniaストアが動作しない

**症状:**
- ストアの状態が更新されない
- `useAuthStore is not a function`

**原因と解決策:**

1. **Piniaが初期化されていない**
   - `main.js` で `createPinia()` が実行されているか確認:
   ```javascript
   import { createPinia } from 'pinia'
   app.use(createPinia())
   ```

2. **ストアの定義エラー**
   - `defineStore` の第一引数 (ID) が重複していないか確認

---

### 3.3. ルーティングが動作しない

**症状:**
- ページ遷移が失敗する
- `Cannot GET /dashboard` エラー

**原因と解決策:**

1. **Vue Routerが初期化されていない**
   - `main.js` で `router` がインポート・使用されているか確認

2. **履歴モードの設定**
   - `router/index.js` で `createWebHistory` を使用:
   ```javascript
   const router = createRouter({
     history: createWebHistory(),
     routes
   })
   ```

3. **開発サーバーのリライト設定**
   - `vite.config.js` に追加:
   ```javascript
   server: {
     historyApiFallback: true
   }
   ```

---

### 3.4. API呼び出しエラー

#### 3.4.1. 401 Unauthorized

**症状:**
- ログイン後のAPI呼び出しが401エラー

**原因:**
- JWTトークンがリクエストヘッダーに含まれていない

**解決策:**
`services/api.js` でインターセプタを確認:
```javascript
axios.interceptors.request.use(config => {
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
  }
  return config
})
```

---

#### 3.4.2. 404 Not Found

**症状:**
- APIエンドポイントが見つからない

**解決策:**
1. バックエンドが起動しているか確認
2. URLが正しいか確認 (`http://localhost:8080/api/...`)
3. ブラウザの開発者ツールでリクエストURLを確認

---

## 4. データベース関連

### 4.1. マイグレーションエラー

**症状:**
```
ERROR: column "xxx" of relation "yyy" already exists
```

**解決策:**
1. `spring.jpa.hibernate.ddl-auto` を `validate` に変更
2. または既存テーブルを削除して再作成:
   ```sql
   DROP TABLE IF EXISTS user_progress, questions, chapters, users CASCADE;
   ```

---

### 4.2. データ投入エラー

**症状:**
```
ERROR: duplicate key value violates unique constraint
```

**原因:**
- 既にデータが存在する

**解決策:**
1. `data.sql` の先頭に追加:
   ```sql
   DELETE FROM user_progress;
   DELETE FROM questions;
   DELETE FROM chapters;
   DELETE FROM users;
   ```

2. または `INSERT ... ON CONFLICT DO NOTHING` を使用

---

## 5. 認証・認可関連

### 5.1. 管理者ページにアクセスできない

**症状:**
- 管理者ユーザーでログインしても `/admin/**` にアクセスできない

**原因と解決策:**

1. **ロールが正しく設定されていない**
   - データベースの `users.role` が `ROLE_ADMIN` になっているか確認

2. **Piniaストアのロール判定が誤っている**
   - `authStore.isAdmin` のgetterを確認:
   ```javascript
   isAdmin: (state) => state.user?.role === 'ROLE_ADMIN'
   ```

3. **ナビゲーションガードのロジックエラー**
   - `router/index.js` の `beforeEach` を確認

---

## 6. パフォーマンス関連

### 6.1. ページの読み込みが遅い

**原因と解決策:**

1. **問題数が多すぎる**
   - ページネーションを実装
   - 遅延ロード (Lazy Loading) を使用

2. **データベースクエリが最適化されていない**
   - インデックスを追加
   - N+1問題を解消 (`@EntityGraph` を使用)

3. **フロントエンドのバンドルサイズが大きい**
   - コード分割 (Code Splitting)
   - 不要な依存関係を削除

---

## 7. セキュリティ関連

### 7.1. XSS (クロスサイトスクリプティング) 対策

**対策:**
- Vueのテンプレート構文 (`{{ }}`) は自動的にエスケープされる
- `v-html` の使用を避ける

---

### 7.2. CSRF (クロスサイトリクエストフォージェリ) 対策

**対策:**
- JWTを使用しているため、Spring SecurityのCSRF保護は無効化可能
- ただし、状態を持つ場合は有効化を検討

---

## 8. よくある質問 (FAQ)

### Q1: パスワードを忘れた場合の対処法は?

**A:** 現バージョンではパスワードリセット機能はありません。管理者がデータベースを直接更新してください:
```sql
UPDATE users 
SET password = '$2a$10$...'  -- 新しいハッシュ化パスワード
WHERE username = 'testuser';
```

---

### Q2: 問題を20問以上作成したい章がある

**A:** 問題はランダムで20問選択されます。多く作成すればバリエーションが増えます。

---

### Q3: 合格基準を変更したい

**A:** `QuizService` の採点ロジックを変更:
```java
boolean passed = score >= 90;  // 90%に変更
```

---

## バージョン履歴

| バージョン | 日付 | 変更内容 |
|-----------|------|---------|
| 1.0 | 2025-10-26 | 初版作成 |
