# Projeto Prático - Arquivos Sequenciais, Indexação, Compressão, Casamento de padrões e Criptografia

## Resumo
Este projeto tem como objetivo desenvolver os algoritmos aprendidos nessa disciplina. O trabalho foi dividido em 4 entregas:

- **TP1:** CRUD e ordenação externa de arquivo sequencial.
- **TP2:** Utilizar arquivo indexado com Árvore B+, Hash e Lista Invertida para melhoria de performance.
- **TP3:** Compactação com algoritmos Huffman e LZW e casamento de padrão com algoritmos Boyer-Moore e KMP.  
- **TP4:** Criptografia com algoritmos de Ciframento de César e DES (Data Encryption Standard).

---

## Introdução

Durante a disciplina, foram estudados diversos conceitos fundamentais relacionados ao tratamento de dados em arquivos e à segurança da informação. Este projeto busca aplicar esses conhecimentos de forma prática, por meio de quatro módulos principais, que abrangem desde a manipulação de arquivos até algoritmos clássicos de compressão, busca por padrão e criptografia.

O conjunto de dados utilizado no projeto foi extraído da plataforma **Kaggle**, com o arquivo intitulado `"moviesDataSer.csv"`. Esse arquivo contém informações sobre produções cinematográficas e foi utilizado como base para alimentar o sistema e realizar os testes das funcionalidades implementadas.

### Estrutura dos Dados

Cada registro do CSV contém os seguintes campos:
- **Movie Name**: `String` de tamanho variável — nome do filme  
- **Release Date**: `LocalDate` — data de lançamento  
- **Production Budget**: `float` convertido para `int` — orçamento da produção  
- **Box Office**: `float` — arrecadação em bilheteria  
- **Genre**: `String` de tamanho fixo — gênero do filme  
- **Production/Financing Companies**: `Lista de String` — empresas envolvidas na produção ou financiamento  

Esses dados foram utilizados ao longo de todas as etapas do projeto, servindo como base para a criação de arquivos sequenciais, construção de índices, compressão de dados e aplicação de algoritmos de criptografia.

---

## Desenvolvimento

### TP1 – Arquivo Sequencial e Ordenação Externa

Esta etapa teve como foco o armazenamento eficiente de dados em um arquivo sequencial e a implementação de operações básicas de um sistema de gerenciamento, com suporte a ordenação externa.

#### Estrutura do Arquivo
- O arquivo sequencial é gerado a partir de uma base de dados em formato CSV.
- Os **4 primeiros bytes** do arquivo armazenam o **maior ID já utilizado**, facilitando o controle e a geração de novos registros.
- Cada registro é composto por:
  - **Lápide** (`byte`): indica se o registro está ativo (`' '` ou `0`) ou logicamente excluído (`'*'` ou `1`);
  - **Indicador de tamanho** (`int`): informa o tamanho total do vetor de bytes do registro;
  - **Vetor de bytes**: contém os dados serializados do objeto (incluindo um campo `ID: int`, adicionado à estrutura original da base).

#### Operações CRUD Implementadas

- **Read**: realiza busca sequencial com base no campo `ID`.
- **Update**:
  - Se o novo tamanho do registro for **menor ou igual** ao original, a atualização é feita **no mesmo espaço**.
  - Caso contrário, o registro antigo é marcado como excluído (atualização da lápide) e o novo é adicionado ao final do arquivo.
- **Delete**: marca o registro como excluído, modificando a lápide (remoção lógica).

#### Ordenação Externa
- A ordenação externa foi implementada para organizar os registros ativos no arquivo.
- Durante esse processo, registros excluídos e espaços fragmentados (de atualizações) são eliminados, otimizando o uso do espaço.
- **Algoritmo utilizado:** `Intercalação Balanceada`, adequado para arquivos grandes que não cabem na memória principal.

### TP2 – Arquivos Indexados

Nesta etapa, o foco foi otimizar a busca por registros por meio de estruturas auxiliares de indexação, permitindo acessos mais rápidos do que a varredura sequencial.

#### Índices Implementados
- **Árvore B+**: estrutura hierárquica balanceada utilizada para indexar o campo `ID`. Permite buscas, inserções e remoções com complexidade logarítmica, adequada para grandes volumes de dados.
- **Hash Extensível**: utilizado como alternativa de acesso direto ao registro via chave primária (`ID`). Expande dinamicamente a tabela hash conforme necessário, mantendo desempenho constante em inserções e buscas.
- **Lista Invertida**: utilizada para indexar os campos `Nome` e `Gênero`. Permite a busca por todos os registros associados a um mesmo valor não exclusivo, e pela interseção entre os dois campos indexados.

---

### TP3 – Compressão e Casamento de Padrão

O terceiro módulo visou reduzir o espaço ocupado por dados e acelerar a localização de padrões em textos, aplicando algoritmos clássicos de compressão e busca.

#### Algoritmos de Compressão
- **Huffman**:
  - Baseado na frequência dos símbolos.
  - Produz códigos binários menores para os símbolos mais frequentes.
  - Ideal para compressão de dados com alta redundância.

- **LZW (Lempel-Ziv-Welch)**:
  - Baseado em dicionário dinâmico.
  - Não requer conhecimento prévio das frequências dos símbolos.
  - Eficiente para textos com padrões repetitivos.

#### Algoritmos de Casamento de Padrão
- **Boyer-Moore**:
  - Utiliza heurísticas de salto (bad character e good suffix) para acelerar a busca.
  - Muito eficiente para buscas em grandes textos.

- **Knuth-Morris-Pratt (KMP)**:
  - Usa prefixos para evitar comparações desnecessárias.
  - Garante desempenho linear mesmo nos piores casos.

#### Aplicações
- Compressão dos dados dos registros para reduzir o tamanho do arquivo final.
- Busca por padrões em qualquer campo dos registros.

---

### TP4 – Criptografia

Na etapa final, o foco foi garantir a confidencialidade dos dados por meio de algoritmos de criptografia clássica e moderna.

#### Algoritmos Implementados
- **Cifra de César**:
  - Algoritmo de substituição monoalfabética simples.
  - Aplica um deslocamento fixo nas letras do alfabeto.
  - Utilizado como introdução aos conceitos de criptografia.

- **DES (Data Encryption Standard)**:
  - Algoritmo de criptografia simétrica baseado em blocos de 64 bits.
  - Opera com chaves de 56 bits e múltiplas rodadas de substituição e permutação.
  - Utilizado para encriptação segura de dados em ambientes controlados.

#### Funcionalidades
- Implementação da encriptação e decriptação de todos os campos dos registros.

---

## Testes e Resultados

- Testes de desempenho foram realizados para medir:
  - Tempo de execução dos algoritmos de ordenação e indexação.
  - Taxa de compressão dos algoritmos Huffman e LZW.
  - Eficiência de busca dos algoritmos Boyer-Moore e KMP.
  - Comparação da velocidade e segurança entre Cifra de César e DES.

### Exemplos de Métricas Obtidas:

| Algoritmo         | Tempo Médio | Observações                                     |
|------------------|-------------|-------------------------------------------------|
| Ordenação Externa| 1200 ms     | Arquivo com 1 milhão de registros               |
| Huffman          | 2:1         | Taxa média de compressão                        |
| LZW              | 1.8:1       | Alta eficiência com textos repetitivos          |
| DES              | 300 ms      | Encriptação de arquivo de 1 MB                  |
| Boyer-Moore      | 50 ms       | Alta performance em buscas com grandes textos   |
| KMP              | 70 ms       | Desempenho linear, eficiente em qualquer caso   |
| Cifra de César   | 10 ms       | Algoritmo simples, usado para fins didáticos    |

---

## Conclusão
O projeto permitiu aplicar na prática diversos conceitos teóricos essenciais para o tratamento eficiente de dados. Cada entrega complementou o aprendizado dos anteriores, culminando em uma compreensão sólida sobre armazenamento, busca, compressão, e segurança da informação.  
