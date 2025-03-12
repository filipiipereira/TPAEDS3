/**
 * Classe que implementa operações de armazenamento sequencial para objetos do tipo Film.
 * As operações incluem inserção, recuperação, atualização e exclusão.
 */
import java.io.RandomAccessFile;
import java.util.ArrayList;
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

    public static void QuickSort(ArrayList<Film> arrayList, int esq, int dir){
        int i = esq;
        int j = dir;
        Film pivo = arrayList.get((i + j)/2);
        while(i <= j){
            while(pivo.getId() > arrayList.get(i).getId()){
                i++;
            }
            while(pivo.getId() < arrayList.get(j).getId()){
                j--;
            }
            if(i <= j){
                swap(arrayList, i, j);
                i++;
                j--;
            }
        }
        if(esq < j) QuickSort(arrayList, esq, j);
        if(i < dir) QuickSort(arrayList, i, dir);
    }
    public static void swap(ArrayList<Film> arrayList,int a, int b){
        Film temp = arrayList.get(b);
        arrayList.set(b, arrayList.get(a));
        arrayList.set(a, temp);
    }
    public static void QuickSort(ArrayList<Film> arrayList){
        QuickSort(arrayList, 0, arrayList.size()-1);
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
                            QuickSort(lastBlockFilm);
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
        Intercalation(m, b, totalRecords);
    }

    public static void Intercalation(int m, int b, int registrosTotais){
        System.out.println(registrosTotais);
        int numerosDeArquivos = m;
        int quantidadeRegistrosPorBloco = b;
        int numeroDaIntercalacao = 0;
        while(numerosDeArquivos > 1){
            
            //controla os nomes dos arquivos(par ou impar)
            numeroDaIntercalacao++;
            String arquivosAtuais;
            String arquivosProximos;
            if(numeroDaIntercalacao % 2 == 1){
                arquivosAtuais = "TempFile";
                arquivosProximos = "TempFileAux";
            } 
            else{
                arquivosAtuais = "TempFileAux";
                arquivosProximos = "TempFile";
            }

            //proximo numero de arquivos calculo
            int proximoNumeroDeArquivos = 0;
            int calculo = (registrosTotais / (quantidadeRegistrosPorBloco * numerosDeArquivos));
            if(calculo >= numerosDeArquivos){
                proximoNumeroDeArquivos = numerosDeArquivos;
            }
            else if((registrosTotais % (quantidadeRegistrosPorBloco * numerosDeArquivos)) == 0){
                proximoNumeroDeArquivos = (registrosTotais / (quantidadeRegistrosPorBloco * numerosDeArquivos));
            }   
            else{
                proximoNumeroDeArquivos = calculo + 1;
            }
            System.out.println("numeros arquivos atuais: " + numerosDeArquivos);

            //iniciailizdo vetores que guardam os ponteiro dos arquivos
            long posicoesAtual[] = new long[numerosDeArquivos];
            for(int i = 0; i < numerosDeArquivos; i++){
                posicoesAtual[i] = 0;
            }
            long posicoesProximo[] = new long[proximoNumeroDeArquivos];
            for(int i = 0; i < proximoNumeroDeArquivos; i++){
                posicoesProximo[i] = 0;
            }
            //para cada intercalacao le se o numero total de registros
            int totaisRegistrosLidos = 0;
            boolean acabouIntercalacao = false;
            while(totaisRegistrosLidos < registrosTotais && !acabouIntercalacao){
                
                //cada secao vai ser escrita em um caminho diferente
                int quantidadeCaminhosProximo = 0;                
                while(quantidadeCaminhosProximo < proximoNumeroDeArquivos && !acabouIntercalacao){

                    //para cada parte da intercalacao le se o numero de registros da secao
                    int registrosLidosPorSecao = 0;
                    while(registrosLidosPorSecao < numerosDeArquivos*quantidadeRegistrosPorBloco  && !acabouIntercalacao){

                        //cada arquivo so pode ser lido em blocos
                        //inicializacao do vetor que controla ate onde pode ser lido
                        int quantidadeDeLeiturasPorArquivoAtual[] = new int[numerosDeArquivos];
                        for(int i = 0; i < numerosDeArquivos; i++){
                            quantidadeDeLeiturasPorArquivoAtual[i] = 0;
                        }

                        //iniciando as leituras para achar o arquivo que possui o menor id
                        int menorId = Integer.MAX_VALUE;
                        int ArquivoMenorId = -1;
                        for (int i = 0; i < numerosDeArquivos; i++) {
                            try (RandomAccessFile arquivoAtual = new RandomAccessFile(arquivosAtuais + (i + 1), "rw")) {
                                arquivoAtual.seek(posicoesAtual[i] + 5);
                                //condicoes para leitura, o arquivo nao ter lido o bloco inteiro e o arquivo nao tiver acabado
                                if (quantidadeDeLeiturasPorArquivoAtual[i] < quantidadeRegistrosPorBloco && arquivoAtual.getFilePointer() < arquivoAtual.length()){
                                    int id = arquivoAtual.readInt();
                                    if (id < menorId) {
                                        menorId = id;
                                        ArquivoMenorId = i;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //ler o filme de menor id e marcar a posicao do arquivo
                        Film filme = null;
                        if(ArquivoMenorId != -1){
                            try (RandomAccessFile arquivoAtual = new RandomAccessFile(arquivosAtuais + (ArquivoMenorId+1), "rw")) {
                                arquivoAtual.seek(posicoesAtual[ArquivoMenorId] + 5);//5 = byte flag + register size;
                                if(arquivoAtual.getFilePointer() < arquivoAtual.length()){
                                    filme = ReadFilm(arquivoAtual);
                                    posicoesAtual[ArquivoMenorId] += 5 + filme.registerByteSize();
                                    quantidadeDeLeiturasPorArquivoAtual[ArquivoMenorId]++;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //escrever o filme no arquivo proximo na posicao quantidade caminhos proximos

                        if(filme != null){
                            try (RandomAccessFile arquivoProximo = new RandomAccessFile(arquivosProximos + (quantidadeCaminhosProximo+1), "rw")) {
                                arquivoProximo.seek(posicoesProximo[quantidadeCaminhosProximo]);
                                WriteFilm(arquivoProximo, filme);
                                posicoesProximo[quantidadeCaminhosProximo] += 5 + filme.registerByteSize(); 
                                //aumentando um no contador de registros
                                registrosLidosPorSecao++;   
                                totaisRegistrosLidos++;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else acabouIntercalacao = true;
                    }

                    quantidadeCaminhosProximo++;
                    System.out.println("secao: " + registrosLidosPorSecao);
                }
            }
            System.out.println("total: " + totaisRegistrosLidos);
            quantidadeRegistrosPorBloco *= numerosDeArquivos;
            numerosDeArquivos = proximoNumeroDeArquivos;
        }
        System.out.println("intercalacao: " + numeroDaIntercalacao);
    }
}
