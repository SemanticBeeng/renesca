package renesca.table

import org.junit.runner.RunWith
import org.specs2.mock._
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import renesca._

@RunWith(classOf[JUnitRunner])
class TableSpec extends Specification with Mockito {
  implicit def intToJson(x: Int) = x.asJson
  implicit def stringToJson(x: String) = x.asJson
  implicit def listToJson[T: Encoder](xs: List[T]) = xs.asJson
  implicit def keyValue[T: Encoder](t: (String, T)) = (NonBacktickName(t._1), t._2.asJson)

  "Table" should {
    "access row cells by column" in {
      val columnToIndex = Map(("a", 0), ("b", 1))
      val row = Row(IndexedSeq(5, 6), columnToIndex)

      row("a").asNumber.get.toLong.get mustEqual 5
      row("b").asNumber.get.toLong.get mustEqual 6
    }

    "access rows by index" in {
      val columnToIndex = Map(("a", 0), ("b", 1))
      val row1 = Row(IndexedSeq("x", "y"), columnToIndex)
      val row2 = Row(IndexedSeq("hau", "rein"), columnToIndex)
      val table = Table(List("a", "b"), Vector(row1, row2))

      table(0) mustEqual row1
      table(1) mustEqual row2
    }

    "test non-emptyness" in {
      val table = Table(List("a", "b"), Vector.empty[Vector[ParameterValue]])

      table.nonEmpty mustEqual false
      table.isEmpty mustEqual true
    }

    "test non-emptyness" in {
      val columnToIndex = Map(("a", 0), ("b", 1))
      val row1 = Row(IndexedSeq("x", "y"), columnToIndex)
      val row2 = Row(IndexedSeq("hau", "rein"), columnToIndex)
      val table = Table(List("a", "b"), Vector(row1, row2))

      table.nonEmpty mustEqual true
      table.isEmpty mustEqual false
    }
  }

  "TableFactory" should {
    "create Table from raw data" in {
      val table = Table(List("p", "q"), List(List[ParameterValue](1, 2), List[ParameterValue](1, 4)))

      table.columns mustEqual List("p", "q")
      table.rows(0) mustEqual Row(Array[ParameterValue](1, 2), Map(("p", 0), ("q", 1)))
      table.rows(1) mustEqual Row(Array[ParameterValue](1, 4), Map(("p", 0), ("q", 1)))
    }
  }
}