import java.time.LocalDate;
import static java.time.ZoneOffset.UTC;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe Controller responsável por gerenciar a interação com o usuário e manipular objetos da classe Film.
 */
public class Controller {
    
    /**
     * Método responsável por capturar os dados de um filme a partir da entrada do usuário.
     * 
     * @param scanner Objeto Scanner para entrada do usuário.
     * @return Objeto Film preenchido com os dados informados pelo usuário.
     */
    public static Film Form(Scanner scanner) {
        System.out.print("Name: ");
        scanner.nextLine(); // Limpa o buffer
        String name = scanner.nextLine();

        System.out.print("Date(yyyy-MM-dd): ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(scanner.nextLine(), formatter);
        long epochDate = date.atStartOfDay(UTC).toEpochSecond();

        System.out.print("Budget: ");
        int budget = scanner.nextInt();

        System.out.print("Global box-office: ");
        float boxOffice = scanner.nextFloat();

        System.out.print("Genre: ");
        scanner.nextLine(); // Limpa o buffer
        String genre = scanner.nextLine();

        System.out.print("Financing companies (type '0' to stop): ");
        List<String> financingCompanies = new ArrayList<>();
        String financingCompany = scanner.nextLine();
        
        while (!financingCompany.equals("0")) {
            financingCompanies.add(financingCompany);
            financingCompany = scanner.nextLine();
        }

        return new Film(1, name, epochDate, budget, boxOffice, financingCompanies, genre);
    }

    /**
     * Método responsável por inserir um novo filme no arquivo sequencial.
     * 
     * @param scanner Objeto Scanner para entrada do usuário.
     * @return true se a inserção foi bem-sucedida, false caso contrário.
     */
    public static boolean Insert(Scanner scanner) {
        return SequentialFile.Insert(Form(scanner));
    }

    /**
     * Método responsável por atualizar um filme existente com base no ID informado.
     * 
     * @param scanner Objeto Scanner para entrada do usuário.
     * @return true se a atualização foi bem-sucedida, false caso contrário.
     */
    public static boolean Update(Scanner scanner) {
        System.out.println("Which ID: ");
        int id = scanner.nextInt();
        SequentialFile.Get(id).toStr();
        Film film = Form(scanner);
        film.setId(id);
        return SequentialFile.Update(film);
    }

    /**
     * Método responsável por deletar um filme com base no ID informado.
     * 
     * @param scanner Objeto Scanner para entrada do usuário.
     * @return true se a exclusão foi bem-sucedida, false caso contrário.
     */
    public static boolean Delete(Scanner scanner) {
        System.out.println("Which ID: ");
        int id = scanner.nextInt();
        return SequentialFile.Delete(id);
    }

    /**
     * Método responsável por buscar um filme com base no ID informado.
     * 
     * @param scanner Objeto Scanner para entrada do usuário.
     * @return Objeto Film correspondente ao ID informado.
     */
    public static Film Get(Scanner scanner) {
        System.out.println("Which ID: ");
        int id = scanner.nextInt();
        return SequentialFile.Get(id);
    }
}