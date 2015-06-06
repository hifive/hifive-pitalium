hifive-test-library
==============================================================

hifive-test-libraryのプロジェクトです。

## ビルド手順

1. リポジトリのクローン  
`$ git clone git@github.com:hifive/hifive-test-library.git`

2. Apche ivyを利用しhifive-test-libraryプロジェクトに必要なライブラリを追加  
`hifive-test-library/ivy_build.xml` の resolve ターゲットを実行します。

  - コマンドラインから  
  `$ cd hifive-test-library`  
  `$ ant -buildfile ivy_build.xml`

  - IDE（eclipse）から
  hifive-test-libraryプロジェクトをインポート -> `hifive-test-library/ivy_build.xml` を右クリック -> 実行 -> Antビルド

3. ビルドを実行
`hifive-test-library/build.xml` の build ターゲットを実行します。

  - コマンドラインから  
  `$ cd hifive-test-library`  
  `$ ant -buildfile build.xml`

  - IDE（eclipse）から  
  `hifive-test-library/build.xml` を右クリック -> 実行 -> Antビルド

`hifive-test-library/target/hifive-test-library-（バージョン）.jar` が生成されます。


## APIドキュメント（JavaDocドキュメント）の生成方法

`hifive-test-library/build.xml` の javadoc ターゲットを実行します。

`hifive-test-library/target/doc` の下にドキュメントが生成されます。
