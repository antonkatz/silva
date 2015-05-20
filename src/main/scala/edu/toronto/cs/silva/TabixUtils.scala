package edu.toronto.cs.silva

import scala.util.Try
import sys.process._

/**
 *
 */
private[silva] object TabixUtils
{
  private val TABIX = "tabix"

  /** A very naive check for tabix availability. */
  private[silva] def checkAvailable =
    Try {(0 to 1) contains (TABIX !)} getOrElse false

  private[silva] def rawTabix(chromosome: String, position: Range)
    (implicit config: TabixConfiguration): Option[String] =
  {
    val command = (TABIX + " %s %s:%d-%d") format(config.filePath, chromosome, position.start, position.end)
    Try {command !!} toOption
  }
}
