package edu.toronto.cs.silva.tabix

import edu.toronto.cs.silva.TabixConfiguration
import scala.language.postfixOps
import scala.sys.process._
import scala.util.Try
import edu.toronto.cs.silva.GeneticUtils._

/**
 * Provides wrapper like functions for tabix use.
 */
private[tabix] object TabixUtils
{
  private val TABIX = "tabix"

  /**
   * A very naive check for tabix availability.
   */
  def checkAvailable =
    Try {(0 to 1) contains (TABIX !)} getOrElse false

  private[tabix] def rawTabix(chromosome: Char, position: Range)
    (implicit config: TabixConfiguration): Option[String] =
  {
    val command = (TABIX + " %s %s:%d-%d") format(config.filePath, chromosome, position.start, position.end)
    Try {command !!} toOption
  }

  def getTabixEntries(chromosome: Char, position: Range)
    (implicit configuration: TabixConfiguration): Option[Array[TabixEntry]] =
    rawTabix(chromosome, position)(configuration) map { output =>
      val lines = output split "\n" filterNot (_.isEmpty)
      lines flatMap lineToEntry
    }

  /**
   * @param l
   * @return [[TabixEntry]] with the alt, ref, and harmfulness populated from `l`, or [[None]] if any of those
   *         properties have not been found in `l`
   */
  private[tabix] def lineToEntry(l: String): Option[TabixEntry] =
  {
    val columns = l split "\t"
    columns(2).toBaseChar flatMap { ref =>
      columns(3).toBaseChar flatMap { alt =>
        columns(7).toHarmfulness flatMap { harmfulness =>
          columns(1).toPosition map { pos =>
            new TabixEntry(pos, ref, alt, harmfulness)
          }
        }
      }
    }
  }

  private implicit def stringToGenetic(s: String): GeneticString = new GeneticString(s)
  private class GeneticString(s: String)
  {
    def toBaseChar: Option[Char] = if (s.length == 1) {
      Option(checkBase(s.head)) flatten
    } else
    {
      None
    }

    def toHarmfulness = Try {s.toDouble} toOption

    def toPosition = Try {s.toInt} toOption
  }
}

private case class TabixEntry(position: Int, ref: Char, alt: Char, harmfulness: Double) {
  def toBasisTriple = (position, ref, alt)

  def toAbsolutelyPositioned(chromosome: Char) = (chromosome, position, ref, alt, harmfulness)
}
