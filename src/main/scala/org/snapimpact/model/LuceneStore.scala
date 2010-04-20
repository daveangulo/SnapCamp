package org.snapimpact.model

/**
 * Created by IntelliJ IDEA.
 * User: dave
 * Date: Apr 11, 2010
 * Time: 12:21:18 PM
 * To change this template use File | Settings | File Templates.
 */

import org.apache.lucene._
import analysis._
import index._
import search._
import store._
import standard.StandardAnalyzer
import document._
import queryParser._
import util._
import org.snapimpact.etl.model.dto.FootprintFeed

object LoadHelper {
  implicit def feedSplitter(in: FootprintFeed): Seq[(String, Option[String])] = {
    in.opportunities.opps.map(opp => {
      List(
        opp.description match {
          case Some(desc) => desc -> Some("desc")
          case _ => "" -> Some("desc")
        },
        opp.title match {
          case Some(title) => title -> Some("title")
          case _ => "" -> Some("title")
        },
        opp.categoryTags match {
          case Some(categoryTags) => categoryTags.toString -> Some("categoryTags")
          case _ => "" -> Some("categoryTags")
        },
        opp.audienceTags match {
          case Some(audienceTags) => audienceTags.toString -> Some("audienceTags")
          case _ => "" -> Some("audienceTags")
        }
       )
    })
  }
}

import LoadHelper._

object LuceneStore {
  val analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT)

  // Store the index in memory:
  val directory = new RAMDirectory()
  // To store an index on disk, use this instead:
  //Directory directory = FSDirectory.open("/tmp/testindex");

  // Now search the index:
  //val isearcher = new IndexSearcher(directory, true) // read-only=true

  def getWriter: IndexWriter = new IndexWriter(directory, analyzer, true,
    new IndexWriter.MaxFieldLength(25000))

  def getSearcher: IndexSearcher = new IndexSearcher(directory, true); // read-only=true

  def getParser(field: String): QueryParser = new QueryParser(Version.LUCENE_CURRENT, field, analyzer)

  def apply = this
}

class LuceneStore extends SearchStore {
  val ls = LuceneStore

  def add[T](guid: GUID, item: T)(implicit splitter: T => Seq[(String, Option[String])]): Unit = {
    val iwriter = ls.getWriter

    for{(data, field) <- item}
      {
        val doc = new Document()
        doc.add(new Field("guid", guid.toString, Field.Store.YES,
          Field.Index.ANALYZED))
        field match {
          case Some(f) => doc.add(new Field(f, data, Field.Store.YES,
            Field.Index.ANALYZED))
          case _ => doc.add(new Field("Misc", data, Field.Store.YES,
            Field.Index.ANALYZED))
        }

        iwriter.addDocument(doc)
      }

    iwriter.close()
  }

  def remove(guid: GUID): Unit = {
    val iwriter = ls.getWriter

    iwriter.deleteDocuments(new Term("guid", guid.toString))
  }

  def update[T](guid: GUID, item: T)(implicit splitter: T => Seq[(String, Option[String])]): Unit = {

  }

  def find(search: String,
           first: Int = 0, max: Int = 200,
           inSet: Option[Seq[GUID]] = None): List[GUID] = {List(GUID.create)}
}