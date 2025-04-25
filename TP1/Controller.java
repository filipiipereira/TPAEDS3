import java.time.LocalDate;
import static java.time.ZoneOffset.UTC;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
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

    public static Movie Form(Scanner scanner) {
        System.out.print("\nNew name: ");
        scanner.nextLine(); // Limpa o buffer
        String name = scanner.nextLine();

        LocalDate date = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        boolean controleDataValida = false;
        while (!controleDataValida) {
            try {
                System.out.print("New date (yyyy-MM-dd): ");
                String dateInput = scanner.nextLine();
                date = LocalDate.parse(dateInput, formatter);
                controleDataValida = true;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Please enter the correct format (yyyy-MM-dd).");
            }
        }

        long epochDate = date.atStartOfDay(UTC).toEpochSecond();
        int budget = 0;
        boolean controleBudgetValido = false;

        while (!controleBudgetValido) {
        try {
            System.out.print("New budget: ");
            budget = scanner.nextInt();
            controleBudgetValido = true; 
        } catch (InputMismatchException e) {
            System.out.println("Invalid value! Please enter an integer number for the budget.");
            scanner.nextLine();
        }   
}
        float boxOffice = 0.0f;
        boolean controleBoxOfficeValido = false;
        while (!controleBoxOfficeValido) {
            try {
                System.out.print("New global box-office: ");
                boxOffice = scanner.nextFloat();
                controleBoxOfficeValido = true; 
            } catch (InputMismatchException e) {
                System.out.println("Invalid value! Please enter a valid decimal number for the global box-office.");
                scanner.nextLine(); 
            }
        }
        System.out.print("New genre: ");
        scanner.nextLine(); // Limpa o buffer
        String genre = scanner.nextLine();

        System.out.print("New financing companies (type '0' to stop): ");
        List<String> financingCompanies = new ArrayList<>();
        String financingCompany = scanner.nextLine();

        while (!financingCompany.equals("0")) {
            financingCompanies.add(financingCompany);
            financingCompany = scanner.nextLine();
        }

        return new Movie(1, name, epochDate, budget, boxOffice, financingCompanies, genre);
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
        Movie movie = Form(scanner);
        movie.setId(id);
        return SequentialFile.Update(movie);
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

    public static Movie Get(Scanner scanner) {
        System.out.println("Which ID: ");
        int id = scanner.nextInt();
        return SequentialFile.Get(id);
    }
}