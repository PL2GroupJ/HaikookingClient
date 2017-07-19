package jp.ac.ynu.pl2017.groupj.gui.credit

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TextArea
import java.net.URL
import java.util.*

class Credit : Initializable {
    @FXML lateinit var textArea: TextArea
    private val model = CreditModel()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        textArea.textProperty().bind(model.text)
    }
}
