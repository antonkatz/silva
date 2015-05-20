package edu.toronto.cs.silva

import org.scalatest.{Matchers, FlatSpec}
import edu.toronto.cs.silva.TabixUtils._

/**
 *
 */
class TabixUtilsTest extends FlatSpec with Matchers
{
  "Check for tabix availability" should "return true if present" in {
    checkAvailable shouldBe true
  }

  "Raw tabix output" should "be present" in {
    implicit val config = new TabixConfiguration
    val r = rawTabix("X", 2800000 to 2830000)

    r shouldBe defined
    r.get should not be empty
  }
}
