/**
 * Classe que implementa operações de armazenamento sequencial para objetos do tipo Film.
 * As operações incluem inserção, recuperação, atualização e exclusão.
 */
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SequentialFile {
    private static String FILE_NAME = "SequentialFile.dat";

    public static void WriteFilm(RandomAccessFile file, Film film){
        try {
            file.writeByte(0); // flag
            file.writeInt(film.registerByteSize());
            file.writeInt(film.getId());
            file.writeUTF(film.getName());
            file.writeLong(film.getDate());
            file.writeInt(film.getBudget());
            file.writeFloat(film.getBoxOffice());
            
            // Escreve o gênero como string de tamanho fixo
            String genre = film.getGenre();
            for (int i = 0; i < 10; i++) {
                file.writeByte(i < genre.length() ? genre.charAt(i) : ' ');
            }
            
            file.writeInt(film.getFinancingCompanies().size());
            for (String company : film.getFinancingCompanies()) {
                file.writeUTF(company);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Film ReadFilm(RandomAccessFile file){
        Film film = null;
        try {
            int registerId = file.readInt();
            String registerName = file.readUTF();
            long registerDate = file.readLong();
            int registerBudget = file.readInt();
            float registerBoxOffice = file.readFloat();
            byte[] genreBytes = new byte[10];
            file.readFully(genreBytes);
            String registerGenre = new String(genreBytes).trim();
            int numCompanies = file.readInt();
            List<String> registerFinancingCompanies = new ArrayList<>();
            for (int i = 0; i < numCompanies; i++) {
                registerFinancingCompanies.add(file.readUTF());
            }
            film = new Film(registerId, registerName, registerDate, registerBudget, registerBoxOffice, registerFinancingCompanies, registerGenre);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return film;
    }

    /**
     * Insere um novo filme no arquivo sequencial.
     * @param film Objeto Film a ser inserido.
     * @return true se a inserção for bem-sucedida, false caso contrário.
     */
    public static boolean Insert(Film film) {
        boolean response = false;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw")) {
            int objectId;
            if (file.length() != 0) {
                file.seek(0);
                int lastId = file.readInt();
                objectId = lastId + 1;
            } else {
                objectId = 1;
            }
            film.setId(objectId);
            file.seek(0);
            file.writeInt(film.getId());
            file.seek(file.length());
            WriteFilm(file, film);
            response = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Recupera um filme do arquivo sequencial pelo ID.
     * @param id Identificador do filme.
     * @return Objeto Film correspondente ou null se não encontrado.
     */
    public static Film Get(int id) {
        Film film = null;
        boolean find = false;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "r")) {
            file.seek(4); // Pula o último ID salvo
            while (file.getFilePointer() < file.length() && !find) {
                Byte flag = file.readByte();
                int registerLength = file.readInt();
                long pos = file.getFilePointer();
                if (flag == '*') {
                    file.seek(pos + registerLength);
                } else {
                    Film recordFilm = ReadFilm(file);
                    if (recordFilm.getId() == id) {
                        film = recordFilm;
                        find = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return film;
    }

    /**
     * Atualiza um filme existente no arquivo sequencial.
     * @param newFilm Novo objeto Film atualizado.
     * @return true se a atualização for bem-sucedida, false caso contrário.
     */
    public static boolean Update(Film newFilm) {
        boolean response = false;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw")) {
            file.seek(4);
            while (file.getFilePointer() < file.length() && !response) {
                long pos = file.getFilePointer();
                byte flag = file.readByte();
                int registerSize = file.readInt();
                if (flag != '*') {
                    int id = file.readInt();
                    if (id == newFilm.getId()) {
                        int newSize = newFilm.registerByteSize();
                        if (newSize <= registerSize) {
                            file.seek(pos);
                            WriteFilm(file, newFilm);
                        } else {
                            file.seek(pos);
                            file.writeByte('*'); // Marcar como excluído
                            file.seek(file.length());
                            WriteFilm(file, newFilm);
                        }
                        response = true;
                    } else {
                        file.seek(pos + 5 + registerSize); //register size does not count flag byte and registter size bytes(+5)
                    }
                } else {
                    file.seek(pos + 5 + registerSize); //register size does not count flag byte and registter size bytes(+5)
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Exclui um filme do arquivo sequencial pelo ID.
     * @param id Identificador do filme a ser excluído.
     * @return true se a exclusão for bem-sucedida, false caso contrário.
     */
    public static boolean Delete(int id) {
        boolean response = false;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw")) {
            file.seek(4);
            while (file.getFilePointer() < file.length() && !response) {
                long position = file.getFilePointer();
                byte flag = file.readByte();
                int registerSize = file.readInt();
                if (flag == 0) {
                    int readID = file.readInt();
                    if (readID == id) {
                        file.seek(position);
                        file.writeByte('*');
                        response = true;
                    } else {
                        file.seek(position + 1 + 4 + registerSize);
                    }
                } else {
                    file.seek(position + 1 + 4 + registerSize);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public static void ExternalSort(int b, int m){
        //Distribution
        int totalRecords = 0;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "r")) {
            int quantityRecords;
            int quantityPaths;
            file.seek(4); //jumping lastId record
            while(file.getFilePointer() != file.length()){
                quantityPaths = 0;
                while(quantityPaths < m && file.getFilePointer() != file.length()){
                    Film blockFilm[] = new Film[b];
                    quantityRecords = 0;
                    while(quantityRecords < b && file.getFilePointer() != file.length()){
                        //reading record
                        Byte flag = file.readByte();
                        int registerLength = file.readInt(); 
                        long pos = file.getFilePointer();
                        if(flag == '*'){
                            file.seek(pos + registerLength);
                        }
                        else{ 
                            Film film = ReadFilm(file);
                            blockFilm[quantityRecords] = film;
                            quantityRecords++;
                            totalRecords++;
                        }
                    }
                    Arrays.sort(blockFilm, Comparator.comparingInt(film -> film.getId()));
                    try (RandomAccessFile tempFile = new RandomAccessFile("TempFile" + (quantityPaths+1), "rw")) {
                        tempFile.seek(file.length());
                        for(Film film : blockFilm){
                            WriteFilm(tempFile, film);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    quantityPaths++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intercalation(m, b, totalRecords, "TempFile", "TempFileTwo");
    }

    public static void Intercalation(int currentNumberOfFiles, int b, int totalRecords, String fileName1, String fileName2){
        if(currentNumberOfFiles > 1){
            int nextNumbemOfFiles = (int)Math.ceil(totalRecords/(currentNumberOfFiles*b));
            //positionsTempFile
            long positionTempFile[] = new long[currentNumberOfFiles];
            for(int i = 0; i < currentNumberOfFiles; i++){
                positionTempFile[i] = 0;
            }
            //positionsTempFile2
            long positionTempFile2[] = new long[nextNumbemOfFiles];
            for(int i = 0; i < nextNumbemOfFiles; i++){
                positionTempFile2[i] = 0;
            }
            for(int tRecords = 0; tRecords < totalRecords; tRecords++){
                for(int paths = 0; paths < nextNumbemOfFiles && tRecords < totalRecords; paths++){
                    for(int records = 0; records < currentNumberOfFiles*b && tRecords < totalRecords; records++){
                        int quantity[] = new int[currentNumberOfFiles];
                        for(int i = 0; i < currentNumberOfFiles; i++){
                            quantity[i] = 0;
                        }
                        int smallerId = 0;
                        try (RandomAccessFile tempFile = new RandomAccessFile(fileName1 + 1, "rw")) {
                            tempFile.seek(positionTempFile[1] + 5);//5 = byte flag + register size;
                            smallerId = tempFile.readInt();
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                        int smallestidPath = 1;
                        for(int i = 2; i <= currentNumberOfFiles; i++){
                            int id = 0;
                            if(positionTempFile[i] < b){
                                try (RandomAccessFile tempFile = new RandomAccessFile(fileName1 + i, "rw")) {
                                    if(tempFile.getFilePointer() < tempFile.length()){
                                        tempFile.seek(positionTempFile[i] + 5);//5 = byte flag + register size;
                                        id = tempFile.readInt();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if(id < smallerId){
                                    smallerId = id;
                                    smallestidPath = i;
                                }
                            }
                        }
                        Film film = null;
                        try (RandomAccessFile tempFile = new RandomAccessFile(fileName1 + smallestidPath, "rw")) {
                                tempFile.seek(positionTempFile[smallestidPath] + 5);//5 = byte flag + register size;
                                film = ReadFilm(tempFile);
                                positionTempFile[smallestidPath] = tempFile.getFilePointer();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } try (RandomAccessFile tempFile = new RandomAccessFile(fileName2 + (records+1), "rw")) {
                            tempFile.seek(positionTempFile2[nextNumbemOfFiles]);
                            WriteFilm(tempFile, film);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            Intercalation(nextNumbemOfFiles, currentNumberOfFiles*b, totalRecords, fileName2, fileName1);
        }
    }
}
