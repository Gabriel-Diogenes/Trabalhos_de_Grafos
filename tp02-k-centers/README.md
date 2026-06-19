# Trabalho Prático N.02: Problema dos k-Centros

**Disciplina:** Teoria dos Grafos e Computabilidade  
**Integrantes:** Gabriel Alves da Silva Diógenes e Rafael Mortimer Colares

## Descrição

Implementação em **Java** e comparação de dois algoritmos para o problema dos k-centros:

1. **Exato:** busca binária sobre raios candidatos + programação linear inteira (ojAlgo, Java puro)
2. **Aproximado:** heurística gulosa de Gonzalez (fator de aproximação 2)

## Requisitos

- Java 17+
- Maven 3.8+ (ou importar como projeto Maven no IntelliJ)
- Para compilar o relatório: distribuição LaTeX (pdflatex)

## IntelliJ IDEA

1. **File → Open** e selecione a pasta do projeto (ou o `pom.xml`)
2. Quando perguntar, confie no projeto e **importe como Maven**
3. Aguarde o IntelliJ baixar as dependências (`ojalgo`, `jfreechart`)
4. Em **File → Project Structure → Project**, confirme **SDK = 17**
5. Clique com o botão direito em `Main.java` → **Run 'Main.main()'**

Se ainda aparecer erro vermelho: abra a aba **Maven** (lateral direita) → clique no ícone **Reload All Maven Projects** (seta circular).

A configuração de execução **Main** já está em `.idea/runConfigurations/Main.xml`, com diretório de trabalho na raiz do projeto.

## Uso

```bash
# Compilar e executar tudo (dados, experimentos, tabela e graficos)
mvn compile exec:java

# Compilar relatório
cd relatorio
pdflatex relatorio.tex
```

A classe `Main` executa automaticamente:
1. Verificação/download das instâncias OR-Library
2. Experimentos com algoritmo exato e aproximado (40 instâncias)
3. Tabela completa e resumo estatístico no terminal
4. Geração dos gráficos em `relatorio/figuras/`
5. Salvamento do CSV em `results/resultados.csv`

## Estrutura

```
pom.xml
src/main/java/br/puc/tgc/kcentros/
  Main.java                Ponto de entrada (executa tudo)
  CarregadorInstancias.java Leitura e Floyd-Warshall
  KCentrosExato.java       Algoritmo exato (PLI com ojAlgo)
  KCentrosAproximado.java  Algoritmo de Gonzalez
  BaixadorDados.java       Download das instâncias
  Experimentos.java        Execução dos experimentos
  GeradorGraficos.java     Geração de gráficos
  CaminhosProjeto.java     Caminhos de arquivos do projeto
  DadosInstancia.java      Dados de uma instância
  RaiosOtimos.java         Raios ótimos de referência
data/                      Instâncias pmed1..pmed40
results/                   Resultados em CSV
relatorio/                 Relatório em LaTeX e figuras
```

## Resultados

Os resultados completos estão em `results/resultados.csv` e no relatório PDF.

O algoritmo exato obtém o raio ótimo em praticamente todas as instâncias. O algoritmo aproximado apresenta gap médio de aproximadamente 49% em relação ao ótimo, com tempo de execução muito inferior.
