package kz.project.totp;

import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private final Scanner scanner = new Scanner(System.in);
    private final UserRepository repository = new CsvUserRepository("data/users.csv");
    private final SecretKeyManager secretKeyManager = new SecretKeyManager();
    private final TotpAuthenticator authenticator =
            new TotpAuthenticator(new TotpGenerator());

    public void run() {
        System.out.println("=================================");
        System.out.println("       TOTP ҚАУІПСІЗДІК ЖҮЙЕСІ");
        System.out.println("=================================");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> addUser();
                    case "2" -> showUsers();
                    case "3" -> generateCode();
                    case "4" -> verifyCode();
                    case "5" -> showRemainingTime();
                    case "0" -> {
                        running = false;
                        System.out.println("Бағдарлама аяқталды.");
                    }
                    default -> System.out.println("Қате таңдау. Қайта енгізіңіз.");
                }
            } catch (Exception e) {
                System.out.println("Қате: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("1. Жаңа пайдаланушы қосу");
        System.out.println("2. Пайдаланушылар тізімін көру");
        System.out.println("3. Уақытша код генерациялау");
        System.out.println("4. Кодты тексеру");
        System.out.println("5. Кодтың қалған уақытын көрсету");
        System.out.println("0. Шығу");
        System.out.print("Таңдауыңыз: ");
    }

    private void addUser() throws Exception {
        System.out.print("Пайдаланушы аты: ");
        String username = scanner.nextLine().trim();

        if (username.isBlank() || username.contains(",")) {
            throw new IllegalArgumentException("Аты бос болмауы және үтір болмауы керек.");
        }
        if (repository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Бұл пайдаланушы бұрын тіркелген.");
        }

        String secret = secretKeyManager.generateSecret();
        User user = new User(repository.nextId(), username, secret);
        repository.save(user);

        System.out.println("Пайдаланушы сәтті қосылды.");
        System.out.println("Құпия кілт (демо үшін): " + secret);
        System.out.println("Бұл кілтті қауіпсіз жерде сақтаңыз.");
    }

    private void showUsers() throws Exception {
        List<User> users = repository.findAll();
        if (users.isEmpty()) {
            System.out.println("Әзірге пайдаланушы жоқ.");
            return;
        }
        System.out.println("ID | Пайдаланушы");
        for (User user : users) {
            System.out.println(user.getId() + " | " + user.getUsername());
        }
    }

    private User selectUser() throws Exception {
        showUsers();
        System.out.print("Пайдаланушы ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        User user = repository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("Мұндай ID табылмады.");
        }
        return user;
    }

    private void generateCode() throws Exception {
        User user = selectUser();
        System.out.println("Ағымдағы TOTP коды: "
                + authenticator.generateCode(user.getSecret()));
    }

    private void verifyCode() throws Exception {
        User user = selectUser();
        System.out.print("6 таңбалы кодты енгізіңіз: ");
        String code = scanner.nextLine().trim();

        boolean valid = authenticator.verifyCode(user.getSecret(), code);
        if (valid) {
            System.out.println("Код дұрыс. Тексеру сәтті өтті.");
        } else {
            System.out.println("Код қате немесе жарамдылық уақыты аяқталған.");
        }
    }

    private void showRemainingTime() {
        System.out.println("Кодтың жаңаруына дейін: "
                + authenticator.getRemainingSeconds() + " секунд");
    }
}
