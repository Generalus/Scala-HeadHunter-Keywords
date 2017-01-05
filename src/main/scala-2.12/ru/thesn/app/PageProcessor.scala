package ru.thesn.app

object PageProcessor {

    type JsonMap = Map[String, _]

    private val defaultPerPage = 100
    private val replacePattern = "(</?.*?>)|(quot)".r
    private val descriptionSplitPattern = "[^A-Za-z0-9 ]|( and )|( or )".r

    case class Statistics(errGroups: Int, successGroups: Int, errVacancies: Int, successVacancies: Int)

    def makeGroupUrl(keyword: String, perPage: Int, page: Int) = s"https://api.hh.ru/vacancies?text=$keyword&per_page=$perPage&page=$page"

    def process(keyword: String, pages: Int): (List[(String, Int)], Statistics) = {

        val workPages : Int = (pages - 1) / defaultPerPage

        val (errGroups, successGroups) = (0 to workPages)
                .map(makeGroupUrl(keyword, defaultPerPage, _))
                .map(JsonLoader.loadContent)
                .partition(_.isLeft)

        val (errVacancies, successVacancies) = successGroups
                .map(_.right.get)
                .flatMap(m => m("items").asInstanceOf[List[JsonMap]])
                .map(m => m("id").toString)
                .map(makeVacancyUrl)
                .map(JsonLoader.loadContent)
                .partition(_.isLeft)

        val resultProducts = successVacancies
                .map(_.right.get)
                .map(_("description").toString)

                // working with text
                .map(replacePattern.replaceAllIn(_, ";"))
                .filter(isRussianTest)
                .flatMap(descriptionSplitPattern.split(_).distinct)

                // working with separate words
                .map(_.trim)
                .filter(_.length > 1)
                .filter(!_.matches("\\d+"))
                .map(_.toLowerCase())
                .groupBy(identity)
                .mapValues(_.size)
                .toList
                .sortWith(_._2 > _._2)
                .take(200)


        (
                resultProducts,
                Statistics (
                    errGroups.map(_.left.get).size,
                    successGroups.map(_.right.get).size,
                    errVacancies.map(_.left.get).size,
                    successVacancies.map(_.right.get).size
                )
        )

    }

    private def makeVacancyUrl(id: String) = s"https://api.hh.ru/vacancies/$id"

    def isRussianTest(text: String): Boolean = {
        val rusLettersCount = text.count(i => i >= 'а' && i <= 'я')
        (text.length() - rusLettersCount) * 1f / text.length() < 0.5 // magic coefficient
    }

}
