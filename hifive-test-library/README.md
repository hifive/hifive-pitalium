hifive-test-library
====

hifive-test-libraryのプロジェクトです。

## ビルド手順

1. リポジトリのクローン
  $ git clone git@github.com:hifive/hifive-test-library.git

2. Apche ivyを利用しhifive-test-libraryプロジェクトに必要なライブラリを追加

hifive-test-library//ivy_build.xmlのresolveターゲットを実行します。

- コマンドラインから
    $ cd hifive-test-library
    $ ant -buildfile ive_build.xml

- IDE（eclipse）から
    hifive-test-libraryプロジェクトをインポート -> hifive-test-library/ivy_build.xmlを右クリック -> 実行 -> Antビルド

3. ビルドを実行

hifive-test-library/build.xmlのbuildターゲットを実行します。

- コマンドラインから
    $ cd hifive-test-library
    $ ant -buildfile build.xml

- IDE（eclipse）から
    hifive-test-library/build.xmlを右クリック -> 実行 -> Antビルド

hifive-test-library/target/hifive-test-library-（バージョン）.jarが生成されます。


## APIドキュメント（JavaDocドキュメント）の生成方法

build.xmlのjavadocターゲットを実行します。

hifive-test-library/target/doc の下にドキュメントが生成されます。
