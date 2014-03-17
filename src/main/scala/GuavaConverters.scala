package guavapants

import com.google.common.base.{ Equivalence, Function => GFunction, Optional, Predicate, Supplier }
import com.google.common.collect.Ordering
import com.google.common.util.concurrent.{ FutureCallback, Futures, ListenableFuture }
import java.util.concurrent.{ Executor, TimeoutException, TimeUnit }
import scala.concurrent.{ Await, CanAwait, ExecutionContext, ExecutionException, Future, TimeoutException }
import scala.concurrent.duration.Duration
import scala.util.{ Failure, Success, Try }

/** compatibility module for scala <=> guava transformations */
object GuavaConverters {
  class ScalaGuavaFunction[A, B](fn: (A => B)) extends GFunction[A, B] {
    def apply(a: A) = fn(a)
  }
  class ScalaGuavaPredicate[A](fn: (A => Boolean)) extends Predicate[A] {
    def apply(a: A) = fn(a)
  }
  class ScalaGuavaSupplier[A](fn: () => A) extends Supplier[A] {
    def get: A = fn()
  }
  class ScalaEquivalence[A](eq: Equiv[A]) extends Equivalence[A] {
    def doEquivalent(x: A, y: A): Boolean = eq.equiv(x, y)
    def doHash(t: A): Int = t.hashCode
  }
  class ScalaListenableFuture[A](future: Future[A]) extends ListenableFuture[A] {

    override def addListener(listener: Runnable, executor: Executor) {
      future.onComplete({ _ => listener.run() })(ExecutionContext.fromExecutor(executor))
    }

    // j.u.c.Future methods

    @throws(classOf[InterruptedException])
    @throws(classOf[ExecutionException])
    override def get(): A = Await.result(future, Duration.Inf)

    @throws(classOf[InterruptedException])
    @throws(classOf[ExecutionException])
    @throws(classOf[TimeoutException])
    override def get(timeout: Long, unit: TimeUnit): A =
      Await.result(future, Duration(timeout, unit))

    override def cancel(interrupts: Boolean): Boolean = false
    override def isCancelled(): Boolean = false
    override def isDone(): Boolean = future.isCompleted
  }

  class ListenableScalaFuture[A](future: ListenableFuture[A]) extends Future[A] {
    private def asGuavaCallback[U](c: Try[A] => U): FutureCallback[A] =
      new FutureCallback[A] {
        override def onSuccess(result: A) = c(Success(result))
        override def onFailure(t: Throwable) = c(Failure(t))
      }

    override def onComplete[U](callback: Try[A] => U)(implicit ec: ExecutionContext): Unit =
      Futures.addCallback(future, asGuavaCallback(callback))

    override def isCompleted: Boolean = future.isDone

    override def value: Option[Try[A]] =
      isCompleted match {
        case false => None
        case true  => Some(Try(future.get()))
      }

    @throws(classOf[TimeoutException])
    @throws(classOf[InterruptedException])
    override def ready(atMost: Duration)(implicit permit: CanAwait): this.type = {
      try atMost match {
        case Duration.Inf => future.get()
        case f => future.get(f.toNanos, TimeUnit.NANOSECONDS)
      } catch {
        case e: TimeoutException => throw e
        case e: InterruptedException => throw e
        case _: Throwable => ()
      }
      this
    }

    @throws(classOf[Exception])
    override def result(atMost: Duration)(implicit permit: CanAwait): A =
      ready(atMost).value.get match {
        case Failure(e) => throw e
        case Success(r) => r
      }
  }

  implicit class OptionalAsScala[A](val guavaOpt: Optional[A]) extends AnyVal {
    def asScala: Option[A] = if (guavaOpt.isPresent) Some(guavaOpt.get) else None
  }
  implicit class SupplierAsScala[A](val guavaSupplier: Supplier[A]) extends AnyVal {
    def asScala: (() => A) = () => guavaSupplier.get()
  }
  implicit class FunctionAsScala[A, B](val guavaFn: GFunction[A, B]) extends AnyVal {
    def asScala: (A => B) = guavaFn.apply(_)
  }
  implicit class PredicateAsScala[A](val pred: Predicate[A]) extends AnyVal {
    def asScala: (A => Boolean) = pred.apply(_)
  }
  implicit class OrderingAsScala[A](val guavaOrdering: Ordering[A]) extends AnyVal {
    def asScala: math.Ordering[A] = math.Ordering.comparatorToOrdering(guavaOrdering)
  }
  implicit class EquivalenceAsScala[A](val eq: Equivalence[A]) extends AnyVal {
    def asScala: math.Equiv[A] = math.Equiv.fromFunction(eq.equivalent(_, _))
  }
  implicit class ListenableFutureAsScala[A](val guavaFuture: ListenableFuture[A]) extends AnyVal {
    def asScala: Future[A] = new ListenableScalaFuture(guavaFuture)
  }

  implicit class OptionAsGuava[A](val opt: Option[A]) extends AnyVal {
    def asGuava: Optional[A] = opt.map(Optional.of(_)).getOrElse(Optional.absent[A]())
  }
  implicit class Function0AsGuava[A](val fn: () => A) extends AnyVal {
    def asGuava: Supplier[A] = new ScalaGuavaSupplier(fn)
  }
  implicit class Function1AsGuava[A, B](val fn: (A => B)) extends AnyVal {
    def asGuava: GFunction[A, B] = new ScalaGuavaFunction(fn)
  }
  implicit class BoolFunctionAsGuava[A](val fn: (A => Boolean)) extends AnyVal {
    def asGuava: Predicate[A] = new ScalaGuavaPredicate(fn)
  }
  implicit class OrderingAsGuava[A](val order: math.Ordering[A]) extends AnyVal {
    def asGuava: Ordering[A] = Ordering.from(order)
  }
  implicit class EquivAsGuava[A](val eq: math.Equiv[A]) extends AnyVal {
    def asGuava: Equivalence[A] = new ScalaEquivalence(eq)
  }
  implicit class FutureAsGuava[A](val future: Future[A]) extends AnyVal {
    def asGuava: ListenableFuture[A] = new ScalaListenableFuture(future)
  }
}
