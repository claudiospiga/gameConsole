package Gioco;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Main {
	
	//attributi
    static Scanner scanner = new Scanner(System.in);
    static int vitaEroe = 100;
    static int pietre = 0;
    static int esperienza = 0;
    static int xp = 0;
    static Random random = new Random();
    //connessione
    public static class DatabaseUtil {
        private static final String URL = "jdbc:mysql://localhost:3306/console";
        private static final String USER = "root";
        private static final String PASSWORD = "marina97!";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }
    //pstmt tabella
    public static class GameManager {
        public static void updatePlayer(int playerId, int pietre, int esperienza) {
            String sql = "UPDATE giocatori SET pietre = ?, esperienza = ? WHERE id = ?";
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, pietre);
                pstmt.setInt(2, esperienza);
                pstmt.setInt(3, playerId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Errore durante l'aggiornamento del giocatore: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        public static void getPlayer(int playerId) {
            String sql = "SELECT vita, pietre, esperienza FROM giocatori WHERE id = ?";
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, playerId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int vita = rs.getInt("vita");
                    int pietre = rs.getInt("pietre");
                    int esperienza = rs.getInt("esperienza");
                    System.out.println("Dati del Giocatore:");
                    System.out.println("Vita: " + vita);
                    System.out.println("Pietre: " + pietre);
                    System.out.println("Esperienza: " + esperienza);
                } else {
                    System.out.println("Nessun dato trovato per il giocatore con ID: " + playerId);
                }
            } catch (SQLException e) {
                System.out.println("Errore durante il recupero dei dati del giocatore: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Benvenuto nel gioco!");
        selectOrCreatePlayer();
        // Loop di gioco
        while (vitaEroe > 0) {
            System.out.println("\nFai la tua scelta!");
            System.out.println("1. Esplora un nuovo mondo!");
            System.out.println("2. Vai sulla terra per riposarti e comprare attrezzatura!");
            System.out.println("3. Controlla i tuoi dati spaziali!");

            int scelta = scanner.nextInt();
            switch (scelta) {
                case 1:
                    nuovoMondo();
                    break;
                case 2:
                    terra();
                    break;
                case 3:
                    check();
                    break;
            }
        }
    }
     //selezione o creazione new player
    public static void selectOrCreatePlayer() {
        System.out.println("Vuoi creare un nuovo giocatore o usare un profilo esistente? (Nuovo/Esistente)");
        String decision = scanner.next();
        if (decision.equalsIgnoreCase("Nuovo")) {
            System.out.println("Inserisci il nome del nuovo giocatore:");
            String nome = scanner.next();
            createPlayer(nome);
        } else {
            System.out.println("Inserisci l'ID del giocatore esistente:");
            int playerId = scanner.nextInt();
            GameManager.getPlayer(playerId);
        }
    }

    public static void createPlayer(String nome) {
        String sql = "INSERT INTO giocatori (nome, vita, pietre, esperienza, livello) VALUES (?, 100, 0, 0, 1)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, nome);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int playerId = rs.getInt(1);
                    System.out.println("Nuovo giocatore creato con ID: " + playerId);
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante la creazione del giocatore: " + e.getMessage());
            e.printStackTrace();
        }
    }
     
    public static void check() {
        System.out.println("Inserisci l'ID del giocatore per controllare i dati:");
        int playerId = scanner.nextInt();
        GameManager.getPlayer(playerId);
    }
    //nuovo mondo
    public static void nuovoMondo() {
        int probabilita = random.nextInt(100);
        if (probabilita <= 50) {
            System.out.println("Sei arrivato in un mondo ostile!");
            fight();
        } else {
            System.out.println("Sei arrivato in un mondo pieno di risorse!");
            vitaEroe += random.nextInt(5);
            pietre += random.nextInt(3);
            System.out.println("Ora hai tot pietre: " + pietre);
        }
    }
    //metodo combattimento
    public static void fight() {
        int mostro = 50;
        while (vitaEroe > 0 && mostro > 0) {
            System.out.println("1. Vuoi attaccare?");
            System.out.println("2. Vuoi scappare?");
            int decisione = scanner.nextInt();
            switch (decisione) {
                case 1:
                    int dannoA = random.nextInt(10);
                    vitaEroe -= dannoA;
                    mostro -= danno();
                    System.out.println("Hai inflitto " + danno() + " al mostro");
                    System.out.println("La tua nave è stata danneggiata con " + dannoA + " salute");
                    System.out.println("La tua salute è " + vitaEroe);
                    System.out.println("La salute del pianeta è " + mostro);
                    break;
                case 2:
                    System.out.println("Sei tornato sulla terra!");
                    return;
            }
        }
        if (vitaEroe <= 0) {
            System.out.println("Hai perso!");
        } else {
            System.out.println("Hai sconfitto il pianeta ostile! Continua la tua avventura....");
            pietre += random.nextInt(3) + 1;
            xp += 20;
            checkLevelUp();
        }
    }
     //metodo terra
    public static void terra() {
        vitaEroe = 100;
        System.out.println("1. Vuoi comprare armatura?");
        System.out.println("2. Vuoi tornare all'avventura?");
        int decisione = scanner.nextInt();
        switch (decisione) {
            case 1:
                System.out.println("Hai tot pietre: " + pietre);
                System.out.println("1. Armatura leggera +10, costo 5 pietre");
                System.out.println("2. Armatura media +15, costo 10 pietre");
                int scelta = scanner.nextInt();
                if (scelta == 1 && pietre >= 5) {
                    pietre -= 5;
                    vitaEroe += 10;
                    System.out.println("Hai comprato la nuova armatura!");
                    System.out.println("Ora hai " + vitaEroe + " vita totale");
                } else if (scelta == 2 && pietre >= 10) {
                    pietre -= 10;
                    vitaEroe += 15;
                    System.out.println("Hai comprato la nuova armatura!");
                    System.out.println("Ora hai " + vitaEroe + " vita totale");
                } else {
                    System.out.println("Non hai abbastanza pietre!");
                }
                break;
            case 2:
                System.out.println("Torni all'avventura!");
                break;
        }
    }
    //metodo per il danno 
    public static int danno() {
        return random.nextInt(10) + 1;
    }
     //metodo level up
    public static void checkLevelUp() {
        if (xp >= 100) {
            vitaEroe += 50;  // Incrementa vita per ogni livello guadagnato
            xp -= 100;       // Decrementa XP di 100 per ogni livello guadagnato
            System.out.println("Sei aumentato di livello! Ora hai " + vitaEroe + " di vita totale e " + xp + " XP residui.");
        }
    }
}
