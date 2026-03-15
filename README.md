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

```bash
# linux または mac
./mvnw spring-boot:run

# windows
mvnw.cmd spring-boot:run
```
- Windowsでコマンドがエラーになる場合、「# linux または mac」の方のコマンドを試してみてください。

`http://localhost:8080` でアプリケーションにアクセスします。

### クラウド環境構築

初回のみ、以下のコマンドを実行して、リポジトリをクローンし、Dockerコンテナを起動します。

```bash
sudo su - exam

# リポジトリをクローン
git clone https://github.com/Java-exam-2026/{チーム名}-java-exam-v2.git
cd java-exam-v2
./mvnw compile jib:dockerBuild
docker compose up -d
docker ps
```

#### testing実行

testingはbuildされたdockerイメージを使用していないため、git pullで変更を反映させるだけでOKです。

```bash
sudo su - exam
cd java-exam-v2

# 変更を反映させるためにリポジトリを更新
git pull
docker compose up -d
docker ps
```

#### prod実行

prodはbuildされたdockerイメージを使用しているため、変更を反映させるためにリポジトリを更新した後、Dockerイメージを再ビルドする必要があります。

```bash
sudo su - exam
cd java-exam-v2

# 変更を反映させるためにリポジトリを更新
git pull

# 変更を反映させるためにDockerイメージを再ビルド
./mvnw compile jib:dockerBuild

# 古いイメージを削除してディスク容量を確保
docker image prune -f

docker compose up -d
docker ps
```

## データベース

以下からアプリをインストール:
https://sqlitebrowser.org/

このアプリケーションは、ローカルのSQLiteデータベース `java-exam.db` を使用します。
データベースは初回実行時に `src/main/resources/data.sql` のデータで初期化されます。

