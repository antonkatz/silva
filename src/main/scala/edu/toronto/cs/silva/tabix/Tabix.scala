package edu.toronto.cs.silva.tabix

import edu.toronto.cs.silva.{Vcf, TabixConfiguration}
import edu.toronto.cs.silva.Vcf.HarmfulnessEntry

/**
 *
 */
private[silva] object Tabix
{
  private implicit val configuration = new TabixConfiguration

  def matchToVcf(chromosome: Char, position: Range, entries: Iterable[Vcf.Entry]):
  Option[Iterable[HarmfulnessEntry]] = {
    TabixUtils.getTabixEntries(chromosome, position) map { tabixEntries ⇒
      val partialVcfEntries = entries map { e ⇒ (e._2, e._3, e._4) }
      val hashedTabixEnties = tabixEntries.foldLeft { Map[(Int, Char, Char), TabixEntry]() } { (m, e) ⇒
        m + (e.toBasisTriple → e)}
      // harmfulness
      partialVcfEntries flatMap {ve ⇒ hashedTabixEnties get ve map (_.toAbsolutelyPositioned(chromosome))}
    }
  }
}
