# 調査リポジトリ

## Android アプリ

androidディレクトリ配下がAndroidアプリのプロジェクト

### アプリビルド

1. AndroidStudioでandroidディレクトリをimportする
2. pusherのAPPのkeyとclusterを環境に合わせて設定する
- https://github.com/TakashiOkayama/NsLj360/blob/main/android/app/src/main/java/jp/loiterjoven/nslj360/Constant.kt#L9-L10
```
const val PUSHER_APP_CLUSTER = "xxx"
const val PUSHER_APP_KEY = "xxxxxxxxxxxxxxxxxx"
```
3. ビルドする

<img src="https://user-images.githubusercontent.com/39586845/126472049-236899a8-e16e-412a-803e-2ee51f5481d2.jpg" width="320px">

## バックエンドサーバー

serverディレクトリ配下がバックエンドサーバー

### pusherの認証サーバー
presence channel と private channel を使用するために必要となる

### 事前準備
MongoDB をインストールして起動しておく。

https://docs.mongodb.com/manual/installation/

以下はmac環境の起動コマンド
```
mongod --config /usr/local/etc/mongod.conf
```

### ローカル起動

1. serverディレクトリに遷移する
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
