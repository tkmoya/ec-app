# EC App

## 概要

会員登録したユーザーが商品を購入することができるECサイトです。

好きな商品を一覧から選択して、購入したい個数を入力して購入画面で金額を確認して購入することができます。

ゲストユーザーでも商品の閲覧・検索は可能です。

## 主な機能

- 新規会員登録
- ログイン機能
- 会員情報修正・削除
- 商品検索
- ページネーション
- カート機能
- バリデーションチェック

## 使用技術

- Java
- Tomcat
- Spring Boot
- JDBC Template
- Thymeleaf
- MySQL
- Tailwind CSS
- JavaScript

## セットアップ手順

1. **リポジトリをクローン**

2. **Dockerコンテナを起動**  
    docker-compose.ymlをもとにMySQLコンテナを起動します。

3. **接続情報を設定**  
   src/main/resources/application.ymlにDBの接続情報を適宜変更します。

4. **開発サーバーを起動**  
    gradle bootRunでアプリケーションを起動します。

5. **アプリケーションにアクセス**  
   ブラウザで `http://localhost:8080` を開きます。