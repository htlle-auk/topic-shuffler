package at.htlle.auk.shuffler.csvreader;

import at.htlle.auk.shuffler.model.Topic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TopicFiller {
    
    public static Map<String, List<Topic>> fillTopics() {
        Map<String, List<Topic>> result = new TreeMap<>();
        
        InputStream inputStream = TopicFiller.class.getClassLoader().getResourceAsStream("rdp-pools.txt");
        if (inputStream == null) {
            throw new IllegalArgumentException("Resource not found: rdp-pools.txt");
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String currentKey = null;
            List<Topic> currentTopics = null;
            boolean expectingKey = true;
                    
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                
                if (trimmedLine.isEmpty()) {
                    expectingKey = true;
                    currentKey = null;
                    currentTopics = null;
                    continue;
                }
                        
                if (expectingKey) {
                    currentKey = trimmedLine;
                    
                    if (result.containsKey(currentKey)) {
                        throw new IllegalArgumentException("Duplicate key found: " + currentKey);
                    }
                            
                    currentTopics = new ArrayList<>();
                    result.put(currentKey, currentTopics);
                    expectingKey = false;
                } else {
                    Objects.requireNonNull(currentTopics).add(new Topic(trimmedLine));
                }
            }
                    
            return result;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read resource:", e);
        }
    }
    
    public static Map<String, List<Topic>> fillTopicsOLD() {
        return Map.of(
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
    }
}
