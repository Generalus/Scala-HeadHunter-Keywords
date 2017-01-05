package ru.thesn.app

import ru.thesn.app.PageProcessor.Statistics


object Main {

    def main(args: Array[String]) {

        println("Enter the keyword:")
        val keyword = scala.io.StdIn.readLine()

        println("Data is being processed...")
        val initialData = JsonLoader.loadContent(PageProcessor.makeGroupUrl(keyword, 1, 0))

        val (result : List[(String, Int)], Statistics(errGroups, successGroups, errVacancies, successVacancies)) = initialData match {
            case Left(message) => throw new ServerError(s"Initial request was produced incorrect answer: $message")
            case Right(map) =>
                val pages = map("pages").toString.toDouble.toInt
                if (pages > 1) {
                    PageProcessor.process(keyword, pages)
                }
        }

        println(s"\nGroups (errors / successful results): $errGroups / $successGroups")
        println(s"Vacancies (errors / successful results): $errVacancies / $successVacancies\n")

        if (successVacancies > 0) {
            val maxLength = result.maxBy(_._1.length)._1.length
            result.foreach(t => println(s"${t._1 + " " * (maxLength - t._1.length)} ${t._2}"))
        }
    }

    class ServerError (val message: String) extends Exception (message)

}