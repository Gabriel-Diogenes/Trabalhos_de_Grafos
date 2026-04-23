# Pontes em Grafos e Caminhos Eulerianos — Implementação em Java

## Estrutura dos Arquivos

| Arquivo | Responsabilidade |
|---|---|
| `Graph.java` | Representação do grafo (lista de adjacência), BFS de conectividade |
| `NaiveBridgeFinder.java` | Detecção naïve de pontes (remove aresta + BFS) |
| `TarjanBridgeFinder.java` | Detecção de pontes pelo algoritmo de Tarjan (1974) |
| `FleuryAlgorithm.java` | Algoritmo de Fleury com as duas estratégias |
| `GraphGenerator.java` | Gerador de grafos aleatórios (euleriano, semi, não-euleriano) |
| `Main.java` | Demonstração e experimentos de tempo |

---

## Como Compilar e Executar

```bash
javac *.java
java -Xss64m Main
```

A flag `-Xss64m` aumenta o stack para DFS profunda em grafos grandes.

---

## Explicação dos Algoritmos

### 1. Método Naïve de Detecção de Pontes

Para cada aresta (u, v) do grafo:
1. Remove a aresta temporariamente.
2. Executa BFS para verificar conectividade.
3. Se o grafo ficou desconexo → a aresta é uma **ponte**.
4. Restaura a aresta.

**Complexidade:** O(E × (V + E))

### 2. Algoritmo de Tarjan (1974)

Realiza uma única DFS e mantém dois vetores:
- `disc[v]` — tempo de descoberta de v na DFS.
- `low[v]` — menor `disc[]` alcançável a partir da subárvore de v (incluindo back edges).

**Condição de ponte:** a aresta (u → v), onde v é filho de u, é uma ponte se:

```
low[v] > disc[u]
```

Isso significa que nenhum vértice na subárvore de v consegue alcançar u ou seus ancestrais por outro caminho, então remover (u,v) desconecta o grafo.

**Complexidade:** O(V + E) — uma única passagem DFS.

### 3. Algoritmo de Fleury

Constrói o caminho/circuito euleriano passo a passo:
1. Determina o vértice de início (grau ímpar se caminho, qualquer se circuito).
2. A cada passo, escolhe uma aresta adjacente que **não seja ponte** (quando possível).
3. Remove a aresta escolhida e avança para o próximo vértice.
4. Repete até não restar arestas.

A regra de evitar pontes garante que o grafo permaneça conexo ao longo do percurso.

### 4. Condições Eulerianas

| Graus ímpares | Tipo | Resultado |
|---|---|---|
| 0 | Euleriano | Circuito euleriano |
| 2 | Semi-euleriano | Caminho euleriano |
| > 2 | Não euleriano | Não existe |

---

## Resultados dos Experimentos

```
Vértices     Tipo            Naïve (ms)           Tarjan (ms)
----------------------------------------------------------------------
100          Euleriano       0.02                 0.02
100          Semi-Euler.     0.02                 0.02
100          Não-Euler.      0.01                 0.01

1000         Euleriano       0.13                 0.13
1000         Semi-Euler.     0.12                 0.15
1000         Não-Euler.      0.08                 0.08

10000        Euleriano       N/A (impraticável)   2.63
10000        Semi-Euler.     N/A (impraticável)   1.16
10000        Não-Euler.      N/A (impraticável)   0.15

100000       Euleriano       N/A (impraticável)   4.03
100000       Semi-Euler.     N/A (impraticável)   20.26
100000       Não-Euler.      N/A (impraticável)   0.91
```

### Análise

- **Para V ≤ 1.000:** os dois métodos têm tempo similar, pois o grafo gerado tem poucas arestas (≈ V) e a BFS do naïve é rápida.
- **Para V ≥ 10.000:** o naïve torna-se impraticável (complexidade quadrática em E). O Tarjan mantém tempo sub-linear ao número de arestas, completando 100.000 vértices em milissegundos.
- **Grafos não-eulerianos:** Fleury retorna imediatamente após verificar a condição de grau, daí o tempo ser desprezível.
- **Semi-eulerianos com 100k:** o tempo maior (20ms) deve-se à quantidade de arestas geradas e recalculos de Tarjan a cada passo do Fleury — ainda assim viável.

---

## Referências

1. **Tarjan, R.E. (1974).** "A note on finding the bridges of a graph." *Information Processing Letters*, 2(6), 160-161. DOI: 10.1016/0020-0190(74)90047-1

2. **Fleury (1883).** "Deux problèmes de Géométrie de situation." *Journal de mathématiques élémentaires*, 2ème série, t. II, pp. 257-261.

3. **Euler, L. (1736).** "Solutio problematis ad geometriam situs pertinentis." *Commentarii Academiae Scientiarum Imperialis Petropolitanae*, 8, 128-140.

4. **Cormen, T.H., Leiserson, C.E., Rivest, R.L., Stein, C. (2009).** *Introduction to Algorithms*, 3rd ed. MIT Press. (Seções 22.2 BFS, 22.3 DFS)

5. **Sedgewick, R. & Wayne, K. (2011).** *Algorithms*, 4th Edition. Addison-Wesley. (Capítulo 4 — Graph Algorithms, bridges e DFS)

6. **Skiena, S. (2008).** *The Algorithm Design Manual*, 2nd ed. Springer. (Seção 5.6.3 — Eulerian Cycle)

7. **West, D.B. (2001).** *Introduction to Graph Theory*, 2nd ed. Prentice Hall. (Condições eulerianas)

8. **Erdős, P. & Rényi, A. (1959).** "On random graphs I." *Publicationes Mathematicae Debrecen*, 6, 290-297. (Modelo G(n,m) para grafos aleatórios)

9. **GeeksForGeeks** — "Bridge in a graph" e "Fleury's Algorithm for printing Eulerian Path or Circuit." Consultados como referência de implementação, código reescrito e adaptado. https://www.geeksforgeeks.org
