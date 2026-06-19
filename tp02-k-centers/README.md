# Trabalho Prático N.02: Problema dos k-Centros

**Disciplina:** Teoria dos Grafos e Computabilidade  
**Integrantes:** Gabriel Alves da Silva Diógenes e Rafael Mortimer Colares

## Descrição

Implementação em **Java** e comparação de dois algoritmos para o problema dos k-centros:

1. **Exato:** busca binária sobre raios candidatos + programação linear inteira (ojAlgo, Java puro)
2. **Aproximado:** heurística gulosa de Gonzalez (fator de aproximação 2)

## Requisitos

- Java 17+
- Maven 3.8+
- Para compilar o relatório: distribuição LaTeX (pdflatex)

## Uso

```bash
# Compilar o projeto
mvn compile

# Baixar instâncias da OR-Library
mvn exec:java -Dexec.mainClass=br.puc.tgc.kcenters.DownloadData

# Executar experimentos (40 instâncias)
mvn exec:java -Dexec.mainClass=br.puc.tgc.kcenters.Experiments

# Gerar gráficos para o relatório
mvn exec:java -Dexec.mainClass=br.puc.tgc.kcenters.GeneratePlots

# Compilar relatório
cd relatorio
pdflatex relatorio.tex
```

## Estrutura

```
pom.xml
src/main/java/br/puc/tgc/kcenters/
  InstanceLoader.java      Leitura e Floyd-Warshall
  ExactKCenters.java       Algoritmo exato (ILP com ojAlgo)
  ApproximateKCenters.java Algoritmo de Gonzalez
  DownloadData.java        Download das instâncias
  Experiments.java         Execução dos experimentos
  GeneratePlots.java       Geração de gráficos
data/                      Instâncias pmed1..pmed40
results/                   Resultados em CSV
relatorio/                 Relatório em LaTeX e figuras
```

## Resultados

Os resultados completos estão em `results/resultados.csv` e no relatório PDF.

O algoritmo exato obtém o raio ótimo em praticamente todas as instâncias. O algoritmo aproximado apresenta gap médio de aproximadamente 49% em relação ao ótimo, com tempo de execução muito inferior.
