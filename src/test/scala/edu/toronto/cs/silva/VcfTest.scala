package edu.toronto.cs.silva

import org.scalatest.{FlatSpec, Matchers}

/**
 * Testing the parsing of VCF files.
 */
class VcfTest extends FlatSpec with Matchers {
  "Comments" should "not be present" in {
    val r = Vcf.getLines(VcfTest.contents)
    r.filter(_ startsWith "#") shouldBe empty
    r should have size 24
  }

  "(Only) valid lines" should "be present" in {
    val l = Vcf.getLines(VcfTest.contents)
    val r = Vcf.convertToValidEntries(l)
    r should contain allOf (('2', 7584668, 'G', 'A'), ('1', 15211, 'T', 'G'), ('1', 14773, 'C', 'T'),
        //2 alts
        ('1', 15274, 'A', 'T'), ('1', 15274, 'A', 'G'))
    assert(!r.exists(e ⇒ e._1 == '2' && e._2 == 7629186))
  }

  "Harmfulness socres" should "be found" in {
    val r = Vcf.getHarmfulness(VcfTest.contents)
    r
  }
}

object VcfTest {
  val contents = "##FORMAT=<ID=DP,Number=1,Type=Integer,Description=\"# high-quality bases\">\n##FORMAT=<ID=SP," +
      "Number=1,Type=Integer,Description=\"Phred-scaled strand bias P-value\">\n##FORMAT=<ID=PL,Number=G," +
      "Type=Integer,Description=\"List of Phred-scaled genotype likelihoods\">\n##PG:samtools_mpileup,VN:0.1.17," +
      "CL:/lb/project/mugqic/software/tools/samtools-0.1.18/samtools mpileup -E -q 1 -u -D -S -L 1000 -f " +
      "/lb/project/mugqic/projects/jschwart/reflib/hg19_chr_files/hg19_wRandomsNew.fa " +
      "/lb/project/mugqic/projects/jschwart/exome/KymBoycott/174/KB_174_81272/KB_174_81272.t30l30.realigned.markdup" +
      ".sorted.fixmate.final.bam | /lb/project/mugqic/software/tools/samtools-0.1.18/bcftools/bcftools view -vcg - > " +
      "/lb/project/mugqic/projects/jschwart/exome/KymBoycott/174/KB_174_81272/KB_174_81272.raw" +
      ".vcf\n#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tKB_174_81272\nchr1\t10109\t.\tA\tT\t5.56\t" +
      ".\tDP=5;VDB=0.0122;AF1=0.5162;AC1=1;DP4=2,0,1,1;MQ=22;FQ=-15.1;PV4=1,0.4,1,1\tGT:PL:DP:SP:GQ\t0/1:34,0," +
      "12:4:0:17\nchr1\t10583\t.\tG\tA\t6.2\t.\tDP=1;AF1=1;AC1=2;DP4=0,0,0,1;MQ=60;FQ=-30\tGT:PL:DP:SP:GQ\t1/1:35,3," +
      "0:1:0:4\nchr1\t13273\t.\tG\tC\t40\t.\tDP=39;VDB=0.0413;AF1=0.5;AC1=1;DP4=22,8,8,1;MQ=23;FQ=43;PV4=0.65,0.4,1,0" +
      ".13\tGT:PL:DP:SP:GQ\t0/1:70,0,168:39:2:73\nchr1\t14773\t.\tC\tT\t67\t.\tDP=57;VDB=0.0418;AF1=0.5;AC1=1;DP4=20," +
      "21,7,8;MQ=23;FQ=70;PV4=1,0.07,1,1\tGT:PL:DP:SP:GQ\t0/1:97,0,173:56:0:99\nchr1\t14907\t.\tA\tG\t189\t.\tDP=231;" +
      "VDB=0.0384;AF1=0.5;AC1=1;DP4=110,31,73,12;MQ=32;FQ=192;PV4=0.16,0.086,0.11,0.041\tGT:PL:DP:SP:GQ\t0/1:219,0," +
      "255:226:8:99\nchr1\t14930\t.\tA\tG\t216\t.\tDP=335;VDB=0.0364;AF1=0.5;AC1=1;DP4=145,61,90,35;MQ=33;FQ=219;" +
      "PV4=0.8,0.034,0.1,1\tGT:PL:DP:SP:GQ\t0/1:246,0,255:331:1:99\nchr1\t15211\t.\tT\tG\t55\t.\tDP=14;VDB=0.0440;" +
      "AF1=0.5;AC1=1;DP4=1,4,1,7;MQ=24;FQ=19.1;PV4=1,1,0.47,1\tGT:PL:DP:SP:GQ\t0/1:85,0,46:13:0:49\nchr1\t15274\t" +
      ".\tA\tT,G\t22\t.\tDP=4;VDB=0.0317;AF1=1;AC1=2;DP4=0,0,0,4;MQ=23;FQ=-36\tGT:PL:DP:SP:GQ\t1/1:57,12,3,51,0," +
      "48:4:0:15\nchr1\t15820\t.\tG\tT\t95\t.\tDP=38;VDB=0.0400;AF1=0.5;AC1=1;DP4=12,6,14,5;MQ=26;FQ=97.8;PV4=0.73,1," +
      "1,1\tGT:PL:DP:SP:GQ\t0/1:125,0,139:37:1:99\n" +
      "chr2\t7584668\t.\tG\tA\t6.98\t.\tDP=1;AF1=1;AC1=2;DP4=0,0,0,1;MQ=60;FQ=-30\tGT:PL:DP:SP:GQ\t1/1:36,3," +
      "0:1:0:4\nchr2\t7586100\t.\tA\tG\t4.13\t.\tDP=2;AF1=0.5002;AC1=1;DP4=0,1,1,0;MQ=60;FQ=3.14;PV4=1,1,1," +
      "1\tGT:PL:DP:SP:GQ\t0/1:32,0,29:2:0:30\nchr2\t7588353\t.\tC\tA\t3.55\t.\tDP=1;AF1=1;AC1=2;DP4=0,0,1,0;MQ=60;" +
      "FQ=-30\tGT:PL:DP:SP:GQ\t0/1:31,3,0:1:0:4\nchr2\t7600619\t.\tC\tT\t7.8\t.\tDP=1;AF1=1;AC1=2;DP4=0,0,1,0;MQ=60;" +
      "FQ=-30\tGT:PL:DP:SP:GQ\t1/1:37,3,0:1:0:4\nchr2\t7606483\t.\tC\tA\t7.8\t.\tDP=1;AF1=1;AC1=2;DP4=0,0,0,1;MQ=60;" +
      "FQ=-30\tGT:PL:DP:SP:GQ\t1/1:37,3,0:1:0:4\nchr2\t7610417\t.\tC\tA\t4.13\t.\tDP=3;AF1=0.4998;AC1=1;DP4=1,1,0,1;" +
      "MQ=60;FQ=6.2;PV4=1,1,1,1\tGT:PL:DP:SP:GQ\t0/1:32,0,68:3:0:31\nchr2\t7612713\t.\tC\tA\t3.01\t.\tDP=3;AF1=0" +
      ".4998;AC1=1;DP4=2,0,1,0;MQ=60;FQ=4.77;PV4=1,1,1,1\tGT:PL:DP:SP:GQ\t0/1:30,0,58:3:0:28\nchr2\t7613885\t" +
      ".\tG\tA\t4.13\t.\tDP=2;AF1=0.5;AC1=1;DP4=0,1,0,1;MQ=60;FQ=3.81;PV4=1,1,1,1\tGT:PL:DP:SP:GQ\t0/1:32,0," +
      "31:2:0:31\nchr2\t7629186\t.\taaaggaaggaagg\taaaggaagg\t81.2\t.\tINDEL;DP=2;VDB=0.0177;AF1=1;AC1=2;DP4=0,0,1,1;" +
      "MQ=60;FQ=-40.5\tGT:PL:DP:SP:GQ\t1/1:120,6,0:2:0:10\nchr2\t7696898\t.\tC\tCT\t4.42\t.\tINDEL;DP=1;AF1=1;AC1=2;" +
      "DP4=0,0,1,0;MQ=60;FQ=-37.5\tGT:PL:DP:SP:GQ\t0/1:40,3,0:1:0:3\nchr2\t7701637\t.\tA\tT\t9.53\t.\tDP=1;AF1=1;" +
      "AC1=2;DP4=0,0,0,1;MQ=60;FQ=-30\tGT:PL:DP:SP:GQ\t1/1:39,3,0:1:0:5\nchr2\t7709752\t.\tC\tA\t7.8\t.\tDP=1;AF1=1;" +
      "AC1=2;DP4=0,0,1,0;MQ=60;FQ=-30\tGT:PL:DP:SP:GQ\t1/1:37,3,0:1:0:4\nchr2\t7718233\t.\tG\tA\t33\t.\tDP=4;VDB=0" +
      ".0170;AF1=0.5;AC1=1;DP4=1,1,1,1;MQ=60;FQ=29;PV4=1,1,1,1\tGT:PL:DP:SP:GQ\t0/1:63,0,57:4:0:59\nchr2\t7718418\t" +
      ".\tT\tC\t11.3\t.\tDP=1;AF1=1;AC1=2;DP4=0,0,0,1;MQ=60;FQ=-30\tGT:PL:DP:SP:GQ\t1/1:41,3,0:1:0:5\nchr2\t7723685\t" +
      ".\tC\tT\t26\t.\tDP=3;VDB=0.0094;AF1=0.5002;AC1=1;DP4=1,0,0,2;MQ=60;FQ=6.19;PV4=0.33,0.14,1,0" +
      ".33\tGT:PL:DP:SP:GQ\t0/1:56,0,32:3:0:35"
}
