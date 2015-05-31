package edu.toronto.cs.silva

/**
 *
 */
private[silva] class TabixConfiguration
{
  private[silva] val filePath = "data/sorted_vcf_like_res.txt.gz"
}

object Messages {
  val noVCF = "Either no VCF file was uploaded or could not process the VCF file"
  val couldNotFindHarmfulness = "Could not find harmfulness scores"
  val maxProcesses = "The server is overloaded. Please try again later"
  val idDoesNotExist = "The requested result does not exist"
  
  val logWarningMaxProcess = "Maximum number of processes reached"
  val logHasExpired = "Some results expired"
}

object ServerSettings {
  /** Expire requests in 20 min. */
  val expireIn = 1000 * 60 * 20

  val maxSimultaneousProcesses = 3
}
