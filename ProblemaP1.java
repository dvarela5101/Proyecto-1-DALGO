import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.Arrays;


public class ProblemaP1 {

    public static void main(String[] args) throws IOException {
        StreamTokenizer in = new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)));
        StringBuilder salida = new StringBuilder();

        in.nextToken();
        int casos = (int) in.nval;

        for (int caso = 0; caso < casos; caso++) {
            in.nextToken();
            int n = (int) in.nval; // numero de orbitas
            in.nextToken();
            int m = (int) in.nval; // numero de posiciones por orbita
            in.nextToken();
            int p = (int) in.nval; // numero de portales

            // Energia de movimiento por orbita, 1-indexada: energia[i] es e_i
            int[] energia = new int[n + 1];
            for (int i = 1; i <= n; i++) {
                in.nextToken();
                energia[i] = (int) in.nval;
            }

            // Portales: cada fila es {orbitaOrigen, posicionOrigen, orbitaDestino, posicionDestino}
            int[][] portales = new int[p][4];
            for (int k = 0; k < p; k++) {
                in.nextToken();
                portales[k][0] = (int) in.nval; // xs
                in.nextToken();
                portales[k][1] = (int) in.nval; // ys
                in.nextToken();
                portales[k][2] = (int) in.nval; // xe
                in.nextToken();
                portales[k][3] = (int) in.nval; // ye
            }

            long energiaMinima = energiaMinimaRuta(n, m, energia, portales);

            if (energiaMinima < 0) {
                salida.append("NO EXISTE").append('\n');
            } else {
                salida.append(energiaMinima).append('\n');
            }
        }

        System.out.print(salida);
    }

    /**
     * Calcula la energia minima para ir de (1,1) a (n,m).
     *
     *
     * @param n        numero de orbitas (1 <= n <= 10^3)
     * @param m        posiciones por orbita (1 <= m <= 10^3)
     * @param energia  costo de moverse dentro de cada orbita, 1-indexado
     * @param portales filas {orbitaOrigen, posOrigen, orbitaDestino, posDestino}
     * @return energia minima de (1,1) a (n,m), o -1 si no existe ruta
     *
     * Complejidad: O(n*m + p) en tiempo y en espacio
     */
    private static long energiaMinimaRuta(int n, int m, int[] energia, int[][] portales) {
        final long INF = Long.MAX_VALUE / 4; // "no alcanzado todavia"; /4 evita desbordamiento al sumarle energia
        final int ancho = m + 1; // cuantas casillas ocupa cada orbita dentro del arreglo plano
        final int p = portales.length;

        // dist[i*ancho + j] = costo minimo para llegar a la posicion (i,j).
        // Arranca todo en INF salvo (1,1), el punto de partida, que cuesta 0.
        long[] dist = new long[(n + 1) * ancho];
        Arrays.fill(dist, INF);
        dist[1 * ancho + 1] = 0; // dist(1,1) = 0

        // Agrupamos los portales por su orbita de origen (counting sort,
        // formato CSR) para poder consultar "los portales que salen de la
        // orbita i" sin necesitar una lista dinamica por orbita.

        // Cuantos portales salen de cada orbita
        int[] conteo = new int[n + 2];
        for (int k = 0; k < p; k++) {
            int xs = portales[k][0];
            conteo[xs]++;
        }

        // inicio[i] = donde empieza el bloque de portales de
        // la orbita i dentro de los arreglos planos de abajo
        int[] inicio = new int[n + 2];
        for (int i = 1; i <= n; i++) {
            inicio[i + 1] = inicio[i] + conteo[i];
        }

        // Colocamos cada portal en su casillero final. "cursor" es una
        // copia de "inicio" que se va avanzando a medida que ubicamos
        // portales, para no perder los limites originales de cada bloque.
        
        int[] cursor = inicio.clone();
        int[] portalYS = new int[p]; // posicion de origen del portal, dentro de su orbita
        int[] portalXE = new int[p]; // orbita de destino del portal
        int[] portalYE = new int[p]; // posicion de destino del portal
        for (int k = 0; k < p; k++) {
            int xs = portales[k][0];
            int destino = cursor[xs]++; // casillero asignado a este portal, y se avanza el cursor
            portalYS[destino] = portales[k][1];
            portalXE[destino] = portales[k][2];
            portalYE[destino] = portales[k][3];
        }
        // Desde aca, "los portales que salen de la orbita i" son el rango
        // [inicio[i], inicio[i+1]) de portalYS/XE/YE.

        // Procesamos las orbitas en orden, de 1 a n. Es valido porque el
        // enunciado garantiza xs < xe en todo portal: nunca hay que volver
        // a una orbita anterior.
        for (int i = 1; i <= n; i++) {
            int base = i * ancho; // primer indice de la fila de la orbita i dentro del arreglo plano
            int e_i = energia[i];

            // Barrido izquierda -> derecha: para cada posicion j, revisa
            // si conviene llegar desde la vecina de la izquierda (ya
            // resuelta, porque el bucle va en orden creciente de j).
            for (int j = 2; j <= m; j++) {
                long nuevoCosto = dist[base + j - 1] + e_i;
                if (nuevoCosto < dist[base + j]) {
                    dist[base + j] = nuevoCosto;
                }
            }

            // Barrido derecha -> izquierda: lo mismo pero desde la vecina
            // de la derecha. Al terminar, dist[base+j] ya es el costo
            // minimo definitivo para llegar a esa posicion de la orbita i.
            for (int j = m - 1; j >= 1; j--) {
                long nuevoCosto = dist[base + j + 1] + e_i;
                if (nuevoCosto < dist[base + j]) {
                    dist[base + j] = nuevoCosto;
                }
            }

            // Disparamos los portales que salen de esta orbita: cada uno
            // empuja su costo (ya definitivo) hacia la orbita destino, que
            // siempre es una orbita mayor a i, o sea que aun no se procesa.
            for (int idx = inicio[i]; idx < inicio[i + 1]; idx++) {
                int ys = portalYS[idx];
                int xe = portalXE[idx];
                int ye = portalYE[idx];
                long costoOrigen = dist[base + ys];
                int destinoIdx = xe * ancho + ye;
                if (costoOrigen < dist[destinoIdx]) {
                    dist[destinoIdx] = costoOrigen;
                }
            }
        }

        // La respuesta es el costo definitivo de llegar a (n,m)
        long respuesta = dist[n * ancho + m];
        return (respuesta >= INF) ? -1 : respuesta;
    }
}
