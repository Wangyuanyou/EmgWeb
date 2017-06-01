package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
/**
  * Created by wangyuanyou on 5/20/17.
  */
case class Source(id:Int, name:String,sourcetype:String,filepath:String)
object Source {
  val simple = {
      get[Int]("id") ~
      get[String]("name") ~
      get[String]("sourcetype") ~
      get[String]("filepath") map {
      case id ~ name ~ sourcetype ~ filepath => Source(id, name, sourcetype, filepath)
    }
  }

  def listAllSource(): Seq[Source] = {
    DB.withConnection { implicit connection =>
      SQL("select * from source").as(Source.simple *)
    }
  }
  def deleteOneSource(id:Int):Unit= {
    DB.withConnection { implicit connection =>
      SQL("delete from source where id = {id}").on('id -> id).executeUpdate()
    }
  }
  def updateOneSource(one:Source)={
    DB.withConnection { implicit connection =>
      SQL("update source set name={name},sourcetype={sourcetype} where id = {id}").
        on('id->one.id,'name->one.name,'sourcetype->one.sourcetype).executeUpdate()
    }
  }
  def addOneSource(one:Source):Unit={
    DB.withConnection { implicit connection =>
      SQL("insert into jobLib(id,name,sourcetype,filepath) values ({id},{name},{sourcetype},{filepath})").
        on('id->one.id,'name->one.name,'sourcetype->one.sourcetype,'filepath->one.filepath).executeUpdate()
    }
  }
  def getNamebyID(id:Int):Seq[Source]= {
    val sql_str="select * from source where id="+id
    DB.withConnection { implicit connection =>
      SQL(sql_str).as(Source.simple *)
    }
  }
}
