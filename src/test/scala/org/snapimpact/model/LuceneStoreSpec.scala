package org.snapimpact.model

/**
 * Created by IntelliJ IDEA.
 * User: dave
 * Date: Apr 11, 2010
 * Time: 12:52:22 PM
 * To change this template use File | Settings | File Templates.
 */

import _root_.org.specs._
import _root_.org.specs.runner._
import _root_.org.specs.Sugar._
import _root_.scala.xml._

import _root_.org.snapimpact.etl.model.dto._

import helpers._
import net.liftweb.util._
import LoadHelper._

class LuceneStoreTest extends Runner(new LuceneStoreSpec) with JUnit with Console

class LuceneStoreSpec extends Specification {
  lazy val luceneStore: LuceneStore = new LuceneStore

  "Lucene Store" should {
    "Associate add a GUID" in {
      val guid = GUID.create()

      val subject = XML.loadFile("src/test/resources/sampleData0.1.r1254.xml")
      val footprintFeed = FootprintFeed.fromXML(subject)

      luceneStore.add(guid,footprintFeed)

      val ir = org.apache.lucene.index.IndexReader.open(LuceneStore.directory, true)
      val d = ir.document(0)

      println(d.toString)

      1 must be_==(1)
    }
  }
}