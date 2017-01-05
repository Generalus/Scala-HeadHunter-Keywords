package ru.thesn.app

import scala.io.Source
import scala.util.{Failure, Success, Try}
import scala.util.parsing.json.JSON


object JsonLoader {
    type JsonMap = Map[String, _]

    def loadContent(url: String): Either[String, JsonMap] = Try(Source.fromURL(url)("UTF-8").mkString) match {
        case Success(s) => JSON.parseFull(s) match {
            case Some(map: JsonMap)
                if map.contains("errors") && map.contains("description") => Left(map("description").toString)
            case Some(map: JsonMap) => Right(map)
            case _ => Left(s"Incorrect json was loaded from $url!")
        }
        case Failure(e) => Left(e.toString)
    }
}
