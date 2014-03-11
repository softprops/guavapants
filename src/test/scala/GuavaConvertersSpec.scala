package guavapants

import org.scalatest.FunSpec
import com.google.common.base.{ Function => GFunction, Optional, Predicate, Supplier }
import com.google.common.collect.Ordering
import GuavaConverters._
import scala.collection.JavaConverters._

class GuavaConvertersSpec extends FunSpec {
  describe("GuavaConverters") {

    it ("should convert optionals") {
      assert(Some(1).asGuava === Optional.of(1))
      assert(Optional.of(1).asScala === Some(1))
      assert(None.asGuava === Optional.absent)
      assert(Optional.absent[Int].asScala === None)
    }

    it ("should convert functions") {
      val gf: GFunction[Int, String] = ((_:Int).toString).asGuava
      assert(gf.apply(1) === "1")
      val sf: Function1[Int, String] = gf.asScala
      assert(sf(1) === "1")
    }

    it ("should convert predicates") {
      val gp: Predicate[String] = ((_:String).isEmpty).asGuava
      assert(gp.apply("") === true)
      val sf: Function[String, Boolean] = gp.asScala
      assert(sf.apply("") === true)
    }

    it ("should convert suppliers") {
      val gs: Supplier[Int] = (() => 1).asGuava
      assert(gs.get() === 1)
      val sf: Function0[Int] = gs.asScala
      assert(sf() === 1)
    }

    it ("should convert orderings") {
      val go: Ordering[Int]  = math.Ordering.Int.reverse.asGuava
      val jxs = List(1,2,3).asJava
      assert(go.sortedCopy(jxs).asScala === List(3,2,1))
      val so: math.Ordering[Int] = go.asScala
      assert(List(1,2,3).sorted(so) === List(3,2,1))
    }

  }
}
