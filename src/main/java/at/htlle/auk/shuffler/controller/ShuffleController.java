package at.htlle.auk.shuffler.controller;

import at.htlle.auk.shuffler.model.Topic;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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

    // debounce for resize events
    private final PauseTransition fontResizeDebounce = new PauseTransition(Duration.millis(180));

    // last applied font size (optional, for debugging)
    private double lastAppliedFontSize = -1;

    // guards scheduling so we don't attach multiple listeners
    private boolean adjustScheduled = false;


    @FXML private GridPane grid;
    @FXML private ComboBox<String> subjectCombo;
    @FXML private TextField nameField;

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

        fontResizeDebounce.setOnFinished(e -> adjustLabelsFontSize());

        if (grid.getScene() != null) {
            grid.widthProperty().addListener((obs, o, n) -> fontResizeDebounce.playFromStart());
            grid.heightProperty().addListener((obs, o, n) -> fontResizeDebounce.playFromStart());
            Platform.runLater(() -> { grid.applyCss(); grid.layout(); adjustLabelsFontSize(); });
        } else {
            grid.sceneProperty().addListener((obs, oldS, newS) -> {
                if (newS != null) {
                    grid.widthProperty().addListener((o, ov, nv) -> fontResizeDebounce.playFromStart());
                    grid.heightProperty().addListener((o, ov, nv) -> fontResizeDebounce.playFromStart());
                    Platform.runLater(() -> { grid.applyCss(); grid.layout(); adjustLabelsFontSize(); });
                }
            });
        }
        grid.widthProperty().addListener((obs, oldV, newV) -> {
            fontResizeDebounce.playFromStart();
            scheduleAdjustLabelsFontSize();         // ensure adjustment is scheduled
        });
        grid.heightProperty().addListener((obs, oldV, newV) -> {
            fontResizeDebounce.playFromStart();
            scheduleAdjustLabelsFontSize();
        });

    }

    /**
     * Load topics for the currently selected subject and create cards.
     */
    private void loadTopics() {
        cards.clear();
        selected.clear();
        isShuffled = false;
        grid.getChildren().clear();

        // clear optional name on subject change
        if (nameField != null) {
            nameField.clear();
        }

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
        // Clear grid and reset transforms on cards to avoid leftover translations/rotations
        grid.getChildren().clear();

        for (int i = 0; i < cards.size(); i++) {
            StackPane card = cards.get(i);

            // Reset any old animation transforms/rotations from previous runs
            card.setTranslateX(0);
            card.setTranslateY(0);
            card.setRotate(0);
            card.setScaleX(1.0);
            card.setScaleY(1.0);

            grid.add(card, i % 4, i / 4);
        }

        // Ensure responsive bindings are applied (safe to call repeatedly)
        applyResponsiveBindings();

        // after grid.add(...) etc. -> ensure layout pass and schedule font adjustment
        grid.applyCss();
        grid.layout();

        // schedule a safe adjustment that waits for measured bounds
        scheduleAdjustLabelsFontSize();

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

        // get optional user name (trimmed) — null if empty
        String user = (nameField != null && !nameField.getText().isBlank())
                ? nameField.getText().trim()
                : null;

        // build message, include user only if provided
        String userPart = (user != null) ? " | user=" + user : "";
        String message = String.format("Subject=%s%s | selected=[%s, %s] | final=%s",
                subject,
                userPart,
                first == null ? "<unknown>" : first,
                second == null ? "<unknown>" : second,
                finalText == null ? "<unknown>" : finalText);

        // log as INFO
        LOGGER.info(message);

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

    /**
     * Responsive bindings that are safe to call multiple times.
     * - binds each card's prefWidth/prefHeight once (per card)
     * - sizes are computed from the grid's width/height (avoids circular dependency)
     */
    private void applyResponsiveBindings() {
        // If scene not ready yet, register once and retry later
        if (grid.getScene() == null) {
            grid.sceneProperty().addListener((obs, oldS, newS) -> {
                if (newS != null) {
                    Platform.runLater(this::applyResponsiveBindings);
                }
            });
            return;
        }

        // If grid hasn't been measured yet, schedule a retry after layout pass
        if (grid.getWidth() <= 0 || grid.getHeight() <= 0) {
            Platform.runLater(() -> {
                // second chance after layout
                if (grid.getWidth() > 0 && grid.getHeight() > 0) {
                    applyResponsiveBindings();
                } else {
                    // final fallback: bind with safe defaults
                    bindWithDefaults();
                }
            });
            return;
        }

        // compute bindings (based on current grid size)
        final int columns = 4;
        final int rows = 2;
        double hgap = grid.getHgap();
        double vgap = grid.getVgap();

        DoubleBinding cardWidthBinding = Bindings.createDoubleBinding(() -> {
            double totalW = grid.getWidth();
            double totalGaps = (columns - 1) * hgap;
            double usable = Math.max(0, totalW - totalGaps - 10);
            double w = usable / columns;
            return Math.max(120.0, Math.min(480.0, w)); // clamp
        }, grid.widthProperty());

        DoubleBinding cardHeightBinding = Bindings.createDoubleBinding(() -> {
            double totalH = grid.getHeight();
            double totalGaps = (rows - 1) * vgap;
            double usable = Math.max(0, totalH - totalGaps - 10);
            double h = usable / rows;
            return Math.max(80.0, Math.min(360.0, h)); // clamp
        }, grid.heightProperty());

        // Apply bindings to any cards that haven't been bound yet
        for (StackPane card : cards) {
            Object applied = card.getProperties().get("responsiveApplied");
            if (Boolean.TRUE.equals(applied)) continue;

            // defensive min/max
            card.setMinSize(80, 60);
            card.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            card.prefWidthProperty().bind(cardWidthBinding);
            card.prefHeightProperty().bind(cardHeightBinding);

            Object frontObj = card.getProperties().get("front");
            Object backObj = card.getProperties().get("back");
            Object labelObj = card.getProperties().get("frontLabel");

            if (frontObj instanceof StackPane) {
                StackPane front = (StackPane) frontObj;
                front.prefWidthProperty().bind(card.widthProperty());
                front.prefHeightProperty().bind(card.heightProperty());
                front.setMinSize(javafx.scene.layout.Region.USE_COMPUTED_SIZE,
                        javafx.scene.layout.Region.USE_COMPUTED_SIZE);
            }
            if (backObj instanceof javafx.scene.image.ImageView) {
                javafx.scene.image.ImageView iv = (javafx.scene.image.ImageView) backObj;
                iv.fitWidthProperty().bind(card.widthProperty());
                iv.fitHeightProperty().bind(card.heightProperty());
                iv.setPreserveRatio(true);
            }
            if (labelObj instanceof Label) {
                Label lbl = (Label) labelObj;
                lbl.setWrapText(true);
                lbl.maxWidthProperty().bind(card.widthProperty().multiply(0.9));

//                lbl.maxWidthProperty().bind(card.widthProperty().multiply(0.9));
//                lbl.styleProperty().bind(Bindings.createStringBinding(
//                        () -> String.format("-fx-font-size: %.0fpx;", Math.max(12.0, cardWidthBinding.get() * 0.12)),
//                        cardWidthBinding
//                ));
            }

            card.getProperties().put("responsiveApplied", Boolean.TRUE);
        }

        // Force an extra layout pass to stabilize sizes immediately
        Platform.runLater(() -> {
            grid.applyCss();
            grid.layout();
        });
    }

    // helper used as very conservative fallback if sizes can't be measured
    private void bindWithDefaults() {
        DoubleBinding defaultWidth = Bindings.createDoubleBinding(() -> 240.0);
        DoubleBinding defaultHeight = Bindings.createDoubleBinding(() -> 160.0);
        for (StackPane card : cards) {
            if (Boolean.TRUE.equals(card.getProperties().get("responsiveApplied"))) continue;
            card.prefWidthProperty().bind(defaultWidth);
            card.prefHeightProperty().bind(defaultHeight);
            card.getProperties().put("responsiveApplied", Boolean.TRUE);
        }
    }

    /**
     * Compute the largest font size that fits ALL topic labels into their cards,
     * then apply that uniform font size to every label.
     *
     * This method measures the text using a Text node with wrapping to the label width
     * and checks the resulting height against the available label height.
     *
     * It must be called after layout (cards have valid widths/heights).
     */
    private void adjustLabelsFontSize() {
        // if no cards nothing to do
        if (cards.isEmpty()) return;

// if any card hasn't been measured yet, schedule a safe retry and return
        for (StackPane card : cards) {
            if (card.getWidth() <= 0 || card.getHeight() <= 0) {
                scheduleAdjustLabelsFontSize();
                return;
            }
        }


        // compute per-card available width/height for the label (use the same margins you have elsewhere)
        // choose conservative paddings: label area is 90% of card width, 70% of card height
        double minAvailableWidth = Double.MAX_VALUE;
        double minAvailableHeight = Double.MAX_VALUE;

        List<Label> labels = new ArrayList<>();
        for (StackPane card : cards) {
            Object lblObj = card.getProperties().get("frontLabel");
            if (lblObj instanceof Label) {
                Label lbl = (Label) lblObj;
                labels.add(lbl);

                double availW = card.getWidth() * 0.90;   // 90% of card width
                double availH = card.getHeight() * 0.70;  // 70% of card height (allow some top/bottom padding)
                minAvailableWidth = Math.min(minAvailableWidth, availW);
                minAvailableHeight = Math.min(minAvailableHeight, availH);
            }
        }
        // defensive fallback if something went wrong with measurements
        if (minAvailableWidth == Double.MAX_VALUE || minAvailableHeight == Double.MAX_VALUE) {
            // use conservative defaults if measurement failed
            minAvailableWidth = 200.0;
            minAvailableHeight = 80.0;
        }

        if (labels.isEmpty()) return;

        // determine the longest text among labels (we will check all labels though)
        // do a binary search on font size between reasonable bounds
        int lo = 8;     // minimal readable font
        int hi = 120;   // upper bound (will be clamped by measurements)
        int best = lo;

        // Use the family of the first label (preserve style)
        String family = labels.get(0).getFont() != null ? labels.get(0).getFont().getFamily() : Font.getDefault().getFamily();

        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            if (allLabelsFitWithFont(labels, family, mid, minAvailableWidth, minAvailableHeight)) {
                best = mid;      // mid fits -> try larger
                lo = mid + 1;
            } else {
                hi = mid - 1;    // mid too large -> try smaller
            }
        }

        final double chosen = best;

        // apply chosen font to all labels (use Platform.runLater to avoid interfering with layout)
        Platform.runLater(() -> {
            for (Label lbl : labels) {
                lbl.setWrapText(true);
                lbl.setFont(Font.font(family, chosen));
                // only set inline style if styleProperty is NOT bound (defensive)
                if (!lbl.styleProperty().isBound()) {
                    lbl.setStyle(String.format("-fx-font-size: %.0fpx;", chosen));
                }
            }
        });

    }

    /**
     * Schedule a single execution of adjustLabelsFontSize() once the grid has a valid layout.
     * This is robust against timing issues: it waits for grid.layoutBounds to become >0 and
     * ensures we only schedule one pending adjustment at a time.
     */
    private void scheduleAdjustLabelsFontSize() {
        if (adjustScheduled) return;
        adjustScheduled = true;

        // if grid already measured, run once on next pulse
        if (grid.getWidth() > 0 && grid.getHeight() > 0) {
            Platform.runLater(() -> {
                try {
                    grid.applyCss();
                    grid.layout();
                    adjustLabelsFontSize();
                } finally {
                    adjustScheduled = false;
                }
            });
            return;
        }

        // otherwise wait for the grid layoutBounds to be available
        ChangeListener<Bounds> boundsListener = new ChangeListener<>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends Bounds> obs, Bounds oldB, Bounds newB) {
                if (newB.getWidth() > 0 && newB.getHeight() > 0) {
                    // remove listener and schedule adjustment on FX thread
                    grid.layoutBoundsProperty().removeListener(this);
                    Platform.runLater(() -> {
                        try {
                            grid.applyCss();
                            grid.layout();
                            adjustLabelsFontSize();
                        } finally {
                            adjustScheduled = false;
                        }
                    });
                }
            }
        };
        grid.layoutBoundsProperty().addListener(boundsListener);
    }


    /** helper: check if ALL labels fit when rendered with the given font size into given bounds */
    private boolean allLabelsFitWithFont(List<Label> labels,
                                         String family,
                                         int fontSize,
                                         double wrapWidth,
                                         double maxHeight) {
        if (wrapWidth <= 0 || maxHeight <= 0) return false;
        for (Label lbl : labels) {
            String text = lbl.getText();
            if (text == null) text = "";

            // use Text node to measure wrapped text height precisely
            Text measuring = new Text(text);
            measuring.setFont(Font.font(family, fontSize));
            measuring.setWrappingWidth(wrapWidth);
            // width is controlled by wrappingWidth; now measure height
            double measuredH = measuring.getLayoutBounds().getHeight();

            // safety margin: allow a couple pixels
            if (measuredH > maxHeight + 1.0) {
                return false;
            }
        }
        return true;
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
            // front label: wrap text, centered. actual sizing is controlled by responsive bindings.
            Label frontLabel = new Label(text);
            frontLabel.getStyleClass().add("card-front");
            frontLabel.setWrapText(true);
            frontLabel.setMaxWidth(Double.MAX_VALUE); // allow parent / bindings to control width
            frontLabel.setAlignment(Pos.CENTER);
            frontLabel.setTextAlignment(TextAlignment.CENTER);

            // front container: leave pref size to computed (do not hardcode sizes here)
            StackPane front = new StackPane(frontLabel);
            front.setPrefSize(javafx.scene.layout.Region.USE_COMPUTED_SIZE, javafx.scene.layout.Region.USE_COMPUTED_SIZE);

            // back image: preserve ratio, do not set fixed fit here (bindings will apply)
            ImageView backView = new ImageView(backImage);
            backView.setPreserveRatio(true);
            backView.setSmooth(true);
            backView.getStyleClass().add("card-back");

            // stack front over back; visibility toggled by showFront/showBack
            StackPane card = new StackPane(front, backView);
            card.getStyleClass().add("card");
            frontLabel.maxWidthProperty().bind(card.widthProperty().multiply(0.9));
            // initial state: show front (topic text), hide back (logo)
            front.setVisible(true);
            backView.setVisible(false);

            // store references for later access (showFront/showBack, label extraction, responsive bindings)
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
