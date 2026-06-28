<div align="center">
  <h1>JFunction</h1>
  <p><strong>Java 26 轻量级函数式编程库</strong></p>

  <p>
    <a href="#"><img src="https://img.shields.io/badge/Java-26-blue.svg" alt="Java Version"/></a>
    <a href="#"><img src="https://img.shields.io/badge/Maven-3.9+-green.svg" alt="Maven"/></a>
    <a href="#"><img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License"/></a>
    <a href="#"><img src="https://img.shields.io/badge/JUnit-5.11.4-orange.svg" alt="JUnit"/></a>
  </p>
</div>

# JFunction

一个基于 **Java 26** 新特性构建的轻量级函数式编程库。

JFunction 利用 sealed interface、record pattern、primitive type patterns、`LazyConstant`、Stream Gatherers 以及 Structured Concurrency 等语言能力，提供类型安全、不可变、零依赖的函数式抽象。

## 环境要求

- **JDK 26**（需启用 `--enable-preview`）
- **Maven 3.9+**

## 安装依赖

```xml
<dependency>
    <groupId>io.github.tuyucheng777</groupId>
    <artifactId>jfunction</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 核心类

| 包 | 核心类型 | 说明 |
|---|---|---|
| `core` | `Option`, `Either`, `Try`, `Lazy`, `Validation` | 代数数据类型 (ADT) |
| `fn` | `Fn` | 函数组合、柯里化、偏应用、memoize |
| `tuple` | `Tuples`, `Tuple2/3/4` | 不可变元组 |
| `match` | `Match`, `Predicates` | JEP 530 原生类型模式匹配工具 |
| `stream` | `JGatherers`, `JStreams` | JEP 485 Stream Gatherers 扩展 |
| `concurrent` | `Pair` | JEP 525 结构化并发工具 |

## 使用的 Java 26 新特性

| 特性 | JEP | 应用位置 |
|---|---|---|
| Sealed Interfaces + Record Patterns | 409/440 | 所有 ADT 的 `switch` 解构 |
| Primitive Type Patterns | 530 | `Match.classifyNumber()` / `narrowToInt()` |
| `LazyConstant` | 526 | `Lazy` — 线程安全、可常量折叠的惰性求值 |
| Stream Gatherers | 485 | `JGatherers` — scan / takeWhile / groupConsecutive 等 |
| Structured Concurrency | 525 | `Pair` — all / race / gather 并行组合 |
| Markdown Doc Comments (`///`) | 467 | 全部源码文档注释 |

## 用法示例

### Option — 安全的可空值处理

```java
import io.github.jfunction.core.Option;

Option.of(42)
    .filter(n -> n > 10)
    .map(n -> n * 2)
    .match(v -> "result: " + v, () -> "empty");
// => "result: 84"

Option.ofNullable(null).getOrElse("default");
// => "default"
```

### Either — 显式错误建模

```java
import io.github.jfunction.core.Either;

Either<String, Integer> result = Either.right(10);
result.map(n -> n + 1).fold(err -> -1, v -> v);
// => 11
```

### Try — 异常安全计算

```java
import io.github.jfunction.core.Try;

Try.of(() -> Integer.parseInt("abc"))
    .recover(e -> -1)
    .get();
// => -1
```

### Validation — 累积错误收集

```java
import io.github.jfunction.core.Validation;

var name = Validation.<String, String>valid("Alice");
var age  = Validation.<String, Integer>invalid("Age must be positive");

Validation.combine(name, age, (n, a) -> n + ":" + a);
// => Invalid(["Age must be positive"])
```

### Lazy — 基于 LazyConstant 的惰性求值

```java
import io.github.jfunction.core.Lazy;

Lazy<String> expensive = Lazy.of(() -> loadFromDB());
// 首次调用 get() 时才计算，之后缓存
expensive.get();
```

### Fn — 函数组合与柯里化

```java
import io.github.jfunction.fn.Fn;

var addThenDouble = Fn.pipe(
    (Integer n) -> n + 1,
    (Integer n) -> n * 2
);
addThenDouble.apply(3); // => 8

var add = Fn.curry((Integer a, Integer b) -> a + b);
add.apply(1).apply(2); // => 3
```

### Match — JEP 530 原生类型模式匹配

```java
import io.github.jfunction.match.Match;

Match.classifyNumber(42);   // => "positive int: 42"
Match.rangeLabel(50);        // => "medium"
Match.narrowToInt(100L);     // => Option.of(100)
Match.narrowToInt(Long.MAX_VALUE); // => Option.none()
```

### JGatherers — Stream Gatherers 扩展

```java
import io.github.jfunction.stream.JGatherers;
import java.util.stream.Stream;

// 前缀累计和
Stream.of(1, 2, 3, 4)
    .gather(JGatherers.scan(() -> 0, Integer::sum))
    .toList();
// => [1, 3, 6, 10]

// 连续相等元素分组
Stream.of(1, 1, 2, 2, 2, 3)
    .gather(JGatherers.groupConsecutive())
    .toList();
// => [[1, 1], [2, 2, 2], [3]]
```

### Pair — JEP 525 结构化并发

```java
import io.github.jfunction.concurrent.Pair;

// 并行执行，全部成功才返回
var results = Pair.all(
    () -> fetchFromServiceA(),
    () -> fetchFromServiceB()
);
// => Try<List<String>>

// 竞争执行，返回最快成功的结果
var fastest = Pair.race(
    () -> queryMirror1(),
    () -> queryMirror2()
);
// => Try<String>
```

## 设计原则

- **不可变** — 所有类型均为 sealed interface + record，天然不可变
- **零依赖** — 仅依赖 JDK 标准库，无第三方运行时依赖
- **类型安全** — 利用 sealed + exhaustive switch 消除遗漏分支
- **Preview-ready** — 面向 Java 26 preview 特性，展示语言最新能力