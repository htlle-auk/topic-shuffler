package at.htlle.auk.shuffler.controller;

import at.htlle.auk.shuffler.model.Topic;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.*;

public class ShuffleController {
    @FXML private GridPane grid;
    @FXML private ComboBox<String> subjectCombo;

    private Map<String, List<Topic>> subjectTopics;
    private List<StackPane> cards = new ArrayList<>();
    private final List<StackPane> selected = new ArrayList<>();
    private boolean isShuffled = false;

    @FXML
    public void initialize() {
        subjectTopics = Map.of(
                "UFW", List.of(
                        new Topic("1 Unternehmensrecht und öffentliches Wirtschaftsrecht"),
                        new Topic("2 Arbeits- und Steuerrecht"),
                        new Topic("3 Privatrecht"),
                        new Topic("4 Personalmanagement, Entrepreneurship und Innovation"),
                        new Topic("5 Marketing und Vertrieb"),
                        new Topic("6 Buchhaltung und Bilanzierung"),
                        new Topic("7 Finanzierung und Investitionsrechnung"),
                        new Topic("8 Controlling und Mitarbeiterführung")
                ),
                "BET", List.of(
                        new Topic("1 Materialwirtschaft, Logistik"),
                        new Topic("2 Vollkostenrechnung"),
                        new Topic("3 Teilkostenrechnung und sonstige Systeme der Kostenrechnung"),
                        new Topic("4 Arbeitsvorbereitung, Produktionsplanung und -steuerung"),
                        new Topic("5 Unternehmensorganisation, Arbeitsplatz- und Betriebsstättenplanung"),
                        new Topic("6 Projektmanagement"),
                        new Topic("7 Qualitätsmanagementsysteme"),
                        new Topic("8 Statistische Methoden im Qualitäts- und Umweltmanagement")
                ),
                "INFI", List.of(
                        new Topic("1 IT-Hardware"),
                        new Topic("2 Betriebssysteme"),
                        new Topic("3 Office Suite"),
                        new Topic("4 Betriebsdatenerfassung"),
                        new Topic("5 Materialwirtschaft im ERP-System"),
                        new Topic("6 Produktionsplanung und -steuerung im ERP-System"),
                        new Topic("7 Vertrieb im ERP-System"),
                        new Topic("8 Informationssysteme ")
                ),
                "NTVS", List.of(
                        new Topic("1 Netzwerktechnik"),
                        new Topic("2 Virtualisierung"),
                        new Topic("3 Embedded Systems"),
                        new Topic("4 Industrie 4.0"),
                        new Topic("5 API Technologien"),
                        new Topic("6 Robotik"),
                        new Topic("7 Gleichstrom und Halbleitertechnik"),
                        new Topic("8 Computer Vision")
                ),
                "SYP", List.of(
                        new Topic("1 Seq. Projektmanagement"),
                        new Topic("2 Agile Methoden"),
                        new Topic("3 Entwicklungstools"),
                        new Topic("4 Systemkonzeption"),
                        new Topic("5 Dokumentation"),
                        new Topic("6 Risikomanagement"),
                        new Topic("7 Abschätzungen"),
                        new Topic("8 Systembetreuung")
                ),
                "POS", List.of(
                        new Topic("1 Objektorientierte Programmierung"),
                        new Topic("2 Vererbung, abstrakte Klassen, Interfaces"),
                        new Topic("3 komplexe Datenstrukturen und Algorithmen"),
                        new Topic("4 Design Patterns"),
                        new Topic("5 Multithreading"),
                        new Topic("6 GUI Development"),
                        new Topic("7 Testing"),
                        new Topic("8 Development Tools")
                ),
                "GGP", List.of(
                        new Topic("1 Europa im Wandel (GGP-Fächerverbindend)"),
                        new Topic("2 Globale Entwicklungstrends (GGP-Fächerverbindend)"),
                        new Topic("3 Trends in der Sozialgeographie"),
                        new Topic("4 Wirtschaftsräume und Wirtschaftspolitik"),
                        new Topic("5 Die Erde im Wandel"),
                        new Topic("6 Wechselwirkungen von Kultur, Gesellschaft und Wirtschaft in der Geschichte"),
                        new Topic("7 Historische politische Entwicklungen und Konflikte sowie die Bedeutung für die Gegenwart"),
                        new Topic("8 Politische Ideologien, Systeme und Akteure")
                ),
                "NAWI", List.of(
                        new Topic("1 Teilchenstruktur der Materie - woraus alles besteht"),
                        new Topic("2 Energie, Elektrizität und erneuerbare Energieträger"),
                        new Topic("3 Ruhende und bewegte Körper und Medien"),
                        new Topic("4 Mechanische Wellen; optische und akustische Phänomene"),
                        new Topic("5 Wärme in Alltag und Technik"),
                        new Topic("6 Moderne Physik und ihre Anwendungen"),
                        new Topic("7 Anorganische Rohstoffe und ihre Veredelung"),
                        new Topic("8 Organische Grundstoffe und ihre Verwendung"),
                        new Topic("9 Stoffumwandlungen und ökologische Aspekte in den Naturwissenschaften"),
                        new Topic("10 Grundlagen lebendiger Systeme")
                )
        );
        subjectCombo.getItems().addAll(subjectTopics.keySet());
        subjectCombo.setOnAction(e -> loadTopics());
        subjectCombo.getSelectionModel().selectFirst();
        loadTopics();
    }

    private void loadTopics() {
        cards.clear();
        selected.clear();
        isShuffled = false;
        grid.getChildren().clear();

        String fach = subjectCombo.getValue();
        List<Topic> topics = subjectTopics.get(fach);
        for (Topic t : topics) {
            StackPane card = CardFactory.createCard(t.getName());
            card.setOnMouseClicked(this::onCardClicked);
            cards.add(card);
        }
        layoutCards();
    }

    private void layoutCards() {
        grid.getChildren().clear();
        for (int i = 0; i < cards.size(); i++) {
            grid.add(cards.get(i), i % 4, i / 4);
        }
    }

    @FXML
    private void onShuffle() {
        if (isShuffled) return;
        isShuffled = true;

        Collections.shuffle(cards);
        for (int i = 0; i < cards.size(); i++) {
            final int index = i;
            StackPane card = cards.get(index);
            // Zwei-Phasen-Flip: vornirgendphase verstecken, dann rückseite zeigen
            RotateTransition flip1 = new RotateTransition(Duration.millis(200), card);
            flip1.setAxis(Rotate.Y_AXIS);
            flip1.setFromAngle(0);
            flip1.setToAngle(90);
            flip1.setDelay(Duration.millis(index * 100));
            RotateTransition flip2 = new RotateTransition(Duration.millis(200), card);
            flip2.setAxis(Rotate.Y_AXIS);
            flip2.setFromAngle(90);
            flip2.setToAngle(180);
            flip2.setOnFinished(e -> CardFactory.showBack(card));
            SequentialTransition seq = new SequentialTransition(flip1, flip2);
            seq.setOnFinished(e -> animateMove(card, index));
            seq.play();
        }
        PauseTransition pause = new PauseTransition(Duration.millis(400 + cards.size() * 100 + 300));
        pause.setOnFinished(e -> {
            cards.forEach(c -> { c.setTranslateX(0); c.setTranslateY(0); c.setRotate(0); });
            layoutCards();
        });
        pause.play();
    }

    private void animateMove(StackPane card, int index) {
        Point2D old = card.localToScene(0, 0);
        double cellX = grid.getLayoutX() + (index % 4) * (card.getWidth() + grid.getHgap());
        double cellY = grid.getLayoutY() + (index / 4) * (card.getHeight() + grid.getVgap());
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), card);
        tt.setByX(cellX - old.getX());
        tt.setByY(cellY - old.getY());
        tt.play();
    }

    private void onCardClicked(MouseEvent event) {
        StackPane card = (StackPane) event.getSource();
        if (!isShuffled || selected.size() >= 2) return;

        int idx = selected.size();
        // Flip zur Vorderseite
        RotateTransition flip1 = new RotateTransition(Duration.millis(200), card);
        flip1.setAxis(Rotate.Y_AXIS);
        flip1.setFromAngle(180);
        flip1.setToAngle(90);
        RotateTransition flip2 = new RotateTransition(Duration.millis(200), card);
        flip2.setAxis(Rotate.Y_AXIS);
        flip2.setFromAngle(90);
        flip2.setToAngle(0);
        flip2.setOnFinished(e -> {
            CardFactory.showFront(card);
            selected.add(card);
            card.getStyleClass().add("selected");
            if (selected.size() == 2) {
                PauseTransition revealPause = new PauseTransition(Duration.millis(300));
                revealPause.setOnFinished(ev -> revealAll());
                revealPause.play();
            }
        });
        new SequentialTransition(flip1, flip2).play();
    }

    private void revealAll() {
        for (StackPane card : cards) {
            if (!selected.contains(card)) {
                // Selbstähnlich Flip
                RotateTransition flip1 = new RotateTransition(Duration.millis(200), card);
                flip1.setAxis(Rotate.Y_AXIS);
                flip1.setFromAngle(180);
                flip1.setToAngle(90);
                RotateTransition flip2 = new RotateTransition(Duration.millis(200), card);
                flip2.setAxis(Rotate.Y_AXIS);
                flip2.setFromAngle(90);
                flip2.setToAngle(0);
                flip2.setOnFinished(e -> CardFactory.showFront(card));
                new SequentialTransition(flip1, flip2).play();
            }
        }
    }
}

class CardFactory {
    private static final Image backImage;

    static {
        InputStream is = CardFactory.class.getResourceAsStream("/images/card_back.png");
        if (is == null) {
            throw new RuntimeException("card-back.png nicht gefunden im classpath unter /com/example/images/");
        }
        backImage = new Image(is);
    }

    static StackPane createCard(String text) {
        Label frontLabel = new Label(text);
        frontLabel.getStyleClass().add("card-front");
        frontLabel.setWrapText(true);
        frontLabel.setMaxWidth(220);
        frontLabel.setAlignment(Pos.CENTER);
        frontLabel.setTextAlignment(TextAlignment.CENTER);
        StackPane front = new StackPane(frontLabel);
        front.setPrefSize(240, 160);

        ImageView backView = new ImageView(backImage);
        backView.setFitWidth(240);
        backView.setFitHeight(160);
        backView.setPreserveRatio(true);
        backView.getStyleClass().add("card-back");

        StackPane card = new StackPane(front, backView);
        card.getStyleClass().add("card");
        // initial: only front sichtbar
        front.setVisible(true);
        backView.setVisible(false);
        // speichere References für showFront/back
        card.getProperties().put("front", front);
        card.getProperties().put("back", backView);
        return card;
    }

    static void showBack(StackPane card) {
        StackPane front = (StackPane) card.getProperties().get("front");
        ImageView back = (ImageView) card.getProperties().get("back");
        front.setVisible(false);
        back.setVisible(true);
    }

    static void showFront(StackPane card) {
        StackPane front = (StackPane) card.getProperties().get("front");
        ImageView back = (ImageView) card.getProperties().get("back");
        back.setVisible(false);
        front.setVisible(true);
    }
}
