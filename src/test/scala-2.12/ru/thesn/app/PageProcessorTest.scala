package ru.thesn.app

import org.scalatest.FlatSpec


class PageProcessorTest extends FlatSpec {

    "text" should "be russian" in {
        val text =
            """
              |Опыт разработки на Scala, знание и умение работать с scala collections, понимание for-comprehensions
              |Опыт разработки сетевых приложений, желателен опыт использования Netty, Akka Streams, Akka Http
              |Умение писать и оптимизировать SQL-запросы к Postgres
            """.stripMargin
        assert(PageProcessor.isRussianTest(text))
    }

    "text" should "not be russian" in {
        val text =
            """
              |Experience working in Scrum/Agile development methodologies
              |Excellent English communication skills, both verbal and written
              |Understands the business needs and provide optimal customer experience to our users
              |Experience writing unit tests
            """.stripMargin
        assert(!PageProcessor.isRussianTest(text))
    }

    "groupUrl" should "be correct" in {
        val groupUrl = PageProcessor.makeGroupUrl("scala", 100, 0)
        assert("https://api.hh.ru/vacancies?text=scala&per_page=100&page=0" == groupUrl)
    }



}
