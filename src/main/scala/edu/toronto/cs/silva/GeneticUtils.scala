package edu.toronto.cs.silva

/**
 * Basic functions related to genetics.
 */
object GeneticUtils {
  private val allowedBases = Set('A', 'T', 'G', 'C')

  /**
   * @return uppercase of the character passed in if it is a valid base, [[None]] otherwise
   * @param b
   */
  /*fixme. ideally should not be here. */
  def checkBase(b: Char): Option[Char] =
  {
    val base = b.toUpper
    if (allowedBases contains base) {
      Option(base)
    } else None
  }
}
