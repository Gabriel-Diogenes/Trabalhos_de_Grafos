# Pontes em Grafos e Caminhos Eulerianos — Implementação em Java

## Estrutura dos Arquivos

| Arquivo | Responsabilidade |
|---|---|
| `Grafo.java` | Representação do grafo (lista de adjacência), busca em largura para conectividade |
| `DetectorDePontesNaive.java` | Detecção naïve de pontes (remove aresta + busca em largura) |
| `DetectorDePontesTarjan.java` | Detecção de pontes pelo algoritmo de Tarjan (1974) via busca em profundidade |
| `AlgoritmoFleury.java` | Algoritmo de Fleury com as duas estratégias de detecção |
| `GeradorDeGrafos.java` | Gerador de grafos aleatórios (euleriano, semi-euleriano, não-euleriano) |
| `Main.java` | Demonstração qualitativa e experimentos de tempo |
 
---

## Como Compilar e Executar

### Pré-requisito: Java JDK instalado

**Windows:** baixe em https://adoptium.net e instale normalmente.

**Linux (Ubuntu/Debian):**
```bash
sudo apt install default-jdk
```

**macOS:**
```bash
brew install openjdk
```

Verifique a instalação:
```bash
java -version
javac -version
```

### Compilar

Coloque todos os arquivos `.java` numa mesma pasta e, dentro dela, execute:

```bash
javac *.java
```

Nenhuma mensagem de erro significa que compilou com sucesso.

### Executar

```bash
java -Xss64m Main
```

A flag `-Xss64m` aumenta o tamanho da pilha de chamadas, necessário para a busca em profundidade recursiva funcionar corretamente em grafos com 100.000 vértices sem estourar o stack.

### Usando uma IDE (opcional)

Se preferir não usar o terminal, abra a pasta diretamente no **IntelliJ IDEA** ou no **VS Code** (com a extensão *Extension Pack for Java*). Ambos compilam e executam com um clique.
 
---

## Explicação dos Algoritmos

### 1. Método Naïve de Detecção de Pontes

Para cada aresta (u, v) do grafo:
1. Remove a aresta temporariamente.
2. Executa uma busca em largura para verificar conectividade.
3. Se o grafo ficou desconexo → a aresta é uma **ponte**.
4. Restaura a aresta.
   **Complexidade:** O(E × (V + E))

### 2. Algoritmo de Tarjan (1974)

Realiza uma única busca em profundidade e mantém dois vetores:
- `tempoDescoberta[v]` — instante em que v foi visitado na busca em profundidade.
- `baixo[v]` — menor `tempoDescoberta[]` alcançável a partir da subárvore de v (incluindo arestas de retorno).
  **Condição de ponte:** a aresta (u → v), onde v é filho de u na busca em profundidade, é uma ponte se:

```
baixo[v] > tempoDescoberta[u]
```

Isso significa que nenhum vértice na subárvore de v consegue alcançar u ou seus ancestrais por outro caminho, logo remover (u, v) desconecta o grafo.

**Complexidade:** O(V + E) — uma única passagem de busca em profundidade.

### 3. Algoritmo de Fleury

Constrói o caminho ou circuito euleriano passo a passo:
1. Determina o vértice de início (grau ímpar se caminho, qualquer vértice se circuito).
2. A cada passo, escolhe uma aresta adjacente que **não seja ponte** (quando possível).
3. Remove a aresta escolhida e avança para o próximo vértice.
4. Repete até não restar arestas.
   A regra de evitar pontes garante que o grafo permaneça conexo ao longo do percurso.

### 4. Condições Eulerianas

| Vértices de grau ímpar | Tipo | Resultado |
|---|---|---|
| 0 | Euleriano | Circuito euleriano existe |
| 2 | Semi-euleriano | Caminho euleriano existe |
| > 2 | Não euleriano | Não existe caminho nem circuito euleriano |
 
---

## Resultados dos Experimentos

```
Vertices     Tipo            Naive (ms)           Tarjan (ms)
----------------------------------------------------------------------
100          Euleriano       0.02                 0.02
100          Semi-Euler.     0.01                 0.02
100          Nao-Euler.      0.01                 0.01
 
1000         Euleriano       0.13                 0.14
1000         Semi-Euler.     0.12                 0.12
1000         Nao-Euler.      0.07                 0.09
 
10000        Euleriano       N/A (lento)          2.41
10000        Semi-Euler.     N/A (lento)          1.06
10000        Nao-Euler.      N/A (lento)          0.09
 
100000       Euleriano       N/A (lento)          8.27
100000       Semi-Euler.     N/A (lento)          2.84
100000       Nao-Euler.      N/A (lento)          1.15
```

### Análise

- **Para V ≤ 1.000:** os dois métodos têm tempo similar, pois os grafos gerados têm poucas arestas (≈ V) e a busca em largura do naïve é rápida.
- **Para V ≥ 10.000:** o naïve torna-se impraticável devido à sua complexidade quadrática em E. O Tarjan mantém tempo próximo ao linear, completando grafos de 100.000 vértices em poucos milissegundos.
- **Grafos não-eulerianos:** o Fleury retorna imediatamente após verificar a condição de grau, por isso o tempo é desprezível.
- **Semi-eulerianos com 100k:** o tempo ligeiramente maior deve-se à quantidade de arestas geradas e ao recálculo do Tarjan a cada passo do Fleury — ainda assim completamente viável.
---

## Referências

1. **Tarjan, R.E. (1974).** "A note on finding the bridges of a graph." *Information Processing Letters*, 2(6), 160-161. DOI: 10.1016/0020-0190(74)90047-1
2. **Fleury (1883).** "Deux problèmes de Géométrie de situation." *Journal de mathématiques élémentaires*, 2ème série, t. II, pp. 257-261.
3. **Euler, L. (1736).** "Solutio problematis ad geometriam situs pertinentis." *Commentarii Academiae Scientiarum Imperialis Petropolitanae*, 8, 128-140.
4. **Cormen, T.H., Leiserson, C.E., Rivest, R.L., Stein, C. (2009).** *Introduction to Algorithms*, 3rd ed. MIT Press. (Seções 22.2 — Busca em Largura, 22.3 — Busca em Profundidade)
5. **Sedgewick, R. & Wayne, K. (2011).** *Algorithms*, 4th Edition. Addison-Wesley. (Capítulo 4 — Graph Algorithms, pontes e busca em profundidade)
6. **Skiena, S. (2008).** *The Algorithm Design Manual*, 2nd ed. Springer. (Seção 5.6.3 — Eulerian Cycle)
7. **West, D.B. (2001).** *Introduction to Graph Theory*, 2nd ed. Prentice Hall. (Condições eulerianas)
8. **Erdős, P. & Rényi, A. (1959).** "On random graphs I." *Publicationes Mathematicae Debrecen*, 6, 290-297. (Modelo G(n,m) para grafos aleatórios)
9. **GeeksForGeeks** — "Bridge in a graph" e "Fleury's Algorithm for printing Eulerian Path or Circuit." Consultados como referência de implementação; código reescrito e adaptado. https://www.geeksforgeeks.org