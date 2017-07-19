package jp.ac.ynu.pl2017.groupj.gui.credit

import javafx.beans.property.SimpleStringProperty

class CreditModel {
    val text = SimpleStringProperty()   // クレジットの全文

    init {
        javaClass.classLoader.getResourceAsStream("text/credit.txt").use {
            text.value = it.reader().readText()
        }
    }
}