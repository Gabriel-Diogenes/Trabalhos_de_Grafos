# Trabalhos de Teoria de Grafos e Computabilidade

Repositório com o conjunto de trabalhos práticos e implementações desenvolvidos na disciplina **Teoria de Grafos e Computabilidade** da **PUC Minas**. Os projetos cobrem algoritmos clássicos em grafos — detecção de pontes, caminhos eulerianos, caminhos mínimos, fluxo máximo e problemas de otimização combinatória — implementados principalmente em **Java**.

---

## Trabalhos

### 1. Pontes em Grafos e Caminhos Eulerianos

**Pasta:** raiz do repositório (`Grafo.java`, `Main.java`, etc.)

Implementação e comparação de estratégias para grafos não direcionados: detecção de pontes (método naïve e algoritmo de Tarjan) e construção de caminhos ou circuitos eulerianos com o algoritmo de Fleury. Inclui gerador de grafos aleatórios (eulerianos, semi-eulerianos e não eulerianos) e experimentos de desempenho em instâncias de até 100.000 vértices.

| Arquivo | Descrição |
|---|---|
| `Grafo.java` | Grafo com lista de adjacência e BFS para conectividade |
| `DetectorDePontesNaive.java` | Detecção naïve de pontes (remove aresta + BFS) |
| `DetectorDePontesTarjan.java` | Detecção de pontes via Tarjan (1974) |
| `AlgoritmoFleury.java` | Caminho/circuito euleriano com estratégia naïve ou Tarjan |
| `GeradorDeGrafos.java` | Geração de grafos aleatórios para testes |
| `Main.java` | Demonstração qualitativa e benchmark de tempo |

```bash
javac *.java
java -Xss64m Main
```

---

### 2. Caminho Mínimo (Dijkstra)

**Pasta:** `Implementacao3/`

Algoritmo de Dijkstra para caminho mínimo em grafo **direcionado e ponderado**. O grafo é lido de arquivos de texto; o programa exibe distância mínima, quantidade de arestas e o caminho completo. Critério de desempate: entre caminhos com mesmo custo, escolhe o de **menor número de arestas**. Inclui instâncias de teste densas e esparsas (5 a 20 vértices).

```bash
cd Implementacao3
javac CaminhoMinimo.java
java CaminhoMinimo
```

---

### 3. Caminhos Disjuntos em Arestas (Fluxo Máximo)

**Pasta:** `Implementacao4/files/`

Resolução do problema de **caminhos disjuntos em arestas** entre origem e destino, reduzido a **fluxo máximo** (Edmonds-Karp com BFS). O fluxo calculado determina a quantidade máxima de caminhos disjuntos; em seguida, os caminhos são extraídos e exibidos. Há executor de testes em grafos aleatórios e em grades, com exportação de resultados em CSV.

| Arquivo | Descrição |
|---|---|
| `CaminhosDisjuntos.java` | Ponto de entrada e saída formatada |
| `FluxoMaximo.java` | Fluxo máximo e extração de caminhos |
| `ExecutorTestes.java` | Bateria de testes e tabelas de desempenho |
| `GeradorGrafos.java` | Geração de instâncias aleatórias e em grade |

```bash
cd Implementacao4/files
javac *.java
java CaminhosDisjuntos grade_5x5.txt
java ExecutorTestes
```

---

### 4. Problema dos k-Centros (Trabalho Prático N.02)

**Pasta:** `tp02-k-centers/`

Comparação de dois algoritmos para o problema dos **k-centros** (minimizar o raio máximo de um conjunto de centros):

1. **Exato:** busca binária sobre raios candidatos + programação linear inteira (ojAlgo)
2. **Aproximado:** heurística gulosa de Gonzalez (fator de aproximação 2)

Experimentos sobre as 40 instâncias `pmed1`–`pmed40` da OR-Library, com relatório em LaTeX, gráficos e CSV de resultados. Detalhes de execução e estrutura do projeto estão em [`tp02-k-centers/README.md`](tp02-k-centers/README.md).

```bash
cd tp02-k-centers
mvn compile exec:java
```

---

## Estrutura do repositório

```
Trabalhos_de_Grafos/
├── Grafo.java, Main.java, ...     # Trabalho 1 — Pontes e Euleriano
├── Implementacao3/                  # Trabalho 2 — Dijkstra
├── Implementacao4/files/            # Trabalho 3 — Caminhos disjuntos
└── tp02-k-centers/                  # Trabalho 4 — k-Centros (Maven)
```

---

## Requisitos

| Trabalho | Requisito |
|---|---|
| 1, 2 e 3 | Java JDK (recomendado 11+) |
| 4 (k-centros) | Java 17+, Maven 3.8+ |

Para compilar o relatório LaTeX do TP02: distribuição TeX com `pdflatex`.

---

## Referências principais

- **Tarjan (1974)** — detecção de pontes em grafos
- **Fleury (1883) / Euler (1736)** — caminhos e circuitos eulerianos
- **Cormen et al. (CLRS)** — Dijkstra, BFS, fluxo máximo
- **Gonzalez (1985)** — aproximação 2 para k-centros
- **OR-Library** — instâncias p-median / pmed para o TP02
