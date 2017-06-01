package models

/**
  * Created by wangyuanyou on 5/15/17.
  */
import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

case class Job(id:Int, jobname:String,jobpath:String,jobtype:String,jobinfo:String)

object Job{
  val simple = {
    get[Int]("id")~
    get[String]("jobname")~
    get[String]("jobpath")~
    get[String]("jobtype")~
    get[String]("jobinfo")map{
      case id~jobname~jobpath~jobtype~jobinfo=>Job(id,jobname,jobpath,jobtype,jobinfo)
    }

  }
  def listAllJob():Seq[Job]= {
    DB.withConnection { implicit connection =>
      SQL("select * from jobLib").as(Job.simple *)
    }
  }
  def addOneJob(one:Job):Unit={
    DB.withConnection { implicit connection =>
      SQL("insert into jobLib(id,jobname,jobpath,jobtype,jobinfo) values ({id},{jobname},{jobpath},{jobtype},{jobinfo})").
        on('id->one.id,'jobname->one.jobname,'jobpath->one.jobpath,'jobtype->one.jobtype,'jobinfo->one.jobinfo).executeUpdate()
    }
  }
  def deleteOneJob(id:Int):Unit= {
    DB.withConnection { implicit connection =>
      SQL("delete from jobLib where id = {id}").on('id -> id).executeUpdate()
    }
  }
  def updateOneJOb(one:Job):Unit={
    DB.withConnection { implicit connection =>
      SQL("update jobLib set jobname={jobname},jobpath={jobpath},jobtype={jobtype},jobinfo={jobinfo} where id = {id}").
        on('id->one.id,'jobname->one.jobname,'jobpath->one.jobpath,'jobtype->one.jobtype,'jobinfo->one.jobinfo).executeUpdate()
    }
  }
  def selectJobByType(select:String):Seq[Job]={
    DB.withConnection { implicit connection =>
      SQL("select * from jobLib where jobtype='input'").as(Job.simple *)
    }

  }
}
