/**
 * Classe que implementa operações de armazenamento sequencial para objetos do tipo Film.
 * As operações incluem inserção, recuperação, atualização e exclusão.
 */
import java.io.RandomAccessFile;
import java.util.ArrayList;
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

    public static void QuickSort(Film[] array, int esq, int dir){
        int i = esq;
        int j = dir;
        Film pivo = array[(i + j)/2];
        while(i <= j){
            while(pivo.getId() > array[i].getId()){
                i++;
            }
            while(pivo.getId() < array[j].getId()){
                j--;
            }
            if(i <= j){
                swap(array, i, j);
                i++;
                j--;
            }
        }
        if(esq < j) QuickSort(array, esq, j);
        if(i < dir) QuickSort(array, i, dir);
    }
    public static void swap(Film array[],int a, int b){
        Film temp = array[b];
        array[b] = array[a];
        array[a] = temp;
    }
    public static void QuickSort(Film[] array){
        QuickSort(array, 0, array.length-1);
    }

    public static void ExternalSort(int b, int m){
        //Distribution
        int totalRecords = 0;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "r")) {
            int quantityRecords;
            int quantityPaths;
            file.seek(4); //jumping lastId record
            boolean startingFile = true;
            while(file.getFilePointer() < file.length()){
                quantityPaths = 0;
                while(quantityPaths < m && file.getFilePointer() < file.length()){
                    Film blockFilm[] = new Film[b];
                    quantityRecords = 0;
                    while(quantityRecords < b && file.getFilePointer() < file.length()){
                        //reading record
                        byte flag = file.readByte();
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
                    try (RandomAccessFile tempFile = new RandomAccessFile("TempFile" + (quantityPaths+1), "rw")) {
                        if(file.getFilePointer() >= file.length()){
                            ArrayList<Film> lastBlockFilm = new ArrayList<>();
                            for(Film film : blockFilm){
                                if(film != null){
                                    lastBlockFilm.add(film);
                                }
                            }
                            lastBlockFilm.sort(Comparator.comparingInt(Film::getId));
                            for(int i = 0; i < lastBlockFilm.size(); i++){
                                tempFile.seek(tempFile.length());
                                WriteFilm(tempFile, lastBlockFilm.get(i));
                            }
                        }
                        else{
                            QuickSort(blockFilm);
                            for(int i = 0; i < blockFilm.length; i++){
                                tempFile.seek(tempFile.length());
                                WriteFilm(tempFile, blockFilm[i]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    quantityPaths++;
                    startingFile = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intercalation(m, b, totalRecords, "TempFile", "TempFileTwo");
    }

    public static void Intercalation(int currentNumberOfFiles, int b, int totalRecords, String fileName1, String fileName2){
        System.out.println("Entrei na funcao");
        if(currentNumberOfFiles > 1){
            int nextNumberOfFiles = (int)Math.ceil(totalRecords/(currentNumberOfFiles*b)) < currentNumberOfFiles ? (int)Math.ceil(totalRecords/(currentNumberOfFiles*b)) : currentNumberOfFiles;
            System.out.println("Current number of files:" + currentNumberOfFiles);
            //positionsTempFile
            long positionTempFile[] = new long[currentNumberOfFiles];
            for(int i = 0; i < currentNumberOfFiles; i++){
                positionTempFile[i] = 0;
            }
            //positionsTempFile2
            long positionTempFile2[] = new long[nextNumberOfFiles];
            for(int i = 0; i < nextNumberOfFiles; i++){
                positionTempFile2[i] = 0;
            }
            int tRecords = 0;
            while(tRecords < totalRecords){
                System.out.println("quant registros" + tRecords);
                for(int paths = 0; paths < nextNumberOfFiles && tRecords < totalRecords; paths++){
                    System.out.println("caminho" + paths);
                    int quantity[] = new int[currentNumberOfFiles];
                    for(int i = 0; i < currentNumberOfFiles; i++){
                        quantity[i] = 0;
                    }
                    for(int records = 0; records < currentNumberOfFiles*b && tRecords < totalRecords; records++){
                        int smallerId = Integer.MAX_VALUE;
                        int smallestidPath = -1;
                        for (int i = 0; i < currentNumberOfFiles && tRecords < totalRecords; i++) {
                            if (quantity[i] < b) {
                                try (RandomAccessFile tempFile = new RandomAccessFile(fileName1 + (i + 1), "rw")) {
                                    if (positionTempFile[i] < tempFile.length()) {  //
                                        tempFile.seek(positionTempFile[i] + 5);
                                        int id = tempFile.readInt();
                                        if (id < smallerId) {
                                            smallerId = id;
                                            smallestidPath = i;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        System.out.println("posicao menor id: " + smallestidPath);
                        Film film = null;
                        if (smallestidPath != -1) {
                            try (RandomAccessFile tempFile = new RandomAccessFile(fileName1 + (smallestidPath+1), "rw")) {
                                tempFile.seek(positionTempFile[smallestidPath] + 5);//5 = byte flag + register size;
                                film = ReadFilm(tempFile);
                                positionTempFile[smallestidPath] += 5 + film.registerByteSize();
                                quantity[smallestidPath]++;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if(film != null){
                            try (RandomAccessFile tempFile = new RandomAccessFile(fileName2 + (paths+1), "rw")) {
                                tempFile.seek(positionTempFile2[paths]);
                                WriteFilm(tempFile, film);
                                tRecords++;
                                positionTempFile2[paths] += 5 + film.registerByteSize(); 
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println("records: " + records);
                    }
                }
            }
            Intercalation(nextNumberOfFiles, currentNumberOfFiles*b, totalRecords, fileName2, fileName1);
        }
    }
}
