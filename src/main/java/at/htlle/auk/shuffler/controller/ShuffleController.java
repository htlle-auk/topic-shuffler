package at.htlle.auk.shuffler.controller;

import at.htlle.auk.shuffler.model.Topic;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;

/**
 * Controller for the TopicShuffler UI.
 *
 * Behavior summary:
 * - choose subject in ComboBox -> shows 8 topic cards (front = topic text)
 * - "Shuffle" flips every card to its back (logo) and animates positions
 * - click a card after shuffle -> flips it to front (reveals topic). User may reveal two cards.
 * - after second reveal: all remaining cards are revealed; user must then click one of the two selected
 *   to make the final choice (final card gets CSS class "chosen", other selected stays "selected").
 *
 * Notes:
 * - This controller expects a card-back image at:
 *   /at/htlle/auk/shuffler/images/card-back.png in resources.
 * - It logs final selections using SLF4J (INFO level). Configure Logback/SLF4J in your project for file logging.
 */
public class ShuffleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShuffleController.class);

    @FXML private GridPane grid;
    @FXML private ComboBox<String> subjectCombo;

    private Map<String, List<Topic>> subjectTopics;
    private final List<StackPane> cards = new ArrayList<>();
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

    /**
     * Load topics for the currently selected subject and create cards.
     */
    private void loadTopics() {
        cards.clear();
        selected.clear();
        isShuffled = false;
        grid.getChildren().clear();

        String subject = subjectCombo.getValue();
        List<Topic> topics = subjectTopics.getOrDefault(subject, Collections.emptyList());

        for (Topic t : topics) {
            StackPane card = CardFactory.createCard(t.getName());
            card.setOnMouseClicked(this::onCardClicked);
            cards.add(card);
        }

        layoutCards();
    }

    /**
     * Place card nodes into the grid (4 columns x 2 rows).
     */
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

        // 1) remember old scene positions
        Map<StackPane, Point2D> oldPositions = new HashMap<>();
        for (StackPane card : cards) {
            Point2D old = card.localToScene(0, 0);
            oldPositions.put(card, old);
        }

        // 2) shuffle the card list
        Collections.shuffle(cards);

        // 3) put the cards immediately into the grid in the new order
        grid.getChildren().clear();
        for (int i = 0; i < cards.size(); i++) {
            grid.add(cards.get(i), i % 4, i / 4);
        }

        // 4) force layout so nodes have correct new positions/sizes
        grid.applyCss();
        grid.layout();

        // 5) compute new scene positions and set initial translate offsets (so they appear at old pos)
        List<Animation> animations = new ArrayList<>();
        for (StackPane card : cards) {
            Point2D old = oldPositions.getOrDefault(card, card.localToScene(0, 0));
            Point2D now = card.localToScene(0, 0);

            // set offset so card visually stays at old position (but is actually in new grid cell)
            double offsetX = old.getX() - now.getX();
            double offsetY = old.getY() - now.getY();
            card.setTranslateX(offsetX);
            card.setTranslateY(offsetY);

            // 6) flip to back (two-phase) and then animate translate to (0,0)
            RotateTransition flip1 = new RotateTransition(Duration.millis(180), card);
            flip1.setAxis(Rotate.Y_AXIS);
            flip1.setFromAngle(0);
            flip1.setToAngle(90);

            RotateTransition flip2 = new RotateTransition(Duration.millis(180), card);
            flip2.setAxis(Rotate.Y_AXIS);
            flip2.setFromAngle(90);
            flip2.setToAngle(180);
            flip2.setOnFinished(e -> CardFactory.showBack(card));

            TranslateTransition move = new TranslateTransition(Duration.millis(350), card);
            move.setToX(0);
            move.setToY(0);
            move.setInterpolator(Interpolator.EASE_BOTH);
            // we want move to start after flip2
            SequentialTransition seq = new SequentialTransition(new ParallelTransition(flip1, new PauseTransition(Duration.millis(0))), flip2, move);
            seq.setDelay(Duration.millis(cards.indexOf(card) * 60)); // slight stagger
            animations.add(seq);
        }

        // 7) play all animations (they are independent). Using a ParallelTransition groups them.
        ParallelTransition all = new ParallelTransition();
        all.getChildren().addAll(animations);
        all.setOnFinished(e -> {
            // ensure final transforms reset
            for (StackPane c : cards) {
                c.setTranslateX(0);
                c.setTranslateY(0);
                c.setRotate(0);
            }
            // final layout to be safe
            layoutCards();
        });
        all.play();
    }

    /**
     * Handle card click: reveal card (back -> front). Up to two reveals allowed.
     * Prevent selecting the same card twice.
     */
    private void onCardClicked(MouseEvent event) {
        StackPane card = (StackPane) event.getSource();
        // ignore clicks before shuffle or when already two selections exist
        if (!isShuffled || selected.size() >= 2) return;

        // Prevent the same card being selected twice
        if (selected.contains(card)) {
            return;
        }

        // flip from back (180) to front (0) with two-phase rotation
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
            // add visual selection if not already present (defensive)
            if (!card.getStyleClass().contains("selected")) {
                card.getStyleClass().add("selected");
            }
            selected.add(card);

            if (selected.size() == 2) {
                // short delay then reveal & immediately dim/deactivate non-selected cards
                PauseTransition revealPause = new PauseTransition(Duration.millis(250));
                revealPause.setOnFinished(ev -> revealAndDimNonSelected());
                revealPause.play();
            }
        });

        new SequentialTransition(flip1, flip2).play();
    }

    /**
     * Reveal all non-selected cards, immediately dim & deactivate them,
     * then enable final choice on the two selected cards.
     */
    private void revealAndDimNonSelected() {
        for (StackPane card : cards) {
            if (!selected.contains(card)) {
                // flip animation
                RotateTransition flip1 = new RotateTransition(Duration.millis(200), card);
                flip1.setAxis(Rotate.Y_AXIS);
                flip1.setFromAngle(180);
                flip1.setToAngle(90);

                RotateTransition flip2 = new RotateTransition(Duration.millis(200), card);
                flip2.setAxis(Rotate.Y_AXIS);
                flip2.setFromAngle(90);
                flip2.setToAngle(0);

                // when the front is shown, mark as not-chosen and deactivate clicks immediately
                flip2.setOnFinished(e -> {
                    CardFactory.showFront(card);
                    if (!card.getStyleClass().contains("not-chosen")) {
                        card.getStyleClass().add("not-chosen");
                    }
                    card.setOnMouseClicked(null);
                });

                new SequentialTransition(flip1, flip2).play();
            }
        }

        // After a short pause allow final choice on the two selected cards
        PauseTransition allowChoose = new PauseTransition(Duration.millis(200));
        allowChoose.setOnFinished(e -> enableFinalChoice());
        allowChoose.play();
    }


    /**
     * Make only the two selected cards clickable for the user's final choice.
     * Other cards are deactivated.
     */
    private void enableFinalChoice() {
        for (StackPane card : cards) {
            if (!selected.contains(card)) {
                card.setOnMouseClicked(null);
            }
        }
        for (StackPane sCard : new ArrayList<>(selected)) {
            final StackPane finalCard = sCard;
            finalCard.setOnMouseClicked(ev -> finalizeChoice(finalCard));
        }
    }

    /**
     * Finalize the user's choice:
     * - mark the chosen card with 'chosen' CSS class (green)
     * - keep the other previously selected card with 'selected' (yellow)
     * - dim never-selected cards (add 'not-chosen')
     * - log the selection (INFO)
     * - deactivate further clicks
     */
    private void finalizeChoice(StackPane chosen) {
        // extract the two initially selected topic texts (robust)
        String first = extractLabelText(selected.size() > 0 ? selected.get(0) : null);
        String second = extractLabelText(selected.size() > 1 ? selected.get(1) : null);
        String finalText = extractLabelText(chosen);
        String subject = subjectCombo == null ? "<unknown>" : subjectCombo.getValue();

        // log the selection BEFORE modifying UI state
        LOGGER.info("Subject={} | selected=[{}, {}] | final={}", subject, first, second, finalText);

        // visual marking
        if (!chosen.getStyleClass().contains("chosen")) {
            chosen.getStyleClass().add("chosen");
        }
        ScaleTransition st = new ScaleTransition(Duration.millis(250), chosen);
        st.setByX(0.08);
        st.setByY(0.08);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();

        // deactivate all cards to prevent further interaction
        for (StackPane c : cards) {
            c.setOnMouseClicked(null);
        }

        // dim never-selected cards
        for (StackPane c : cards) {
            if (!selected.contains(c)) {
                if (!c.getStyleClass().contains("not-chosen")) {
                    c.getStyleClass().add("not-chosen");
                }
            }
        }

        // keep the other selected card as yellow: do NOT remove "selected" class
        // The chosen card already has "chosen" added; both will remain visible.

        // Optionally: keep 'selected' list if you need it later; do not clear it here.
    }

    /**
     * Robust extraction of the label text shown on a card.
     * First checks the "frontLabel" property, then searches children for a Label.
     */
    private String extractLabelText(StackPane card) {
        if (card == null) return "<unknown>";

        Object o = card.getProperties().get("frontLabel");
        if (o instanceof Label) {
            String txt = ((Label) o).getText();
            return txt == null || txt.isBlank() ? "<unknown>" : txt;
        }

        // fallback: search direct children and nested panes
        for (Node child : card.getChildren()) {
            if (child instanceof Label) {
                String txt = ((Label) child).getText();
                return txt == null || txt.isBlank() ? "<unknown>" : txt;
            }
            if (child instanceof Pane) {
                for (Node inner : ((Pane) child).getChildren()) {
                    if (inner instanceof Label) {
                        String txt = ((Label) inner).getText();
                        return txt == null || txt.isBlank() ? "<unknown>" : txt;
                    }
                }
            }
        }
        return "<unknown>";
    }

    /* ----------------------------------------------------------------------
       CardFactory: helper to build cards with distinct front (text) and back (image).
       The factory stores references in the Node properties for easy access.
       ---------------------------------------------------------------------- */
    private static class CardFactory {
        private static final Image backImage;

        static {
            InputStream is = CardFactory.class.getResourceAsStream("/images/card_back.png");
            if (is == null) {
                throw new RuntimeException("card-back.png nicht gefunden im classpath unter /com/example/images/");
            }
            backImage = new Image(is);
        }

        static StackPane createCard(String text) {
            // front label with wrapping and centered alignment
            Label frontLabel = new Label(text);
            frontLabel.getStyleClass().add("card-front");
            frontLabel.setWrapText(true);
            frontLabel.setMaxWidth(220); // adapt to your card width
            frontLabel.setAlignment(Pos.CENTER);
            frontLabel.setTextAlignment(TextAlignment.CENTER);

            StackPane front = new StackPane(frontLabel);
            front.setPrefSize(240, 160); // match your CSS/design

            ImageView backView = new ImageView(backImage);
            backView.setFitWidth(240);
            backView.setFitHeight(160);
            backView.setPreserveRatio(true);
            backView.getStyleClass().add("card-back");

            // order: front above back, visibility controlled by showFront/showBack
            StackPane card = new StackPane(front, backView);
            card.getStyleClass().add("card");

            front.setVisible(true);
            backView.setVisible(false);

            // store references for later retrieval
            card.getProperties().put("front", front);
            card.getProperties().put("back", backView);
            card.getProperties().put("frontLabel", frontLabel);

            return card;
        }

        static void showBack(StackPane card) {
            Object frontObj = card.getProperties().get("front");
            Object backObj = card.getProperties().get("back");
            if (frontObj instanceof StackPane && backObj instanceof ImageView) {
                ((StackPane) frontObj).setVisible(false);
                ((ImageView) backObj).setVisible(true);
            }
        }

        static void showFront(StackPane card) {
            Object frontObj = card.getProperties().get("front");
            Object backObj = card.getProperties().get("back");
            if (frontObj instanceof StackPane && backObj instanceof ImageView) {
                ((ImageView) backObj).setVisible(false);
                ((StackPane) frontObj).setVisible(true);
            }
        }
    }
}
