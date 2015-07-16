hifive-test-library-sample
====

hifive-test-libraryの動作確認用サンプルプロジェクトです。
動作させるための手順を以下に記載します。

## 必要なライブラリを追加
`hifive-test-library-sample/ivy_build.xml` の resolve ターゲットを実行します。

  - コマンドラインから
  `$ cd hifive-test-library-sample`
  `$ ant -buildfile ivy_build.xml`

  - IDE（eclipse）から
  hifive-test-library-sampleプロジェクトをインポート -> `hifive-test-library-sample/ivy_build.xml` を右クリック -> 実行 -> Antビルド

## 環境構築

1. Selenium Hubサーバの起動

下記ページの「Hubサーバのインストール～起動」手順を参照し、Hubサーバを起動します。
http://www.htmlhifive.com/conts/web/view/library/Selenium+Grid

### PCブラウザでテストを実行する場合

2. Selenium Gridサーバの起動

下記ページの「Nodeサーバのインストール～起動」手順を参照し、Nodeサーバを起動します。
http://www.htmlhifive.com/conts/web/view/library/Selenium+Grid

### Android端末でテストを実行する場合

3. Appiumの起動

下記ページの手順を参照し、Appiumを起動します。
http://www.htmlhifive.com/conts/web/view/library/Android%E7%AB%AF%E6%9C%AB%E4%B8%8A%E3%81%A7%E3%83%86%E3%82%B9%E3%83%88%E3%82%92%E8%A1%8C%E3%81%86%EF%BC%88%E3%82%B3%E3%83%9E%E3%83%B3%E3%83%89%E3%83%A9%E3%82%A4%E3%83%B3%EF%BC%89

### iOS端末でテストを実行する場合

4. Appiumの起動

下記ページの手順を参照し、Appiumを起動します。

http://www.htmlhifive.com/conts/web/view/library/iOS%E7%AB%AF%E6%9C%AB%E4%B8%8A%E3%81%A7%E3%83%86%E3%82%B9%E3%83%88%E3%82%92%E8%A1%8C%E3%81%86%EF%BC%88%E3%82%B3%E3%83%9E%E3%83%B3%E3%83%89%E3%83%A9%E3%82%A4%E3%83%B3%EF%BC%89

## 実行環境設定
1. Capabilityを設定します。

src/main/resources/capabilities.jsonを開き、テストを実行するブラウザの設定のみを残して
不要な設定を削除します。

例）Chromeでテストを実行する場合、次のようにします。
  [
   {
    "platform" : "WINDOWS",
    "os" : "WINDOWS",
    "browserName" : "chrome",
    "version" : "40.0"
   }
  ]

なお、各ブラウザ用の設定例はsrc/main/resources/capabilities_*.json に記載しています。

## 正解定義モードで実行
1. MrtSampleTest.javaを実行します。

2. hifive-test-library-sample/results フォルダ以下に実行結果画像が生成されます。

## テスト実行モードで実行
1. 実行モードを切り替えます。

src/main/resources/EnvironmentConfig.jsonを開き、2行目を次のように書き換えます。
  "execMode": "RUN_TEST",

2. MrtSampleTest.javaを実行します。

3. hifive-test-library-sample/results フォルダ以下に実行結果画像が生成されます。

正解定義モードで実行した際の結果画像と、今回の実行結果画像を比較し、差異があった場合は差分画像が出力されます。

