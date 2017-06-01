package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

/**
  * Created by wangyuanyou on 5/19/17.
  */
case class JobConfig(id:Int, jobid:Int,key:String,mean:String,ismust:String,eg:String)
object JobConfig{
  val simple = {
    get[Int]("id") ~
      get[Int]("jobid") ~
      get[String]("key") ~
      get[String]("mean") ~
      get[String]("ismust") ~
      get[String]("eg") map {
      case id ~ jobid ~ key ~ mean ~ ismust ~ eg => JobConfig(id, jobid, key, mean, ismust, eg)
    }
  }
  def listByJobid(jobid:Int):Seq[JobConfig]= {
    val sql_str = "select * from jobconfig where jobid=" + jobid
    DB.withConnection { implicit connection =>
      SQL(sql_str).as(JobConfig.simple *)
    }
  }
}
