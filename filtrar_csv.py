import pandas as pd

# Nome do arquivo CSV de entrada
arquivo_entrada = "Top Movies (Cleaned Data).csv"

# Nome do arquivo CSV de saída
arquivo_saida = "dados_filtrados.csv"

# Lista de colunas a serem mantidas
colunas_desejadas = [
    "Movie Name", 
    "Release Date", 
    "Production Budget (USD)", 
    "Worldwide Box Office (USD)", 
    "Franchise", 
    "Genre"
]

# Carregar o CSV e manter apenas as colunas desejadas
df = pd.read_csv(arquivo_entrada, usecols=colunas_desejadas)

# Salvar o novo CSV filtrado
df.to_csv(arquivo_saida, index=False)

print(f"Arquivo filtrado salvo como {arquivo_saida}")
