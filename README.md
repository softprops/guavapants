# guava pants

[![Build Status](https://travis-ci.org/softprops/guavapants.png?branch=master)](https://travis-ci.org/softprops/guavapants)

<p align="center">
  <img height="400" src="pants.png"/>
</p>

> pants that look good on both your [scala](http://www.scala-lang.org/api/current/) and [guava](https://code.google.com/p/guava-libraries/) types.

## Install

This library targets scala 2.10 and higher.

### via sbt.

Add the following to your sbt build definition

```scala
resolvers += "softprops-maven" at "http://dl.bintray.com/content/softprops/maven"

libraryDependencies += "me.lessis" %% "guavapants" % "0.1.1"
```


## Usage

This library attempts to address the problem that `scala.collection.JavaConverters` addresses for resolving interface discrepancies between Scala collections and Java collections, for Guava types when things start getting uncomfortable. Guava pants provides convenient `asGuava` enrichments on Scala types that map well to equivalent Guava types and `asScala` enrichments on Guava types that map well to equivalent Scala types.

Most usage follows the same convention as `JavaConverters`. Just import `GuavaConverters` into scope.

```scala
import guavapants.GuavaConverters._
```

Then call the `asGuava` or `asScala` method on the target value to achieve a more comfortable fit for which ever interface you are trying to satisfy.

### Functions

Guava defines primitives for [Function objects](http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/base/Function.html) that look very familiar to your everyday Scala functions. They are a bit limited in that they are only defined for functions that take one argument, most likey a design decision for the use case of [transforming collections](https://code.google.com/p/guava-libraries/wiki/FunctionalExplained#Functions).


Given a scala function, you can switch to a Guava type by calling `asGuava` on it.

```scala
val gf: com.google.common.base.Function[Int, String] =
  ((_: Int).toString).asGuava
```

### Predicates

In Guava, there is a special name attributed to functions that take one argument and return a Boolean value called a [Predicate](http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/base/Predicate.html). In Guava the return type is a primitive Boolean type. This works out because Scala's Boolean type can not be null ( try for yourself! ).

Given a scala function returning a Boolean value, you can switch to a Guava type by calling `asGuava` on it.

```scala
val gp: com.google.common.base.Predicate[Int] =
  ((_:Int) % 0 == 0).asGuava
```

### Suppliers

Guava defines a special interface for functions which take no arguments called [Suppliers](http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/base/Supplier.html). In Scala, this is just a function that takes no arguments.

Given a scala function which takes no arguments, you can switch to a Guava type by calling `asGuava` on it.

```scala
val gs: com.google.common.base.Supplier[Int] =
  (() => 32).asGuava
```

### Optionals

Guava shares a primitive similar to Scala's built-in Option type called [Optional](http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/base/Optional.html) It serves the same purpose in indicating the presence or absence of some underlying value.

Given a scala `Option` type, you can convert to a Guava type by calling `asGuava` on it.

```scala
val go: com.google.common.base.Optional[Int] =
  Some(1).asGuava
```

### ListenableFutures

Guava can be a little aggressive in the problems it tries to solve as one library. Among the more interesting things found in its collection are abstractions for Futures which you can register hooks on called [ListenableFutures](http://docs.guava-libraries.googlecode.com/git/javadoc/index.html?com/google/common/util/concurrent/ListenableFuture.html). There is a strong correlation to Scala's built-in Futures in their design. 

Given a scala `Future`, you can convert to a Guava type by calling `asGuava` on it.

```scala
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
val gf: com.google.common.util.concurrent.ListenableFuture[Int] =
  (Future(42)).asGuava
```

## Extras

Things you do not need to know but may appreciate.

### Value Types

Guava pants was designed to be as efficient as possible at runtime. In java code bases where Guava is used, function interfaces are often exposed prolifically ( with good reason ). If you are calling into these interfaces from Scala you will need be interfacing with them a lot at runtime. The types of conversions this library does (implicit conversions) between types has historically had a less than desirable runtime cost. In scala 2.10, there was a new kind of type, a [value type](http://docs.scala-lang.org/overviews/core/value-classes.html) that was introduced to help reduce and eliminate this runtime cost. Guava pants wholeheartedly was designed to take advantage of this Scala 2.10 feature.

## Alternatives

You may also want to consider using the [bijection](https://github.com/twitter/bijection#readme) [guava](https://github.com/twitter/bijection/tree/develop/bijection-guava/src/main/scala/com/twitter/bijection/guava) module.

## Issues

Did I miss something? [Let me know](https://github.com/softprops/guavapants/issues/new?title=something%20you%20missed...).

Doug Tangren (softprops) 2014
