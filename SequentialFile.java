/**
 * Classe que implementa operações de armazenamento sequencial para objetos do tipo Movie.
 * As operações incluem inserção, recuperação, atualização e exclusão.
 */
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
public class SequentialFile {
    private static String FILE_NAME = "SequentialFile.dat";

    private static int numberOfMovies = 0; 

    public static void WriteMovie(RandomAccessFile file, Movie Movie){
        try {
            file.writeByte(0); // flag
            file.writeInt(Movie.registerByteSize());
            file.writeInt(Movie.getId());
            file.writeUTF(Movie.getName());
            file.writeLong(Movie.getDate());
            file.writeInt(Movie.getBudget());
            file.writeFloat(Movie.getBoxOffice());
            
            // Escreve o gênero como string de tamanho fixo
            String genre = Movie.getGenre();
            for (int i = 0; i < 10; i++) {
                file.writeByte(i < genre.length() ? genre.charAt(i) : ' ');
            }
            
            file.writeInt(Movie.getFinancingCompanies().size());
            for (String company : Movie.getFinancingCompanies()) {
                file.writeUTF(company);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Movie ReadMovie(RandomAccessFile file){
        Movie Movie = null;
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
            Movie = new Movie(registerId, registerName, registerDate, registerBudget, registerBoxOffice, registerFinancingCompanies, registerGenre);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Movie;
    }

    /**
     * Insere um novo Moviee no arquivo sequencial.
     * @param Movie Objeto Movie a ser inserido.
     * @return true se a inserção for bem-sucedida, false caso contrário.
     */
    public static boolean Insert(Movie Movie) {
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
            Movie.setId(objectId);
            file.seek(0);
            file.writeInt(Movie.getId());
            file.seek(file.length());
            WriteMovie(file, Movie);
            numberOfMovies++;
            response = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Recupera um Moviee do arquivo sequencial pelo ID.
     * @param id Identificador do Moviee.
     * @return Objeto Movie correspondente ou null se não encontrado.
     */
    public static Movie Get(int id) {
        Movie movie = null;
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
                    Movie recordMovie = ReadMovie(file);
                    if (recordMovie.getId() == id) {
                        movie = recordMovie;
                        find = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movie;
    }

    /**
     * Atualiza um Moviee existente no arquivo sequencial.
     * @param newMovie Novo objeto Movie atualizado.
     * @return true se a atualização for bem-sucedida, false caso contrário.
     */
    public static boolean Update(Movie newMovie) {
        boolean response = false;
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw")) {
            file.seek(4);
            while (file.getFilePointer() < file.length() && !response) {
                long pos = file.getFilePointer();
                byte flag = file.readByte();
                int registerSize = file.readInt();
                if (flag != '*') {
                    int id = file.readInt();
                    if (id == newMovie.getId()) {
                        int newSize = newMovie.registerByteSize();
                        if (newSize <= registerSize) {
                            file.seek(pos);
                            WriteMovie(file, newMovie);
                        } else {
                            file.seek(pos);
                            file.writeByte('*'); // Marcar como excluído
                            file.seek(file.length());
                            WriteMovie(file, newMovie);
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
     * Exclui um Moviee do arquivo sequencial pelo ID.
     * @param id Identificador do Moviee a ser excluído.
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
                        numberOfMovies--;
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
        Distribution(b, m);
        Intercalation(m, b, numberOfMovies);
    }

    public static void Distribution(int b, int m){
        //Distribution
        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "r")) {
            int quantityRecords;
            int quantityPaths;
            file.seek(4); //jumping lastId record
            while(file.getFilePointer() < file.length()){
                quantityPaths = 0;
                while(quantityPaths < m && file.getFilePointer() < file.length()){
                    Movie blockMovie[] = new Movie[b];
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
                            Movie Movie = ReadMovie(file);
                            blockMovie[quantityRecords] = Movie;
                            quantityRecords++;
                        }
                    }
                    try (RandomAccessFile tempFile = new RandomAccessFile("TempFile" + (quantityPaths+1), "rw")) {
                        if(file.getFilePointer() >= file.length()){
                            ArrayList<Movie> lastBlockMovie = new ArrayList<>();
                            for(Movie Movie : blockMovie){
                                if(Movie != null){
                                    lastBlockMovie.add(Movie);
                                }
                            }
                            Movie[] newBlockMovie = lastBlockMovie.toArray(new Movie[0]);
                            Arrays.sort(newBlockMovie, Comparator.comparingInt(Movie::getId));
                            for(int i = 0; i < lastBlockMovie.size(); i++){
                                tempFile.seek(tempFile.length());
                                WriteMovie(tempFile, lastBlockMovie.get(i));
                            }
                        }
                        else{
                            Arrays.sort(blockMovie, Comparator.comparingInt(Movie::getId));
                            for(int i = 0; i < blockMovie.length; i++){
                                tempFile.seek(tempFile.length());
                                WriteMovie(tempFile, blockMovie[i]);
                            }
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
    }

    public static void Intercalation(int m, int b, int registrosTotais){
        int numerosDeArquivos = m;
        int quantidadeRegistrosPorBloco = b;
        int numeroDaIntercalacao = 0;

        while(numerosDeArquivos > 1){
            
            //controla os nomes dos arquivos(par ou impar)
            numeroDaIntercalacao++;
            String nomeArquivosAtuais;
            String nomeArquivosProximos;
            if(numeroDaIntercalacao % 2 == 1){
                nomeArquivosAtuais = "TempFile";
                nomeArquivosProximos = "TempFileAux";
            } 
            else{
                nomeArquivosAtuais = "TempFileAux";
                nomeArquivosProximos = "TempFile";
            }

            //proximo numero de arquivos calculo
            int proximoNumeroDeArquivos = 0;
            int calculo = (registrosTotais / (quantidadeRegistrosPorBloco * numerosDeArquivos));
            if(calculo > numerosDeArquivos){
                proximoNumeroDeArquivos = numerosDeArquivos;
            }
            else if((registrosTotais % (quantidadeRegistrosPorBloco * numerosDeArquivos)) == 0){
                proximoNumeroDeArquivos = calculo;
            }   
            else{
                proximoNumeroDeArquivos = calculo + 1;
            }

            RandomAccessFile[] arquivosAtuais = new RandomAccessFile[numerosDeArquivos];
            RandomAccessFile[] arquivosProximos = new RandomAccessFile[proximoNumeroDeArquivos];

            // Abrir todos os arquivos atuais
            for (int i = 0; i < numerosDeArquivos; i++) {
                try{
                    arquivosAtuais[i] = new RandomAccessFile(nomeArquivosAtuais + (i +1), "rw");    
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < proximoNumeroDeArquivos; i++) {
                try{
                    arquivosProximos[i] = new RandomAccessFile(nomeArquivosProximos + (i + 1), "rw");   
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

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

                    //cada arquivo so pode ser lido em blocos
                    //inicializacao do vetor que controla ate onde pode ser lido
                    int quantidadeDeLeiturasPorArquivoAtual[] = new int[numerosDeArquivos];
                    for(int i = 0; i < numerosDeArquivos; i++){
                        quantidadeDeLeiturasPorArquivoAtual[i] = 0;
                    }

                    //para cada parte da intercalacao le se o numero de registros da secao
                    int registrosLidosPorSecao = 0;
                    while(registrosLidosPorSecao < numerosDeArquivos*quantidadeRegistrosPorBloco  && !acabouIntercalacao){


                        //iniciando as leituras para achar o arquivo que possui o menor id
                        int menorId = Integer.MAX_VALUE;
                        int ArquivoMenorId = -1;
                        for (int i = 0; i < numerosDeArquivos; i++) {
                            try{
                                RandomAccessFile arquivoAtual = arquivosAtuais[i];
                                arquivoAtual.seek(posicoesAtual[i] + 5);
                                //condicoes para leitura, o arquivo nao ter lido o bloco inteiro e o arquivo nao tiver acabado
                                if (quantidadeDeLeiturasPorArquivoAtual[i] < quantidadeRegistrosPorBloco && arquivoAtual.getFilePointer() < arquivoAtual.length() && totaisRegistrosLidos < registrosTotais){
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
                        //ler o Moviee de menor id e marcar a posicao do arquivo
                        Movie Moviee = null;
                        if(ArquivoMenorId != -1){
                            try{
                                RandomAccessFile arquivoAtual = arquivosAtuais[ArquivoMenorId];
                                arquivoAtual.seek(posicoesAtual[ArquivoMenorId] + 5);//5 = byte flag + register size;
                                if(arquivoAtual.getFilePointer() < arquivoAtual.length()){
                                    Moviee = ReadMovie(arquivoAtual);
                                    posicoesAtual[ArquivoMenorId] = arquivoAtual.getFilePointer();
                                    quantidadeDeLeiturasPorArquivoAtual[ArquivoMenorId]++;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //escrever o Moviee no arquivo proximo na posicao quantidade caminhos proximos

                        if(Moviee != null){
                            try{
                                RandomAccessFile arquivoProximo = arquivosProximos[quantidadeCaminhosProximo];
                                arquivoProximo.seek(posicoesProximo[quantidadeCaminhosProximo]);
                                WriteMovie(arquivoProximo, Moviee);
                                posicoesProximo[quantidadeCaminhosProximo] += 5 + Moviee.registerByteSize(); 
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
                }  
            }
            if(quantidadeRegistrosPorBloco * numerosDeArquivos >= registrosTotais) quantidadeRegistrosPorBloco = registrosTotais;
            else quantidadeRegistrosPorBloco *= numerosDeArquivos;
            numerosDeArquivos = proximoNumeroDeArquivos;

            // Fechar todos os arquivos no final
            for (RandomAccessFile arquivo : arquivosAtuais) {
                try{
                    arquivo.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            for (RandomAccessFile arquivo : arquivosProximos) {
                try{
                    arquivo.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        String arquivoOrdenado;
        if(numeroDaIntercalacao % 2 == 1){
            arquivoOrdenado = "TempFileAux1";
        }
        else{
            arquivoOrdenado = "TempFile";
        }
        System.out.println("Arquivo ordenado: " + arquivoOrdenado);
    }
}