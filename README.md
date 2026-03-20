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

### ローカル実行

#### アプリケーション

```bash
# linux または mac
./mvnw spring-boot:run

# windows
mvnw.cmd spring-boot:run
```
- Windowsでコマンドがエラーになる場合、「# linux または mac」の方のコマンドを試してみてください。

`http://localhost:8080` でアプリケーションにアクセスします。

#### データベース 

以下からアプリをインストール:
https://sqlitebrowser.org/

SQLiteデータベース `java-exam.db` に接続して使用してください。
データベースは初回実行時に `src/main/resources/data.sql` のデータで初期化されます。

### 本番構築

事前に公開鍵をサーバーに登録するため、連携してください。

初回に docker-compose.yml#L21 のXXXXX部分を自分のチームにしてcommitしてmasterにpushしてください。

#### アプリケーション

```bash
ssh ユーザー名@サーバーIPアドレス
sudo su - exam

# 初回のみリポジトリをクローン
# github classic tokenを使用してクローンしてください。git pullでも必要になります。
# https://docs.github.com/ja/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens
git clone https://github.com/Java-exam-2026/{チーム名}-java-exam-v2.git

# すでにクローンしている場合は、以下のコマンドで最新の状態に更新
cd {チーム名}-java-exam-v2
git pull

# アプリケーションを起動
docker compose up -d

# 以下を実行してアプリケーションが起動したことを確認する
docker compose logs
```

`{チーム名}-.java-exam.com` でアプリケーションにアクセスします。

#### データベース

sqlite3コマンドを使用して、SQLiteデータベースに接続できます。

```bash
ssh ユーザー名@サーバーIPアドレス
sudo su - exam
cd {チーム名}-java-exam-v2

# データベースに接続
sqlite3 java-exam-prod.db  
```
