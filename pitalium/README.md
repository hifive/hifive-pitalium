pitalium
==============================================================

pitaliumのプロジェクトです。

## ビルド手順

1. リポジトリのクローン
`$ git clone git@github.com:hifive/hifive-pitalium.git`

2. Apche ivyを利用しpitaliumプロジェクトに必要なライブラリを追加
`pitalium/ivy_build.xml` の resolve ターゲットを実行します。

  - コマンドラインから
  `$ cd pitalium`
  `$ ant -buildfile ivy_build.xml`

  - IDE（eclipse）から
  pitaliumプロジェクトをインポート -> `pitalium/ivy_build.xml` を右クリック -> 実行 -> Antビルド

3. ビルドを実行
  `pitalium/build.xml` の build ターゲットを実行します。

  - コマンドラインから
  `$ cd pitalium`
  `$ ant -buildfile build.xml`

  - IDE（eclipse）から
  `pitalium/build.xml` を右クリック -> 実行 -> Antビルド

  `pitalium/target/pitalium-（バージョン）.jar` が生成されます。


## APIドキュメント（JavaDocドキュメント）の生成方法

`pitaliumy/build.xml` の javadoc ターゲットを実行します。

`pitalium/target/doc` の下にドキュメントが生成されます。
