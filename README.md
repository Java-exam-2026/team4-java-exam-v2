# Java試験システム V2

このプロジェクトは、フロントエンドにThymeleaf、データベースにSQLiteを使用したJava試験システムの再構築版です。

## 機能

- **フロントエンド**: Thymeleaf + Bootstrap 5
- **データベース**: SQLite
- **セキュリティ**: Spring Security (フォームログイン)
- **機能**:
  - ログイン (ユーザー: testuser / user123, 管理者: admin / admin123)
  - チャプター一覧と進捗状況のダッシュボード
  - クイズ受験インターフェース
  - 結果表示

## 実行方法

アプリケーションを実行する:
   ```bash
   # linux または mac
   ./mvnw spring-boot:run

   # windows
   mvnw.cmd spring-boot:run
   ```
- Windowsでコマンドがエラーになる場合、「# linux または mac」の方のコマンドを試してみてください。

3. `http://localhost:8080` でアプリケーションにアクセスします。

## データベース

以下からアプリをインストール: 
https://sqlitebrowser.org/ 

このアプリケーションは、ローカルのSQLiteデータベース `java-exam.db` を使用します。
データベースは初回実行時に `src/main/resources/data.sql` のデータで初期化されます。

## プロジェクト構造

- `src/main/java/com/javaexam`: バックエンドロジック (Controllers, Services, Repositories, Entities)
- `src/main/resources/templates`: Thymeleafテンプレート (login, dashboard, quiz, result)
- `src/main/resources/application.properties`: 設定ファイル
