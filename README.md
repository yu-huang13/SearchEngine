#程序说明
《信息检索》大作业，使用Lucene实现指定功能的搜索引擎。

#文件说明
- bin/IR_Project.jar：可直接运行的程序包
- source code/IR_Project：程序源代码

#程序运行说明
##启动
###查看帮助
查看帮助：

```java -jar IR_Project.jar -h```

输出为：

```
Usage: java Main.java [-options] -i directory
Options:
-h -help    :  Print usage information
-i -index Directory   :  Set directory of index.
-s -source File   :  Set path of source file and create index. You can set mode by using option -m.
-m -mode Mode   :  Index create mode(default mode: create). args: create | append
-a -analyzer Analyzer   :  Set analyzer(default Analyzer: AnsjAnalyzer). args: standard | ansj | cjk
-w -word2vec Directory   :  Set path of word2vec model and turn on the word association function.
```

###创建索引并启动搜索引擎（索引覆盖模式）
读取`data/source/CNKI_journal_v2.txt`原始数据，在`data/index/ansj`处建立索引（将覆盖原有索引），Analyzer使用`AnsjAnalyzer`：

```
java -jar IR_Project.jar -i data/index/ansj -s data/source/CNKI_journal_v2.txt -a ansj
```
或

```
java -jar IR_Project.jar -i data/index/ansj -s data/source/CNKI_journal_v2.txt -a ansj -m create
```

###创建索引并启动搜索引擎（索引追加模式）
读取`data/source/CNKI_journal_v2.txt`原始数据，在`data/index/ansj`处建立索引（将与原有索引合并），Analyzer使用`AnsjAnalyzer`：

```
java -jar IR_Project.jar -i data/index/ansj -s data/source/CNKI_journal_v2.txt -a ansj -m append
```

###读取索引并启动搜索引擎
读取`data/index/ansj`处的索引，以`AnsjAnalyzer`为分析器，启动搜索引擎：

```
java -jar IR_Project.jar -i data/index/ansj -a ansj
```

###读取索引并启动同义词搜索引擎
```
java -jar IR_Project.jar -i data/index/ansj -a ansj -w data/index/model/data/model/word2vec/vectors_s200_w8_iter15.bin
```
若无`-w`选项则无同义词搜索功能。

##搜索
###单域搜索
仅填写`input query`字段，其余字段按`回车键`跳过，示例如下：

```
input query: 计算机
题名:
作者:
出版单位:
年(起始年份 结束年份):
```

###多域限制搜索
填写所需的字段，其中`年`字段的格式为`起始年份 结束年份`，表示搜索区间为`[起始年份, 结束年份]`，起始年份与结束年份之间用**空格**分开，示例如下：

```
input query: 计算机
题名: 并行处理
作者: 梁兴琦
出版单位:
年(起始年份 结束年份):1998 1999
```


#时间说明
将CNKI\_journal\_v2.txt中27000个文本全部索引，在本机大约需要5分钟的时间。

#bug
临时发现，在输入查询关键字时无法使用退格，这将在下次作业中进行修复。

