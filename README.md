# 調査リポジトリ

## Android アプリ

- `android`ディレクトリ配下がAndroidアプリのプロジェクト
- `pusher`を使用してAndroidの端末間でメッセージの送受信を行う
- ユーザー名はAPIサーバー側で未登録なら新規追加される

<img src="https://user-images.githubusercontent.com/39586845/126472049-236899a8-e16e-412a-803e-2ee51f5481d2.jpg" width="320px">

### アプリビルド手順

1. AndroidStudioでandroidディレクトリをimportする
2. pusherのAPPのkeyとclusterを環境に合わせて設定する
- https://github.com/TakashiOkayama/NsLj360/blob/main/android/app/src/main/java/jp/loiterjoven/nslj360/Constant.kt#L9-L10
```
const val PUSHER_APP_CLUSTER = "xxx"
const val PUSHER_APP_KEY = "xxxxxxxxxxxxxxxxxx"
```
3. ビルドする


## APIサーバー

- `server`ディレクトリ配下がAPIサーバーのプロジェクト
- `pusher`を使用するための認証やメッセージ送信のエンドポイントを用意
- `pusher`の `presence channel` と `private channel` を使用するためには認証用のサーバーが必要となる

### 事前準備
MongoDB をインストールして起動しておく。

https://docs.mongodb.com/manual/installation/

以下はmac環境の起動コマンド
```
mongod --config /usr/local/etc/mongod.conf
```

### ローカル起動

1. `server`ディレクトリに遷移する
2. pusherのAPPのappId、key、secret、clusterを環境に合わせて設定する
- https://github.com/TakashiOkayama/NsLj360/blob/main/server/src/index.js
```
const pusher = new Pusher({
  appId: "xxxxxxx",
  key: "xxxxxxxxxxxxxxxxxxx",
  secret: "xxxxxxxxxxxxxxxxxxx",
  cluster: "xxx",
});
```
3. 以下のコマンドを実行
```
npm install --no-save
node src/index.js
```

## コンテンツサーバー

- staticディレクトリ配下がコンテンツサーバーのプロジェクト
- ブラウザからURLアクセスをするとhtmlの静的コンテンツが返される
- htmlコンテンツは、Androidアプリ起動用のカスタムURLスキーマ設定したボタンとアンカーが設定されている

### ローカル起動

1. `static`ディレクトリに遷移する
2. 以下のコマンドを事項
```
npm install --no-save
node index.js
```
3. ブラウザを起動してURLにアクセスする
```
http://localhost:3000/
http://xxx.xxx.xxx.xxx:3000/
```
