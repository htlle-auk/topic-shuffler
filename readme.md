# TopicShuffler

![Build](https://img.shields.io/badge/build-maven-blue) ![License](https://img.shields.io/badge/license-MIT-brightgreen)

## Short description

TopicShuffler is a small JavaFX application for classroom use.
Pick a subject, see 8 topic-cards (or even more), press **Shuffle** to hide them with an animated card-back, reveal two topics by clicking cards, then choose one of the two as the final topic.
Designed for desktop use.
Should be easy to extend to mobile use.

---

## Features

* Per-subject topic sets (choose subject → 8 cards displayed)
* Animated shuffle (flip + move) and flip transitions
* Two-step selection: reveal two topics, then final choice from those two
* Responsive layout and CSS styling (card backs, chosen / not-chosen states)
* Built with Java, FXML and CSS — easy to adapt to your curriculum

---

## Tech stack

* Java 17+ (tested with JDK 17 and 20)
* JavaFX 20 (controls, fxml)
* Maven for build & dependency management

---

## Repo layout (suggested)

```
/src
  /main
    /java
      /at/htlle/auk/shuffler        # application packages (controllers, model, main)
    /resources
      /at/htlle/auk/shuffler
        ShuffleView.fxml
        styles.css
        /images
          card-back.png
          screenshot.png
pom.xml
README.md
LICENSE
```

---

## Quick start — development

### Prerequisites

* JDK 17+ installed and `JAVA_HOME` set
* Maven installed
* (For running from IDE) configure JavaFX SDK in your IDE or use `javafx-maven-plugin`

### Run from IDE

Open project in IntelliJ or Eclipse and run the main class:

```
at.htlle.auk.shuffler.Shuffler
```

### Run from command line (via Maven)

This will use the javafx-maven-plugin (make sure `pom.xml` contains the plugin config):

```bash
# runs the app (maven will fetch JavaFX artifacts)
mvn clean javafx:run
```

### Build JAR

```bash
mvn clean package
# result: target/topicshuffler-1.0-SNAPSHOT.jar (name depends on your pom)
```

---

## Packaging — create native installer (recommended)

Bundling with **jpackage** gives you a native app (Windows .exe / macOS .dmg / Linux .deb) that contains a runtime so end users don't need to install Java/JavaFX.

1. Download and unpack JavaFX SDK for your platform: e.g. `C:\javafx-sdk-20` or `/opt/javafx-sdk-20`.

2. From the `target/` folder run (Windows example):

```bash
jpackage \
  --name TopicShuffler \
  --app-version 1.0 \
  --type exe \
  --input . \
  --main-jar topicshuffler.jar \
  --main-class at.htlle.auk.shuffler.Shuffler \
  --module-path "C:\javafx-sdk-20\lib" \
  --add-modules javafx.controls,javafx.fxml \
  --icon path\to\icon.ico
```

Or (cross-platform modular approach) if you use `module-info.java` and built a module JAR:

```bash
jpackage \
  --name TopicShuffler \
  --app-version 1.0 \
  --type exe \
  --input . \
  --module at.htlle.auk.shuffler/at.htlle.auk.shuffler.Shuffler \
  --module-path "C:\javafx-sdk-20\lib;." \
  --add-modules javafx.controls,javafx.fxml \
  --icon path\to\icon.ico
```

**Notes**

* Replace `topicshuffler.jar`, `at.htlle.auk.shuffler.Shuffler`, and the JavaFX path to match your build.
* `jpackage` is part of recent JDKs — use the same JDK version you target or the `jpackage` that matches your runtime requirements.

---

## Troubleshooting

### Error: “JavaFX runtime components are missing”

This happens when JavaFX modules are not available on the module-path at runtime. Solutions:

* Run with the `javafx-maven-plugin` (`mvn javafx:run`) during development.
* Start JAR with `--module-path` and `--add-modules`, e.g.:

```bash
java --module-path "C:\javafx-sdk-20\lib" --add-modules javafx.controls,javafx.fxml -jar topicshuffler.jar
```

* Or use `jpackage` to create a native image so end users do not need to handle JavaFX.

---

## UI / Styling notes

* CSS file: `styles.css` controls card appearance (`.card`, `.card-front`, `.card-back`, `.selected`, `.chosen`, `.not-chosen`).
* Card back image: `src/main/resources/at/htlle/auk/shuffler/images/card-back.png`.
* Place screenshots (for README) in `src/main/resources/...` and reference them as `resources/images/screenshot.png`.

---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feat/foo`)
3. Commit and push
4. Open a pull request

Please keep code style consistent and add TODO comments for pedagogical hints when you modify the controllers (useful for classroom exercises).

---

## License

This project is MIT licensed — see [LICENSE](LICENSE) for details.

---

Have fun!