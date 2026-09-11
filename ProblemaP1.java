import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;

/**
 * Autor(es): <completar nombre(s)>
 *
 * ISIS 2112 - Diseno de Algoritmos
 * Semestre 2026-20 - Proyecto, Parte 1
 * Problema: Termion - ruta de energia minima en un campo de n orbitas
 * por m posiciones, con portales de teletransportacion sin costo.
 */
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
     * Calcula la energia minima que requiere un termion para viajar desde la
     * entrada del campo, ubicada en la posicion (1,1), hasta la salida,
     * ubicada en la posicion (n,m).
     *
     * Dentro de una orbita i, el termion puede moverse entre posiciones
     * adyacentes consumiendo energia[i] por movimiento. Cada portal k
     * permite viajar, sin costo, desde la orbita portales[k][0] posicion
     * portales[k][1] hacia la orbita portales[k][2] posicion portales[k][3],
     * en una unica direccion.
     *
     * @param n        numero de orbitas del campo (1 <= n <= 10^3)
     * @param m        numero de posiciones por orbita (1 <= m <= 10^3)
     * @param energia  energia de movimiento por orbita, 1-indexada (tamano n+1);
     *                 energia[i] es el costo de un movimiento dentro de la orbita i
     * @param portales matriz p x 4 con la descripcion de los portales:
     *                 {orbitaOrigen, posicionOrigen, orbitaDestino, posicionDestino}
     * @return la energia minima de la ruta optima entre (1,1) y (n,m),
     *         o -1 si no existe ninguna ruta posible
     */
    private static long energiaMinimaRuta(int n, int m, int[] energia, int[][] portales) {
        // TODO: Implementar el algoritmo que calcule la ruta de menor consumo
        // de energia entre la posicion (1,1) y la posicion (n,m).
        //
        // Sugerencia: modelar el campo como un grafo dirigido de pesos no
        // negativos donde cada nodo representa una posicion (orbita, posicion) y:
        //   - existe una arista entre posiciones adyacentes de una misma
        //     orbita i con peso energia[i] (el movimiento dentro de la
        //     orbita se puede hacer en ambos sentidos);
        //   - existe una arista de peso 0 por cada portal, unicamente en la
        //     direccion que este indique (de su origen a su destino).
        // Luego aplicar un algoritmo de camino minimo (por ejemplo Dijkstra,
        // ya que todos los pesos son no negativos) desde el nodo (1,1)
        // hasta el nodo (n,m).
        //
        // Retornar la energia minima si existe una ruta, o -1 en caso
        // contrario (se traduce en la salida "NO EXISTE").
        long INF = Long.MAX_VALUE/4;
        long[][] dist = new long[n+1][m+1]; //dist[i][j] es el minimo costo para llegar a (i,j)
        for (int i=1; i<=n; i++){
            for (int j=1; j<=m; j++){
                dist[i][j] = INF; //la distancia inicial para llegar a todas se pone como infinito
            }

        }
        dist[1][1]=0; //siempre la celda inicial es 0 porque no gasta energia
        ArrayList<int[]>[] salen= new ArrayList[n+1]; //salen[i] es la lista de portales que tienen origen en la orbita i
        for (int i=1; i<=n;i++){
            salen[i] = new ArrayList<>();
        }
        for(int k=0;k<portales.length;k++){
            int orbitaOrigen = portales[k][0];
            salen[orbitaOrigen].add(portales[k]);

        }



        for(int i=1;i<=n;i++){ //pasar de orbita en orbita, nunca se devuelve
            for (int j=2;j<=m;j++){ //pasar de posiciones de izquierda a derecha
                long nuevoCosto = dist[i][j-1]+energia[i];
                dist[i][j]=Math.min(dist[i][j],nuevoCosto);
            }
            for(int j=m-1; j>=1;j--){ // de derecha a izquierda
                long nuevoCosto=dist[i][j+1]+energia[i];
                dist[i][j]=Math.min(dist[i][j],nuevoCosto);
            }

            for (int[]portal:salen[i]){//para cada poratl que sale de la orbita i 
                int ys=portal[1];
                int xe=portal[2];
                int ye=portal[3];
                dist[xe][ye]=Math.min(dist[xe][ye],dist[i][ys]);
            }
        }
        if (dist[n][m]>=INF){
            return -1; //no se encuentra la ruta

        }
        return dist[n][m];
    }
}
//COMPLEJIDAD TEMPORAL= O(nm+p), se recorren todas las posiciones y todos los portales
//COMPLEJIDAD ESPACIAL = O(nm+p), se guarda en una estructura las posiciones y otra portales
