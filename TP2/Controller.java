import java.time.LocalDate;
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
                date = LocalDate.parse(dateInput);
                controleDataValida = true;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Please enter the correct format (yyyy-MM-dd).");
            }
        }
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

        return new Movie(1, name, date, budget, boxOffice, financingCompanies, genre);
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
        Movie film = Form(scanner);
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

    public static Movie Get(Scanner scanner) {
        System.out.println("Which ID: ");
        int id = scanner.nextInt();
        return SequentialFile.Get(id);
    }

    public static void Sort(Scanner scanner){
        System.out.print("Digite a quantidade de caminhos: ");
        int m = scanner.nextInt();
        System.out.print("Digite a quantidade de registros por bloco: ");
        int b = scanner.nextInt();
        SequentialFile.ExternalSort(b,m);
    }
}