package edu.toronto.cs.silva.tabix

import edu.toronto.cs.silva.TabixConfiguration
import edu.toronto.cs.silva.tabix.TabixUtils._
import org.scalatest.{FlatSpec, Matchers}

class TabixUtilsTest extends FlatSpec with Matchers
{
  private implicit val configuration = new TabixConfiguration

  "Check for tabix availability" should "return true if present" in {
    checkAvailable shouldBe true
  }

  "Raw tabix output" should "be present" in {
    val r = rawTabix('X', 2800000 to 2830000)

    r shouldBe defined
    r.get should not be empty
  }

  "Lines of raw tabix" should "be converted correctly" in {
    val line = "X\t2999053\tA\tT\tc.405A>T\tARSF\tNM_001201539\t0.013"
    val r = lineToEntry(line)
    r shouldBe defined
    r.get.ref should equal ('A')
    r.get.alt should equal ('T')
    r.get.harmfulness should equal (0.013)
  }

  "27 entries" should "be retrieved by tabix" in {
    val r = getTabixEntries('1', 2980000 to 3000000)

    r shouldBe defined
    r.get should have size 27
    r.get should contain allOf(new TabixEntry(2985829, 'A', 'T', 0.017), new TabixEntry(2985832, 'C', 'A', 0.010))
  }
}
