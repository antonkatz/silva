package edu.toronto.cs.silva

/**
 *
 */
private[silva] class TabixConfiguration
{
  private[silva] val filePath = "data/sorted_vcf_like_res.txt.gz"
}

object ErrorMessages {
  val noVCF = "Either no VCF file was uploaded or could not process the VCF file"
  val couldNotFindHarmfulness = "Could not find harmfulness scores"
}
