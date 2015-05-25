package edu.toronto.cs.silva

import edu.toronto.cs.silva.tabix.{Tabix, TabixUtils}

import scala.language.postfixOps
import scala.util.Try
import edu.toronto.cs.silva.GeneticUtils._

/**
 * Processing of VCF files.
 */
private[silva] object Vcf {
  type Entry = (Char, Int, Char, Char)
  /** An entry in a list describing the harmfulness of a allele at a position in a chromosome.
    * (chromosome, position, ref, alt, harmfulness) */
  type HarmfulnessEntry = (Char, Int, Char, Char, Double)

  /** Processes every entry in the VCF file and returns non-zero harmfulness entries. */
  def getHarmfulness(vcf: String): Option[Iterable[HarmfulnessEntry]] = {
    val lines = getLines(vcf)
    val entries = convertToValidEntries(lines)
    val harmByChromosome = entries groupBy (_._1) flatMap {chrom ⇒ getHarmfulness(chrom._1, chrom._2)}
    if (harmByChromosome isEmpty) None else Option(harmByChromosome flatten)
  }

  /**
   * Calls tabix and filters for alleles that are potentially harmful and are present in `entries`.
   * @param entries should be entries for a particular chromosome (a subset in general)
   * @return non-zero harm matched entries
   */
  private def getHarmfulness(chromosome: Char, entries: Iterable[Entry]): Option[Iterable[HarmfulnessEntry]] = {
    if (entries.isEmpty) return None

    val positionRange = (entries minBy(_._2) _2) to (entries maxBy(_._2) _2)
    Tabix.matchToVcf(chromosome, positionRange, entries) map {e ⇒ e.filterNot(_._5 == 0.0)}
  }

  /** Returns entries that can be processed by tabix (ie. single base change). */
  private[silva] def convertToValidEntries(lines: Iterable[String]) = lines flatMap lineToValidEntries

  private def lineToValidEntries(line: String): Iterable[Entry] = {
    val columns = ({line split "\t"} map {_ toUpperCase} toIndexedSeq) lift
    val entries: Option[Iterable[Entry]] = columns(4) map getValidAlternates flatMap {alts ⇒
      columns(3) flatMap getValidBase flatMap {ref ⇒
        columns(0) flatMap getChromosome flatMap {chrom ⇒
          columns(1) flatMap getValidPosition map {pos ⇒
            // each alt gets an entry
            alts.map({a ⇒ (chrom, pos, ref, a)})
          }
        }
      }
    }
    entries getOrElse Iterable()
  }

  private def getChromosome(c: String) = {
    val single = c.replaceFirst("(?i)chr", "")
    if (single.length == 1) Option(single charAt 0) else None
  }

  private def getValidBase(base: String) = if (base.length == 1) checkBase(base charAt 0) else None

  private def getValidAlternates(alts: String): Iterable[Char] = {alts split ","} map getValidBase flatten

  private def getValidPosition(pos: String) = Try {pos toInt} toOption

  /** Gets non-comment lines. */
  private[silva] def getLines(vcf: String) = {
    val lines = vcf.split("\n")
    filterComments(lines)
  }

  /*todo. should the assumption that comments are always on top be made? */
  private def filterComments(lines: Iterable[String]) = lines filterNot (_ startsWith "#")
}
