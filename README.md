# Juego de Puzle Deslizante Mejorado con IA

**Descripción**

Este es un juego de rompecabezas deslizante desarrollado en Android Studio. El juego presenta una cuadrícula de 3x3 donde los jugadores deben reorganizar las piezas para formar una imagen completa.

**Características**

* Seguimiento de Puntuaciones: Los jugadores pueden ver su puntuación actual y competir por el mejor tiempo.

* Asistencia de IA: Utiliza algoritmos de búsqueda en profundidad y A* con heurísticas de distancia de Manhattan. La IA ayuda a los jugadores de dos maneras:

* Resolución Automática: Al seleccionar una opción, la IA puede resolver el rompecabezas automáticamente, mostrando el camino óptimo desde el estado actual hasta la solución. Esto se logra evaluando todos los movimientos posibles y eligiendo el más eficiente.

* Sugerencias para el Jugador: La IA también puede proporcionar sugerencias a los jugadores sobre los movimientos más efectivos a realizar en cada turno, optimizando así su estrategia y aumentando la posibilidad de resolver el rompecabezas con éxito.

* Modo de 2 Jugadores: Los jugadores pueden competir entre sí en un modo de juego de dos personas.

**Tecnologías Utilizadas**

* Android Studio
* Java
* Algoritmos de Inteligencia Artificial (Búsqueda en Profundidad, A* con Heurística de Distancia de Manhattan)

**Cómo Funciona la IA**

* Búsqueda en Profundidad: Este algoritmo se utiliza para explorar todas las posibles configuraciones del rompecabezas. Se expande a lo largo de un camino en profundidad antes de retroceder, lo que permite explorar diferentes rutas de solución.

* Algoritmo A*: Este es un algoritmo de búsqueda más avanzado que utiliza una heurística para estimar la distancia desde el estado actual hasta el objetivo. Utiliza la distancia de Manhattan como su heurística principal, que calcula la suma de las distancias verticales y horizontales de cada pieza hasta su posición objetivo.

* Heurística de Distancia de Manhattan: Esta técnica mide cuántas posiciones una pieza está alejada de su posición correcta. Cuanto menor sea la puntuación, más cerca estará el rompecabezas de ser resuelto, lo que permite que la IA elija el camino más eficiente.

# AI-Enhanced Slider Puzzle Game

**Description**

This is a slider puzzle game developed in Android Studio. The game features a 3x3 grid where players must rearrange the tiles to form a complete image.

**Features**

* Score Tracking: Players can view their current score and compete for the best time.
  
* AI Assistance: Utilizes depth-first search and A* algorithms with Manhattan distance heuristics. The AI assists players in two main ways:
  
* Automatic Solving: By selecting an option, the AI can automatically solve the puzzle, showing the optimal path from the current state to the solution. This is achieved by evaluating all possible moves and selecting the most efficient one.
  
* Player Suggestions: The AI can also provide suggestions to players regarding the most effective moves to make at each turn, optimizing their strategy and increasing the likelihood of successfully solving the puzzle.
  
* 2-Player Mode: Players can compete against each other in a two-player game mode.
  
**Technologies Used**

* Android Studio
  
* Java
  
* Artificial Intelligence Algorithms (Depth-First Search, A* with Manhattan Distance Heuristic)

**How the AI Works**

* Depth-First Search: This algorithm is used to explore all possible configurations of the puzzle. It expands along a path in depth before backtracking, allowing for the exploration of different solution routes.

* A Algorithm*: This is a more advanced search algorithm that uses a heuristic to estimate the distance from the current state to the goal. It employs the Manhattan distance as its primary heuristic, calculating the sum of the vertical and horizontal distances of each piece from its target position.

* Manhattan Distance Heuristic: This technique measures how far each piece is from its correct position. The lower the score, the closer the puzzle is to being solved, allowing the AI to choose the most efficient path.
